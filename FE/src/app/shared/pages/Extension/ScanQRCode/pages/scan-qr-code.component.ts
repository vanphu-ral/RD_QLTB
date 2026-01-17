import { Component, ElementRef, ViewChild, ChangeDetectorRef } from '@angular/core';
import { BrowserMultiFormatReader, BarcodeFormat } from '@zxing/browser';
import { DecodeHintType } from '@zxing/library';
import { SharedModule } from '../../../../../share.module';
import { CommonModule } from '@angular/common';
import { InformationTabComponent } from "../components/information-tab-component/information-tab.component";
import { DeviceService } from '../../../DeviceManager/Device/Service/device.service';
import { Device } from '../../../../models/DeviceManager/device.model';

@Component({
  selector: 'app-scan-qr-code',
  standalone: true,
  imports: [SharedModule, CommonModule, InformationTabComponent],
  templateUrl: './scan-qr-code.component.html',
  styleUrls: ['./scan-qr-code.component.scss']
})
export class ScanQrCodeComponent {

  @ViewChild('preview') preview!: ElementRef<HTMLVideoElement>;
  serial: string = '';
  reader = new BrowserMultiFormatReader();
  hints = new Map();
  facingMode: 'environment' | 'user' = 'environment';
  videoStream: MediaStream | null = null;
  codeReader: BrowserMultiFormatReader = new BrowserMultiFormatReader();

  optionScans = [{ label: 'Scan máy', value: 1 }, { label: 'Scan camera', value: 2 }];
  scanType: number = 2;

  device: any = { line: {} };

  constructor(private cdr: ChangeDetectorRef, private deviceService: DeviceService) {
  }

  ngOnInit(): void {
    this.hints.set(DecodeHintType.POSSIBLE_FORMATS, [
      BarcodeFormat.QR_CODE,
      BarcodeFormat.CODE_128,
      BarcodeFormat.CODE_39,
      BarcodeFormat.EAN_13,
      BarcodeFormat.EAN_8,
      BarcodeFormat.UPC_A,
      BarcodeFormat.UPC_E,
      BarcodeFormat.ITF,
      BarcodeFormat.CODABAR,
    ]);

    this.reader = new BrowserMultiFormatReader(this.hints);
  }

  ngAfterViewInit(): void {
    if (this.scanType == 2) {
      this.startScan();
    }
  }

  async startScan() {
    // stop stream cũ nếu có
    if (this.videoStream) {
      this.videoStream.getTracks().forEach(track => track.stop());
      this.videoStream = null;
    }

    const constraints: MediaStreamConstraints = {
      video: { facingMode: this.facingMode }
    };

    try {
      this.videoStream = await navigator.mediaDevices.getUserMedia(constraints);
      const videoEl = this.preview.nativeElement;
      videoEl.srcObject = this.videoStream;
      videoEl.setAttribute('playsinline', 'true');
      await videoEl.play();

      this.codeReader.decodeFromVideoElement(videoEl, (result, error) => {
        if (result) {
          this.serial = result.getText();
          this.cdr.detectChanges();
          this.getDeviceInfo(this.serial);
        }
      });
    } catch (err) {
      console.error('Cannot access camera:', err);
    }
  }

  async toggleCamera() {
    if (this.videoStream) {
      this.videoStream.getTracks().forEach(track => track.stop());
      this.videoStream = null;
    }
    this.facingMode = this.facingMode === 'environment' ? 'user' : 'environment';
    await this.startScan();
  }

  stopScan() {
    const video = this.preview?.nativeElement;
    if (video && video.srcObject) {
      const stream = video.srcObject as MediaStream;
      stream.getTracks().forEach(track => track.stop());
      video.srcObject = null;
    }
  }

  getDeviceInfo(serial: any) {
    this.deviceService.getBySerialNumber(serial).subscribe({
      next: (device) => {
        this.device = device;
        this.device.DateUseAndInstall = `${new Date(this.device.dateManufacture).getFullYear()} - ${new Date(this.device.installationDate).getFullYear()}`;
        this.device.displayLocation = device.line?.name || device.team?.name || device.branch?.name || ''
        this.serial = '';
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error fetching device info:', err);
      }
    });
  }


}