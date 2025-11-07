import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { SharedModule } from '../../../../../../share.module';
import { Util } from '../../../../../core/utils/utils-function';
import { SupplyService } from '../../../Supply/Service/supply.service';
import { SupplyDetailService } from '../../../Supply/Service/supply-detail.service';
import { DeviceSupplyUseService } from '../../Service/device-supply-use.service';
import _ from 'lodash';
import { BranchService } from '../../../../Categories/Branch/Service/branch.service';
import { LineService } from '../../../../Categories/Line/Service/line.service';
import { TeamService } from '../../../../Categories/Team/Service/team.service';
import { DeviceRelocationHistory } from '../../../../../models/DeviceManager/device-relocation-history.model';
import { DeviceRelocationHistoryService } from '../../Service/device-relocation-histories.service';
import { DeviceService } from '../../Service/device.service';


@Component({
    selector: 'app-move-device-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './move-device.dialog.html',
    styleUrls: ['./move-device.dialog.scss'],
})
export class MoveDeviceDialog {

    data: any;
    model: DeviceRelocationHistory = new DeviceRelocationHistory();

    listBranches: any[] = [];
    listLines: any[] = [];
    listTeams: any[] = [];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private branchService: BranchService,
        private lineService: LineService,
        private teamService: TeamService,
        private deviceService: DeviceService,
        private deviceRelocationHistoryService: DeviceRelocationHistoryService,
        private cdr: ChangeDetectorRef
    ) {
        this.data = config.data;
    }

    ngOnInit() {
        this.deviceService.getById(this.data.id).subscribe(device => {
            this.data = device;
            this.cdr.detectChanges();
        });
        this.branchService.getAll().subscribe(branches => {
            this.listBranches = branches;
            this.cdr.detectChanges();
        });
        this.lineService.getAll().subscribe(lines => {
            this.listLines = lines;
            this.cdr.detectChanges();
        });
        this.teamService.getAll().subscribe(teams => {
            this.listTeams = teams;
            this.cdr.detectChanges();
        });
    }

    

    close() {
        this.ref.close();
    }

    submit() {
        this.model.device = { id: this.data.id };
        this.model.oldBranchId = this.data.branch.id;
        this.model.oldLineId = this.data.line.id;
        this.model.oldTeamId = this.data.team.id;
        this.model.movedAt = new Date();
        this.model.movedBy = Util.getUserNameLogin();
        this.data.branch = { id: this.model.newBranchId };
        this.data.line = { id: this.model.newLineId };
        this.data.team = { id: this.model.newTeamId };
        this.deviceService.update(this.data.id, this.data).subscribe(() => {
            this.deviceRelocationHistoryService.create(this.model).subscribe(() => { this.ref.close(true);});
        });
    }
}