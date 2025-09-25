import { ChangeDetectorRef, Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { PlanService } from '../Service/plan.service';
import { Column } from '../../../../models/Core/column.model';
import { Observable } from 'rxjs';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { CheckListDeviceDialog } from '../Dialogs/check-list-device-dialog/check-list-device.dialog';
import { EvaluateDeviceDialog } from '../Dialogs/evaluate-device-dialog/evaluate-devicedialog';
import { TableRowCollapseEvent, TableRowExpandEvent } from 'primeng/table';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ActivatedRoute, Router } from '@angular/router';
import { Util } from '../../../../core/utils/utils-function';

@Component({
  selector: 'view-evaluate',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './view-evaluate.page.html',
  styleUrls: ['./view-evaluate.page.scss'],
})
export class ViewEvaluatePage {


  constructor(public apiService: PlanService, private router: Router, private route: ActivatedRoute, private dialogService: DialogService, private cdr: ChangeDetectorRef, private messageService: MessageService, private confirmationService: ConfirmationService) { }

  ngOnInit(): void {
    this.loadData();
  }

  loadData() {
   
  }

 
}
