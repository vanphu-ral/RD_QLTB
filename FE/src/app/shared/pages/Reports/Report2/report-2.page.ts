import { Component } from '@angular/core';
import { SharedModule } from '../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { NavigationService } from '../../../service/navigation.service';
import { BranchService } from '../../Categories/Branch/Service/branch.service';
import { ReportService } from '../service/report.service';


@Component({
  selector: 'report-2',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './report-2.page.html',
  styleUrls: ['./report-2.page.scss'],
})
export class Report2Page {

  filter: any = {};

  listBranchs: any[] = [];

  data: any[] = [];
  loading: boolean = false

  constructor(private navigationService: NavigationService, private branchService: BranchService, private reportService: ReportService) { }

  ngOnInit(): void {
    this.prepareData()
    this.loadData()
  }

  prepareData() {
    this.branchService.getAll().subscribe(res => {
      this.listBranchs = res
    })
  }

  loadData() {
    this.reportService.getMaintenanceReport(this.filter, 0, 10).subscribe(res => {
      console.log(res);

      // this.data = res
    })
  }

  search() {
    this.loadData()
  }

  public onBack(): void {
    this.navigationService.back();
  }
}
