import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanResult } from "../../../../../models/PlanManger/plan-result.model";
import { CheckDeviceDialog } from "../check-device-dialog/check-device.dialog";
import { SupplyDetailService } from "../../../../DeviceManager/Supply/Service/supply-detail.service";
import { Device } from "../../../../../models/DeviceManager/device.model";
import { DeviceSupplyUseService } from "../../../../DeviceManager/Device/Service/device-supply-use.service";
import { ReplaceSupplyDialog } from "../replace-supply-dialog/replace-supply.dialog";
import { SupplyReplacement } from "../../../../../models/PlanManger/supply-replacement.model";
import { SupplyReplacementHistory } from "../../../../../models/PlanManger/supply-replace-history.model";
import { SupplyReplacementHistoryService } from "../../Service/supply-replace-history.service";
import { forkJoin } from "rxjs";

@Component({
    selector: 'app-supply-replacement-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './supply-replacement.dialog.html',
    styleUrls: ['./supply-replacement.dialog.scss'],
})
export class SupplyReplacementDialog {

    data: any;
    checkList: any[] = [];
    listSupplys: any[] = [];
    listSupplyReplace: SupplyReplacement[] = [];
    supplyReplaceHistory: SupplyReplacementHistory = new SupplyReplacementHistory();
    listSupplyReplaceHistory: SupplyReplacementHistory[] = [];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private dialogService: DialogService,
        private deviceSupplyUseService: DeviceSupplyUseService,
        private cdr: ChangeDetectorRef,
        private supplyRepplaceHistoryService: SupplyReplacementHistoryService
    ) {
        this.data = config.data;
    }

    ngOnInit() {
        this.loadData();
        console.log(this.data);
        
    }

    loadData() {
        const deviceId = _.get(this.data, 'device.deviceId') ?? _.get(this.data, 'device.id') ?? this.data.device;
        const planResultId = this.data?.supplyReplacement?.[0]?.planResult?.id ?? this.data?.planResultId ?? null;

        // gọi API vật tư thiết bị và lịch sử thay thế (nếu có planResultId)
        const obs1 = this.deviceSupplyUseService.getBySupplyId(deviceId);
        const obs2 = planResultId ? this.supplyRepplaceHistoryService.getByPlanResultId(planResultId) : null;

        if (obs2) {
            forkJoin([obs1, obs2]).subscribe({
                next: ([deviceSupplies, replaceHistories]: any) => {
                    this.listSupplys = deviceSupplies || [];
                    this.checkList = this.mergeSuppliesWithHistory(_.cloneDeep(this.listSupplys), replaceHistories || []);
                    this.cdr.detectChanges();
                },
                error: (err) => { console.error(err); }
            });
        } else {
            // không có planResultId => chỉ lấy danh sách vật tư thiết bị
            obs1.subscribe({
                next: (deviceSupplies: any) => {
                    this.listSupplys = deviceSupplies || [];
                    // nếu chưa có lịch sử thì checkList = listSupplys
                    this.checkList = _.cloneDeep(this.listSupplys);
                    this.cdr.detectChanges();
                }
            });
        }
    }

    /**
 * Merge logic:
 * - deviceSupplies: mảng DeviceSupplyUse { supply: {id,...}, quantityUsed, serial, ... }
 * - histories: mảng SupplyReplacementHistory
 * Assumptions:
 * - mỗi history có fields: oldSupply (object) hoặc old_supply_id, quantityOld/quantity_old,
 *   newSupply (object) hoặc new_supply_id, quantityChange/quantity_change, dateCheck/date_check
 * - nếu history chỉ có id, bạn có thể mở rộng bằng cách fetch supply info khi cần
 */
    private mergeSuppliesWithHistory(deviceSupplies: any[], histories: any[]) {
        // chuẩn hóa: sort histories theo thời gian (tăng dần)
        const sortedHist = _.orderBy(histories, [
            h => _.get(h, 'dateCheck') ?? _.get(h, 'date_check') ?? _.get(h, 'created_at')
        ], ['asc']);

        // map hiện tại theo supplyId
        const map: { [supplyId: string]: any } = {};

        deviceSupplies.forEach(item => {
            const sid = _.get(item, 'supply.id') ?? _.get(item, 'supply') ?? null;
            if (!sid) return;
            // nếu tồn tại nhiều mục cùng supply id, ta cộng dồn quantityUsed
            if (!map[sid]) {
                map[sid] = _.cloneDeep(item);
            } else {
                map[sid].quantityUsed = (map[sid].quantityUsed || 0) + (item.quantityUsed || 0);
            }
        });

        // áp dụng từng lịch sử
        sortedHist.forEach(h => {
            const oldId = _.get(h, 'oldSupply.id') ?? _.get(h, 'old_supply_id') ?? _.get(h, 'old_supply') ?? null;
            const newId = _.get(h, 'newSupply.id') ?? _.get(h, 'new_supply_id') ?? _.get(h, 'new_supply') ?? null;
            const qtyOld = _.get(h, 'quantityOld') ?? _.get(h, 'quantity_old') ?? 0;
            const qtyChange = _.get(h, 'quantityChange') ?? _.get(h, 'quantity_change') ?? 0;

            if (oldId) {
                if (map[oldId]) {
                    map[oldId].quantityUsed = (map[oldId].quantityUsed || 0) - qtyOld;
                    if ((map[oldId].quantityUsed || 0) <= 0) {
                        // remove nếu <= 0
                        delete map[oldId];
                    }
                } else {
                    // nếu bản ghi old không có trong map (có thể đã bị remove trước) — ignore hoặc log
                }
            }

            if (newId) {
                if (map[newId]) {
                    // cộng thêm số lượng mới
                    map[newId].quantityUsed = (map[newId].quantityUsed || 0) + qtyChange;
                } else {
                    // tạo mới mục cho new supply — lấy object supply từ history nếu có, nếu không có chỉ tạo skeleton
                    const supplyObj = _.get(h, 'newSupply') ?? { id: newId, name: _.get(h, 'newSupplyName') ?? 'Unknown' };
                    map[newId] = {
                        supply: supplyObj,
                        serial: null,
                        quantityUsed: qtyChange,
                        status: 1
                    };
                }
            }
        });

        // convert map -> array sắp xếp theo tên supply hoặc tùy bạn
        const result = Object.values(map);
        // nếu muốn sắp xếp
        return _.orderBy(result, [r => _.get(r, 'supply.name', '')], ['asc']);
    }



    addNewRow() {
    }

    replaceSupply(index: number) {
        this.supplyReplaceHistory = {
            quantityOld: this.checkList[index].quantityUsed,
            oldSupply: this.checkList[index].supply,
        }
        const ref = this.dialogService.open(ReplaceSupplyDialog, {
            header: 'Chọn thiết bị thay thế',
            width: '70%',
            modal: true,
            data: {
                supply: this.checkList[index],
                device: this.data.device
            }
        });
        ref.onClose.subscribe((result: any) => {
            if (result) {
                console.log(result);
                this.listSupplyReplace.push({
                    supply: result.serial,
                    quantity: result.quantityUsed,
                    note: result.description,
                    planResult: this.data.planResult
                });
                this.supplyReplaceHistory.quantityChange = result.quantityUsed;
                this.supplyReplaceHistory.newSupply = result.serial;
                this.supplyReplaceHistory.reason = result.description;
                this.listSupplyReplaceHistory.push(this.supplyReplaceHistory);
                this.checkList[index] = result
                this.checkList[index].serial = result.serial.serial;
                this.cdr.detectChanges();
            }
        });
    }

    submit() {
        this.ref.close({
            listSupplyReplace: this.listSupplyReplace,
            listSupplyReplaceHistory: this.listSupplyReplaceHistory
        });
    }

    close() {
        this.ref.close();
    }
}