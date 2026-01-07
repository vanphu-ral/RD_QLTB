import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _, { sample } from "lodash";
import { DeviceService } from "../../../../DeviceManager/Device/Service/device.service";
import { PlanService } from "../../../Plan/Service/plan.service";
import { forkJoin } from "rxjs";
import { KeyMappingService } from "../../Service/key-mapping.service";
import { ApprovalService } from "../../../../ApprovalManager/Approval/Service/approval.service";

@Component({
    selector: 'app-print-sample-report-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './print-sample-report.dialog.html',
    styleUrls: ['./print-sample-report.dialog.scss'],
})
export class PrintSampleReportDialog {

    data: any;
    model: any = {};
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
        public config: DynamicDialogConfig,
        private deviceService: DeviceService,
        private planService: PlanService,
        private cdr: ChangeDetectorRef,
        private keyMappingService: KeyMappingService,
        private approvalService: ApprovalService
    ) {
        this.data = config.data.data;
    }

    ngOnInit(): void {
        forkJoin({
            devices: this.deviceService.getAll(),
            plans: this.planService.getAll(),
            keyMappings: this.keyMappingService.getBySampleReport(this.data.id!)
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
                this.ref.close({ type: this.type, listCriterialBySample: this.listCriterialBySample, sampleReport: this.data, plan: this.model.plan, device: this.model.device, listPlanAppr: data });
                
            });
    }

    close() {
        this.ref.close();
    }
}