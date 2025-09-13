import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { SharedModule } from '../../../../../../share.module';
import { Util } from '../../../../../core/utils/utils-function';
import { DeviceParameterUse } from '../../../../../models/DeviceManager/device-parameter-use.model';
import { ParameterService } from '../../../Parameter/Service/parameter.service';
import { DeviceParameterUseService } from '../../Service/device-parameter-use.service';
import _ from 'lodash';

@Component({
    selector: 'app-parameter-list-manager-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './parameter-list-manager.dialog.html',
    styleUrls: ['./parameter-list-manager.dialog.scss'],
})
export class ParameterListManagerDialogComponent {
    data: any;
    listParameterUse: DeviceParameterUse[] = [];
    listParameters: any[] = []

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private cdr: ChangeDetectorRef,
        private parameterService: ParameterService,
        private deviceParameterUseService: DeviceParameterUseService
    ) {
        this.data = config.data;
    }

    ngOnInit() {
        this.loadData();
    }

    loadData() {
        this.deviceParameterUseService.getBySupplyId(_.get(this.data, 'id')).subscribe({
            next: (res) => {
                if (Util.isEmptyArray(res)) {
                    this.listParameterUse.push({ status: 1 })
                } else {
                    this.listParameterUse = res
                    this.cdr.detectChanges();
                }
            }
        })

        this.parameterService.getAll().subscribe(res => {
            this.listParameters = res
            this.cdr.detectChanges();
        })
    }

    addNewRow() {
        this.listParameterUse.push({status: 1});
    }

    deleteRow(index: number) {
        if (this.listParameterUse[index].id) {
            this.deviceParameterUseService.delete(this.listParameterUse[index].id as number).subscribe({
                next: () => {
                    this.listParameterUse.splice(index, 1);
                    this.loadData();
                }
            });
        } else {
            this.listParameterUse.splice(index, 1);
        }
    }
   

    close() {
        this.ref.close();
    }

    submit() {
        this.ref.close(this.listParameterUse);
    }

}
