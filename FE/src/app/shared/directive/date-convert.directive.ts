import { Directive, OnInit, OnDestroy, Optional, Host, AfterViewInit } from '@angular/core';
import { NgModel } from '@angular/forms';
import { Subscription } from 'rxjs';

@Directive({
  selector: '[appDateConvert]'
})
export class DateConvertDirective implements OnInit, AfterViewInit, OnDestroy {
  private modelChangeSubscription: Subscription | null = null;
  private viewChangeSubscription: Subscription | null = null;
  private updating = false;
  private initialValueProcessed = false;

  constructor(@Optional() @Host() private ngModel: NgModel) {}

  ngOnInit(): void {
    if (!this.ngModel) return;

    this.modelChangeSubscription = this.ngModel.update.subscribe((value: any) => {
      if (this.updating) return;
      
      if (value && typeof value === 'string') {
        this.updating = true;
        this.updateView(value);
        this.updating = false;
      }
    });

    if (this.ngModel.valueChanges) {
      this.viewChangeSubscription = this.ngModel.valueChanges.subscribe((value: any) => {
        if (this.updating) return;
        
        if (value instanceof Date) {
          this.updating = true;
          this.updateModel(value);
          this.updating = false;
        }
      });
    }
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      if (this.ngModel && this.ngModel.model && !this.initialValueProcessed) {
        this.processInitialValue(this.ngModel.model);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.modelChangeSubscription) {
      this.modelChangeSubscription.unsubscribe();
    }
    if (this.viewChangeSubscription) {
      this.viewChangeSubscription.unsubscribe();
    }
  }

  private processInitialValue(value: any): void {
    if (value && typeof value === 'string') {
      const date = this.parseDate(value);
      if (date) {
        this.updating = true;
        this.ngModel.valueAccessor!.writeValue(date);
        this.ngModel.control.setValue(date, { emitEvent: false });
        this.updating = false;
        this.initialValueProcessed = true;
      }
    }
  }

  private updateView(value: string): void {
    const date = this.parseDate(value);
    if (date) {
      this.ngModel.valueAccessor!.writeValue(date);
      this.ngModel.control.setValue(date, { emitEvent: false });
    }
  }

  private updateModel(value: Date): void {
    const isoString = this.formatDate(value);
    if (isoString) {
      this.ngModel.control.setValue(isoString, { emitEvent: false });
      this.ngModel.viewToModelUpdate(isoString);
    }
  }

  private parseDate(value: string): Date | null {
    if (!value) return null;
    const parsed = new Date(value);
    if (!isNaN(parsed.getTime())) {
      return parsed;
    }
    const dateParts = value.split('-');
    if (dateParts.length === 3) {
      const year = parseInt(dateParts[0], 10);
      const month = parseInt(dateParts[1], 10) - 1; 
      const day = parseInt(dateParts[2], 10);
      
      const date = new Date(year, month, day);
      if (!isNaN(date.getTime())) {
        return date;
      }
    }
    
    return null;
  }

  private formatDate(value: Date): string | null {
    if (!value || !(value instanceof Date) || isNaN(value.getTime())) {
      return null;
    }
    
    return value.toISOString().split('.')[0];
  }
}