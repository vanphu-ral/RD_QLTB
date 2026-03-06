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
import { RepairErrorDialog } from '../../../../PlanManager/Plan/Dialogs/repair-error-dialog/repair-error.dialog';
import { Util } from '../../../../../core/utils/utils-function';
import { ErrorReport } from '../../../../../models/PlanManger/error-report.model';
import { ConfirmationService, MessageService } from 'primeng/api';
import { PlanResultService } from '../../../../PlanManager/Plan/Service/plan-result.service';
import { PLANTYPE } from '../../../../../enums/plan-type.enum';
import { firstValueFrom } from 'rxjs';
import { AccountService } from '../../../../../core/auth/account/account.service';
import { AddCheckListDialog } from '../../Dialog/add-check-list-dialog/add-check-list.dialog';

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
  listPlanMaintance: any[] = []
  listDeviceStops: any[] = []

  PLANTYPE = PLANTYPE;

  constructor(private cdr: ChangeDetectorRef, private deviceRelocationHistoryService: DeviceRelocationHistoryService, private accountService: AccountService,
    private planDetailService: PlanDetailService, private errorReportService: ErrorReportService, private planResultService: PlanResultService,
    private dialogService: DialogService, private comfirmService: ConfirmationService, private messageService: MessageService) {
  }

  ngOnInit() { this.activeTabIndex = "0"; }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['model'] && this.model?.id) {
      this.loadHistoryMove(this.model.id);
    }
  }

  // Tab change
  onTabChange(event: any) {
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
        this.loadPlan(this.model.qrCode);
        break;

      case "4":
        this.loadPlan(this.model.qrCode);
        break;

      case "5":
        // this.loadSuaChua(this.model.id);
        break;
      
      case "6":
        this.loadHistoryError(this.model.id);
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
      this.listDeviceStops = res.filter((item: any) => item.isRepaired && item.severity == 0).map((error: any) => ({
        ...error,
        totalStopTime: this.calculateStopTime(
          error.timeReported!,
          error.timeRepaired
        )
      }));
      this.cdr.detectChanges();
    });
  }

  async loadPlan(qrCode: string) {
    this.listPlanAudit = [];
    this.listPlanMaintance = [];
    const res = await firstValueFrom(
      this.planDetailService.getPlansBySerial(qrCode)
    );
    res.forEach((plan, i) => {
      if (plan.planType.code == PLANTYPE.DAILYCHECK) {
        plan.planDetails.forEach((detail: any) => {
          detail.sampleReport = JSON.parse(detail.detail);
          // Mỗi ngày kiểm tra có nhiều kết quả (planResults)
          // detail.planResults?.forEach((result: any) => {
          //   this.listPlanAudit.push({
          //     stt: this.listPlanAudit.length + 1,
          //     planName: plan.name,
          //     deviceCode: detail.device?.code,
          //     deviceName: detail.device?.name,
          //     userPerformer: plan.userPerformer || detail.manager || 'N/A',
          //     dateTest: result.dateTest,
          //     planResultId: result.id,
          //     plan: detail,
          //     status: result.status,
          //     enabledCheck: this.isEnabledCheckDevice(result)
          //   });
          // });
          detail.planResults?.forEach((result: any) => {
            const row = {
              stt: this.listPlanAudit.length + 1,
              planName: plan.name,
              deviceCode: detail.device?.code,
              deviceName: detail.device?.name,
              userPerformer: plan.userPerformer || detail.manager || 'N/A',
              dateTest: result.dateTest,
              planResultId: result.id,
              plan: detail,
              status: result.status,
              enabledCheck: false
            };
            row.enabledCheck = this.isEnabledCheckDevice(row);
            this.listPlanAudit.push(row);
          });
        });
        // this.cdr.detectChanges();
      }
      if (plan.planType.code == PLANTYPE.MAINTENANCE) {
        plan.planDetails.forEach((detail: any) => {
          detail.sampleReport = JSON.parse(detail.detail);
          if(Util.isEmptyArray(detail.planResults)) {
            this.listPlanMaintance.push({
              stt: this.listPlanMaintance.length + 1,
              planName: plan.name,
              deviceCode: detail.device?.code,
              deviceName: detail.device?.name,
              userPerformer: plan.userPerformer || detail.manager || 'N/A',
              dateTest: new Date(),
              plan: detail,
              status: 1
            });
            // this.cdr.detectChanges();
          }else {
            detail.planResults?.forEach((result: any) => {
              this.listPlanMaintance.push({
                stt: this.listPlanMaintance.length + 1,
                planName: plan.name,
                deviceCode: detail.device?.code,
                deviceName: detail.device?.name,
                userPerformer: plan.userPerformer || detail.manager || 'N/A',
                dateTest: result.dateTest,
                planResultId: result.id,
                plan: detail,
                status: result.status
              });
            });
            // this.cdr.detectChanges();
          }
        });
      }
    });
    this.cdr.detectChanges();
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

  statusToString(status: number) {
    return Util.statusToString(status);
  }

  getSeverityStatus(status: number): string {
    return Util.statusToSeverity(status);
  }

  isEnabledCheckDevice(row: any) {
    if (row.status === 15) {
      return false;
    }
    const isPowerUser = this.accountService.hasAnyAuthority(['RD_QLTB_ADMIN', 'RD_QLTB_MANAGER']);
    const currentUserName = this.accountService.getUser()?.name;
    const isDirectManager = _.includes(row.userPerformer, currentUserName);
    return isPowerUser || isDirectManager;
  }

  calculateStopTime(start: string, end: string): string {
    if (!start || !end) return '';

    const startTime = new Date(start).getTime();
    const endTime = new Date(end).getTime();

    const diffMs = endTime - startTime;
    if (diffMs <= 0) return '0 phút';

    const diffMinutes = Math.floor(diffMs / (1000 * 60));
    const hours = Math.floor(diffMinutes / 60);
    const minutes = diffMinutes % 60;

    if (hours > 0) {
      return `${hours} giờ ${minutes} phút`;
    }
    return `${minutes} phút`;
  }

  async checkDevice(data: any) {
    let planResult : any;
    if (Util.isEmpty(data.planResultId)) {
      const playload = { dateTest: data.dateTest, userTest: data.userPerformer, status: 1, planDetail: data.plan, statusRepair: 1 };
      const res = await firstValueFrom(
        this.planResultService.create(playload)
      );
      data.planResultId = res;
      data.id = res;
      data.plan.planResults.push(data);
      Object.assign(data, res);
      planResult = data;
    }else {
      planResult = _.find(data.plan.planResults, x => x.id === data.planResultId);
    }
    const childRef = this.dialogService.open(CheckDeviceDialog, {
      header: `Kiểm tra thiết bị`,
      width: '100%',
      modal: true,
      closable: true,
      data: { planResult: planResult, device: data.plan, plan: data.plan.plan },
    });
    childRef.onClose.subscribe((result) => {
      if (result) {
        this.loadPlan(this.model.qrCode);
      }
    });
  }

  repairError(row: any) {
    const ref = this.dialogService.open(RepairErrorDialog, {
      header: `Sửa lỗi - ${row.name} - ${row.severity ? 'Nghiêm trọng' : row.severity === 1 ? 'Bất thường' : 'Nhẹ'} - ${row.timeReported}`,
      width: 'auto',
      data: row,
      modal: true,
      closable: true
    });
    ref.onClose.subscribe((result) => {
      if (result) {
        this.loadHistoryError(this.model.id);
      }
    });
  }

  confirmError(event: any, row: ErrorReport) {
    Util.confirmAndExecute(
      event,
      'Bạn có chắc đã hoàn thành sửa chữa lỗi này?',
      () => {
        row.isRepaired = true;
        return this.errorReportService.update(row.id as number, row)
      },
      'Đã hoàn thành sửa chữa',
      'Lỗi khi hoàn thành',
      this.comfirmService,
      this.messageService,
      () => this.loadHistoryError(this.model.id)
    )
  }

  completeCheckDate(row: any, event: any) {
    Util.confirmAndExecute(
      event,
      'Bạn có chắc đã hoàn thành đợt kiểm tra này này?',
      () => {
        return this.planResultService.updateStatus(row.planResultId as number, 5);
      },
      'Đã hoàn thành đợt kiểm tra',
      'Lỗi khi hoàn thành',
      this.comfirmService,
      this.messageService,
      () => this.loadPlan(this.model.qrCode)
    )
  }

  addDayCheck() {
      const ref = this.dialogService.open(AddCheckListDialog, {
        header: `Thêm ngày kiểm tra`,
        width: 'auto',
        modal: true,
        closable: true,
        data: this.listPlanAudit || [],
      });
      ref.onClose.subscribe(async (result) => {
        if (result) {
          const res = await firstValueFrom(
            this.planResultService.create(result.plaload)
          );
          await this.loadPlan(this.model.qrCode)
          const newData = _.find(this.listPlanAudit, x => x.planResultId === res);
          this.checkDevice(newData);
        }
      });
  }

  dayOff(row: any, event: any) {
    Util.confirmAndExecute(
      event,
      'Bạn có chắc ngày này là ngày nghỉ?',
      () => {
        return this.planResultService.updateStatus(row.planResultId as number, 15);
      },
      'Đã chuyển ngày kiểm tra sang ngày nghỉ',
      'Lỗi khi chuyển ngày kiểm tra sang ngày nghỉ',
      this.comfirmService,
      this.messageService,
      () => this.loadPlan(this.model.qrCode)
    )
  }

}