import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanResult } from "../../../../../models/PlanManger/plan-result.model";
import { SampleReportService } from "../../../SampleReport/Service/sample-report.service";
import { KeyMappingService } from "../../../SampleReport/Service/key-mapping.service";
import { PlanResultDetail } from "../../../../../models/PlanManger/plan-result-detail.model";
import { ErrorReport } from "../../../../../models/PlanManger/error-report.model";
import { AccountService } from "../../../../../core/auth/account/account.service";

@Component({
    selector: 'app-error-report-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './error-report.dialog.html',
    styleUrls: ['./error-report.dialog.scss'],
})
export class ErrorReportDialog {

    data: ErrorReport = new ErrorReport();
    listSeverity: any[] = [
        { label: 'Nghiêm trọng', value: 0 },
        { label: 'Bất thường', value: 1 },
        { label: 'Nhẹ', value: 2 },
        { label: 'Dừng máy đổi sản phẩm', value: 3 }
    ];
    listError: ErrorReport[] = []
    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private accountService: AccountService,
    ) {
        if (Array.isArray(config.data)) {
            this.listError = config.data;
        } else if (config.data) {
            this.listError = config.data.listError || [];
            if (config.data.initialData) {
                Object.assign(this.data, config.data.initialData);
            }
        }
    }

    ngOnInit() {
        this.data.timeReported = new Date();
    }

    sevirityStatus(status: number) {
        return Util.severityErrorToString(status);
    }

    editRow(index: number) {
        this.data = this.listError[index];
        this.listError.splice(index, 1);
    }

    deleteRow(index: number) {
        this.listError.splice(index, 1);
    }

    submit() {
        if (!this.data.name || this.data.name.trim() === '') {
            Util.toastMessage('Vui lòng nhập tên lỗi / lý do dừng máy', 'error');
            return;
        }

        if (this.data.severity === undefined || this.data.severity === null) {
            Util.toastMessage('Vui lòng chọn loại sự cố / lý do dừng', 'error');
            return;
        }

        if (this.data.severity === 3) {
            if (this.data.timeRepaired) {
                if (new Date(this.data.timeRepaired) < new Date(this.data.timeReported!)) {
                    Util.toastMessage('Thời gian bắt đầu chạy lại không được nhỏ hơn thời gian bắt đầu dừng máy', 'error');
                    return;
                }
                this.data.isRepaired = true;
                this.data.repairedBy = this.accountService.getUser()?.fullName || '';
            } else {
                this.data.isRepaired = false;
            }
        } else {
            this.data.isRepaired = false;
        }

        this.data.reportedBy = this.accountService.getUser()?.fullName || '';
        this.listError.push(this.data);
        this.ref.close(this.listError);
    }

    close() {
        this.ref.close();
    }
}
