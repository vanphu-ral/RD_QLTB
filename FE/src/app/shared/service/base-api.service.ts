import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export abstract class BaseApiService<T> {
  constructor(protected http: HttpClient, protected baseUrl: string) {}

  getAll(): Observable<T[]> {
    return this.http.get<T[]>(`${this.baseUrl}`);
  }

  getById(id: number | string): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}/${id}`);
  }

  create(data: T): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}`, data);
  }

  update(id: number | string, data: T): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number | string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
