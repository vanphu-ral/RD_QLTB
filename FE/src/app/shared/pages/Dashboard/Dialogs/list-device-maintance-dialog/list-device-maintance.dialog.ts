import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { SharedModule } from "../../../../../share.module";

@Component({
    standalone: true,
    selector: 'app-list-device-maintance-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './list-device-maintance.dialog.html',
    styleUrls: ['./list-device-maintance.dialog.scss'],
})
export class ListDeviceMaintanceDialog {

    data: any;
    listDeviceMaintanceDue: any[] = [];
    ListDeviceMaintanced: any[] = []

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private cdr: ChangeDetectorRef
    ) {
        this.data = config.data;
        console.log(this.data);
        
    }

    ngOnInit() {
        
    }

    close() {
        this.ref.close();
    }

    submit() {
        this.ref.close(true);
    }
}