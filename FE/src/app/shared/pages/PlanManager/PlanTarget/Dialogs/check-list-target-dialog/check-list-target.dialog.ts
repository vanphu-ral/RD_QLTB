import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanTargetResultService } from "../../Service/plan-target-result.service";
import { PerformPlanDialog } from "../perform-plan-dialog/perform-plan.dialog";
import { ConfirmationService, MessageService } from "primeng/api";

@Component({
    selector: 'app-check-list-target-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './check-list-target.dialog.html',
    styleUrls: ['./check-list-target.dialog.scss'],
})
export class CheckListTargetDialog {

    data: any;
    plan: any;
    checkList: any[] = [];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private planTargetResultService: PlanTargetResultService,
        private dialogService: DialogService,
        private comfirmService: ConfirmationService,
        private messageService: MessageService,
        private cdr: ChangeDetectorRef,
    ) {
        this.data = config.data;
        console.log(this.data);
        
    }

    ngOnInit() {
        this.loadCheckList();
    }

    loadCheckList() {
        this.planTargetResultService.getByPlanTargetId(this.data.id).subscribe(res => {
            this.checkList = res;
            this.cdr.detectChanges();
        });
    }

    statusToString(status: number) {
        return Util.statusToString(status);
    }

    getSeverity(status: number): string {
        return Util.statusToSeverity(status);
    }

    saveCheckDate(row: any) {
        if (!row.executionTime) {
            this.messageService.add({ severity: 'error', summary: 'Lỗi', detail: 'Vui lòng chọn tháng kiểm tra' });
            return;
        }
        const currentDate = new Date(row.executionTime);
        const currentMonth = currentDate.getMonth();
        const currentYear = currentDate.getFullYear();
        const isDuplicate = this.checkList.some((item, index) => {
            if (item === row) return false;

            if (item.executionTime) {
                const date = new Date(item.executionTime);
                return date.getMonth() === currentMonth && date.getFullYear() === currentYear;
            }
            return false;
        });

        if (isDuplicate) {
            this.messageService.add({
                severity: 'warn',
                summary: 'Cảnh báo',
                detail: `Tháng ${currentMonth + 1}/${currentYear} đã tồn tại trong danh sách!`
            });
            return; 
        }
        row.planTargetDevice = { id: this.data.id };
        if (Util.isEmpty(row.id)) {
            this.planTargetResultService.create(row).subscribe((res) => {
                Object.assign(row, res);
                this.loadCheckList();
            });
        } else {
            this.planTargetResultService.update(row.id, row).subscribe((res) => {
                Object.assign(row, res);
                this.loadCheckList();
            });
        }
    }

    view(data: any) {
        const ref = this.dialogService.open(PerformPlanDialog, {
            header: 'Thực hiện kế hoạch',
            width: '100%',
            modal: true,
            data: { data: data, plan: this.data, IsView: true },
            closable: true,
        });
    }

    performPlan(data: any) {
        const ref = this.dialogService.open(PerformPlanDialog, {
            header: 'Thực hiện kế hoạch',
            width: '100%',
            modal: true,
            data: {data: data, plan: this.data},
            closable: true,
        });
        ref.onClose.subscribe((result) => {
            if (result) {
                this.loadCheckList();
            }
        });
    }

    completeCheckDate(row: any, event: any) {
        Util.confirmAndExecute(
            event,
            'Bạn có chắc đã hoàn thành đợt kiểm tra này này?',
            () => {
                row.status = 5;
                return this.planTargetResultService.update(row.id as number, row);
            },
            'Đã hoàn thành đợt kiểm tra',
            'Lỗi khi hoàn thành',
            this.comfirmService,
            this.messageService,
            () => this.loadCheckList()
        )
    }



    addNewRow() {
        this.checkList.push({ dateTest: new Date(), status: 1 });
    }

    deleteRow(index: number) {
        if (this.checkList[index].id) {
            this.planTargetResultService.delete(this.checkList[index].id).subscribe(() => { this.loadCheckList(); });
        } else {
            this.checkList.splice(index, 1);
        }
    }


    close() {
        this.ref.close();
    }
}