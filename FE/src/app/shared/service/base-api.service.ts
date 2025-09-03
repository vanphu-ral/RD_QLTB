import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type CreateEntity<T> = Omit<T, 'id'> & { id?: number | string };

export abstract class BaseApiService<T> {
  private readonly fullBaseUrl: string;
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

    const newEntity = { ...entity, createdAt: isoLocalVN, updatedAt: isoLocalVN };

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
    };
    return this.http.put<T>(`${this.fullBaseUrl}/${id}`, updatedEntity, { withCredentials: true });
  }

  delete(id: number | string): Observable<void> {
    return this.http.delete<void>(`${this.fullBaseUrl}/${id}`, { withCredentials: true });
  }
}