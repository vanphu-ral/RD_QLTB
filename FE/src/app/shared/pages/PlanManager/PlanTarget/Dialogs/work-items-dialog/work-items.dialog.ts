import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { AccountService } from "../../../../../core/auth/account/account.service";
import { ListItemPlanTarget } from "../../../../../models/PlanTarget/Snapshot/list-item-plan-target.model";
import { Util } from "../../../../../core/utils/utils-function";

@Component({
    selector: 'app-work-items-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './work-items.dialog.html',
    styleUrls: ['./work-items.dialog.scss'],
})
export class WorkItemsDialog {

    data: ListItemPlanTarget = new ListItemPlanTarget();
    model: any = {};
    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private accountService: AccountService,
    ) {
        this.data = config.data;
        console.log(this.data);
        
    }

    ngOnInit() {
        if(this.data.target) {
            this.data.duaration = this.data.duaration.split(',');
        }
    }


    submit() {
        this.data.duaration = Util.arrayToString(this.data.duaration);
        this.ref.close(this.data);
    }

    close() {
        this.ref.close();
    }
}