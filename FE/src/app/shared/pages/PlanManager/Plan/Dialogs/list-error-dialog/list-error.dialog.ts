import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { switchMap } from "rxjs";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanResult } from "../../../../../models/PlanManger/plan-result.model";
import { CheckDeviceDialog } from "../check-device-dialog/check-device.dialog";
import { PlanResultService } from "../../Service/plan-result.service";
import { ErrorReportService } from "../../Service/error-report.service";
import { RepairErrorDialog } from "../repair-error-dialog/repair-error.dialog";
import { ErrorReport } from "../../../../../models/PlanManger/error-report.model";
import { ConfirmationService, MessageService } from "primeng/api";
import { ErrorReportSeriousDialog } from "../../../../Records/ReportDeviceIncident/Dialogs/error-report-serious-dialog/error-report-serious.dialog";
import { Router } from "@angular/router";
import { DeviceService } from "../../../../DeviceManager/Device/Service/device.service";
import { ReportDeviceIncidentService } from "../../../../Records/ReportDeviceIncident/service/report-device-incident.service";

@Component({
    selector: 'app-list-error-dialog',
    standalone: true,
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
        private messageService: MessageService,
        private router: Router,
        private deviceService: DeviceService,
        private reportDeviceIncidentService: ReportDeviceIncidentService
    ) {
        this.data = config.data;
        console.log(this.data);
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
                return this.errorReportService.update(row.id as number, row).pipe(
                    switchMap(() => this.deviceService.updateStatusDevice(this.data.device.id, 1))
                );
            },
            'Đã hoàn thành sửa chữa',
            'Lỗi khi hoàn thành',
            this.comfirmService,
            this.messageService,
            () => this.loadErrorList()
        );
    }

    repairError(row: any) {
        const ref = this.dialogService.open(RepairErrorDialog, {
            header: `Sửa lỗi - ${row.name} - ${row.severity ? 'Nghiêm trọng' : row.severity === 1 ? 'Bất thường' : 'Nhẹ'} - ${row.timeReported}`,
            width: '40%',
            data: row,
            modal: true,
            closable: true
        });
    }

    acceptanceError(row: any) {
        this.reportDeviceIncidentService.checkExistByErrorReportId(row.id).subscribe((res: any) => {
            if (res && res.exists === 1) {
                Util.ConfirmMessage('Biên bản sự cố đã tồn tại cho sự cố này! Để chỉnh sửa, vui lòng vào Quản lý biên bản -> Biên bản sự cố thiết bị.', 'error');
                return;
            }
            this.ref.close();
            this.router.navigate(
                ['/ReportDeviceIncident/add'],
                {
                    state: {
                        error: row,
                        planDetail: this.data,
                        IsAddMode: true
                    }
                }
            );
        });
    }

    close() {
        this.ref.close();
    }
}
