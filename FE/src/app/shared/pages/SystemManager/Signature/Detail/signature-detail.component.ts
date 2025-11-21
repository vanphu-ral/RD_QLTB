import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { SignatureService } from '../Service/signature.service';
import { Util } from '../../../../core/utils/utils-function';
import { CriterialGroup } from '../../../../models/PlanManger/criterial-group.model';
import { Signature } from '../../../../models/SystemManager/signature.model';
import _ from 'lodash';

@Component({
  selector: 'app-signature-detail',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './signature-detail.component.html',
  styleUrls: ['./signature-detail.component.scss']
})
export class SignatureDetailComponent extends BasePageComponent<Signature> {

  listUsers: any[] = []

  constructor(
    protected override apiService: SignatureService,
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.loadListUser();
  }

  loadListUser() {
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
  }

  onFileSelect(event: any) {
    const file: File = event.files[0];
    const reader = new FileReader();
    reader.onload = () => {
      if (this.model) {
        this.model.imageLink = reader.result as string; // gán ngay
      }
    };

    reader.readAsDataURL(file);
  }



  public override save(): void {
    if (this.model) {

      this.model = Util.prepareModel(this.model);
      console.log(this.model);

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