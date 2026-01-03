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
  results: { value: string, time: string }[]; // Mảng kết quả (O, A, X, //) cho ca này vào ngày này
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
  performer: string; // Người thực hiện
  dailyResults: DayResults[]; // Thay đổi: Mảng kết quả cho 31 ngày (chứa các ca)
}

// Định nghĩa lại Interface cho nhóm công việc
interface GroupedCritical {
  criticalGroup: string;
  details: UniqueDetail[];
  rowspan: number; // Rowspan = Số chi tiết * Số ca (details.length * 5)
}

const ICON_SVG = {
  O: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" style="width:14px;height:14px;fill:none;stroke:black;stroke-width:40"><circle cx="256" cy="256" r="192"/></svg>`,
  X: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512" style="width:12px;height:12px;fill:black"><path d="M342.6 150.6c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0L192 210.7 86.6 105.4c-12.5-12.5-32.8-12.5-45.3 0s-12.5 32.8 0 45.3L146.7 256 41.4 361.4c-12.5 12.5-12.5 32.8 0 45.3s32.8 12.5 45.3 0L192 301.3 297.4 406.6c12.5 12.5 32.8 12.5 45.3 0s12.5-32.8 0-45.3L237.3 256 342.6 150.6z"/></svg>`,
  A: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" style="width:14px;height:14px;fill:black;transform:rotate(270deg)"><path d="M0 256a256 256 0 1 1 512 0A256 256 0 1 1 0 256zM188.3 147.1c-7.6 4.2-12.3 12.3-12.3 20.9l0 176c0 8.7 4.7 16.7 12.3 20.9s17.1 3.3 23.7-2.4l128-112c5.9-5.2 9.3-12.7 9.3-20.5s-3.4-15.3-9.3-20.5l-128-112c-6.5-5.7-16.1-6.6-23.7-2.4z"/></svg>`
};

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
      .findApprovalsByEntityIdAndEntityType(this.sampleReport.id, 'sample_reports')
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
  // groupPlanDetails(): void {
  //   const details = this.model.planResultDetail || [];
  //   const uniqueGroups = new Map<string, Map<string, any[]>>();

  //   // 1. Nhóm dữ liệu: Group -> CriticalName -> Array of Results
  //   details.forEach((item: any) => {
  //     const groupName = (item.criticalGroup || '').replace(/"/g, '');
  //     const criticalName = item.criticalName || '';

  //     if (!uniqueGroups.has(groupName)) {
  //       uniqueGroups.set(groupName, new Map<string, any[]>());
  //     }
  //     const nameMap = uniqueGroups.get(groupName)!;

  //     if (!nameMap.has(criticalName)) {
  //       nameMap.set(criticalName, []);
  //     }
  //     nameMap.get(criticalName)?.push(item);
  //   });

  //   // 2. Chuyển Map thành mảng GroupedCritical và xử lý dailyResults
  //   let runningTT = 1;
  //   this.groupedDetails = Array.from(uniqueGroups.entries()).map(([groupName, nameMap]) => {
  //     const groupDetails: UniqueDetail[] = Array.from(nameMap.entries()).map(([criticalName, items]) => {

  //       // Khởi tạo mảng kết quả 31 ngày, mỗi ngày có 5 ca mặc định (chưa có kết quả)
  //       const dailyResults: DayResults[] = Array.from({ length: 31 }, (_, i) => ({
  //         day: i + 1,
  //         sessionResults: DEFAULT_SESSIONS.map(session => ({
  //           session: session,
  //           results: [] // Mặc định rỗng
  //         }))
  //       }));

  //       // Lấp đầy kết quả thực tế vào mảng dailyResults theo NGÀY VÀ CA
  //       items.forEach((res: any) => {
  //         const dateTest = res.planResult?.dateTest;
  //         const resultValue = this.mapResultToIcon(res.result);
  //         const inspectionSession = res.inspectionSession;
  //         const examinationTime = res.examinationTime; // Lấy thông tin Ca 1, Ca 2, Ngày

  //         if (dateTest && resultValue && resultValue !== '//' && this.model.planDetail?.createdAt) {
  //           const testDate = new Date(dateTest);
  //           const planDate = new Date(this.model.planDetail.createdAt);

  //           if (testDate.getMonth() === planDate.getMonth() && testDate.getFullYear() === planDate.getFullYear()) {
  //             const day = testDate.getDate();
  //             const dayResult = dailyResults[day - 1];

  //             if (dayResult) {
  //               const sessionResult = dayResult.sessionResults.find(s =>
  //                 s.session.toLowerCase() === (inspectionSession || '').toLowerCase().trim()
  //               );

  //               if (sessionResult) {
  //                 // LƯU Ý QUAN TRỌNG: Đẩy object vào thay vì string
  //                 sessionResult.results.push({
  //                   value: resultValue,
  //                   time: examinationTime || ''
  //                 });
  //               }
  //             }
  //           }
  //         }
  //       });

  //       // Lấy thông tin người thực hiện từ kết quả gần nhất
  //       const latestResult = items
  //         .slice()
  //         .sort((a: any, b: any) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())[0] || {};

  //       return {
  //         tt: runningTT++, // Tăng số thứ tự
  //         criticalName: criticalName,
  //         criticalCode: latestResult.criticalCode || '',
  //         frequency: latestResult.frequency || '',
  //         userTest: latestResult.createdBy || latestResult.planResult?.userTest || '',
  //         dailyResults: dailyResults,
  //       } as UniqueDetail;
  //     });

  //     return {
  //       criticalGroup: groupName,
  //       details: groupDetails,
  //       // Rowspan = Số chi tiết * 5 Ca kiểm tra
  //       rowspan: groupDetails.length * DEFAULT_SESSIONS.length,
  //     } as GroupedCritical;
  //   });

  //   this.cdr.detectChanges();
  // }

  groupPlanDetails(): void {
    // 1. Kiểm tra nguồn dữ liệu chính
    const results = this.model.planResultDetail || [];
    const uniqueGroups = new Map<string, Map<string, any[]>>();

    if (results.length > 0) {
      // --- TRƯỜNG HỢP CÓ DỮ LIỆU KIỂM TRA (GIỮ NGUYÊN LOGIC CŨ) ---
      results.forEach((item: any) => {
        const groupName = (item.criticalGroup || '').replace(/"/g, '');
        const criticalName = item.criticalName || '';
        if (!uniqueGroups.has(groupName)) uniqueGroups.set(groupName, new Map<string, any[]>());
        const nameMap = uniqueGroups.get(groupName)!;
        if (!nameMap.has(criticalName)) nameMap.set(criticalName, []);
        nameMap.get(criticalName)?.push(item);
      });
    } else {
      // --- TRƯỜNG HỢP CHƯA CÓ DỮ LIỆU (LẤY TỪ SAMPLE REPORT) ---
      const mappings = this.sampleReport?.sampleReportKeyMappings || [];
      mappings.forEach((mapping: any) => {
        const criteria = mapping.criterial;
        const groupName = criteria?.criterialGroup?.name || 'Khác';
        const criticalName = criteria?.name || '';
        const frequency = mapping?.frequency || 'Ngày';
        const performer = mapping?.performer || null;

        if (!uniqueGroups.has(groupName)) {
          uniqueGroups.set(groupName, new Map<string, any[]>());
        }
        const nameMap = uniqueGroups.get(groupName)!;

        if (!nameMap.has(criticalName)) {
          // Tạo một object giả lập cấu trúc của ResultDetail để hàm map bên dưới không lỗi
          nameMap.set(criticalName, [{
            criticalGroup: groupName,
            criticalName: criticalName,
            criticalCode: criteria?.code,
            frequency: frequency || '', // Bạn có thể thêm field frequency vào criteria nếu có
            performer: performer,
            isPlaceholder: true // Đánh dấu đây là dữ liệu mẫu
          }]);
        }
      });
    }

    // 2. Chuyển Map thành mảng GroupedCritical (Phần này giữ nguyên cấu trúc nhưng tinh chỉnh xử lý items)
    let runningTT = 1;
    this.groupedDetails = Array.from(uniqueGroups.entries()).map(([groupName, nameMap]) => {
      const groupDetails: UniqueDetail[] = Array.from(nameMap.entries()).map(([criticalName, items]) => {

        const dailyResults: DayResults[] = Array.from({ length: 31 }, (_, i) => ({
          day: i + 1,
          sessionResults: DEFAULT_SESSIONS.map(session => ({
            session: session,
            results: []
          }))
        }));

        // Chỉ lấp đầy kết quả nếu không phải là dữ liệu placeholder
        if (!items[0]?.isPlaceholder) {
          items.forEach((res: any) => {
            const dateTest = res.planResult?.dateTest;
            const resultValue = this.mapResultToIcon(res.result);
            const inspectionSession = res.inspectionSession;
            const examinationTime = res.examinationTime;

            if (dateTest && resultValue && resultValue !== '//' && this.model.planDetail?.createdAt) {
              const testDate = new Date(dateTest);
              const planDate = new Date(this.model.planDetail.createdAt);

              if (testDate.getMonth() === planDate.getMonth() && testDate.getFullYear() === planDate.getFullYear()) {
                const day = testDate.getDate();
                const dayResult = dailyResults[day - 1];
                if (dayResult) {
                  const sessionResult = dayResult.sessionResults.find(s =>
                    s.session.toLowerCase() === (inspectionSession || '').toLowerCase().trim()
                  );
                  if (sessionResult) {
                    sessionResult.results.push({ value: resultValue, time: examinationTime || '' });
                  }
                }
              }
            }
          });
        }

        const latestResult = items[0] || {};

        return {
          tt: runningTT++,
          criticalName: criticalName,
          criticalCode: latestResult.criticalCode || '',
          frequency: latestResult.frequency || '',
          performer: latestResult.performer || '',
          dailyResults: dailyResults,
        } as UniqueDetail;
      });

      return {
        criticalGroup: groupName,
        details: groupDetails,
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
    if (!this.groupedDetails || this.groupedDetails.length === 0) return '';

    const found = this.groupedDetails.some(group =>
      group.details.some(detail => {
        const dayResult = detail.dailyResults.find(d => d.day === day);
        if (!dayResult) return false;
        // Kiểm tra trong mảng Object results
        return dayResult.sessionResults.some(sessionR =>
          sessionR.results.some(r => r.value && r.value !== '//')
        );
      })
    );

    if (found) {
      if (this.signature?.imageLink) {
        return this.sanitizer.bypassSecurityTrustHtml(
          `<img src="${this.signature.imageLink}" style="width: 30px; height: 16px; transform: rotate(90deg);">`
        );
      } else {
        return this.sanitizer.bypassSecurityTrustHtml(`<span style="font-weight: bold;">Đã ký</span>`);
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

    const sessionResult = dayResult.sessionResults.find(s => s.session === session);
    const results = sessionResult ? sessionResult.results : [];

    if (!results || results.length === 0) return '//';

    const baseUrl = window.location.origin;

    // const icons = results.map((res: any) => {
    //   // Chuẩn hóa tên ca để so sánh: "Ca 1" -> "ca1", "Ca 2" -> "ca2"
    //   const timeKey = (res.time || '').toLowerCase().replace(/\s/g, '');

    //   // Logic 1: Nếu là Ca 1 hoặc Ca 2 -> Trả về ảnh
    //   if (timeKey === 'ca1' || timeKey === 'ca2') {
    //     let statusSlug = '';
    //     if (res.value === 'O') statusSlug = 'oke';
    //     else if (res.value === 'A') statusSlug = 'edit';
    //     else if (res.value === 'X') statusSlug = 'error';

    //     if (statusSlug) {
    //       // Đường dẫn: assets/imgs/ca1-oke.jpg, ca2-error.jpg...
    //       return `<img src="assets/icon/${timeKey}-${statusSlug}.svg" class="icon-img" alt="${timeKey}-${statusSlug}"/>`;
    //     }
    //   }

    //   // Logic 2: Nếu không phải Ca 1/Ca 2 (ví dụ: "Ngày") -> Giữ nguyên icon FontAwesome
    //   switch (res.value) {
    //     case 'O':
    //       return '<i class="far fa-circle"></i>';
    //     case 'A':
    //       return '<i class="fa-solid fa-circle-play fa-rotate-270"></i>';
    //     case 'X':
    //       return '<i class="fas fa-times"></i>';
    //     default:
    //       return '';
    //   }
    // }).filter(Boolean);

    const icons = results.map((res: any) => {
      const timeKey = (res.time || '').toLowerCase().replace(/\s/g, '');
      if (timeKey === 'ca1' || timeKey === 'ca2') {
        let statusSlug = '';
        if (res.value === 'O') statusSlug = 'oke';
        else if (res.value === 'A') statusSlug = 'edit';
        else if (res.value === 'X') statusSlug = 'error';

        if (statusSlug) {
          // Thêm baseUrl vào trước đường dẫn
          return `<img src="${baseUrl}/assets/icon/${timeKey}-${statusSlug}.svg" class="icon-img" style="width:16px; height:16px;" />`;
        }
      }
      // ... (logic icon font-awesome giữ nguyên)
      switch (res.value) {
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

  /**
 * Kiểm tra xem ngày cụ thể có phát sinh sửa chữa (isRepaired = true) không
 * Dựa trên mảng errorReport trong sampleReport
 */
  getRepairStatusForDay(day: number): SafeHtml | string {
    if (!this.model.errorReport || !Array.isArray(this.model.errorReport)) {
      return '';
    }

    // Lấy tháng/năm hiện tại từ model
    const planDate = new Date(this.model.planDetail.createdAt);
    const month = planDate.getMonth();
    const year = planDate.getFullYear();

    // Tìm trong danh sách lỗi xem có mục nào ngày đó đã sửa xong không
    const repaired = this.model.errorReport.find((err: any) => {
      const errorDate = new Date(err.timeReported);
      return errorDate.getDate() === day &&
        errorDate.getMonth() === month &&
        errorDate.getFullYear() === year &&
        err.isRepaired === true;
    });

    if (repaired) {
      // Trả về icon màu xanh báo hiệu đã sửa xong
      return this.sanitizer.bypassSecurityTrustHtml(
        '<i class="fa-solid fa-wrench" style="color: red;" title="Đã sửa chữa"></i>'
      );
    }
    return '';
  }

  printDiv() {
    const printContents = document.getElementById('print-section')?.innerHTML;
    if (!printContents) return;

    const popupWin = window.open('', '_blank', 'width=1200,height=800');

    // 1. Lấy tất cả link CSS (bao gồm FontAwesome, PrimeIcons, Bootstrap...)
    const styles = Array.from(document.querySelectorAll('link[rel="stylesheet"], style'))
      .map(s => s.outerHTML)
      .join('');

    popupWin!.document.open();
    popupWin!.document.write(`
    <html>
      <head>
        <title>In báo cáo</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        ${styles} 
        <style>
          /* Đảm bảo icon và màu sắc hiển thị khi in */
          * {
            -webkit-print-color-adjust: exact !important;
            print-color-adjust: exact !important;
          }
          th, td { border: 1px solid black !important; }
          img {
            max-width: 100%;
            -webkit-print-color-adjust: exact; /* Ép trình duyệt in màu/ảnh nền */
          }
          .icon-img {
            width: 20px !important; /* Đảm bảo icon không bị quá to hoặc mất kích thước */
            height: auto;
            display: block;
            margin: 0 auto;
          }

          @media print {
            @page { size: A3 landscape; margin: 5mm; }
            .no-print { display: none; }
          }
        </style>
      </head>
      <body>
        ${printContents}
        <script>
          async function prepareAndPrint() {
            // Chờ ảnh load
            const imgs = Array.from(document.images);
            await Promise.all(imgs.map(img => {
              if (img.complete) return Promise.resolve();
              return new Promise(resolve => { img.onload = resolve; img.onerror = resolve; });
            }));

            // Chờ Font load (FontAwesome)
            if (document.fonts) {
              await document.fonts.ready;
            }

            setTimeout(() => {
              window.print();
              window.close();
            }, 1000); // Tăng delay lên 1s để chắc chắn font icon đã render
          }
          prepareAndPrint();
        </script>
      </body>
    </html>
  `);
    popupWin!.document.close();
  }

  public override save(): void {
    throw new Error('Method not implemented.');
  }
}