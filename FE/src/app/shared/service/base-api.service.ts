import { HttpClient, HttpParams } from '@angular/common/http';
import { inject } from '@angular/core';
import { map, Observable, switchMap } from 'rxjs';
import { AccountService } from '../core/auth/account/account.service';
import { environment } from '../../../environments/environment';
import { Page } from '../models/Core/page.model';

export type CreateEntity<T> = Omit<T, 'id'> & { id?: number | string };

export abstract class BaseApiService<T> {
  public readonly fullBaseUrl: string;
  protected accountService = inject(AccountService);
  private tokenUrl = 'http://192.168.68.90:8080/auth/realms/QLSX/protocol/openid-connect/token';
  private usersUrl = 'http://192.168.68.90:8080/auth/admin/realms/QLSX/users?first=0&max=2000';
  private approvalUrl = `${environment.apiBaseUrl}/api/approvals`;
  constructor(protected http: HttpClient, protected baseUrl: string) {
    this.fullBaseUrl = `${environment.apiBaseUrl}/${this.baseUrl}`;
  }

  getAll(): Observable<T[]> {
    return this.http.get<T[]>(`${this.fullBaseUrl}`);
  }

  getApproved(): Observable<T[]> {
    return this.http.get<T[]>(`${this.fullBaseUrl}/approve`);
  }

  getAllByPaged(filters: any = {}, page: number = 0): Observable<Page<any>> {
    let params = new HttpParams()
      .set('page', page.toString());
    Object.keys(filters).forEach(key => {
      let value = filters[key];
      if (value !== null && value !== undefined && value !== '') {
        if (value instanceof Date) {
          const year = value.getFullYear();
          const month = (value.getMonth() + 1).toString().padStart(2, '0');
          const day = value.getDate().toString().padStart(2, '0');

          value = `${year}-${month}-${day}`;
        }
        params = params.set(key, value);
      }
    });

    return this.http.get<Page<any>>(
      `${this['fullBaseUrl']}/paged`,
      { params }
    );
  }

  getById(id: number | string): Observable<T> {
    return this.http.get<T>(`${this.fullBaseUrl}/${id}`);
  }

  create(entity: CreateEntity<T>): Observable<T> {
    const now = new Date();
    const isoLocalVN = now.getFullYear() + '-' +
      String(now.getMonth() + 1).padStart(2, '0') + '-' +
      String(now.getDate()).padStart(2, '0') + 'T' +
      String(now.getHours()).padStart(2, '0') + ':' +
      String(now.getMinutes()).padStart(2, '0') + ':' +
      String(now.getSeconds()).padStart(2, '0');

    const newEntity = { ...entity, createdAt: isoLocalVN, updatedAt: isoLocalVN, createdBy: this.accountService.getUser()?.fullName ?? 'unknown' };

    return this.http.post<T>(this.fullBaseUrl, newEntity);
  }

  update(id: number | string, data: T): Observable<T> {
    const now = new Date();
    const updatedAtVN = now.getFullYear() + '-' +
                    String(now.getMonth() + 1).padStart(2, '0') + '-' +
                    String(now.getDate()).padStart(2, '0') + 'T' +
                    String(now.getHours()).padStart(2, '0') + ':' +
                    String(now.getMinutes()).padStart(2, '0') + ':' +
                    String(now.getSeconds()).padStart(2, '0');
    const updatedEntity = {
      ...data,
      updatedAt: updatedAtVN,
      updatedBy: this.accountService.getUser()?.fullName ?? 'unknown',
    };
    return this.http.put<T>(`${this.fullBaseUrl}/${id}`, updatedEntity);
  }

  delete(id: number | string): Observable<void> {
    return this.http.delete<void>(`${this.fullBaseUrl}/${id}`);
  }

  getToken(): Observable<string> {
    const body = new HttpParams().set('grant_type', 'password').set('client_id', 'iso').set('username', 'admin').set('password', '123321');

    return this.http
      .post<any>(this.tokenUrl, body.toString(), {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      })
      .pipe(map(res => res.access_token));
  }

  getUsers(): Observable<any[]> {
    return this.getToken().pipe(
      switchMap(token =>
        this.http.get<any[]>(this.usersUrl, {
          headers: { Authorization: `Bearer ${token}` },
        }),
      ),
    );
  }

  createApprovalEntity(dto: any, entityType: string): Observable<any> {
    return this.http.post(`${this.approvalUrl}/approval?entityType=${entityType}`, dto);
  }

  approvalEntity(dto: any): Observable<any> {
    return this.http.put(`${this.approvalUrl}/${dto.id}`, dto);
  }
}