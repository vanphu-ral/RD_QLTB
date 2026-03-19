import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PLANTYPE } from "../../../../../enums/plan-type.enum";
import { KeyMappingService } from "../../../SampleReport/Service/key-mapping.service";
import { PlanResultDetail } from "../../../../../models/PlanManger/plan-result-detail.model";
import { ErrorReportDialog } from "../error-report-dialog/error-report.dialog";
import { SupplyReplacementDialog } from "../supply-replacement-dialog/supply-replacement.dialog";
import { PlanCheck } from "../../../../../models/PlanManger/plan-check.model";
import { PlanResultService } from "../../Service/plan-result.service";
import { SupplyReplacementHistoryService } from "../../Service/supply-replace-history.service";
import { SupplyReplacementHistory } from "../../../../../models/PlanManger/supply-replace-history.model";
import { SupplyDetailService } from "../../../../DeviceManager/Supply/Service/supply-detail.service";
import { DeviceService } from "../../../../DeviceManager/Device/Service/device.service";
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { PlanResultCheckLogService } from "../../Service/plan-result-check-log.service";
import { CheckHistoryDialog } from "../check-history-dialog/check-history.dialog";

@Component({
    selector: 'app-check-device-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './check-device.dialog.html',
    styleUrls: ['./check-device.dialog.scss'],
})
export class CheckDeviceDialog {

    data: any;
    model: PlanCheck = new PlanCheck();
    listCriterial: PlanResultDetail[] = [];
    listInspectionSessions: any[] = Util.listInspectionSession();
    listExaminationTimes: any[] = Util.listExaminationTime();
    listResult: any[] = ["OK", "Đã điều chỉnh", "Có bất thường"];
    listStatus: any[] = [{ label: 'Đã kiểm tra', value: 1 }, { label: 'Chưa kiểm tra', value: 2 }, { label: 'Không kiểm tra', value: 3 }];
    listSupplyReplaceHistory: SupplyReplacementHistory[] = [];
    PLANTYPE = PLANTYPE;

    isCheckAll: boolean = false;

    groupedData: any[] = [];

    isMobile: boolean = false;

