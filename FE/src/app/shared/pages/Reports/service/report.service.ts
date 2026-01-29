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
            payload
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
        return this.http.post<any>(`${this['fullBaseUrl']}/maintenance`, payload, { params });
    }


    //Report 3
    getErrorReportSummary(filter: any, page: number, size: number): Observable<any> {
        let params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString())
            .set('fromDate', filter.fromDate.toISOString()) 
            .set('toDate', filter.toDate.toISOString());

        if (filter.branchIds && filter.branchIds.length > 0) {
            filter.branchIds.forEach((id: number) => {
                params = params.append('branchIds', id.toString());
            });
        }
        if (filter.teamIds?.length > 0) {
            filter.teamIds.forEach((id: any) => params = params.append('teamIds', id.toString()));
        }
        if (filter.groupIds && filter.groupIds.length > 0) {
            filter.groupIds.forEach((id: number) => {
                params = params.append('groupIds', id.toString());
            });
        }

        return this.http.get<any>(`${this['fullBaseUrl']}/error-summary`, { params });
    }
    
    // Report 3 - Comprehensive Report
    getComprehensiveReport(
        filter: any,
        page: number = 0,
        size: number = 100
    ): Observable<any> {

        let params = new HttpParams()
            .set('fromDate', filter.fromDate.toISOString()) 
            .set('toDate', filter.toDate.toISOString())
            .set('page', page.toString())
            .set('size', size.toString());

        if (filter.branchIds && filter.branchIds.length > 0) {
            filter.branchIds.forEach((id: number) => {
                params = params.append('branchIds', id.toString());
            });
        }
        if (filter.teamIds?.length > 0) {
            filter.teamIds.forEach((id: any) => params = params.append('teamIds', id.toString()));
        }
        if (filter.groupIds && filter.groupIds.length > 0) {
            filter.groupIds.forEach((id: number) => {
                params = params.append('groupIds', id.toString());
            });
        }

        return this.http.get<any>(`${this['fullBaseUrl']}/comprehensive-report`, { params });
    }
}
