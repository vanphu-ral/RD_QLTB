import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanResult } from "../../../../../models/PlanManger/plan-result.model";
import { CheckDeviceDialog } from "../check-device-dialog/check-device.dialog";
import { PlanResultService } from "../../Service/plan-result.service";
import { ErrorReportService } from "../../Service/error-report.service";
import { RepairErrorDialog } from "../repair-error-dialog/repair-error.dialog";
import { ErrorReport } from "../../../../../models/PlanManger/error-report.model";
import { ConfirmationService, MessageService } from "primeng/api";
import { ErrorReportSeriousDialog } from "../error-report-serious-dialog/error-report-serious.dialog";

@Component({
    selector: 'app-list-error-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './list-error.dialog.html',
    styleUrls: ['./list-error.dialog.scss'],
})
export class ListErrorDialog {

    data: any;
    listError: ErrorReport[] = [];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private dialogService: DialogService,
        private cdr: ChangeDetectorRef,
        private errorReportService: ErrorReportService,
        private comfirmService: ConfirmationService,
        private messageService: MessageService
    ) {
        this.data = config.data;
    }

    ngOnInit() {
        this.loadErrorList();

    }

    loadErrorList() {
        this.errorReportService.getAllErrorByPlanDetailId(this.data.id).subscribe((data) => {
            this.listError = data;
            this.cdr.detectChanges();
        });
    }

    getSeverity(status: number): any {
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

    deleteRow(index: number) {
        this.listError.splice(index, 1);
    }

    confirmError(event: any, row: ErrorReport) {
        Util.confirmAndExecute(
            event,
            'Bạn có chắc đã hoàn thành sửa chữa lỗi này?',
            () => {
                row.isRepaired = true;
                return this.errorReportService.update(row.id as number, row)
            },
            'Đã hoàn thành sửa chữa',
            'Lỗi khi hoàn thành',
            this.comfirmService,
            this.messageService,
            () => this.loadErrorList()
        )
    }

    repairError(row: any) {
        const ref = this.dialogService.open(RepairErrorDialog, {
            header: `Sửa lỗi - ${row.name} - ${row.severity ? 'Nghiêm trọng' : row.severity === 1 ? 'Bất thường' : 'Nhẹ'} - ${row.timeReported}`,
            width: '40%',
            data: row,
            closable: true
        });
    }


    acceptanceError(row: any) {
        const ref = this.dialogService.open(ErrorReportSeriousDialog, {
            header: `Tiếp nhận lỗi nghiêm trọng - ${row.name}`,
            width: '50%',
            data: row,
            closable: true
        });
    }


    close() {
        this.ref.close();
    }
}