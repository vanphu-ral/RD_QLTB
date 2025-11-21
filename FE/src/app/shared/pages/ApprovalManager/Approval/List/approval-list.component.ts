import { Component } from '@angular/core';
import { BaseTableComponent } from '../../../../base/base-table-component/base-table.component';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { Column } from '../../../../models/Core/column.model';
import { ApprovalService } from '../Service/approval.service';
import _ from 'lodash';
import { Router } from '@angular/router';
import { ApprovalStateService } from '../Service/approval-state.service';
import { CustomDatePipe } from '../../../../pipes/custom-date.pipe';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'approval-list',
  standalone: true,
  imports: [SharedModule, BaseTableComponent, FormsModule],
  templateUrl: './approval-list.component.html',
  styleUrls: ['./approval-list.component.scss'],
})
export class ApprovalListComponent {
  selectedStatus: string | null = null;

  columns: Column[] = [
    { Field: 'id', Header: 'ID', IsHide: true },
    { Field: 'data.code', Header: 'Mã tài liệu', IsSearch: true, TypeSearch: 'text' },
    { Field: 'data.name', Header: 'Tên tài liệu', IsSearch: true, TypeSearch: 'text' },
    { Field: 'approval', Header: 'Loại tài liệu', IsSearch: true, TypeSearch: 'text' },
    { Field: 'approval.userApproval.username', Header: 'Người phê duyệt', IsSearch: true, TypeSearch: 'text' },
    { Field: 'approval.status', Header: 'Trạng thái', IsSearch: true, TypeSearch: 'text' },
    { Field: 'approval.createdBy', Header: 'Người tạo', IsSearch: true, TypeSearch: 'text', pipe: DatePipe },
    { Field: 'approval.createdAt', Header: 'Ngày tạo', IsSearch: true, TypeSearch: 'date' },
    { Field: 'approval.updatedAt', Header: 'Ngày cập nhật', IsSearch: true, TypeSearch: 'date', style: { 'min-width': '150px' } },
  ];

  constructor(public apiService: ApprovalService, private router: Router, private approvalStateService: ApprovalStateService) { }


  getEntityType(value: any) {
    switch (_.get(value, 'entityType')) {
      case 'sample_reports':
        return 'Mẫu biên bản';
      case 'plans':
        return 'Kế hoạch';
      case 'acceptances':
        return 'Biên bản nghiệm thu';
      default:
        return '';
    }
  }

  statusToString(status: any) {
    switch (status) {
      case 1:
        return 'Chưa duyệt';
      case 2:
        return 'Đã duyệt';
      case 6:
        return 'Bị từ chối';
      default:
        return '';
    }
  }

  statusToSeverity(status: any) {
    switch (status) {
      case 1:
        return 'warning';
      case 2:
        return 'success';
      case 3:
        return 'danger';
      default:
        return '';
    }
  }

  approvalDocument(data: any, openInNewTab = false) {
    // bảo đảm có approval
    const approval = data?.approval;
    if (!approval) {
      console.warn('No approval object in data', data);
      return;
    }

    const entityType: string | undefined = approval.entityType;
    const entityId: number | string | undefined = approval.entityId;

    if (!entityType || entityId == null) {
      console.warn('Missing entityType or entityId', approval);
      return;
    }

    // Map entityType từ backend sang route path phía FE
    const routeMap: { [key: string]: string } = {
      'sample_reports': 'SampleReports',
      'plans': 'Plans',
      // thêm mapping nếu có entityType khác
    };

    const basePath = routeMap[entityType];
    if (!basePath) {
      console.warn('No route mapping for entityType', entityType);
      return;
    }
    const idStr = encodeURIComponent(String(entityId));
    const urlPath = `/${basePath}/${idStr}/approval`;
    const fullUrl = `${location.origin}${urlPath}`;

    if (openInNewTab) {
      window.open(fullUrl, '_blank');
    } else {
      this.approvalStateService.setApproval(approval);
      this.router.navigate([`/${basePath}`, idStr, 'approval']).catch(err => {
        console.error('Router navigate error', err);
        window.location.href = fullUrl;
      });
    }
  }
}
