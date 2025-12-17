import { Component } from '@angular/core';
import { SharedModule } from '../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { NavigationService } from '../../../service/navigation.service';
import { ReportService } from '../service/report.service';
import { BranchService } from '../../Categories/Branch/Service/branch.service';
import { TeamService } from '../../Categories/Team/Service/team.service';


@Component({
  selector: 'report-1',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './report-1.page.html',
  styleUrls: ['./report-1.page.scss'],
})
export class Report1Page {

  filter: any = {}

  listBranchs: any[] = []
  listTeams: any[] = []
  data: any[] = []
  
  loading: boolean = false

  constructor(private navigationService: NavigationService,private branchService: BranchService,private teamService: TeamService ,private reportService: ReportService) { }

  ngOnInit(): void {
    this.prepareData()
    this.loadData()
  }

  prepareData() {
    this.branchService.getAll().subscribe(res => {
      this.listBranchs = res
    })
    this.teamService.getAll().subscribe(res => {
      this.listTeams = res
    })
  }

  loadData(){
    this.reportService.getReports(this.filter).subscribe(res => {
      console.log(res);
      
      // this.data = res
    })
  }

  search(){
    this.loadData()
  }

  public onBack(): void {
    this.navigationService.back();
  }
}
