import { ChangeDetectorRef, Component } from '@angular/core';
import { SharedModule } from '../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { NavigationService } from '../../../service/navigation.service';
import { ReportService } from '../service/report.service';
import { BranchService } from '../../Categories/Branch/Service/branch.service';
import { TeamService } from '../../Categories/Team/Service/team.service';
import { Util } from '../../../core/utils/utils-function';
import { forkJoin, map, Observable, tap } from 'rxjs';


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

  constructor(private navigationService: NavigationService, private branchService: BranchService, private teamService: TeamService, private reportService: ReportService, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    Util.setCurrentMonthRange(this.filter, 'startDate', 'endDate');
    this.prepareData().subscribe(() => {
      this.loadData();
    });
  }

  prepareData(): Observable<void> {
    return forkJoin({
      branches: this.branchService.getAll(),
      teams: this.teamService.getAll()
    }).pipe(
      tap(({ branches, teams }) => {
        this.listBranchs = branches;
        this.listTeams = teams;

        this.filter.branchIds = branches.map(item => item.id);
        this.filter.groupIds = teams.map(item => item.id);

        this.cdr.detectChanges();
      }),
      map(() => void 0)
    );
  }

  loadData() {
    this.reportService.getReports(this.filter).subscribe(res => {
      console.log(res);
      this.data = res
      this.cdr.detectChanges();
    })
  }

  search() {
    this.loadData()
  }

  public onBack(): void {
    this.navigationService.back();
  }
}
