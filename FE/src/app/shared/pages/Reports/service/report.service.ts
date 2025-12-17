import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BaseApiService } from '../../../service/base-api.service';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ReportService extends BaseApiService<any> {
    constructor(http: HttpClient) {
        super(http, 'api/reports');
    }

    formatDateOnly(date?: Date): string | null {
        if (!date) return null;
        const yyyy = date.getFullYear();
        const mm = String(date.getMonth() + 1).padStart(2, '0');
        const dd = String(date.getDate()).padStart(2, '0');
        return `${yyyy}-${mm}-${dd}`;
    }


    //Report 1
    getReports(filter: any): Observable<any[]> {
        const payload = {
            ...filter,
            startDate: this.formatDateOnly(filter.startDate),
            endDate: this.formatDateOnly(filter.endDate)
        };
        return this.http.post<any[]>(
            `${this['fullBaseUrl']}`,
            payload, { withCredentials: true }
        );
    }

    //Report 2
    getMaintenanceReport(filter: any,page: number,size: number): Observable<any> {
        const payload = {
            ...filter,
            startDate: this.formatDateOnly(filter.startDate),
            endDate: this.formatDateOnly(filter.endDate)
        };
        const params = new HttpParams()
            .set('page', page)
            .set('size', size);
        return this.http.post<any>(`${this['fullBaseUrl']}/maintenance`, payload, { params, withCredentials: true });
    }
}
