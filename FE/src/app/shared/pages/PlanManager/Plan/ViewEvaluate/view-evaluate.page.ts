import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { BasePageComponent } from '../../../../base/base-page-component/base-page.component';
import { PlanDetailService } from '../Service/plan-detail.service';
import { SignatureService } from '../../../SystemManager/Signature/Service/signature.service';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { BaseApprovalComponent } from "../../../../base/base-approval-component/base-approval.component";
import { catchError, of } from 'rxjs';

// Danh sách các Ca kiểm tra mặc định
const DEFAULT_SESSIONS = ['Đầu ca', 'Giữa ca', 'Cuối ca', 'Hằng tuần', 'Ngày'];

// Định nghĩa mới: Kết quả cho một Ca kiểm tra cụ thể trong một ngày
interface DailySessionResult {
  session: string; // Tên Ca kiểm tra (Đầu ca, Giữa ca,...)
  results: string[]; // Mảng kết quả (O, A, X, //) cho ca này vào ngày này
}

// Định nghĩa mới: Kết quả cho tất cả các Ca kiểm tra trong một ngày
interface DayResults {
  day: number;
  sessionResults: DailySessionResult[]; // Mảng kết quả của 5 ca mặc định
}

// Định nghĩa lại Interface cho chi tiết công việc duy nhất
interface UniqueDetail {
  tt: number; // Số thứ tự
  criticalName: string;
  criticalCode: string;
  frequency: string;
  userTest: string; // Người thực hiện
  dailyResults: DayResults[]; // Thay đổi: Mảng kết quả cho 31 ngày (chứa các ca)
}

// Định nghĩa lại Interface cho nhóm công việc
interface GroupedCritical {
  criticalGroup: string;
  details: UniqueDetail[];
  rowspan: number; // Rowspan = Số chi tiết * Số ca (details.length * 5)
}

@Component({
  selector: 'view-evaluate',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './view-evaluate.page.html',
  styleUrls: ['./view-evaluate.page.scss'],
})
export class ViewEvaluatePage extends BasePageComponent<any> {

  public groupedDetails: GroupedCritical[] = [];
  public planInfo: any = {}; // Có thể dùng model.planDetail.createdAt để tính ngày tháng
  public signature: any = {};

  listUserApproval: any[] = [];
  listUserApprReport: any[] = [];
  listUserStatusAppr: any[] = [];
  listUsers: any[] = [];
  userMap: Record<string, string> = {};
  Math = Math;

  sampleReport: any = {};

  // Dùng để lặp qua 5 ca kiểm tra trong HTML
  public readonly DEFAULT_SESSIONS = DEFAULT_SESSIONS;

  constructor(
    protected override apiService: PlanDetailService,
    private signatureService: SignatureService,
    private sanitizer: DomSanitizer
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.sampleReport = JSON.parse(this.model.planDetail.detail);
    this.planInfo = this.model.planDetail;
    this.signatureService.getByUsername('admin').subscribe({
        next: (data) => {
            this.signature = data;
        },
        error: (err) => {
            console.error('Không tìm thấy chữ ký của người thực hiện:', err);
            this.signature = { imageLink: null }; // Đặt rỗng/null nếu lỗi
            this.groupPlanDetails();
        },
        complete: () => {
            // Sau khi cố gắng lấy chữ ký (dù thành công hay thất bại), gọi hàm chính
            this.groupPlanDetails();
        }
    });
    this.loadApprovals();
    this.approvalService.getUsers().subscribe(users => {
        this.listUsers = users;
        this.userMap = users.reduce((acc, u) => {
            const fullName = [u.firstName, u.lastName].filter(Boolean).join(' ').trim();
            acc[u.username] = fullName || u.username;
            return acc;
        }, {} as Record<string, string>);
        this.cdr.detectChanges();
    });
  }

