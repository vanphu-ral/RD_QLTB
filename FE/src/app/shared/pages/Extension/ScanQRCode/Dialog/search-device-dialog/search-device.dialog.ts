import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import { BranchService } from "../../../../Categories/Branch/Service/branch.service";
import { TeamService } from "../../../../Categories/Team/Service/team.service";
import { LineService } from "../../../../Categories/Line/Service/line.service";
import { DeviceService } from "../../../../DeviceManager/Device/Service/device.service";
import { forkJoin } from "rxjs";

@Component({
    selector: 'app-search-device-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './search-device.dialog.html',
    styleUrls: ['./search-device.dialog.scss'],
})
export class SearchDeviceDialog {

    listBranch: any[] = [];
    listTeam: any[] = [];
    listLine: any[] = [];

    filteredTeams: any[] = [];
    filteredLines: any[] = [];

    selectedBranch: any = null;
    selectedTeam: any = null;
    selectedLine: any = null;
    deviceName: string = '';

    listDevice: any[] = [];
    selectedDevice: any = null;

    totalRecords: number = 0;
    page: number = 0;
    size: number = 10;

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private cdr: ChangeDetectorRef,
        private branchService: BranchService,
        private teamService: TeamService,
        private lineService: LineService,
        private deviceService: DeviceService,
    ) {}

    ngOnInit() {
        forkJoin({
            branches: this.branchService.getAll(),
            teams: this.teamService.getAll(),
            lines: this.lineService.getAll(),
        }).subscribe(({ branches, teams, lines }) => {
            this.listBranch = branches;
            this.listTeam = teams;
            this.listLine = lines;
            this.filteredTeams = [...teams];
            this.filteredLines = [...lines];
            this.cdr.detectChanges();
        });
    }

    onBranchChange(event: any) {
        this.selectedTeam = null;
        this.selectedLine = null;
        if (event.value) {
            this.filteredTeams = this.listTeam.filter((t: any) => t.branch?.id === event.value);
        } else {
            this.filteredTeams = [...this.listTeam];
        }
        this.filteredLines = this.selectedTeam
            ? this.listLine.filter((l: any) => l.team?.id === this.selectedTeam)
            : [...this.listLine];
        this.cdr.detectChanges();
        this.search();
    }

    onTeamChange(event: any) {
        this.selectedLine = null;
        if (event.value) {
            this.filteredLines = this.listLine.filter((l: any) => l.team?.id === event.value);
        } else {
            this.filteredLines = this.selectedBranch
                ? this.listLine.filter((l: any) => {
                    const team = this.listTeam.find((t: any) => t.id === l.team?.id);
                    return team?.branch?.id === this.selectedBranch;
                })
                : [...this.listLine];
        }
        this.cdr.detectChanges();
        this.search();
    }

    onLineChange(event: any) {
        this.search();
    }

    search() {
        const filters: any = {};
        if (this.deviceName && this.deviceName.trim() !== '') {
            filters['name'] = this.deviceName.trim();
        }
        if (this.selectedBranch) {
            const branch = this.listBranch.find((b: any) => b.id === this.selectedBranch);
            if (branch) filters['branch.name'] = branch.name;
        }
        if (this.selectedTeam) {
            const team = this.listTeam.find((t: any) => t.id === this.selectedTeam);
            if (team) filters['team.name'] = team.name;
        }
        if (this.selectedLine) {
            const line = this.listLine.find((l: any) => l.id === this.selectedLine);
            if (line) filters['line.name'] = line.name;
        }

        this.deviceService.getAllByPaged(filters, this.page).subscribe((res: any) => {
            this.listDevice = res.content || [];
            this.totalRecords = res.totalElements;
            this.cdr.detectChanges();
        });
    }

    onPageChange(event: any) {
        this.page = event.first / event.rows;
        this.size = event.rows;
        this.search();
    }

    selectDevice(device: any) {
        this.ref.close(device);
    }

    close() {
        this.ref.close();
    }
}
