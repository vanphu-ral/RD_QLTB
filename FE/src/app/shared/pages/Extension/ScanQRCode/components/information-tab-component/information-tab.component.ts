import { Component, ElementRef, ViewChild, ChangeDetectorRef, Input, SimpleChanges, OnChanges } from '@angular/core';
import { BrowserMultiFormatReader, BarcodeFormat } from '@zxing/browser';
import { DecodeHintType } from '@zxing/library';
import { SharedModule } from '../../../../../../share.module';
import { CommonModule } from '@angular/common';
import { DeviceService } from '../../../../DeviceManager/Device/Service/device.service';
import { DeviceRelocationHistoryService } from '../../../../DeviceManager/Device/Service/device-relocation-histories.service';
import { PlanDetailService } from '../../../../PlanManager/Plan/Service/plan-detail.service';
import { ErrorReportService } from '../../../../PlanManager/Plan/Service/error-report.service';
import { DialogService } from 'primeng/dynamicdialog';
import { CheckDeviceDialog } from '../../../../PlanManager/Plan/Dialogs/check-device-dialog/check-device.dialog';
import _ from 'lodash';

@Component({
  selector: 'app-information-tab',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './information-tab.component.html',
  styleUrls: ['./information-tab.component.scss']
})
export class InformationTabComponent implements OnChanges {

  @Input() model: any;
  activeTabIndex: string = "0";

  listHistory: any[] = []
  listError: any[] = []
  listRepaired: any[] = []
  listPlanAudit: any[] = []

  constructor(private cdr: ChangeDetectorRef, private deviceRelocationHistoryService: DeviceRelocationHistoryService, private planDetailService: PlanDetailService, private errorReportService: ErrorReportService, private dialogService: DialogService) {
  }

  ngOnInit() { this.activeTabIndex = "0"; }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['model'] && this.model?.id) {
      this.loadHistoryMove(this.model.id);
    }
  }

  // Tab change
  onTabChange(event: any) {
    console.log(event);
    
    // if (!this.model?.id) return;
    switch (event) {
      case "0":
        this.loadHistoryMove(this.model.id);
        break;

      case "1":
        this.loadHistoryError(this.model.id);
        break;

      case "2":
        this.loadHistoryError(this.model.id);
        break;

      case "3":
        this.loadPlan(this.model.serialNumber);
        break;

      case "4":
        // this.loadBaoTri(this.model.id);
        break;

      case "5":
        // this.loadSuaChua(this.model.id);
        break;

      default:
        break;
    }
  }

  // Load Data By Tab
  loadHistoryMove(deviceId: number) {
    this.deviceRelocationHistoryService.getHistoryMoveByDeviceId(deviceId).subscribe(res => {
      this.listHistory = res;
      this.cdr.detectChanges();
    });
  }

  loadHistoryError(deviceId: number) {
    this.errorReportService.findByPlanResultId(deviceId).subscribe(res => {
      this.listError = res;
      this.listRepaired = res.filter((item: any) => item.isRepaired);
      console.log(res); 
      
      this.cdr.detectChanges();
    });
  }

  loadPlan(serial: string) {
    this.listPlanAudit = [];
    this.planDetailService.getPlansBySerial(serial).subscribe(res => {
      res.forEach((plan, i) => {
        plan.planDetails.forEach((detail: any) => {
          detail.sampleReport = JSON.parse(detail.detail);
          // Mỗi ngày kiểm tra có nhiều kết quả (planResults)
          detail.planResults?.forEach((result: any) => {
            this.listPlanAudit.push({
              stt: this.listPlanAudit.length + 1,
              planName: plan.name,
              deviceCode: detail.device?.code,
              deviceName: detail.device?.name,
              userPerformer: plan.userPerformer || detail.manager || 'N/A',
              dateTest: result.dateTest,
              planResultId: result.id,
              plan: detail
            });
          });
        });
      });
      console.log(this.listPlanAudit);
      
      this.cdr.detectChanges();
    });
  }


  getSeverity(status: number): any {
    switch (status) {
      case 0:
        return 'Nghiêm trọng';
      case 1:
        return 'Bất thường';
      case 2:
        return 'Nhẹ';
      default:
        return '';
    }
  }

  checkDevice(data: any) {
    const planResult = _.find(data.plan.planResults, x => x.id === data.planResultId);
    const childRef = this.dialogService.open(CheckDeviceDialog, {
      header: `Kiểm tra thiết bị`,
      width: '100%',
      modal: true,
      closable: true,
      data: { planResult: planResult, device: data.plan, plan: data.plan.plan },
    });
    childRef.onClose.subscribe((result) => {
      if (result && result.length > 0) {
      }
    });
  }

}