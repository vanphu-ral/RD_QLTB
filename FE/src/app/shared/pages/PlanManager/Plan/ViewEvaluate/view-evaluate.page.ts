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
  results: { value: string, time: string, createdBy?: string }[]; // Mảng kết quả (O, A, X, //) cho ca này vào ngày này
}

// Định nghĩa mới: Kết quả cho tất cả các Ca kiểm tra trong một ngày
interface DayResults {
  day: number;
  sessionResults: DailySessionResult[]; // Mảng kết quả của 5 ca mặc định
  daySignatures?: string[];
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



@Component({
  selector: 'view-evaluate',
  standalone: true,
  imports: [SharedModule, FormsModule, BaseApprovalComponent],
  providers: [ConfirmationService, MessageService],
  templateUrl: './view-evaluate.page.html',
  styleUrls: ['./view-evaluate.page.scss'],
})
export class ViewEvaluatePage extends BasePageComponent<any> {

  public groupedDetails: GroupedCritical[] = [];
  public planInfo: any = {}; // Có thể dùng model.planDetail.createdAt để tính ngày tháng
  public signature: any = {};
  public activeDays: Set<number> = new Set();

  listUserApproval: any[] = [];
  listUserApprReport: any[] = [];
  listUserStatusAppr: any[] = [];
  listUsers: any[] = [];
  userMap: Record<string, string> = {};
  Math = Math;

  sampleReport: any = {};

  weeklySignatures: { [key: number]: { image: string | null, signed: boolean } } = {};

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
    console.log(this.isApprovalMode);
    
    this.sampleReport = JSON.parse(this.model.planDetail.detail);
    this.planInfo = this.model.planDetail;
    this.loadApprovals();

    const details = this.model.planResultDetail || [];
    const uniqueUsernames = [...new Set(details.map((item: any) => item.createdBy).filter(Boolean))] as string[];
    if (uniqueUsernames.length > 0) {
      this.signatureService.getByListUsernames(uniqueUsernames).subscribe({
        next: (signatures: any[]) => {
          // Lưu map chữ ký để tra cứu nhanh: { 'username': 'imageLink' }
          this.signature = signatures.reduce((acc, s) => {
            acc[s.username] = s.imageLink;
            return acc;
          }, {});
          this.groupPlanDetails();
        },
        error: (err) => {
          console.error('Lỗi lấy danh sách chữ ký người thực hiện:', err);
          this.groupPlanDetails();
        }
      });
    } else {
      this.groupPlanDetails();
    }

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
      .findApprovalsByEntityIdAndEntityType(this.model.planDetail.id, 'plan_details')
      .subscribe((data: any[]) => {
        // 1. Reset dữ liệu cũ
        this.weeklySignatures = {};

        if (data && data.length > 0) {
          // 2. Lấy danh sách username duy nhất
          const usernames = [...new Set(data.map(x => x.username).filter(u => u))];

          // 3. Gọi API lấy chữ ký
          this.signatureService.getByListUsernames(usernames).pipe(
            catchError(err => {
              console.error('Lỗi lấy danh sách chữ ký plan_details:', err);
              return of([]); // Trả về mảng rỗng để vẫn chạy logic hiển thị text "Đã ký"
            })
          ).subscribe((signatures: any[]) => {

            // 4. Map dữ liệu vào các tuần
            data.forEach(item => {
              if (!item.createdAt) return;

              const createdDate = new Date(item.createdAt);
              const day = createdDate.getDate(); // Lấy ngày trong tháng (1-31)
              let week = 0;

              // Chia tuần dựa trên colspan của bảng (7 ngày/tuần)
              if (day <= 7) week = 1;
              else if (day <= 14) week = 2;
              else if (day <= 21) week = 3;
              else if (day <= 28) week = 4;
              else week = 5;

              // Tìm chữ ký tương ứng với username
              const sig = signatures.find(s => s.username === item.username);
              const imageLink = sig ? sig.imageLink : null;

              // Lưu vào biến weeklySignatures
              // Ưu tiên: Nếu tuần đó đã có dữ liệu rồi thì có thể ghi đè (tùy logic, ở đây lấy cái mới nhất tìm thấy)
              this.weeklySignatures[week] = {
                image: imageLink,
                signed: true // Đánh dấu là đã có bản ghi
              };
            });

            this.cdr.detectChanges();
          });
        }
      });
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

