import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Định nghĩa kiểu dữ liệu cho đối tượng khi thêm mới
export type CreateEntity<T> = Omit<T, 'id'> & { id?: number | string };

export abstract class BaseApiService<T> {
  constructor(protected http: HttpClient, protected baseUrl: string) {}

  getAll(): Observable<T[]> {
    return this.http.get<T[]>(`${this.baseUrl}`, { withCredentials: true });
  }

  getById(id: number | string): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }

  create(entity: CreateEntity<T>): Observable<T> {
    const now = new Date();
    // Thêm createdAt và updatedAt vào đối tượng trước khi gửi
    const newEntity = {
      ...entity,
      createdAt: now,
      updatedAt: now,
    };
    return this.http.post<T>(this.baseUrl, newEntity, { withCredentials: true });
  }

  update(id: number | string, data: T): Observable<T> {
    const now = new Date();
    // Thêm updatedAt vào đối tượng trước khi gửi
    const updatedEntity = {
      ...data,
      updatedAt: now,
    };
    return this.http.put<T>(`${this.baseUrl}/${id}`, updatedEntity, { withCredentials: true });
  }

  delete(id: number | string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }
}