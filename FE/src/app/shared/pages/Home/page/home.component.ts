import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, PLATFORM_ID, signal } from '@angular/core';
import { Account } from '../../../core/auth/account/account.model';
import { AccountService } from '../../../core/auth/account/account.service';
import { LoginService } from '../../../core/auth/login/login.service';
import { SharedModule } from '../../../../share.module';
import { HttpClient } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { LoginFormComponent } from "../component/login-from.component";
import { ApplicationConfigService } from '../../../core/config/application-config.service';
import ChartDataLabels from 'chartjs-plugin-datalabels';

@Component({
    selector: 'app-home',
    standalone: true,
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss'],
    imports: [CommonModule, SharedModule, LoginFormComponent]
})
export class HomeComponent {
    account = signal<Account | null>(null);

    filter: any = {};
    listBranchs: any[] = [];
    listTeams: any[] = [];

    mixedData: any;
    barData: any;
    pieData: any;
    chartOptions: any;

    chartPlugins = [ChartDataLabels];
    pieOptions: any;

    constructor(private accountService: AccountService, private cdr: ChangeDetectorRef) { }

    ngOnInit(): void {
        this.accountService.identity().subscribe(account => this.account.set(account));
        this.initChart()
    }

    search() {
        console.log(this.filter);
    }

    initChart() {
        this.mixedData = {
            labels: ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'],
            datasets: [
                {
                    type: 'line', // Chỉ định là đường
                    label: 'Kế hoạch',
                    borderColor: '#ef4444', // Màu đỏ
                    borderWidth: 2,
                    fill: false,
                    tension: 0.4,
                    data: [50, 60, 70, 65, 80, 75, 90]
                },
                {
                    type: 'bar', // Chỉ định là cột
                    label: 'Thực tế',
                    backgroundColor: '#1094ab', // Màu Teal
                    data: [45, 55, 75, 60, 85, 70, 85],
                    borderColor: 'white',
                    borderWidth: 2
                }
            ]
        };
        this.barData = {
            labels: ['Xưởng A', 'Xưởng B', 'Xưởng C', 'Xưởng D'],
            datasets: [{
                label: 'Số lượng bảo trì',
                backgroundColor: '#f59e0b',
                data: [45, 75, 50, 85]
            }]
        };
        this.pieData = {
            labels: ['Đang chạy', 'Đang dừng', 'Đang sửa'],
            datasets: [{
                data: [540, 120, 41],
                backgroundColor: ['#1094ab', '#f59e0b', '#ef4444'],
                hoverBackgroundColor: ['#0e7a8c', '#d97706', '#dc2626']
            }]
        };
        // this.chartOptions = {
        //     maintainAspectRatio: false,
        //     aspectRatio: 0.8,
        //     plugins: {
        //         legend: { labels: { color: '#495057' } }
        //     },
        //     scales: {
        //         x: { grid: { display: false } },
        //         y: { grid: { color: '#ebedef' } }
        //     }
        // };


        this.chartOptions = {
            maintainAspectRatio: false,
            aspectRatio: 0.8,
            plugins: {
                legend: { labels: { color: '#495057' } },
                
                // Cấu hình cho Datalabels
                datalabels: {
                    anchor: 'end', // Neo vị trí (start, center, end)
                    align: 'top',  // Căn chỉnh so với điểm neo (để số nằm trên đầu cột)
                    color: '#495057', // Màu chữ
                    font: {
                        weight: 'bold',
                        size: 11
                    },
                    formatter: (value: any, context: any) => {
                        // Tùy chỉnh hiển thị (ví dụ thêm đơn vị)
                        return value; 
                    }
                }
            },
            layout: {
                padding: {
                    top: 20 // Thêm padding top để số không bị cắt mất khi ở đỉnh biểu đồ
                }
            },
            scales: {
                x: { grid: { display: false } },
                y: { grid: { color: '#ebedef' } }
            }
        };

        this.pieOptions = {
             ...this.chartOptions,
             plugins: {
                 datalabels: {
                     anchor: 'center',
                     align: 'center',
                     color: 'white'
                 }
             }
        }

        this.cdr.markForCheck();
    }

}