import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { SharedModule } from '../../../../../../share.module';
import { Util } from '../../../../../core/utils/utils-function';
import { DeviceSupplyUse } from '../../../../../models/DeviceManager/device-supply-use.model';
import { SupplyService } from '../../../Supply/Service/supply.service';
import { SupplyDetailService } from '../../../Supply/Service/supply-detail.service';
import { DeviceSupplyUseService } from '../../Service/device-supply-use.service';
import _ from 'lodash';
import { map } from 'rxjs/operators';
import { forkJoin } from 'rxjs';

// Cần tạo một interface/type cho dữ liệu dòng để dễ quản lý hơn,
// hoặc mở rộng DeviceSupplyUse để bao gồm supply và serial được chọn
interface MaterialRow extends DeviceSupplyUse {
    // Thêm trường supply để lưu trữ đối tượng Supply được chọn
    supply?: any;
    // row.serial trong HTML đang lưu trữ SupplyDetail
    serial?: any;
}


@Component({
    selector: 'app-material-list-manager-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './material-list-manager.dialog.html',
    styleUrls: ['./material-list-manager.dialog.scss'],
})
export class MaterialListManagerDialogComponent {
    data: any;
    // Sử dụng interface MaterialRow đã mở rộng
    listMaterial: MaterialRow[] = [];
    listSupply: any[] = []
    // serialOptions[index] chứa danh sách SupplyDetail cho hàng index
    serialOptions: { [key: number]: any[] } = {}

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        public supplyService: SupplyService,
        public supplyDetailService: SupplyDetailService,
        public deviceSupplyUseService: DeviceSupplyUseService,
        private cdr: ChangeDetectorRef
    ) {
        this.data = config.data;
    }

    ngOnInit() {
        this.loadData();
    }

    loadData() {
        // 1. Lấy danh sách Vật tư (Supply) trước
        const loadSupply$ = this.supplyService.getAll().pipe(
            map(res => {
                this.listSupply = res;
                return res;
            })
        );

        // 2. Lấy danh sách DeviceSupplyUse và Supply cùng lúc
        // Sử dụng forkJoin để đảm bảo có listSupply trước khi xử lý listMaterial
        forkJoin([
            this.deviceSupplyUseService.getBySupplyId(_.get(this.data, 'id')),
            loadSupply$
        ]).subscribe({
            next: ([deviceSupplyUseList, supplies]) => {
                
                // --- Xử lý listMaterial ---
                if (Util.isEmptyArray(deviceSupplyUseList)) {
                    this.listMaterial = [{ status: 1 }];
                } else {
                    // Ánh xạ dữ liệu để gán đối tượng supply và serial
                    this.listMaterial = deviceSupplyUseList.map(item => {
                        
                        // Lấy Supply ID từ supplyDetail.supply (có thể là object hoặc ID)
                        const rawSupplyId = _.get(item, 'supplyDetail.supply.id') ?? _.get(item, 'supplyDetail.supply') ?? null;
                        
                        // TÌM ĐỐI TƯỢNG SUPPLY: Tìm đối tượng Supply hoàn chỉnh trong listSupply
                        const selectedSupply = supplies.find(s => s.id === rawSupplyId);
                        
                        // Gán đối tượng Supply vào row.supply
                        const materialRow: MaterialRow = {
                            ...item,
                            supply: selectedSupply,
                            serial: _.get(item, 'supplyDetail') ?? null // Tạm thời gán supplyDetail (là đối tượng) vào serial
                        };
                        return materialRow;
                    });
                }
                
                // --- Xử lý Serial Options (Phải load sau khi có supply) ---
                const serialLoadObservables = this.listMaterial.map((item, index) => {
                    const supplyId = _.get(item, 'supply.id');
                    if (supplyId) {
                        // Trả về Observable của việc lấy danh sách serial
                        return this.supplyDetailService.getBySupplyId(supplyId).pipe(
                            map(serialList => ({ index, serialList }))
                        );
                    }
                    return null; // Không cần load
                }).filter(obs => obs !== null);
                
                // Chờ tất cả các serial options được load
                if (serialLoadObservables.length > 0) {
                    forkJoin(serialLoadObservables).subscribe({
                        next: (results) => {
                            results.forEach(result => {
                                this.serialOptions[result.index] = result.serialList;
                                
                                // BƯỚC QUAN TRỌNG: TÌM ĐỐI TƯỢNG SERIAL KHỚP THAM CHIẾU
                                const row = this.listMaterial[result.index];
                                const currentSupplyDetailId = _.get(row, 'serial.id');
                                if (currentSupplyDetailId) {
                                    // Tìm đối tượng SupplyDetail (serial) trong danh sách vừa load
                                    row.serial = result.serialList.find(sd => sd.id === currentSupplyDetailId) ?? null;
                                }
                            });
                            console.log('serialOptions đã được load và gán giá trị.');
                            this.cdr.detectChanges();
                        },
                        error: (err) => {
                            console.error("Lỗi khi load serial options:", err);
                            this.cdr.detectChanges();
                        }
                    });
                } else {
                    this.cdr.detectChanges();
                }
            },
            error: (err) => {
                console.error("Lỗi khi load dữ liệu ban đầu:", err);
                this.cdr.detectChanges();
            }
        });
    }

    // Hàm này được gọi khi thay đổi Vật tư (Supply)
    onChangeSupply(event: any, index: number) {
        const supplyId = _.get(event, 'value.id') ?? event.value;
        
        // Reset Serial cho hàng hiện tại ngay lập tức
        this.listMaterial[index].serial = null; 
        this.serialOptions[index] = []; // Xóa danh sách serial cũ
        
        if (!supplyId) {
            this.cdr.detectChanges();
            return;
        }

        this.supplyDetailService.getBySupplyId(supplyId).subscribe({
            next: (res) => {
                this.serialOptions[index] = res;
                this.cdr.detectChanges();
            },
            error: (err) => {
                console.error("Lỗi khi load serial list:", err);
            }
        });
    }

    addNewRow() {
        this.listMaterial.push({ status: 1 });
    }

    deleteRow(index: number) {
        if (this.listMaterial[index].id) {
            this.deviceSupplyUseService.delete(this.listMaterial[index].id as number).subscribe({
                next: () => {
                    this.listMaterial.splice(index, 1);
                    // Cần xóa cả serialOptions của hàng này
                    delete this.serialOptions[index];
                    // Cần load lại để cập nhật serialOptions[i] của các hàng còn lại
                    // HOẶC: Thay vì loadData(), chỉ cần cập nhật lại index của serialOptions
                    // Do logic deleteRow đã có splice, loadData() là cách an toàn hơn, nhưng
                    // cách tối ưu hơn là chỉ cần splice và cập nhật index.
                    this.listMaterial.splice(index, 1);
                    this.cdr.detectChanges();
                },
                error: (err) => {
                    // Xử lý lỗi nếu cần
                    console.error("Delete failed", err);
                }
            });
        } else {
            this.listMaterial.splice(index, 1);
            // Xóa serialOptions của hàng vừa xóa
            delete this.serialOptions[index];
        }
    }


    close() {
        this.ref.close();
    }

    submit() {
        console.log(this.listMaterial);

        // Map dữ liệu về định dạng DeviceSupplyUse chuẩn để gửi đi
        const materialListToSubmit = this.listMaterial.map(item => {
            // Lấy ID của SupplyDetail (được lưu trong item.serial)
            const supplyDetailId = _.get(item, 'serial.id');

            // Kiểm tra và loại bỏ các hàng không hợp lệ (ví dụ: chưa chọn Serial)
            if (!supplyDetailId) {
                // Tùy theo yêu cầu backend: có thể loại bỏ hoặc báo lỗi
                // Trong ví dụ này, tôi sẽ yêu cầu phải có supplyDetailId nếu muốn lưu
                // Hoặc bạn có thể bổ sung validate trước.
                console.warn(`Hàng vật tư ${item.id} không có Supply Detail ID. Bị bỏ qua.`);
                return null;
            }

            return {
                id: item.id || undefined,
                usageDate: _.get(item, 'usageDate'),
                quantityUsed: _.get(item, 'quantityUsed'),
                description: _.get(item, 'description'),
                status: _.get(item, 'status'),
                // Gửi SupplyDetail object/ID
                supplyDetail: {
                    id: supplyDetailId // ID của SupplyDetail (Mã serial)
                },
                // Giữ nguyên các trường khác nếu có
            } as Partial<DeviceSupplyUse>; // Sử dụng Partial vì các trường khác có thể chưa đủ
        }).filter(item => item !== null); // Lọc bỏ các hàng null

        this.ref.close(materialListToSubmit);
    }
}