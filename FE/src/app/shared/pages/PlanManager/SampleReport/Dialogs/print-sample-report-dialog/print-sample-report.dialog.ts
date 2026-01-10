import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _, { sample } from "lodash";
import { DeviceService } from "../../../../DeviceManager/Device/Service/device.service";
import { PlanService } from "../../../Plan/Service/plan.service";
import { forkJoin } from "rxjs";
import { KeyMappingService } from "../../Service/key-mapping.service";
import { ApprovalService } from "../../../../ApprovalManager/Approval/Service/approval.service";
import { ExportSampleReportCheckLogService } from "../../Service/export-sample-report-check-log.service";
import { AccountService } from "../../../../../core/auth/account/account.service";
import { Util } from "../../../../../core/utils/utils-function";
import { UploadFilePdfDialog } from "../upload-file-pdf/upload-file-pdf.dialog";

@Component({
    selector: 'app-print-sample-report-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './print-sample-report.dialog.html',
    styleUrls: ['./print-sample-report.dialog.scss'],
})
export class PrintSampleReportDialog {

    data: any;
    model: any = {};
    dataExport: any = {};
    listDataExport: any[] = [];
    listDeviceOptions: any[] = [];
    listPlans: any[] = [];
    listTypes: any[] = [{ label: 'PDF', value: 0 }, { label: 'XLSX', value: 1 }];
    listCriterialBySample: any[] = [];
    listCriterial: any[] = [];
    type: any;
    groupedData: any[] = [];
    daysInMonth = Array.from({ length: 31 }, (_, i) => i + 1);

    constructor(
        public ref: DynamicDialogRef,
        private dialogService: DialogService,
        public config: DynamicDialogConfig,
        private deviceService: DeviceService,
        private planService: PlanService,
        private cdr: ChangeDetectorRef,
        private keyMappingService: KeyMappingService,
        private approvalService: ApprovalService,
        private exportSampleReportCheckLogService: ExportSampleReportCheckLogService,
    ) {
        this.data = config.data.data;
    }

    ngOnInit(): void {
        forkJoin({
            devices: this.deviceService.getAll(),
            plans: this.planService.getAll(),
            keyMappings: this.keyMappingService.getBySampleReport(this.data.id!),
            dataExports: this.exportSampleReportCheckLogService.getAll()
        }).subscribe(result => {
            this.listDeviceOptions = result.devices;
            this.listPlans = result.plans;
            this.listCriterialBySample = result.keyMappings.map(x => {
                const group = x.criterial?.criterialGroup || null;
                const criterials = group
                    ? this.listCriterial.filter(c => c.criterialGroup?.id === group.id)
                    : [];
                return {
                    id: x.id,
                    group: group,
                    criterial: x.criterial || null,
                    criterials: criterials,
                    performer: x.performer || null,
                    frequency: x.frequency || null,
                };
            });
            this.listDataExport = result.dataExports;
            this.processData();
            this.cdr.detectChanges();
        })
    }

    processData() {
        const map = new Map();

        this.listCriterialBySample.forEach((item: any) => {
            const groupId = item.group.id;
            if (!map.has(groupId)) {
                map.set(groupId, {
                    groupName: item.group.name,
                    details: []
                });
            }
            map.get(groupId).details.push(item);
        });

        this.groupedData = Array.from(map.values());
    }

    upload(data: any) {
        const ref = this.dialogService.open(UploadFilePdfDialog, {
            header: 'TẬP TIN PDF',
            width: '35%',
            data: data,
            modal: true,
        });
    }

    viewPdf(id: number) {
        const url = `http://localhost:8081/api/exportSampleReportCheckLogs/${id}/view-pdf`;
        window.open(url, '_blank');
    }

    save() {
        this.approvalService
            .findApprovalsByEntityIdAndEntityType(this.model.plan.id, 'plans')
            .subscribe((data) => {
                console.log(data);
                data = Object.values(
                    data.reduce((acc: any, item: any) => {
                        const groupId = item.group?.groupApprovalName?.id;
                        acc[groupId] ??= {
                            groupApprovalName: item.group.groupApprovalName,
                            items: [],
                            userApprovals: []
                        };
                        acc[groupId].items.push(item);
                        acc[groupId].userApprovals.push(item.userApproval);
                        return acc;
                    }, {})
                ).map((group: any) => ({
                    ...group,
                    userApprovals: group.userApprovals.map((u: any) => ({
                        ...u,
                    }))
                }));
                data = data.map(g => ({
                    groupName: g.groupApprovalName.name,
                }));
                console.log(data);
                const dataPrint = { type: this.type, listCriterialBySample: this.listCriterialBySample, sampleReport: this.data, plan: this.model.plan, device: this.model.device, listPlanAppr: data }
                const dataExport = { username: Util.getUserNameLogin(), dateExport: new Date(), data: JSON.stringify(dataPrint) };
                this.exportSampleReportCheckLogService.create(dataExport).subscribe(res => {
                    console.log(res);
                    this.ref.close(dataPrint);
                })
            });
    }

    close() {
        this.ref.close();
    }
}