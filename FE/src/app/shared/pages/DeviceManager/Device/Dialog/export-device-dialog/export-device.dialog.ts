import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DynamicDialogRef, DynamicDialogConfig } from 'primeng/dynamicdialog';
import { SharedModule } from '../../../../../../share.module';
import { BranchService } from '../../../../Categories/Branch/Service/branch.service';
import { TeamService } from '../../../../Categories/Team/Service/team.service';
import { LineService } from '../../../../Categories/Line/Service/line.service';
import { DeviceGroupService } from '../../../DeviceGroup/Service/device-group.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-export-device-dialog',
  standalone: true,
  imports: [CommonModule, SharedModule],
  templateUrl: './export-device.dialog.html',
  styleUrls: ['./export-device.dialog.scss']
})
export class ExportDeviceDialog implements OnInit {
  filter: any = {
    'branch.name': null,
    'team.name': null,
    'line.name': null,
    'group.name': null
  };

  allBranches: any[] = [];
  allTeams: any[] = [];
  allLines: any[] = [];
  allGroups: any[] = [];

  teamOptions: any[] = [];
  lineOptions: any[] = [];

  constructor(
    public ref: DynamicDialogRef,
    public config: DynamicDialogConfig,
    private branchService: BranchService,
    private teamService: TeamService,
    private lineService: LineService,
    private deviceGroupService: DeviceGroupService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    forkJoin({
      branch: this.branchService.getAll(),
      team: this.teamService.getAll(),
      line: this.lineService.getAll(),
      group: this.deviceGroupService.getAll()
    }).subscribe(({ branch, team, line, group }) => {
      this.allBranches = branch;
      this.allTeams = team;
      this.allLines = line;
      this.allGroups = group;
      
      this.updateDropdowns();
      this.cdr.detectChanges();
    });
  }

  onBranchChange() {
    this.filter['team.name'] = null;
    this.filter['line.name'] = null;
    this.updateDropdowns();
  }

  onTeamChange() {
    this.filter['line.name'] = null;
    this.updateDropdowns();
  }

  updateDropdowns() {
    if (this.filter['branch.name']) {
      this.teamOptions = this.allTeams.filter(t => t.branch?.name === this.filter['branch.name']);
    } else {
      this.teamOptions = [...this.allTeams];
    }

    if (this.filter['team.name']) {
      this.lineOptions = this.allLines.filter(l => l.team?.name === this.filter['team.name']);
    } else if (this.filter['branch.name']) {
      const teamNames = this.teamOptions.map(t => t.name);
      this.lineOptions = this.allLines.filter(l => teamNames.includes(l.team?.name));
    } else {
      this.lineOptions = [...this.allLines];
    }
  }

  onSubmit() {
    this.ref.close(this.filter);
  }

  onCancel() {
    this.ref.close(null);
  }
}
