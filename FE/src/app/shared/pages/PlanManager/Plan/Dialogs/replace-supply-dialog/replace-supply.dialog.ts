import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { SupplyDetailService } from "../../../../DeviceManager/Supply/Service/supply-detail.service";
import { SupplyService } from "../../../../DeviceManager/Supply/Service/supply.service";
import { SupplyReplacementHistory } from "../../../../../models/PlanManger/supply-replace-history.model";

@Component({
    selector: 'app-replace-supply-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './replace-supply.dialog.html',
    styleUrls: ['./replace-supply.dialog.scss'],
})
export class ReplaceSupplyDialog {
    data: any;
    model: any = {};
    listSupplys: any[] = [];
    serialOptions: any[] = [];
    listOptionsReplace: any[] = [{ label: 'Thay thế', value: 1 }, { label: 'Hỏng', value: 2 }];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private dialogService: DialogService,
        private supplyDetailService: SupplyDetailService,
        private supplyService: SupplyService,
        private cdr: ChangeDetectorRef
    ) {
        this.data = config.data;
    }

    ngOnInit() {
        this.loadData();
    }

    loadData() {
        this.supplyService.getAll().subscribe({
            next: (res) => {
                this.listSupplys = res;
                this.cdr.detectChanges();
            }
        });
    }

    onChangeSupply(event: any) {
        const supplyId = event.id;
        this.supplyDetailService.getBySupplyId(supplyId).subscribe({
            next: (res) => {
                this.serialOptions = res;
                this.cdr.detectChanges();
            }
        });
    }

    submit() {
        this.model.status = 1;
        this.ref.close(this.model);
    }

    close() {
        this.ref.close();
    }
}