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
    selector: 'app-list-history-move-device-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './list-history-move-device.dialog.html',
    styleUrls: ['./list-history-move-device.dialog.scss'],
})
export class ListHistoryMoveDeviceDialog {

    data: any;
    listHistory: any[] = [];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private deviceRelocationHistoryService: DeviceRelocationHistoryService,
        private cdr: ChangeDetectorRef
    ) {
        this.data = config.data;
    }

    ngOnInit() {
        this.deviceRelocationHistoryService.getHistoryMoveByDeviceId(this.data.id).subscribe(res => {
            this.listHistory = res;
            this.cdr.detectChanges();
        });
    }

    

    close() {
        this.ref.close();
    }

    submit() {
       this.ref.close(true);
    }
}