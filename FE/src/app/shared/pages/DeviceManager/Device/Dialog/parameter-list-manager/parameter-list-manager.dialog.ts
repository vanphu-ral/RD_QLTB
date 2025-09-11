import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { SharedModule } from '../../../../../../share.module';
import { Util } from '../../../../../core/utils/utils-function';

@Component({
    selector: 'app-parameter-list-manager-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './parameter-list-manager.dialog.html',
    styleUrls: ['./parameter-list-manager.dialog.scss'],
})
export class ParameterListManagerDialogComponent {
    data: any;
    listParameter: any[] = [];
    listStatus = [
        { label: 'Tốt', value: 'Tốt' },
        { label: 'Hỏng', value: 'Hỏng' },
        { label: 'Đang sửa', value: 'Đang sửa' },
        { label: 'Đã thanh lý', value: 'Đã thanh lý' },
    ];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private cdr: ChangeDetectorRef
    ) {
        this.data = config.data;
    }

    ngOnInit() {

    }

    addNewRow() {
        this.listParameter.push({status: 1});
    }

    deleteRow(index: number) {
        if (this.listParameter[index].id) {
            // this.supplyDetailService.delete(this.listParameter[index].id as number).subscribe({
            //     next: () => {
            //         this.listParameter.splice(index, 1);
            //         this.loadData();
            //     }
            // });
        } else {
            this.listParameter.splice(index, 1);
        }
    }
   

    close() {
        this.ref.close();
    }

    submit() {
        this.ref.close({ item: this.data });
    }

}
