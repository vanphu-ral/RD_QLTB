import { Component, ElementRef, ViewChild, ChangeDetectorRef, Input, SimpleChanges, OnChanges } from '@angular/core';
import { BrowserMultiFormatReader, BarcodeFormat } from '@zxing/browser';
import { DecodeHintType } from '@zxing/library';
import { SharedModule } from '../../../../../../share.module';
import { CommonModule } from '@angular/common';
import { DeviceService } from '../../../../DeviceManager/Device/Service/device.service';
import { DeviceRelocationHistoryService } from '../../../../DeviceManager/Device/Service/device-relocation-histories.service';
import { PlanDetailService } from '../../../../PlanManager/Plan/Service/plan-detail.service';

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

  constructor(private cdr: ChangeDetectorRef, private deviceRelocationHistoryService: DeviceRelocationHistoryService, private planDetailService: PlanDetailService) {
  }

  ngOnInit() { this.activeTabIndex = "0"; }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['model'] && this.model?.id) {
      this.loadHistoryMove(this.model.id);
    }
  }


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
        // this.loadHistoryMaintenance(this.model.id);
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

  loadHistoryMove(deviceId: number) {
    this.deviceRelocationHistoryService.getHistoryMoveByDeviceId(deviceId).subscribe(res => {
      this.listHistory = res;
      this.cdr.detectChanges();
    });
  }

  loadHistoryError(deviceId: number) {
    // load lịch sử sự cố
  }

  loadPlan(serial: string) {
    this.planDetailService.getPlansBySerial(serial).subscribe(res => {
      console.log(res);
      
      // this.listHistory = res;
      this.cdr.detectChanges();
    });
  }

}