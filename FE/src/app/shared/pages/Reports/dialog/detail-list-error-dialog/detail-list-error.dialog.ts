import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import { SharedModule } from "../../../../../share.module";

@Component({
    selector: 'app-detail-list-error-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './detail-list-error.dialog.html',
    styleUrls: ['./detail-list-error.dialog.scss'],
})
export class DetailListErrorDialog {

    data: any;

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private dialogService: DialogService,
        private cdr: ChangeDetectorRef,
    ) {
        this.data = config.data;
        console.log(this.data);
        
    }

    sevirityStatus(status: number) {
        switch (status) {
            case 0:
                return 'Nghiêm trọng';
            case 1:
                return 'Bất thường';
            case 2:
                return 'Nhẹ';
            default:
                return '';
        }
    }
    
    close() {
        this.ref.close();
    }
}