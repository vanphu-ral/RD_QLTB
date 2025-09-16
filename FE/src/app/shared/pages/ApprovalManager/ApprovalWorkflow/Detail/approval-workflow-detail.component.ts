import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
import { ApprovalWorlflowService } from '../Service/approval-workflow.service';
import { Util } from '../../../../core/utils/utils-function';
import { ApprovalWorkflow } from '../../../../models/ApprovalManager/approval-workflow.model';
import _ from 'lodash';
import { GroupApprovalNameService } from '../../GroupApprovalName/Service/group-approval-name.service';
import { forkJoin, Observable, of } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';
import { ApprovalGroupService } from '../Service/approval-group.service';
import { ApprovalGroupUserService } from '../Service/approval-group-user.service';
import { ApprovalGroup } from '../../../../models/ApprovalManager/approval-group.model';
import { ApprovalGroupUser } from '../../../../models/ApprovalManager/approval-group-user.model';
import { ConfirmationService } from 'primeng/api';


@Component({
  selector: 'app-approval-workflow-detail',
  standalone: true,
  providers: [ConfirmationService],
  imports: [SharedModule, CommonModule],
  templateUrl: './approval-workflow-detail.component.html',
  styleUrls: ['./approval-workflow-detail.component.scss']
})
export class ApprovalWorkflowDetailComponent extends BasePageComponent<ApprovalWorkflow> {

  listApprovalGroupsName: any[] = [];
  listUsers: any[] = []
  constructor(
    protected override apiService: ApprovalWorlflowService,
    private approvalGroupNameService: GroupApprovalNameService,
    private approvalGroupService: ApprovalGroupService,
    private approvalGroupUserService: ApprovalGroupUserService,
    private confirmationService: ConfirmationService
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.apiService.getUsers().subscribe(users => {
      this.listUsers = _.map(users, user => {
        const firstName = user.firstName ?? '';
        const lastName = user.lastName ?? '';
        const fullName = [firstName, lastName].filter(Boolean).join(' ').trim();
        return {
          name: fullName ? `${user.username} - ${fullName}` : user.username,
          username: user.username,
        };
      })
      this.cdr.detectChanges();
    })
    this.approvalGroupNameService.getAll().subscribe(res => {
      this.listApprovalGroupsName = res
      this.cdr.detectChanges();
    })

    if (this.isViewMode || this.isEditMode) {
      this.loadGroupsAndUsers();
    }
  }

  private loadGroupsAndUsers(): void {
    if (!this.model?.id) return;

    this.approvalGroupService.getAll().pipe(
      switchMap(groups => {
        // lọc group thuộc workflow hiện tại
        const relatedGroups = groups.filter(g => g.workflow?.id === this.model.id);

        if (relatedGroups.length === 0) {
          this.model.approvalGroups = [];
          return of([]);
        }

        // map từng group -> load user
        const groupRequests = relatedGroups.map(group =>
          this.approvalGroupUserService.getAll().pipe(
            map(users => {
              group.approvalGroupUser = users.filter(u => u.group?.id === group.id);
              return group;
            })
          )
        );

        return forkJoin(groupRequests);
      })
    ).subscribe({
      next: (groupsWithUsers) => {
        this.model.approvalGroups = groupsWithUsers;
        console.log("Workflow sau khi merge:", this.model);
        this.cdr.detectChanges();
      },
      error: (err) => {
        Util.ConfirmMessage('Không load được group/users', 'error');
        console.error(err);
      }
    });
  }

  // #region fuction nhóm phê duyệt
  addNewApprovalGroup(): void {
    if (Util.isEmptyArray(this.model.approvalGroups)) {
      this.model.approvalGroups = []
    }
    this.model.approvalGroups!.push({});
  }

  removeApprovalGroup(index: number): void {
    const group = this.model.approvalGroups?.[index];
    if (!group) return;

    if ((group as any).id) {
      this.approvalGroupService.delete((group as any).id).subscribe({
        next: () => {
          this.model.approvalGroups?.splice(index, 1);
        },
        error: (err) => {
          console.error('Lỗi khi xoá group:', err);
        }
      });
    } else {
      this.model.approvalGroups?.splice(index, 1);
    }
  }
  // #endregion fuction nhóm phê duyệt

