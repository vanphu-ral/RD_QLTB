import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { SignatureService } from '../Service/signature.service';
import { Column } from '../../../../models/Core/column.model';

@Component({
  selector: 'signature-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './signature-list.component.html',
  styleUrls: ['./signature-list.component.scss'],
})
export class SignatureListComponent {
  selectedStatus: string | null = null;

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'username', Header: 'Tên người ký', IsSearch: true, TypeSearch: 'text' },
    { Field: 'imageLink', Header: 'Có ảnh chữ ký', IsSearch: true, TypeSearch: 'text' },
  ];

  constructor(public apiService: SignatureService) {}
}