  groupPlanDetails(): void {
    // 1. Kiểm tra nguồn dữ liệu chính
    const results = this.model.planResultDetail || [];
    const uniqueGroups = new Map<string, Map<string, any[]>>();
    this.activeDays.clear();

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
        const usersByDay = new Map<number, Set<string>>();
        if (!items[0]?.isPlaceholder) {
          items.forEach((res: any) => {
            const dateTest = res.planResult?.dateTest;
            const resultValue = this.mapResultToIcon(res.result);
            const inspectionSession = res.inspectionSession || res.frequency;
            const examinationTime = res.examinationTime;
            const status = res.status;

            if (dateTest && this.model.planDetail?.createdAt) {
              const testDate = new Date(dateTest);
              const planDate = new Date(this.model.planDetail.createdAt);

              if (testDate.getMonth() === planDate.getMonth() && testDate.getFullYear() === planDate.getFullYear()) {
                const day = testDate.getDate();
                this.activeDays.add(day);
                if (!usersByDay.has(day)) usersByDay.set(day, new Set());
                if (res.createdBy) usersByDay.get(day)?.add(res.createdBy);
                const dayResult = dailyResults[day - 1];
                if (dayResult) {
                  const sessionResult = dayResult.sessionResults.find(s =>
                    s.session.toLowerCase() === (inspectionSession || '').toLowerCase().trim()
                  );
                  if (sessionResult) {
                    if (status === 0) {
                      sessionResult.results.push({ value: 'EMPTY', time: examinationTime || '', createdBy: res.createdBy || '' });
                    } else if (resultValue && resultValue !== '//') {
                      sessionResult.results.push({ value: resultValue, time: examinationTime || '', createdBy: res.createdBy || '' });
                    }
                  }
                }
              }
            }
          });
        }

        dailyResults.forEach(dr => {
          const dayUsers = usersByDay.get(dr.day);
          if (dayUsers) {
            dr.daySignatures = Array.from(dayUsers)
              .map(uname => this.signature[uname]) // Lấy link ảnh từ map signature đã lưu ở ngOnInit
              .filter(Boolean);
          }
        });

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

  getWeekOfMonth(dateStr: string): number {
    const date = new Date(dateStr);
    const day = date.getDate(); // 1 → 31

    if (day <= 7) return 1;
    if (day <= 14) return 2;
    if (day <= 21) return 3;
    if (day <= 28) return 4;
    return 5;
  }

  /**
 * Kiểm tra xem có bất kỳ chi tiết công việc nào có kết quả khác '//' trong ngày cụ thể.
 * @param day Số ngày trong tháng (1-31)
 * @returns HTML icon (chữ ký) nếu có kết quả và có ảnh, hoặc chữ "Đã ký" nếu có kết quả nhưng không có ảnh.
 */
  hasResultForDay(day: number): SafeHtml | '' {
    if (!this.groupedDetails || this.groupedDetails.length === 0) return '';

    // Tập hợp tất cả imageLink của những người có kết quả trong ngày này
    const allSignaturesInDay = new Set<string>();
    let hasAnyResult = false;

    this.groupedDetails.forEach(group => {
      group.details.forEach(detail => {
        const dayResult = detail.dailyResults.find(d => d.day === day);
        if (dayResult) {
          // Kiểm tra xem có kết quả không
          const hasValue = dayResult.sessionResults.some(s => s.results.some(r => r.value && r.value !== '//'));
          if (hasValue) {
            hasAnyResult = true;
            // Nếu có chữ ký thì add vào set
            dayResult.daySignatures?.forEach(img => allSignaturesInDay.add(img));
          }
        }
      });
    });

    if (!hasAnyResult) return '';

    if (allSignaturesInDay.size > 0) {
      const imagesHtml = Array.from(allSignaturesInDay)
        .map(img => `<img src="${img}" style="width: 30px; height: 16px; transform: rotate(90deg); margin-bottom: 4px; display: block;">`)
        .join('');
      return this.sanitizer.bypassSecurityTrustHtml(`<div style="display: flex; flex-direction: column; align-items: center;">${imagesHtml}</div>`);
    } else {
      return this.sanitizer.bypassSecurityTrustHtml(`<span style="font-size: 10px; font-weight: bold;">Đã ký</span>`);
    }
  }

  /**
   * Hàm trả về giá trị cho cột ngày (Dùng để hiển thị icon/ký tự)
   * Đã được cập nhật để nhận thêm tham số session.
   */
  getDailyResult(detail: UniqueDetail, day: number, session: string): string {
    const dayResult = detail.dailyResults.find(d => d.day === day);
    
    // Nếu ngày này hoàn toàn không có dữ liệu ở bất kỳ tiêu chí nào -> Hiện //
    if (!this.activeDays.has(day)) return '//';

    const sessionResult = dayResult?.sessionResults.find(s => s.session === session);
    const results = sessionResult ? sessionResult.results : [];

    if (!results || results.length === 0) {
      // Nếu ngày này có dữ liệu ở tiêu chí khác hoặc ca khác -> Để trắng
      return '';
    }

    const baseUrl = window.location.origin;

    const icons = results.map((res: any) => {
      if (res.value === 'EMPTY') return ''; // Hiển thị rỗng nếu status == 0
      const timeKey = (res.time || '').toLowerCase().replace(/\s/g, '');
      const creatorName = this.userMap[res.createdBy] || res.createdBy || 'N/A';
      const tooltipText = `Người thực hiện: ${creatorName}`;
      if (timeKey === 'ca1' || timeKey === 'ca2') {
        let statusSlug = '';
        if (res.value === 'O') statusSlug = 'oke';
        else if (res.value === 'A') statusSlug = 'edit';
        else if (res.value === 'X') statusSlug = 'error';

        if (statusSlug) {
          return `<img src="${baseUrl}/assets/icon/${timeKey}-${statusSlug}.svg" class="icon-img" style="width:16px; height:16px; cursor: pointer;" title="${tooltipText}" />`;
        }
      }
      switch (res.value) {
        case 'O':
          return `<i class="far fa-circle" title="${tooltipText}" style="cursor:pointer;"></i>`;
        case 'A':
          return `<i class="fa-solid fa-circle-play fa-rotate-270" title="${tooltipText}" style="cursor:pointer;"></i>`;
        case 'X':
          return `<i class="fas fa-times" title="${tooltipText}" style="cursor:pointer;"></i>`;
        default:
          return '';
      }
    });

    return icons.join(' ').trim();
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