    selectedShift: any = null;
    selectedSession: any = null;

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private cdr: ChangeDetectorRef,
        private keyMappingService: KeyMappingService,
        private planResultCheckLogService: PlanResultCheckLogService,
        private dialogService: DialogService,
        private planResultService: PlanResultService,
        private deviceService: DeviceService,
        private supplyReplaceHistoryService: SupplyReplacementHistoryService,
        private supplyDetailService: SupplyDetailService,
        private breakpointObserver: BreakpointObserver
    ) {
        this.data = config.data;
        console.log(this.data);
        this.breakpointObserver.observe([Breakpoints.Handset, Breakpoints.Small])
            .subscribe(result => {
                this.isMobile = result.matches; // True nếu mobile (< ~768px)
                this.cdr.detectChanges(); // Update view nếu cần
            });
    }

    ngOnInit() {
        // 1. Luôn lấy bộ khung đầy đủ từ template gốc trước
        const detail = JSON.parse(this.data.device.detail);
        const fullTemplate = detail.sampleReportKeyMappings.flatMap((x: any) => {
            const times = x.examinationTime 
                ? x.examinationTime.split(',').map((t: string) => t.trim()).filter((t: string) => t !== "") 
                : [null];
            return times.map((time: string | null) => ({
                criticalGroup: x.criterial.criterialGroup.name || null,
                criticalCode: x.criterial?.code || null,
                criticalName: x.criterial?.name || null,
                frequency: x.frequency,
                examinationTimeRequired: time,
                inspectionSession: x.frequency,
                examinationTime: time,
                step: x.step,
                performer: x.performer,
                result: "OK",
                status: 1
            }));
        });

        this.planResultService.getEvaluationByPlanDetailId(this.data.planResult.id).subscribe(res => {
            if (res && !Util.isEmptyArray(res.planResultDetail)) {
                // 2. Nếu đã có dữ liệu từ server (có thể là của ca khác đã lưu)
                const savedDetails = res.planResultDetail;
                this.model = res;
                
                // Merge: Duyệt qua template gốc, nếu tiêu chí nào đã có kết quả lưu thì lấy kết quả đó
                this.model.planResultDetail = fullTemplate.map((templateItem: any) => {
                    // Tìm kiếm dựa trên cả mã tiêu chí VÀ ca yêu cầu (examinationTimeRequired)
                    const savedItem = savedDetails.find((s: any) => 
                        s.criticalCode === templateItem.criticalCode && 
                        (s.examinationTimeRequired === templateItem.examinationTimeRequired || s.examinationTime === templateItem.examinationTimeRequired)
                    );
                    // Nếu có savedItem và có id thì đánh dấu là đã lưu (isCheck = true)
                    return savedItem ? { ...templateItem, ...savedItem, isCheck: !!savedItem.id } : { ...templateItem, isCheck: false };
                });
            } else {
                // 3. Nếu chưa có dữ liệu thì dùng toàn bộ template gốc
                this.model.planResultDetail = fullTemplate.map((x: any) => ({ ...x, isCheck: false }));
            }
            
            this.updateGroupedData();
            this.cdr.detectChanges();
        });
    }

    declareSupplyReplacement() {
        const supplyReplacmentDialog = this.dialogService.open(SupplyReplacementDialog, {
            header: `Khai báo vật tư thay thế`,
            width: '100%',
            modal: true,
            closable: true,
            data: { device: this.data.device , supplyReplacement: this.model.supplyReplacement, historyReplace: this.model.supplyReplacementHistories},
        });
        supplyReplacmentDialog.onClose.subscribe(result => {
            if (result) {
                this.model.supplyReplacement = result.listSupplyReplace;
                this.listSupplyReplaceHistory = result.listSupplyReplaceHistory;
                this.model.deviceCurrentSupplies = result.listCurrentSupply;
                this.cdr.detectChanges();
            }
        });
    }


    declareIssue() {
        const reportDialog = this.dialogService.open(ErrorReportDialog, {
            header: `Khai báo sự cố`,
            width: 'auto',
            modal: true,
            closable: true,
            data: this.model.errorReport,
        });
        reportDialog.onClose.subscribe(result => {
            if (result) {
                this.model.errorReport = result;
            }
        });
    }

    onCheckAllChange(event: any) {
        const valueToSet = event.checked ? 0 : 1;
        if (this.model.planResultDetail) {
            this.model.planResultDetail.forEach((row: any) => {
                row.status = valueToSet;
            });
        }
    }
    onRowCheckChange(row: any) {
        console.log(row);
        if(row.status == 0) {
            row.result = null;
        }
        if (this.model.planResultDetail && this.model.planResultDetail.length > 0) {
            this.isCheckAll = this.model.planResultDetail.every((row: any) => row.status === 0);
        } else {
            this.isCheckAll = false;
        }
    }


    updateGroupedData() {
        if (!this.model.planResultDetail || !this.selectedShift) {
            this.groupedData = [];
            return;
        }
        
        const filteredDetails = this.model.planResultDetail.filter((item: any) => {
            const matchesShift = item.examinationTimeRequired === this.selectedShift;
            const matchesSession = !this.selectedSession || item.inspectionSession === this.selectedSession;
            return matchesShift && matchesSession;
        });
        
        const groups = _.groupBy(filteredDetails, (item) => {
            return `${item.criticalGroup}|${item.step}`;
        });
        this.groupedData = Object.keys(groups).map(key => {
            const firstItem = groups[key][0];
            return {
                groupKey: key,
                groupName: firstItem.criticalGroup,
                step: firstItem.step,              
                items: groups[key]                 
            };
        });
        
    }

    onShiftFilterChange(event: any) {
        this.selectedShift = event.value;
        this.updateGroupedData();
        this.cdr.detectChanges();
    }

    onSessionFilterChange(event: any) {
        this.selectedSession = event.value;
        this.updateGroupedData();
        this.cdr.detectChanges();
    }

    onGroupResultChange(group: any, newValue: any) {
        group.items.forEach((item: any) => {
            if (item.status !== 0) item.result = newValue;
        });
    }

    onGroupStatusChange(group: any, event: any) {
        const status = event.checked ? 0 : 1;
        group.items.forEach((item: any) => {
            item.status = status;
            if (status === 0) {
                item.result = null;
                item.comment = null;  
            }
        });
        this.isCheckAll = this.model.planResultDetail.every((row: any) => row.status === 0);
    }

    onGroupExamTimeChange(group: any, newValue: any) {
        group.items.forEach((item: any) => {
            if (item.status !== 0) {
                item.examinationTime = newValue;
            }
        });
    }
    onGroupSessionChange(group: any, newValue: any) {
        group.items.forEach((item: any) => {
            if (item.status !== 0) {
                item.inspectionSession = newValue;
            }
        });
    }

    viewCheckHistory() {
        const checkHistoryDialog = this.dialogService.open(CheckHistoryDialog, {
            header: `Lịch sử kiểm tra`,
            width: 'auto',
            modal: true,
            closable: true,
            data: this.data.planResult,
        });
    }

    submit() {
        if (!this.selectedShift) {
            Util.toastMessage("Vui lòng chọn ca kiểm tra trước khi lưu", "error");
            return;
        }

        const submitModel = _.cloneDeep(this.model);
        submitModel.planResult = this.data.planResult;
        
        if(Array.isArray(submitModel.planResult.userTest)) {
            submitModel.planResult.userTest = JSON.stringify(submitModel.planResult.userTest);
        }

        // Chỉ gửi đi các tiêu chí đang hiển thị trên màn hình (để tránh lưu rác các tiêu chí đang bị ẩn bởi bộ lọc Thời gian)
        submitModel.planResultDetail = this.model.planResultDetail.filter((item: any) => {
            const matchesShift = item.examinationTimeRequired === this.selectedShift;
            const matchesSession = !this.selectedSession || item.inspectionSession === this.selectedSession;
            return matchesShift && matchesSession;
        });

        // FIX: Đảm bảo không bị mất dấu Ca / Thời gian khi trạng thái là Không KT (status = 0)
        // Nếu không có bước này, mapping data ở lần load sau sẽ thất bại và sinh ra các bản ghi duplicate.
        submitModel.planResultDetail.forEach((x: any) => {
            if (x.status === 0) {
                if (!x.examinationTime) x.examinationTime = x.examinationTimeRequired;
                if (!x.inspectionSession) x.inspectionSession = x.frequency;
            }
        });

        this.listSupplyReplaceHistory = this.listSupplyReplaceHistory.map(x => {
            return {
                ...x,
                planResultId: this.data.planResult.id,
                planId: 1,
                deviceId: this.data.device.device.id
            }
        });
        this.planResultService.saveEvaluation(submitModel).subscribe({
            next: (res) => {
                Util.showSuccessMessage("Lưu kết quả kiểm tra thành công");
                this.ref.close(true);
            }
        });
        const planResultCheckLog = {
            inspection: this.selectedShift,
            content: JSON.stringify(submitModel),
            status: 1,
            planResult: {id: this.data.planResult.id},
        }
        this.planResultCheckLogService.create(planResultCheckLog).subscribe({
            next: (res) => {
                Util.showSuccessMessage("Lưu lịch sử kiểm tra thành công");
            }
        });
        this.supplyReplaceHistoryService.createList(this.listSupplyReplaceHistory).subscribe({
            next: (res) => {
                Util.showSuccessMessage("Lưu lịch sử thay thế vật tư thành công");
            }
        });
        this.listSupplyReplaceHistory.forEach(item => {
            this.supplyDetailService.update(item.oldSupplyDetail.id as number, item.oldSupplyDetail).subscribe();
        })
        if(_.find(this.model.errorReport, x => x.severity === 0)) {
            this.deviceService.updateStatusDevice(this.data.device.device.id, 3).subscribe();
        }
        if(_.find(this.model.errorReport, x => (x.severity === 1) || (x.severity === 2))) {
            this.deviceService.updateStatusDevice(this.data.device.device.id, 2).subscribe();
        }
    }

    close() {
        this.ref.close();
    }
}