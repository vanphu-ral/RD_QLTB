import { ChangeDetectorRef, Directive, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BaseApiService } from '../../service/base-api.service';
import { Observable, take } from 'rxjs';
import * as _ from 'lodash';
import { NavigationService } from '../../service/navigation.service';
import { AccountService } from '../auth/account/account.service';
import { ApprovalStateService } from '../../pages/ApprovalManager/Approval/Service/approval-state.service';
import { ApprovalService } from '../../pages/ApprovalManager/Approval/Service/approval.service';
import { Util } from '../utils/utils-function';

@Directive()
export abstract class BasePageComponent<T> implements OnInit {
  
  public id?: string | number;
  public model: T = {} as T;
  public approvalModel: any = {};
  
  public mode!: 'add' | 'view' | 'edit' | 'approval';
  
  public get isAddMode(): boolean {
    return this.mode === 'add';
  }
  
  public get isEditMode(): boolean {
    return this.mode === 'edit';
  }
  
  public get isViewMode(): boolean {
    return this.mode === 'view';
  }

  public get isApprovalMode(): boolean {
    return this.mode === 'approval';
  }

  listStatus: any[] = [
    { label: 'Kích hoạt', value: 1 },
    { label: 'Vô hiệu hóa', value: 0 }
  ];

  listApprovalStatus: any[] = [
    { label: 'Từ chối', value: 3 },
    { label: 'Duyệt', value: 2 },
  ];

  protected route = inject(ActivatedRoute);
  protected navigationService = inject(NavigationService);
  protected accountService = inject(AccountService);
  protected cdr = inject(ChangeDetectorRef);
  protected approvalStateService = inject(ApprovalStateService);
  protected approvalService = inject(ApprovalService)

  constructor(protected apiService: BaseApiService<T>) {}
  
  ngOnInit(): void {
    this.mode = this.route.snapshot.data['mode'] as 'add' | 'view' | 'edit' | 'approval';
    this.id = this.route.snapshot.paramMap.get('id') ?? undefined;

    if (this.route.snapshot.data['data']) {
      this.model = this.route.snapshot.data['data'];
    }

    if (this.isAddMode) {
      this.initNewModel();
      _.set(this.model as any, 'status', 1);
    }

    if(this.isApprovalMode) {
      this.approvalStateService.getApproval$().pipe(take(1)).subscribe((a: any) => {
        if (a) {
          this.approvalModel = a;
        } else {
          this.approvalService.getById(this.approvalStateService.getApprovalIdFromStorage() as number).subscribe((data: any) => {
            this.approvalModel = data;
          })
        }
      });
    }
    this.cdr.detectChanges();
  }
  
 protected initNewModel(): void {
    try {
      this.model = new (Object as any).getPrototypeOf(this).constructor.name() as T;
    } catch {
      this.model = {} as T;
    }
  }
  public abstract save(): void;

  public onBack(): void {
    this.navigationService.back();
  }

  public Approval() {
    this.apiService.update(_.get(this.model, 'id')!, this.model).subscribe({
      next: () => {
        Util.ConfirmMessage('Cập nhật thành công', 'success');
      },
      error: () => {
        Util.ConfirmMessage('Cập nhật thất bại', 'error');
      }
    }).add(() => this.navigationService.back());
  }
}