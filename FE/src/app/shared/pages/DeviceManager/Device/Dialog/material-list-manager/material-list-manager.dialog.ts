import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { SharedModule } from '../../../../../../share.module';
import { Util } from '../../../../../core/utils/utils-function';

@Component({
    selector: 'app-material-list-manager-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './material-list-manager.dialog.html',
    styleUrls: ['./material-list-manager.dialog.scss'],
})
export class MaterialListManagerDialogComponent {
    data: any;
    listMaterial: any[] = [];
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
        this.listMaterial.push({status: 1});
    }

    deleteRow(index: number) {
        if (this.listMaterial[index].id) {
            // this.supplyDetailService.delete(this.listMaterial[index].id as number).subscribe({
            //     next: () => {
            //         this.listMaterial.splice(index, 1);
            //         this.loadData();
            //     }
            // });
        } else {
            this.listMaterial.splice(index, 1);
        }
    }
   

    close() {
        this.ref.close();
    }

    submit() {
        this.ref.close({ item: this.data });
    }

}
