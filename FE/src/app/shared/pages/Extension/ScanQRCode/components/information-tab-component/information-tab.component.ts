import { Component, ElementRef, ViewChild, ChangeDetectorRef, Input, SimpleChanges, OnChanges } from '@angular/core';
import { BrowserMultiFormatReader, BarcodeFormat } from '@zxing/browser';
import { DecodeHintType } from '@zxing/library';
import { SharedModule } from '../../../../../../share.module';
import { CommonModule } from '@angular/common';
import { DeviceService } from '../../../../DeviceManager/Device/Service/device.service';
import { DeviceRelocationHistoryService } from '../../../../DeviceManager/Device/Service/device-relocation-histories.service';
import { PlanDetailService } from '../../../../PlanManager/Plan/Service/plan-detail.service';
import { ErrorReportService } from '../../../../PlanManager/Plan/Service/error-report.service';

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

  constructor(private cdr: ChangeDetectorRef, private deviceRelocationHistoryService: DeviceRelocationHistoryService, private planDetailService: PlanDetailService, private errorReportService: ErrorReportService) {
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
    this.planDetailService.getPlansBySerial(serial).subscribe(res => {
      console.log(res);
      
      // this.listHistory = res;
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

}