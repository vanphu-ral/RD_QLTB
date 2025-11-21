import { Component, Input } from "@angular/core";
import { SharedModule } from "../../../share.module";


@Component({
    selector: 'app-base-approval',
    standalone: true,
    imports: [SharedModule],
    templateUrl: './base-approval.component.html',
    styleUrls: ['./base-approval.component.scss'],
})
export class BaseApprovalComponent {

    @Input() model: any;
    @Input() approvalModel: any;

    listApprovalStatus: any[] = [
        { label: 'Từ chối', value: 6 },
        { label: 'Duyệt', value: 3 },
    ];

    headers: string[] = ["Anh A", "Chị B", "Anh C"];
    values: boolean[][] = [
        [true, false, true],
        [false, true, false]
    ];
    tableRows: any[] = [];


    constructor() { }

    ngOnInit(): void {
        this.buildTable();
    }

    buildTable() {
        this.tableRows = this.values.map(row =>
            this.headers.reduce((acc: any, header, index) => {
                acc[header] = row[index];
                return acc;
            }, {})
        );
    }
}
