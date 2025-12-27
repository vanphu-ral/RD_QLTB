import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { keyMapping } from '../../../../models/PlanManger/key-mapping.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class KeyMappingService extends BaseApiService<keyMapping> {
    constructor(http: HttpClient) {
        super(http, 'api/keyMappings');
    }

    createList(entities: Array<keyMapping>) {
        return this.http.post(`${this['fullBaseUrl']}/creates`, entities);
    }

    getBySampleReport(sampleReportId: number | string): Observable<keyMapping[]> {
        return this.http.get<keyMapping[]>(`${this['fullBaseUrl']}/bySampleReport/${sampleReportId}`);
    }
}
