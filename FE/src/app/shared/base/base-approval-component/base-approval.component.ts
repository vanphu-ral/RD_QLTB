import { Component, Input, OnChanges, SimpleChanges, ChangeDetectionStrategy, ChangeDetectorRef } from "@angular/core";
import { SharedModule } from "../../../share.module";
import { Util } from "../../core/utils/utils-function";
import { ApprovalService } from "../../pages/ApprovalManager/Approval/Service/approval.service";
import { DataService } from "../../service/send-data.service";
import { Subscription } from "rxjs";

@Component({
    selector: 'app-base-approval',
    standalone: true,
    imports: [SharedModule],
    templateUrl: './base-approval.component.html',
    styleUrls: ['./base-approval.component.scss'],
})
export class BaseApprovalComponent {

    @Input() approvalModel: any = { note: '', status: null };
    listUserApprover: any[] = [];

    subscription: Subscription = new Subscription();

    listApprovalStatus: any[] = [
        { label: 'Từ chối', value: 6 },
        { label: 'Duyệt', value: 3 },
    ];

    listUsers: any[] = [];
    userMap: Record<string, string> = {};

    constructor(private approvalService: ApprovalService, private cdr: ChangeDetectorRef, private dataService: DataService) { }

    ngOnInit(): void {
        this.approvalService.getUsers().subscribe(users => {
            this.listUsers = users;
            this.userMap = users.reduce((acc, u) => {
                const fullName = [u.firstName, u.lastName].filter(Boolean).join(' ').trim();
                acc[u.username] = fullName || u.username; 
                return acc;
            }, {} as Record<string, string>);
            this.cdr.detectChanges();
        });
        this.subscription = this.dataService.data$.subscribe(data => {
            this.listUserApprover = data;
        })
    }
}