  loadApprovals(): void {
    this.approvalService
      .findApprovalsByEntityIdAndEntityType(this.model.planDetail.plan.id, 'plans')
      .subscribe((data) => {
        this.listUserStatusAppr = data;
        const usernames = data.map(x => x.userApproval?.username);
        this.signatureService.getByListUsernames(usernames).pipe(
        catchError(err => {
          console.error('Lỗi lấy danh sách chữ ký:', err);
          return of([]); // Trả về mảng rỗng nếu lỗi
        })
      ).subscribe(signatures => {
          this.listUserApproval = Object.values(
            data.reduce((acc: any, item: any) => {
              const groupId = item.group?.groupApprovalName?.id;
              acc[groupId] ??= {
                groupApprovalName: item.group.groupApprovalName,
                items: [],
                userApprovals: []
              };
              acc[groupId].items.push(item);
              acc[groupId].userApprovals.push(item.userApproval);
              return acc;
            }, {})
          ).map((group: any) => ({
            ...group,
            userApprovals: group.userApprovals.map((u: any) => ({
              ...u,
              imageLink: signatures.find((s: any) => s.username === u.username)?.imageLink || null
            }))
          }));
          this.listUserApproval = this.listUserApproval.map(g => ({
            groupName: g.groupApprovalName.name,
            signatures: g.userApprovals.map((u: any) => u.imageLink)
          }));
          this.cdr.detectChanges();
        });
        this.cdr.detectChanges();
      });

    // Logic load listUserApprReport (giữ nguyên)
    this.approvalService
      .findApprovalsByEntityIdAndEntityType(this.sampleReport.approvalWorkflow.id, 'sample_reports')
      .subscribe((data) => {
        const usernames = data.map(x => x.userApproval?.username);
        this.signatureService.getByListUsernames(usernames).pipe(
        catchError(err => {
          console.error('Lỗi lấy danh sách chữ ký:', err);
          return of([]); // Trả về mảng rỗng nếu lỗi
        })
      ).subscribe(signatures => {
          this.listUserApprReport = Object.values(
            data.reduce((acc: any, item: any) => {
              const groupId = item.group?.groupApprovalName?.id;
              acc[groupId] ??= {
                groupApprovalName: item.group.groupApprovalName,
                items: [],
                userApprovals: []
              };
              acc[groupId].items.push(item);
              acc[groupId].userApprovals.push(item.userApproval);
              return acc;
            }, {})
          ).map((group: any) => ({
            ...group,
            userApprovals: group.userApprovals.map((u: any) => ({
              ...u,
              imageLink: signatures.find((s: any) => s.username === u.username)?.imageLink || null
            }))
          }));
          this.listUserApprReport = this.listUserApprReport.map(g => ({
            groupName: g.groupApprovalName.name,
            signatures: g.userApprovals.map((u: any) => u.imageLink)
          }));
          this.cdr.detectChanges();
        });
        this.cdr.detectChanges();
      });
  }

