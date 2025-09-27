import { HttpClient, HttpParams } from '@angular/common/http';
import { inject } from '@angular/core';
import { map, Observable, switchMap } from 'rxjs';
import { AccountService } from '../core/auth/account/account.service';

export type CreateEntity<T> = Omit<T, 'id'> & { id?: number | string };

export abstract class BaseApiService<T> {
  private readonly fullBaseUrl: string;
  protected accountService = inject(AccountService);
  private tokenUrl = 'http://192.168.68.90:8080/auth/realms/QLSX/protocol/openid-connect/token';
  private usersUrl = 'http://192.168.68.90:8080/auth/admin/realms/QLSX/users?first=0&max=2000';
  private approvalUrl = `http://localhost:8081/api/approvals`;
  constructor(protected http: HttpClient, protected baseUrl: string) {
    this.fullBaseUrl = `http://localhost:8081/${this.baseUrl}`;
  }

  getAll(): Observable<T[]> {
    return this.http.get<T[]>(`${this.fullBaseUrl}`, { withCredentials: true });
  }

  getById(id: number | string): Observable<T> {
    return this.http.get<T>(`${this.fullBaseUrl}/${id}`, { withCredentials: true });
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

    return this.http.post<T>(this.fullBaseUrl, newEntity, { withCredentials: true });
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
    return this.http.put<T>(`${this.fullBaseUrl}/${id}`, updatedEntity, { withCredentials: true });
  }

  delete(id: number | string): Observable<void> {
    return this.http.delete<void>(`${this.fullBaseUrl}/${id}`, { withCredentials: true });
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

  approvalEntity(dto: any, entityType: string): Observable<any> {
    return this.http.post(`${this.approvalUrl}/approval?entityType=${entityType}`, dto, { withCredentials: true });
  }
}