import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService, CreateEntity } from '../../../../service/base-api.service';
import { CriterialGroup } from '../../../../models/PlanManger/criterial-group.model';
import { Signature } from '../../../../models/SystemManager/signature.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SignatureService extends BaseApiService<Signature> {
  constructor(http: HttpClient) {
    super(http, 'api/userImages');
  }

  getByUsername(username: string): Observable<Signature> {
    return this.http.get<Signature>(`${this['fullBaseUrl']}/by-username/${username}`);
  }

  getByListUsernames(usernames: string[]): Observable<Signature[]> {
    return this.http.post<Signature[]>(`${this['fullBaseUrl']}/by-usernames`, usernames);
  }

  override create(entity: CreateEntity<any>): Observable<any> {
    // delete entity.createdBy;
    return this.http.post<any>(`${this['fullBaseUrl']}`, entity);
  }

  override update(id: number, entity: any): Observable<any> {
    // delete entity.updatedBy;
    return this.http.put<any>(`${this['fullBaseUrl']}/${id}`, entity);
  }
}
