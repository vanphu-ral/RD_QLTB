import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { SupplyDetailService } from "../../../../DeviceManager/Supply/Service/supply-detail.service";
import { SupplyService } from "../../../../DeviceManager/Supply/Service/supply.service";
import { SupplyReplacementHistory } from "../../../../../models/PlanManger/supply-replace-history.model";
import { SupplyReplacementHistoryService } from "../../Service/supply-replace-history.service";

@Component({
    selector: 'app-supply-replace-history-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './supply-replace-history.dialog.html',
    styleUrls: ['./supply-replace-history.dialog.scss'],
})
export class SupplyReplaceHistoryDialog {


    data: any;
    historieSupplies: any[] = [];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private supplyReplaceHistoryService: SupplyReplacementHistoryService,
        private cdr: ChangeDetectorRef
    ) {
        this.historieSupplies = config.data;
        console.log(this.historieSupplies);

    }

    close() {
        this.ref.close();
    }
}