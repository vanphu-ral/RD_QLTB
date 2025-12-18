import { ChangeDetectorRef, Component } from '@angular/core';
import { SharedModule } from '../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { NavigationService } from '../../../service/navigation.service';
import { BranchService } from '../../Categories/Branch/Service/branch.service';
import { ReportService } from '../service/report.service';
import { Util } from '../../../core/utils/utils-function';


@Component({
  selector: 'report-2',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './report-2.page.html',
  styleUrls: ['./report-2.page.scss'],
})
export class Report2Page {

  data: any[] = [];

  listBranchs: any[] = [];

  // Page
  loading: boolean = false;
  page: number = 0;
  size: number = 10;
  totalRecords: number = 0;
  selectedPageSize: number = 10;
  pageSizeOptions: number[] = [5, 10, 20, 30, 50, 100];
  totalItems = 0;
  currentPage = 0;
  filter: any = {};

  constructor(private navigationService: NavigationService, private branchService: BranchService, private reportService: ReportService, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.branchService.getAll().subscribe(res => {
      this.listBranchs = res;
      this.filter.branchIds = this.listBranchs.map(item => item.id);
      Util.setCurrentMonthRange(this.filter, 'startDate', 'endDate');
      this.loadData()
    })
  }
  
  loadData() {
    this.loading = true;
    this.reportService.getMaintenanceReport(this.filter, this.page, this.size).subscribe({
      next: (res) => {
        this.data = this.formatData(res.content)
        this.totalRecords = res.totalElements;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
      }
    })
  }

  onPageChange(event: any) {
    this.page = event.first / event.rows;  // compute page index
    this.size = event.rows;
    this.loadData();
  }

  search() {
    this.loadData()
  }

  formatData(data: any[]): any[] {
    const map = new Map<string, any[]>();
    data.forEach(item => {
      const key = [
        item.branchName,
        item.deviceCode,
        item.deviceName,
        item.planCode,
        item.dateTest
      ].join('|');
      if (!map.has(key)) {
        map.set(key, []);
      }
      map.get(key)!.push(item);
    });
    const result: any[] = [];
    map.forEach(group => {
      group.forEach((item, index) => {
        item._isFirst = index === 0;
        item._rowspan = index === 0 ? group.length : 0;
        result.push(item);
      });
    });

    return result;
  }

  public onBack(): void {
    this.navigationService.back();
  }
}
