import { Component, ChangeDetectorRef } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { DeviceService } from '../../../DeviceManager/Device/Service/device.service';

@Component({
  selector: 'app-import-data',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './import-data.component.html',
  styleUrls: ['./import-data.component.scss']
})
export class ImportDataComponent {

  data: any = {}
  listTables: any[] = []

  constructor(private cdr: ChangeDetectorRef, private deviceService: DeviceService) {
  }

  ngOnInit(): void {
   
  }

  
}