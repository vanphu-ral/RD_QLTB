import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { SharedModule } from "../../../../../share.module";
import { DeviceService } from "../../../DeviceManager/Device/Service/device.service";

@Component({
    standalone: true,
    selector: 'app-list-device-maintance-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './list-device-maintance.dialog.html',
    styleUrls: ['./list-device-maintance.dialog.scss'],
})
export class ListDeviceMaintanceDialog {

    data: any[] = [];
    listDeviceMaintanceDue: any[] = [];
    ListDeviceMaintanced: any[] = []

    // Page
    loading: boolean = false;
    page: number = 0;
    size: number = 10;
    totalRecords: number = 0;
    selectedPageSize: number = 10;
    pageSizeOptions: number[] = [5, 10, 20, 30, 50, 100];
    expandedRows = {};
    totalItems = 0;
    currentPage = 0;
    filters: any = {};

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private cdr: ChangeDetectorRef,
        private deviceService: DeviceService
    ) {

    }

    ngOnInit() {
        this.deviceService.getMaintainListPaged(0, {}).subscribe({
            next: (res) => {
                console.log(res);

                // this.listDeviceMaintanceDue = res.items.filter((item: any) => !item.isMaintained);
                // this.ListDeviceMaintanced = res.items.filter((item: any) => item.isMaintained);
            }
        })
    }

    loadData() {
        this.loading = true;
        this.deviceService.getMaintainListPaged(this.page, this.filters).subscribe({
            next: (res) => {
                console.log(res);
                this.data = res.content;
                // this.plans = res.content.map((item: any) => ({
                //     ...item,
                //     details: []
                // }));
                this.totalRecords = res.totalElements;
                this.loading = false;
                this.cdr.detectChanges();
            },
            error: () => {
                this.loading = false;
            }
        });
    }

    onPageChange(event: any) {
        this.page = event.first / event.rows;  // compute page index
        this.size = event.rows;
        this.loadData();
    }

    close() {
        this.ref.close();
    }

    submit() {
        this.ref.close(true);
    }
}