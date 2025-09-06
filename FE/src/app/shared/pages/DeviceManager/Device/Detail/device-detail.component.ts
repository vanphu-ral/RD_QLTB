import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
import { DeviceService } from '../Service/device.service';
import { Util } from '../../../../core/utils/utils-function';
import { AccountService } from '../../../../core/auth/account/account.service';
import { NavigationService } from '../../../../service/navigation.service';
import { DeviceGroup } from '../../../../models/DeviceManager/device-group.model';
import { Device } from '../../../../models/DeviceManager/device.model';
import { DeviceGroupService } from '../../DeviceGroup/Service/device-group.service';
import { BranchService } from '../../../Categories/Branch/Service/branch.service';
import { LineService } from '../../../Categories/Line/Service/line.service';

@Component({
  selector: 'app-device-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './device-detail.component.html',
  styleUrls: ['./device-detail.component.scss']
})
export class DeviceDetailComponent extends BasePageComponent<Device> {

  listDeviceGroups: any[] = [];
  listBranches: any[] = [];
  listLines: any[] = [];

  constructor(
    protected override apiService: DeviceService,
    private deviceGroupService: DeviceGroupService,
    private branchService: BranchService,
    private lineService: LineService,
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.deviceGroupService.getAll().subscribe(groups => {
      this.listDeviceGroups = groups;
      this.cdr.detectChanges();
    });
    this.branchService.getAll().subscribe(branches => {
      this.listBranches = branches;
      this.cdr.detectChanges();
    });
    this.lineService.getAll().subscribe(lines => {
      this.listLines = lines;
      this.cdr.detectChanges();
    });
  }


  public override save(): void {
    if (this.model) {
      this.model = Util.prepareModel(this.model);

      if (this.isAddMode) {
        this.apiService.create(this.model).subscribe({
          next: () => {
            Util.ConfirmMessage('Thêm mới thành công', 'success');
          },
          error: () => {
            Util.ConfirmMessage('Thêm mới thất bại', 'error');
          }
        }).add(() => this.navigationService.back());
      } else {
        this.apiService.update(this.model.id!, this.model).subscribe({
          next: () => {
            Util.ConfirmMessage('Cập nhật thành công', 'success');
          },
          error: () => {
            Util.ConfirmMessage('Cập nhật thất bại', 'error');
          }
        }).add(() => this.navigationService.back());
      }
    }
  }
}