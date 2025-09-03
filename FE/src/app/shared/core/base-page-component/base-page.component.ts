import { Directive, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BaseApiService } from '../../service/base-api.service';
import { Observable } from 'rxjs';
import * as _ from 'lodash';
import { NavigationService } from '../../service/navigation.service';
import { AccountService } from '../auth/account/account.service';

@Directive()
export abstract class BasePageComponent<T> implements OnInit {
  
  public id?: string | number;
  public model: T | null = null;
  
  public mode!: 'add' | 'view' | 'edit';
  
  public get isAddMode(): boolean {
    return this.mode === 'add';
  }
  
  public get isEditMode(): boolean {
    return this.mode === 'edit';
  }
  
  public get isViewMode(): boolean {
    return this.mode === 'view';
  }

  listStatus: any[] = [
    { label: 'Kích hoạt', value: 1 },
    { label: 'Vô hiệu hóa', value: 0 }
  ];

  protected route = inject(ActivatedRoute);
  protected navigationService = inject(NavigationService);
  protected accountService = inject(AccountService);

  constructor(protected apiService: BaseApiService<T>) {}
  
  ngOnInit(): void {
    this.mode = this.route.snapshot.data['mode'] as 'add' | 'view' | 'edit';
    this.id = this.route.snapshot.paramMap.get('id') ?? undefined;

    if (this.route.snapshot.data['data']) {
      this.model = this.route.snapshot.data['data'];
      if(this.isEditMode) {
        _.set(this.model as any, 'updatedBy', this.accountService.getUser()?.email ?? 'unknown');
      }
    }

    if (this.isAddMode) {
      this.initNewModel();
      _.set(this.model as any, 'status', 1);
    }
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
}