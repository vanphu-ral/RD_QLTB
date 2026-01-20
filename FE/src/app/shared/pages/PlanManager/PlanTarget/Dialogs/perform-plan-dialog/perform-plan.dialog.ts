import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _, { result } from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanTargetResultService } from "../../Service/plan-target-result.service";
import { PlanTargetResult } from "../../../../../models/PlanTarget/plan-target-result.model";

@Component({
    selector: 'app-perform-plan-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './perform-plan.dialog.html',
    styleUrls: ['./perform-plan.dialog.scss'],
})
export class PerformPlanDialog {

    data: PlanTargetResult = new PlanTargetResult();
    plan: any;

    isViewMode: boolean = false;

    listEvaluate: any[] = [{ value: 0, name: 'Không đánh giá' }, { value: 1, name: 'Đạt' }, { value: 2, name: 'Không đạt' }];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private dialogService: DialogService,
        private planTargetResultService: PlanTargetResultService,
        private cdr: ChangeDetectorRef,
    ) {
        this.data = config.data.data;
        this.plan = config.data.plan;
        this.isViewMode = config.data.IsView || false;
    }

    ngOnInit() {
        if(Util.isEmptyString(this.data.result)){
            this.data.result = JSON.parse(this.plan.listItems).map((item: any) => {
                return {
                    code: item.code,
                    measurement: item.measurement,
                    target: item.target,
                    result: '',
                    evaluate: 0,
                    nextTarget: 'Duy trì/Dừng',
                    note: '',
                };
            });
            this.cdr.detectChanges();
            console.log(this.data.result);
            
        }else {
            this.data.result = JSON.parse(this.data.result);
        }
    }

    evaluateToString(evaluate: number) {
        const evalItem = this.listEvaluate.find(item => item.value === evaluate);
        return evalItem ? evalItem.name : 'Không đánh giá';
    }

    
    
    deleteRow(index: number) {
        this.data.result!.splice(index, 1);
    }
    
    submit() {
        const payload = {
            ...this.data,
            result: JSON.stringify(this.data.result),
            status: 4
        };
        if(this.data.id){
            this.planTargetResultService.update(this.data.id, payload).subscribe((res: any) => { this.ref.close(true); });
        }
    }

    close() {
        this.ref.close();
    }
}