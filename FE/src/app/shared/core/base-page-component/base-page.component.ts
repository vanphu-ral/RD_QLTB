import { Directive, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BaseApiService } from '../../service/base-api.service';
import { Observable } from 'rxjs';
import * as _ from 'lodash';
import { NavigationService } from '../../service/navigation.service';

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

  constructor(protected route: ActivatedRoute, protected apiService: BaseApiService<T>, protected navigationService: NavigationService) {}

  
  ngOnInit(): void {
    this.mode = this.route.snapshot.data['mode'] as 'add' | 'view' | 'edit';
    this.id = this.route.snapshot.paramMap.get('id') ?? undefined;

    if (this.route.snapshot.data['data']) {
      this.model = this.route.snapshot.data['data'];
    }

    if (this.isAddMode) {
      this.initNewModel();
    }
  }
  
  protected abstract initNewModel(): void;
  public abstract save(): void;

  public onBack(): void {
    this.navigationService.back();
  }
}