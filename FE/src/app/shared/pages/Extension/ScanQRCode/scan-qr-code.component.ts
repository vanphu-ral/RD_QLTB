import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { SharedModule } from '../../../../share.module';
import { Util } from '../../../core/utils/utils-function';

@Component({
  selector: 'app-scan-qr-code',
  standalone: true,
  imports: [SharedModule, CommonModule],
  templateUrl: './scan-qr-code.component.html',
  styleUrls: ['./scan-qr-code.component.scss']
})
export class ScanQrCodeComponent {

  listCriterialGroup: any[] = []

  constructor(
  ) {
  }


  
}