  /**
   * Nhóm các chi tiết kiểm tra theo Hạng mục chính và kết quả theo ngày (dateTest) và Ca kiểm tra (inspectionSession)
   */
  groupPlanDetails(): void {
    const details = this.model.planResultDetail || [];
    const uniqueGroups = new Map<string, Map<string, any[]>>();

    // 1. Nhóm dữ liệu: Group -> CriticalName -> Array of Results
    details.forEach((item: any) => {
      const groupName = (item.criticalGroup || '').replace(/"/g, '');
      const criticalName = item.criticalName || '';

      if (!uniqueGroups.has(groupName)) {
        uniqueGroups.set(groupName, new Map<string, any[]>());
      }
      const nameMap = uniqueGroups.get(groupName)!;

      if (!nameMap.has(criticalName)) {
        nameMap.set(criticalName, []);
      }
      nameMap.get(criticalName)?.push(item);
    });

    // 2. Chuyển Map thành mảng GroupedCritical và xử lý dailyResults
    let runningTT = 1;
    this.groupedDetails = Array.from(uniqueGroups.entries()).map(([groupName, nameMap]) => {
      const groupDetails: UniqueDetail[] = Array.from(nameMap.entries()).map(([criticalName, items]) => {

        // Khởi tạo mảng kết quả 31 ngày, mỗi ngày có 5 ca mặc định (chưa có kết quả)
        const dailyResults: DayResults[] = Array.from({ length: 31 }, (_, i) => ({
          day: i + 1,
          sessionResults: DEFAULT_SESSIONS.map(session => ({
            session: session,
            results: [] // Mặc định rỗng
          }))
        }));

        // Lấp đầy kết quả thực tế vào mảng dailyResults theo NGÀY VÀ CA
        items.forEach((res: any) => {
          const dateTest = res.planResult?.dateTest;
          const resultValue = this.mapResultToIcon(res.result);
          // Lấy Ca kiểm tra từ dữ liệu. Có thể cần chuẩn hóa nếu dữ liệu đầu vào không khớp hoàn toàn
          const inspectionSession = res.inspectionSession;

          if (dateTest && resultValue && resultValue !== '//' && this.model.planDetail?.createdAt) {
            const testDate = new Date(dateTest);
            const planDate = new Date(this.model.planDetail.createdAt);

            // Chỉ lấy kết quả nếu tháng & năm của dateTest khớp với tháng & năm của báo cáo
            if (testDate.getMonth() === planDate.getMonth() && testDate.getFullYear() === planDate.getFullYear()) {
              const day = testDate.getDate();

              const dayResult = dailyResults[day - 1];
              if (dayResult) {
                // Tìm ca kiểm tra tương ứng trong ngày đó (So khớp không phân biệt chữ hoa/thường)
                const sessionResult = dayResult.sessionResults.find(s =>
                  s.session.toLowerCase() === (inspectionSession || '').toLowerCase().trim()
                );

                // Nếu tìm thấy ca và kết quả chưa có trong ca đó
                // Lưu ý: Nếu inspectionSession rỗng/sai, nó sẽ KHÔNG được thêm vào kết quả
                if (sessionResult && !sessionResult.results.includes(resultValue)) {
                  sessionResult.results.push(resultValue);
                }
              }
            }
          }
        });

        // Lấy thông tin người thực hiện từ kết quả gần nhất
        const latestResult = items
          .slice()
          .sort((a: any, b: any) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())[0] || {};

        return {
          tt: runningTT++, // Tăng số thứ tự
          criticalName: criticalName,
          criticalCode: latestResult.criticalCode || '',
          frequency: latestResult.frequency || '',
          userTest: latestResult.createdBy || latestResult.planResult?.userTest || '',
          dailyResults: dailyResults,
        } as UniqueDetail;
      });

      return {
        criticalGroup: groupName,
        details: groupDetails,
        // Rowspan = Số chi tiết * 5 Ca kiểm tra
        rowspan: groupDetails.length * DEFAULT_SESSIONS.length,
      } as GroupedCritical;
    });

    this.cdr.detectChanges();
  }

  /**
 * Kiểm tra xem có bất kỳ chi tiết công việc nào có kết quả khác '//' trong ngày cụ thể.
 * @param day Số ngày trong tháng (1-31)
 * @returns HTML icon (chữ ký) nếu có kết quả và có ảnh, hoặc chữ "Đã ký" nếu có kết quả nhưng không có ảnh.
 */
  hasResultForDay(day: number): SafeHtml | '' {
    if (!this.groupedDetails || this.groupedDetails.length === 0) {
      return '';
    }

    // ... (Logic tìm kiếm 'found' giữ nguyên) ...
    const found = this.groupedDetails.some(group => {
      return group.details.some(detail => {
        const dayResult = detail.dailyResults.find(d => d.day === day);
        if (!dayResult) return false;

        return dayResult.sessionResults.some(sessionR =>
          sessionR.results.some(r => r && r !== '//')
        );
      });
    });

    if (found) {
      // Nếu tìm thấy kết quả trong ngày đó
      if (this.signature?.imageLink) {
        // Trường hợp 1: Có ảnh chữ ký -> Hiển thị ảnh
        return this.sanitizer.bypassSecurityTrustHtml(
          `<img src="${this.signature.imageLink}" style="width: 30px; height: 16px; transform: rotate(90deg);">`
        );
      } else {
        // Trường hợp 2: Không có ảnh chữ ký (hoặc signature là null/rỗng) -> Hiển thị chữ
        // Bạn có thể dùng thẻ div/span để căn giữa hoặc tạo kiểu nếu cần
        return this.sanitizer.bypassSecurityTrustHtml(
          `<span style="font-weight: bold;">Đã ký</span>`
          // Có thể thay bằng chữ cái (V) hoặc ký hiệu khác tùy yêu cầu
        );
      }
    }

    return '';
  }

  /**
   * Hàm trả về giá trị cho cột ngày (Dùng để hiển thị icon/ký tự)
   * Đã được cập nhật để nhận thêm tham số session.
   */
  getDailyResult(detail: UniqueDetail, day: number, session: string): string {
    const dayResult = detail.dailyResults.find(d => d.day === day);
    if (!dayResult) return '//';

    // Tìm kết quả của ca kiểm tra tương ứng trong ngày đó
    const sessionResult = dayResult.sessionResults.find(s => s.session === session);
    const results = sessionResult ? sessionResult.results : [];

    if (!results || results.length === 0) {
      return '//';
    }

    const icons = results.map((symbol: string) => {
      switch (symbol) {
        case 'O':
          return '<i class="far fa-circle"></i>';
        case 'A':
          return '<i class="fa-solid fa-circle-play fa-rotate-270"></i>';
        case 'X':
          return '<i class="fas fa-times"></i>';
        default:
          return '';
      }
    }).filter(Boolean);

    // Ghép các ký hiệu lại (nếu có nhiều kết quả trong cùng một ca/ngày)
    return icons.join(' ');
  }

  // Hàm mapResultToIcon và các hàm khác giữ nguyên...
  mapResultToIcon(result: string | null | undefined): string {
    if (!result) return '//';
    const value = String(result).toUpperCase();
    if (value === 'OK') {
      return 'O';
    } else if (value.includes('ADJUST') || value.includes('ĐIỀU CHỈNH') || value.includes('ĐÃ ĐIỀU CHỈNH')) {
      return 'A';
    } else if (value.includes('ERROR') || value.includes('BẤT THƯỜNG') || value.includes('CÓ BẤT THƯỜNG')) {
      return 'X';
    }
    return '//';
  }

  getDaysArray(): number[] {
    if (this.planInfo.planMonth && this.planInfo.planYear) {
      const numDays = new Date(this.planInfo.planYear, this.planInfo.planMonth, 0).getDate();
      return Array.from({ length: numDays }, (_, i) => i + 1);
    }
    return Array.from({ length: 31 }, (_, i) => i + 1);
  }

  printDiv() {
    const printContents = document.getElementById('print-section')?.innerHTML;
    if (!printContents) return;

    const popupWin = window.open('', '_blank', 'width=1024,height=768');

    popupWin!.document.open();
    popupWin!.document.write(`
      <html>
        <head>
          <title>In báo cáo</title>
          <style>
            /* Thêm các CSS cần thiết cho việc in ấn */
            table, th, td {
              border: 1px solid #000;
              border-collapse: collapse;
            }
            th, td {
              padding: 4px;
              text-align: center;
              vertical-align: middle;
            }
            img {
              max-width: 100%;
            }
          </style>
        </head>
        <body onload="window.print(); window.close();">
          ${printContents}
        </body>
      </html>
    `);
    popupWin!.document.close();
  }

  public override save(): void {
    throw new Error('Method not implemented.');
  }
}