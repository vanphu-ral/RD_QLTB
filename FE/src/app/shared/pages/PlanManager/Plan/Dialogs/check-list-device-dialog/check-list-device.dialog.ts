import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanResult } from "../../../../../models/PlanManger/plan-result.model";
import { CheckDeviceDialog } from "../check-device-dialog/check-device.dialog";
import { PlanResultService } from "../../Service/plan-result.service";
import { ConfirmationService, MessageService } from "primeng/api";
import { PLANTYPE } from "../../../../../enums/plan-type.enum";
import { DayOffCalendarService } from "../../../../Categories/DayOffCalendar/Service/day-off-calendar.service";

@Component({
    selector: 'app-check-list-device-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './check-list-device.dialog.html',
    styleUrls: ['./check-list-device.dialog.scss'],
})
export class CheckListDeviceDialog {

    data: any;
    plan: any;
    checkList: PlanResult[] = [];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private dialogService: DialogService,
        private planResultService: PlanResultService,
        private comfirmService: ConfirmationService,
        private messageService: MessageService,
        private dayOffService: DayOffCalendarService,
        private cdr: ChangeDetectorRef,
    ) {
        this.data = config.data.planDetail;
        this.plan = config.data.plan;
        console.log(this.data);
        console.log(this.plan);

    }

    ngOnInit() {
        this.loadDeviceCheckList();
    }

    loadDeviceCheckList() {
        this.planResultService.getByPlanDetailId(this.data.id).subscribe((res) => {
            res.map((item: any) => {
                item.userTest = JSON.parse(item.userTest);
            });
            if (this.plan.planType.code == PLANTYPE.DAILYCHECK) {
                const today = new Date();
                today.setHours(0, 0, 0, 0);
                this.checkList = res.filter((item: any) => {
                    const testDate = new Date(item.dateTest);
                    const compareDate = new Date(testDate.getTime());
                    compareDate.setHours(0, 0, 0, 0);
                    return compareDate <= today;
                });
                this.checkDayOff();
            }else {
                this.checkList = res;
            }
            console.log(res);
            
            this.cdr.detectChanges();
        });
    }

    checkDayOff() {
        this.dayOffService.getByTeam(this.plan.team.id).subscribe((res) => {
            console.log(res);
            const planResults = this.checkList || []; 
            const dayOffList = res || [];

            planResults.forEach((plan: any) => {
                const testDate = new Date(plan.dateTest);
                testDate.setHours(0, 0, 0, 0);
                const isHoliday = dayOffList.some(off => {
                    const from = new Date(off.fromDate);
                    from.setHours(0, 0, 0, 0);
                    const to = new Date(off.toDate);
                    to.setHours(0, 0, 0, 0);
                    return testDate >= from && testDate <= to;
                });
                if (isHoliday && plan.status !== 15) {
                    this.planResultService.updateStatus(plan.id as number, 15).subscribe(() => {
                        this.loadDeviceCheckList();
                    });
                }
            });
        });
    }



    addNewRow() {
        this.checkList.push({ userTest: this.data.manager, status: 1, planDetail: this.data, dateTest: new Date(), statusRepair: 1 });
    }

    deleteRow(index: number) {
        if (this.checkList[index].id) {
            this.planResultService.delete(this.checkList[index].id).subscribe(() => { this.loadDeviceCheckList(); });
        } else {
            this.checkList.splice(index, 1);
        }
    }

    statusToString(status: number) {
        return Util.statusToString(status);
    }

    getSeverity(status: number): string {
        return Util.statusToSeverity(status);
    }

    saveDeviceCheckDate(row: any) {
        row = Util.simplifyMany(row, ['planDetail']);
        row.userTest = JSON.stringify(row.userTest);
        if (Util.isEmpty(row.id)) {
            this.planResultService.create(row).subscribe((res) => {
                Object.assign(row, res);
                this.loadDeviceCheckList();
            });
        } else {
            this.planResultService.update(row.id, row).subscribe((res) => {
                Object.assign(row, res);
                this.loadDeviceCheckList();
            });
        }
    }

    completeCheckDate(row: any, event: any) {
        Util.confirmAndExecute(
            event,
            'Bạn có chắc đã hoàn thành đợt kiểm tra này này?',
            () => {
                return this.planResultService.updateStatus(row.id as number, 5);
            },
            'Đã hoàn thành đợt kiểm tra',
            'Lỗi khi hoàn thành',
            this.comfirmService,
            this.messageService,
            () => this.loadDeviceCheckList()
        )
    }

    checkDevice(data: any) {
        const childRef = this.dialogService.open(CheckDeviceDialog, {
            header: `Kiểm tra thiết bị`,
            width: '100%',
            modal: true,
            closable: true,
            data: { planResult: data, device: this.data, plan: this.plan },
        });
        childRef.onClose.subscribe((result) => {
            if (result && result.length > 0) {
            }
        });
    }


    close() {
        this.ref.close();
    }
}