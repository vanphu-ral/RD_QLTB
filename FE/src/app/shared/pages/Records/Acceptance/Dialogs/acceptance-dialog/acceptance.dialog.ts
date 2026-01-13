import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanResult } from "../../../../../models/PlanManger/plan-result.model";
import { SampleReportService } from "../../../../PlanManager/SampleReport/Service/sample-report.service";
import { KeyMappingService } from "../../../../PlanManager/SampleReport/Service/key-mapping.service";
import { PlanResultDetail } from "../../../../../models/PlanManger/plan-result-detail.model";
import { ErrorReport } from "../../../../../models/PlanManger/error-report.model";
import { AccountService } from "../../../../../core/auth/account/account.service";
import { ErrorReportService } from "../../../../PlanManager/Plan/Service/error-report.service";
import { ApprovalWorlflowService } from "../../../../ApprovalManager/ApprovalWorkflow/Service/approval-workflow.service";
import { DeviceService } from "../../../../DeviceManager/Device/Service/device.service";
import { Acceptance } from "../../../../../models/PlanManger/acceptance.model";
import { Device } from "../../../../../models/DeviceManager/device.model";
import { PLANTYPE } from "../../../../../enums/plan-type.enum";
import { CriterialService } from "../../../../PlanManager/Criterial/Service/criterial.service";
import { AcceptanceService } from "../../service/acceptance.service";
import { forkJoin } from "rxjs/internal/observable/forkJoin";
import { ɵɵDir } from "@angular/cdk/scrolling";
import { ApprovalService } from "../../../../ApprovalManager/Approval/Service/approval.service";
import { SignatureService } from "../../../../SystemManager/Signature/Service/signature.service";
import { catchError, of } from "rxjs";

@Component({
    selector: 'app-acceptance-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './acceptance.dialog.html',
    styleUrls: ['./acceptance.dialog.scss'],
})
export class AcceptanceDialog {

    model: Acceptance = new Acceptance();
    listUserApproval: any[] = [];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private approvalService: ApprovalService,
        private signatureService: SignatureService,
        private cdr: ChangeDetectorRef
    ) {
        this.model = config.data.data;
    }

    ngOnInit() {
        this.loadApprovals();
    }

    loadApprovals(): void {
        this.approvalService
            .findApprovalsByEntityIdAndEntityType(this.model.id as any, 'acceptances')
            .subscribe((data) => {
                const usernames = data.map(x => x.userApproval?.username);
                this.signatureService.getByListUsernames(usernames).pipe(
                    catchError(err => {
                        console.error('Lỗi lấy danh sách chữ ký:', err);
                        return of([]); // Trả về mảng rỗng nếu lỗi
                    })
                ).subscribe(signatures => {
                    this.listUserApproval = Object.values(
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
                            imageLink: signatures.find((s: any) => s.username === u.username)?.imageLink || null
                        }))
                    }));
                    this.listUserApproval = this.listUserApproval.map(g => ({
                        groupName: g.groupApprovalName.name,
                        signatures: g.userApprovals.map((u: any) => u.imageLink)
                    }));
                    this.cdr.detectChanges();
                });
                this.cdr.detectChanges();
            });
    }

    textForList(reason: string): string[] {
        if (!reason) return [];
        return reason.split(/\r?\n/).filter(line => line.trim() !== '');
    }

    close() {
        this.ref.close();
    }
}