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
  result: string; // O, A, X, //
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
    super.ngOnInit()
    this.signatureService.getByUsername('admin').subscribe((data) => {
      this.signature = data;
      console.log(this.signature);
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
      const groupName = item.criticalGroup.replace(/"/g, '');
      const criticalName = item.criticalName;

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

      const groupDetails: UniqueDetail[] = Array.from(nameMap.entries()).map(([criticalName, results]) => {

        // Khởi tạo mảng kết quả 31 ngày (mặc định là '//')
        const dailyResults: DailyResult[] = Array.from({ length: 31 }, (_, i) => ({
          day: i + 1,
          result: '//'
        }));

        // Lấp đầy kết quả thực tế vào mảng dailyResults
        results.forEach(res => {
          const dateTest = res.planResult?.dateTest; // Lấy ngày kiểm tra
          const resultValue = this.mapResultToIcon(res.result); // Chuyển đổi kết quả sang icon

          if (dateTest) {
            const day = new Date(dateTest).getDate();
            if (day >= 1 && day <= 31) {
              dailyResults[day - 1] = { day: day, result: resultValue };
            }
          } else if (res.result) {
            // Xử lý các trường hợp không có dateTest (có thể là dữ liệu mẫu)
            // Giả định: nếu có result 'OK' nhưng không có ngày, ta có thể bỏ qua hoặc xử lý riêng.
            // Trong mẫu này, ta sẽ chỉ dựa vào dateTest để điền vào cột ngày.
          }
        });

        // Lấy thông tin người thực hiện từ kết quả gần nhất
        const latestResult = results.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())[0];

        return {
          tt: runningTT++, // Tăng số thứ tự
          criticalName: criticalName,
          criticalCode: latestResult.criticalCode,
          frequency: latestResult.frequency,
          userTest: latestResult.createdBy || latestResult.planResult?.userTest || '',
          dailyResults: dailyResults,
        };
      });

      return {
        criticalGroup: groupName,
        details: groupDetails,
        rowspan: groupDetails.length,
      };
    });
    console.log(this.groupedDetails);
    
    this.cdr.detectChanges();
  }

  /**
     * Kiểm tra xem có bất kỳ chi tiết công việc nào có kết quả khác '//' trong ngày cụ thể.
     * Dùng cho phần ký xác nhận của Người thực hiện.
     * @param day Số ngày trong tháng (1-31)
     * @returns HTML icon nếu có kết quả, hoặc rỗng nếu không có.
     */
  hasResultForDay(day: number): any {
    if (!this.groupedDetails || this.groupedDetails.length === 0) {
      return '';
    }

    // Lặp qua tất cả các chi tiết công việc
    const found = this.groupedDetails.some(group => {
      return group.details.some(detail => {
        // Kiểm tra kết quả cho ngày đó
        const result = detail.dailyResults.find(d => d.day === day);
        // Nếu tìm thấy và kết quả KHÁC '//' (nghĩa là O, A, hoặc X)
        return result && result.result !== '//';
      });
    });

    // Nếu có ít nhất một công việc được thực hiện (kết quả khác '//')
    if (found) {
      return this.sanitizer.bypassSecurityTrustHtml(
        `<img src="${this.signature.imageLink}" style="width: 30px; height: 16px; transform: rotate(90deg);">`
      );
    }

    return ''; // Không có công việc nào được thực hiện
  }

  /**
   * Chuyển đổi kết quả 'result' thành ký hiệu/icon
   */
  mapResultToIcon(result: string): string {
    const value = result.toUpperCase();
    if (value === 'OK') {
      return 'O'; // Dấu OK
    } else if (value.includes('ADJUST') || value.includes('ĐIỀU CHỈNH')) {
      return 'A'; // Dấu Điều chỉnh
    } else if (value.includes('ERROR') || value.includes('BẤT THƯỜNG')) {
      return 'X'; // Dấu Bất thường
    }
    return '//';
  }

  // Hàm trả về giá trị cho cột ngày (Dùng để hiển thị icon/ký tự)
  getDailyResult(detail: UniqueDetail, day: number): string {
    const result = detail.dailyResults.find(d => d.day === day);
    const symbol = result ? result.result : '//';

    switch (symbol) {
      case 'O':
        return '<i class="far fa-circle"></i>';
      case 'A':
        return '<i class="fa-solid fa-circle-play fa-rotate-270"></i>';
      case 'X':
        return '<i class="fas fa-times"></i>';
      case '//':
        return '//';
      default:
        return '';
    }
  }

  // Hàm tạo mảng số ngày từ 1 đến 31
  getDaysArray(): number[] {
    // Tính số ngày của tháng hiện tại (cần gọi sau khi planInfo được set)
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
