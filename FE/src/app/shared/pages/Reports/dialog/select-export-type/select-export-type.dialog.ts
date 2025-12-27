import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import _ from 'lodash';
import { SharedModule } from '../../../../../share.module';


@Component({
    selector: 'app-select-export-type-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './select-export-type.dialog.html',
    styleUrls: ['./select-export-type.dialog.scss'],
})
export class ExportTypeDialog {

    data: any = {};
    listTypeExports: any[] = [{ name: 'XLSX', value: 1 }, { name: 'PDF', value: 2 }];
    type: any = 1;

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
    ) {
        this.data = config.data;
    }

    close() {
        this.ref.close();
    }

    submit() {
      this.ref.close(this.type);
    }
}