  // #region fuction người phê duyệt
  addNewApprovalUser(index: number): void {
    if (Util.isEmptyArray(this.model.approvalGroups![index].approvalGroupUser)) {
      this.model.approvalGroups![index].approvalGroupUser = []
    }
    this.model.approvalGroups![index].approvalGroupUser!.push({});
  }


  removeApprovalGroupUser(indexGroup: number, indexUser: number) {
    const group = this.model.approvalGroups?.[indexGroup];
    const user = group?.approvalGroupUser?.[indexUser];
    if (!group || !user) return;
    if ((user as any).id) {
      this.approvalGroupUserService.delete((user as any).id).subscribe({
        next: () => {
          group.approvalGroupUser?.splice(indexUser, 1);
        },
        error: (err) => {
          console.error('Lỗi khi xoá user:', err);
        }
      });
    } else {
      group.approvalGroupUser?.splice(indexUser, 1);
    }
  }


  // Confirm trước khi xoá
  confirmDeleteGroup(groupIndex: number): void {
    this.confirmationService.confirm({
      message: 'Bạn có chắc chắn muốn xóa nhóm phê duyệt này?',
      accept: () => this.removeApprovalGroup(groupIndex)
    });
  }

  confirmDeleteUser(groupIndex: number, userIndex: number): void {
    this.confirmationService.confirm({
      message: 'Bạn có chắc chắn muốn xóa user này?',
      accept: () => this.removeApprovalGroupUser(groupIndex, userIndex)
    });
  }

  private saveWorkflow(workflow: ApprovalWorkflow): Observable<ApprovalWorkflow> {
    return workflow.id
      ? this.apiService.update(workflow.id, workflow).pipe(
        map((res: any) => {
          workflow.id = res?.id ?? res;
          return workflow;
        })
      )
      : this.apiService.create(workflow).pipe(
        map((id: any) => {
          workflow.id = id;
          return workflow;
        })
      );
  }

  private saveGroup(group: ApprovalGroup, workflowId: number): Observable<ApprovalGroup> {
    // tránh vòng lặp
    group.workflow = { id: workflowId };

    const groupOp$ = group.id
      ? this.approvalGroupService.update(group.id, group).pipe(
        map((res: any) => {
          group.id = res?.id ?? res;
          return group;
        })
      )
      : this.approvalGroupService.create(group).pipe(
        map((id: any) => {
          group.id = id;
          return group;
        })
      );

    return groupOp$.pipe(
      switchMap(savedGroup => {
        const users = savedGroup.approvalGroupUser ?? [];
        if (users.length === 0) return of(savedGroup);

        const userRequests = users.map(user => this.saveUser(user, savedGroup.id as number));
        return forkJoin(userRequests).pipe(map(() => savedGroup));
      })
    );
  }

  private saveUser(user: ApprovalGroupUser, groupId: number): Observable<ApprovalGroupUser> {
    // tránh vòng lặp
    user.group = { id: groupId };

    return user.id
      ? this.approvalGroupUserService.update(user.id, user).pipe(
        map((res: any) => {
          user.id = res?.id ?? res;
          return user;
        })
      )
      : this.approvalGroupUserService.create(user).pipe(
        map((id: any) => {
          user.id = id;
          return user;
        })
      );
  }


  public override save(): void {
    if (!this.model) return;
    this.model = Util.prepareModel(this.model);
    this.saveWorkflow(this.model).pipe(
      switchMap(savedWorkflow => {
        const groups = this.model?.approvalGroups ?? [];
        if (groups.length === 0) return of(savedWorkflow);

        const groupRequests = groups.map(g => this.saveGroup(g, savedWorkflow.id!));
        return forkJoin(groupRequests).pipe(map(() => savedWorkflow));
      })
    ).subscribe({
      next: () => {
        Util.ConfirmMessage('Lưu thành công!', 'success');
      },
      error: (err) => {
        Util.ConfirmMessage('Có lỗi xảy ra khi lưu', 'error');
        console.error(err);
      }
    }).add(() => this.navigationService.back());
  }



}