import { Component, ElementRef, ViewChild, ChangeDetectorRef, Input, SimpleChanges, OnChanges } from '@angular/core';
import { BrowserMultiFormatReader, BarcodeFormat } from '@zxing/browser';
import { DecodeHintType } from '@zxing/library';
import { SharedModule } from '../../../../../../share.module';
import { CommonModule } from '@angular/common';
import { DeviceService } from '../../../../DeviceManager/Device/Service/device.service';
import { DeviceRelocationHistoryService } from '../../../../DeviceManager/Device/Service/device-relocation-histories.service';

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

  constructor(private cdr: ChangeDetectorRef, private deviceRelocationHistoryService: DeviceRelocationHistoryService) {
  }

  ngOnInit() { this.activeTabIndex = "0"; }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['model'] && this.model?.id) {
      this.loadHistoryMove(this.model.id);
    }
  }

  
  onTabChange(event: any) {
    if(event.index === 0 && this.model?.id) {
      this.loadHistoryMove(this.model.id);
    }
    // if(event.index === 1 && this.model?.id) {
    //   this.activeTabIndex = 0;
    // }
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

}