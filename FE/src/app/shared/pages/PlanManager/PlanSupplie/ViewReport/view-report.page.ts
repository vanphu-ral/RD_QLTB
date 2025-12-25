import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { PlanSupplieService } from '../Service/plan-supplie.service';
import { PlanSupplie } from '../../../../models/PlanManger/plan-supplie.model';
import { SignatureService } from '../../../SystemManager/Signature/Service/signature.service';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'view-report',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './view-report.page.html',
  styleUrls: ['./view-report.page.scss'],
})
export class ViewReportPage extends BasePageComponent<any> {

  listUserStatusAppr: any[] = [];
  listUserApproval: any[] = [];

  constructor(
    protected override apiService: PlanSupplieService,
    private signatureService: SignatureService
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.approvalService
      .findApprovalsByEntityIdAndEntityType(this.model.id, 'plan_supplies')
      .subscribe((data) => {
        this.listUserStatusAppr = data;
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
          console.log(this.listUserApproval);
          
          this.cdr.detectChanges();
        });
        this.cdr.detectChanges();
      });
  }

  public override save(): void {
    throw new Error('Method not implemented.');
  }
}
