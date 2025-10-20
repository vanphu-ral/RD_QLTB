import { Component } from '@angular/core';
import { SharedModule } from '../../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { BasePageComponent } from '../../../../core/base-page-component/base-page.component';
import { PlanDetailService } from '../Service/plan-detail.service';
import { SignatureService } from '../../../SystemManager/Signature/Service/signature.service';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

interface DailyResult {
  day: number;
  results: string[]; // <-- dùng mảng để chứa nhiều kết quả cùng ngày (O, A, X, ...)
}

// Định nghĩa Interface cho chi tiết công việc duy nhất
interface UniqueDetail {
  tt: number; // Số thứ tự
  criticalName: string;
  criticalCode: string;
  frequency: string;
  userTest: string; // Người thực hiện
  dailyResults: DailyResult[]; // Mảng kết quả cho 31 ngày
}

// Định nghĩa Interface cho nhóm công việc
interface GroupedCritical {
  criticalGroup: string;
  details: UniqueDetail[];
  rowspan: number;
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
  public planInfo: any = {};
  public signature: any = {};

  constructor(
    protected override apiService: PlanDetailService,
    private signatureService: SignatureService,
    private sanitizer: DomSanitizer
  ) {
    super(apiService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.signatureService.getByUsername('admin').subscribe((data) => {
      this.signature = data;
      this.groupPlanDetails();
    });
  }

  /**
   * Nhóm các chi tiết kiểm tra theo Hạng mục chính và kết quả theo ngày (dateTest)
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
        // Khởi tạo mảng kết quả 31 ngày (mặc định rỗng => hiển thị '//')
        const dailyResults: DailyResult[] = Array.from({ length: 31 }, (_, i) => ({
          day: i + 1,
          results: []
        }));

        // Lấp đầy kết quả thực tế vào mảng dailyResults
        items.forEach((res: any) => {
          const dateTest = res.planResult?.dateTest;
          const resultValue = this.mapResultToIcon(res.result);

          if (dateTest) {
            const day = new Date(dateTest).getDate();
            if (day >= 1 && day <= 31) {
              const dayResult = dailyResults[day - 1];
              // tránh duplicate cùng ký hiệu
              if (resultValue && !dayResult.results.includes(resultValue)) {
                dayResult.results.push(resultValue);
              }
            }
          }
        });

        // Lấy thông tin người thực hiện từ kết quả gần nhất (theo createdAt)
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
        rowspan: groupDetails.length,
      } as GroupedCritical;
    });

    this.cdr.detectChanges();
  }

  /**
   * Kiểm tra xem có bất kỳ chi tiết công việc nào có kết quả khác '//' trong ngày cụ thể.
   * Dùng cho phần ký xác nhận của Người thực hiện.
   * @param day Số ngày trong tháng (1-31)
   * @returns HTML icon nếu có kết quả, hoặc rỗng nếu không có.
   */
  hasResultForDay(day: number): SafeHtml | '' {
    if (!this.groupedDetails || this.groupedDetails.length === 0) {
      return '';
    }

    // Lặp qua tất cả các chi tiết công việc
    const found = this.groupedDetails.some(group => {
      return group.details.some(detail => {
        const dayResult = detail.dailyResults.find(d => d.day === day);
        if (!dayResult) return false;
        // Nếu có ít nhất một kết quả khác '//' => coi là có thực hiện
        return dayResult.results.some(r => r && r !== '//');
      });
    });

    if (found && this.signature?.imageLink) {
      return this.sanitizer.bypassSecurityTrustHtml(
        `<img src="${this.signature.imageLink}" style="width: 30px; height: 16px; transform: rotate(90deg);">`
      );
    }

    return '';
  }

  /**
   * Chuyển đổi kết quả 'result' thành ký hiệu/icon
   * Trả về: 'O' | 'A' | 'X' | '//' (chuẩn hóa)
   */
  mapResultToIcon(result: string | null | undefined): string {
    if (!result) return '//';
    const value = String(result).toUpperCase();
    if (value === 'OK') {
      return 'O'; // Dấu OK
    } else if (value.includes('ADJUST') || value.includes('ĐIỀU CHỈNH') || value.includes('ĐÃ ĐIỀU CHỈNH')) {
      return 'A'; // Dấu Điều chỉnh
    } else if (value.includes('ERROR') || value.includes('BẤT THƯỜNG') || value.includes('CÓ BẤT THƯỜNG')) {
      return 'X'; // Dấu Bất thường
    }
    return '//';
  }

  // Hàm trả về giá trị cho cột ngày (Dùng để hiển thị icon/ký tự)
  getDailyResult(detail: UniqueDetail, day: number): string {
    const dayResult = detail.dailyResults.find(d => d.day === day);
    const results = dayResult ? dayResult.results : [];

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

    return icons.join(' ');
  }

  // Hàm tạo mảng số ngày từ 1 đến 31 (hoặc theo tháng trong planInfo)
  getDaysArray(): number[] {
    if (this.planInfo.planMonth && this.planInfo.planYear) {
      const numDays = new Date(this.planInfo.planYear, this.planInfo.planMonth, 0).getDate();
      return Array.from({ length: numDays }, (_, i) => i + 1);
    }
    return Array.from({ length: 31 }, (_, i) => i + 1);
  }

  public override save(): void {
    throw new Error('Method not implemented.');
  }
}
