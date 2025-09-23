import { Directive, OnInit, OnDestroy, Optional, Host } from '@angular/core';
import { NgModel } from '@angular/forms';
import { Subscription } from 'rxjs';

@Directive({
  selector: '[appDateConvert]'
})
export class DateConvertDirective implements OnInit, OnDestroy {
  private sub?: Subscription;
  private writing = false;

  constructor(@Optional() @Host() private ngModel: NgModel) {}

  ngOnInit(): void {
    if (!this.ngModel) return;

    // nếu model ban đầu là string (ví dụ "2025-09-19" hoặc "2025-09-19T00:00:00"), convert sang Date để hiển thị picker
    const initial = (this.ngModel as any).model;
    if (typeof initial === 'string') {
      const parsed = this.parseDateString(initial);
      if (parsed) {
        this.writing = true;
        // set view (datepicker) là Date
        this.ngModel.valueAccessor!.writeValue(parsed);
        // set model (form control) là string (giữ nguyên chuỗi)
        this.ngModel.control.setValue(this.normalizeToLocalDateTimeStringFromString(initial), { emitEvent: false });
        this.writing = false;
      }
    }

    // Subscribe valueChanges: khi picker trả về Date => convert thành string yyyy-MM-ddT00:00:00 và đặt vào model
    this.sub = this.ngModel.control.valueChanges?.subscribe((v: any) => {
      if (this.writing) return;

      // nếu picker trả về Date object
      if (v instanceof Date) {
        this.writing = true;
        const str = this.toLocalDateTimeString(v); // yyyy-MM-ddT00:00:00
        // đặt value của form control thành string (server-friendly)
        this.ngModel.control.setValue(str, { emitEvent: false });
        // báo cho ngModel two-way binding (ngModelChange)
        this.ngModel.viewToModelUpdate(str);
        // giữ picker hiển thị là Date (đảm bảo valueAccessor nhận lại Date)
        this.ngModel.valueAccessor!.writeValue(v);
        this.writing = false;
        return;
      }

      // nếu value là string (ví dụ set từ code khác) -> cập nhật view sang Date
      if (typeof v === 'string') {
        const parsed = this.parseDateString(v);
        if (parsed) {
          this.writing = true;
          this.ngModel.valueAccessor!.writeValue(parsed);
          this.writing = false;
        }
      }
    });
  }

  ngOnDestroy(): void {
    if (this.sub) this.sub.unsubscribe();
  }

  private parseDateString(value: string): Date | null {
    if (!value) return null;

    // nếu dạng yyyy-mm-dd
    const ymd = value.match(/^(\d{4})-(\d{2})-(\d{2})$/);
    if (ymd) {
      const year = +ymd[1], month = +ymd[2] - 1, day = +ymd[3];
      return new Date(year, month, day);
    }

    // nếu dạng yyyy-mm-ddTHH:MM:SS hoặc ISO without Z -> lấy phần date
    const ymdt = value.match(/^(\d{4})-(\d{2})-(\d{2})T/);
    if (ymdt) {
      const year = +ymdt[1], month = +ymdt[2] - 1, day = +ymdt[3];
      return new Date(year, month, day);
    }

    // fallback: thử new Date() (kém ưu tiên)
    const parsed = new Date(value);
    if (!isNaN(parsed.getTime())) {
      return new Date(parsed.getFullYear(), parsed.getMonth(), parsed.getDate());
    }

    return null;
  }

  private toLocalDateTimeString(value: Date): string {
    const y = value.getFullYear();
    const m = String(value.getMonth() + 1).padStart(2, '0');
    const d = String(value.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}T00:00:00`;
  }

  private normalizeToLocalDateTimeStringFromString(v: string): string {
    if (/^\d{4}-\d{2}-\d{2}$/.test(v)) return `${v}T00:00:00`;
    if (/^\d{4}-\d{2}-\d{2}T/.test(v)) return v.split('.')[0];
    return v;
  }
}
