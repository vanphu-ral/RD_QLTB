import { Directive, Input, OnInit, AfterViewInit, OnDestroy, ElementRef, Renderer2, Optional, Host, ChangeDetectorRef } from '@angular/core';
import { NgForm, FormGroupDirective } from '@angular/forms';
import { Subscription } from 'rxjs';
import { Button } from 'primeng/button';

@Directive({
  selector: '[disableIfInvalid]',
  standalone: true
})
export class DisableIfInvalidDirective implements OnInit, AfterViewInit, OnDestroy {
  /**
   * Có thể truyền vào:
   *  - reference tới NgForm (template-driven): #f="ngForm"
   *  - hoặc FormGroupDirective (reactive)
   * Nếu không truyền, directive cố inject tự động.
   */
  @Input('disableIfInvalid') formRef?: NgForm | FormGroupDirective;

  private sub?: Subscription;
  private innerButton?: HTMLButtonElement | null = null;

  constructor(
    private el: ElementRef,
    private renderer: Renderer2,
    private cdr: ChangeDetectorRef,
    @Optional() private injectedNgForm?: NgForm,
    @Optional() private injectedFormGroup?: FormGroupDirective,
    @Optional() @Host() private pButton?: Button
  ) {}

  ngOnInit(): void {
    // chọn source: ưu tiên formRef (được truyền), nếu không thì dùng injected
    const source = this.formRef ?? this.injectedNgForm ?? this.injectedFormGroup;
    if (source && (source as any).statusChanges) {
      this.sub = (source as any).statusChanges.subscribe(() => this.updateDisabled(source));
    }
  }

  ngAfterViewInit(): void {
    // tìm nút <button> bên trong host (p-button sẽ render <button>)
    const host: HTMLElement = this.el.nativeElement;
    this.innerButton = (host.tagName.toLowerCase() === 'button') ? host as HTMLButtonElement : host.querySelector('button');

    // initial check (delay chút để form và controls đã bind xong)
    setTimeout(() => {
      const source = this.formRef ?? this.injectedNgForm ?? this.injectedFormGroup;
      this.updateDisabled(source);
    }, 0);
  }

  private updateDisabled(source?: NgForm | FormGroupDirective | any): void {
    const isInvalid = this.isFormInvalid(source);

    // 1) nếu có instance Component p-button (PrimeNG), set property disabled của component
    if (this.pButton && (this.pButton as any).disabled !== undefined) {
      (this.pButton as any).disabled = isInvalid;
      try { this.cdr.detectChanges(); } catch {}
    }

    // 2) set property lên nút html nếu tìm thấy
    if (this.innerButton) {
      this.renderer.setProperty(this.innerButton, 'disabled', isInvalid);
      // thêm/remove class p-disabled để PrimeCSS nhận biết
      if (isInvalid) {
        this.renderer.addClass(this.innerButton, 'p-disabled');
      } else {
        this.renderer.removeClass(this.innerButton, 'p-disabled');
      }
    } else {
      // fallback: set attr trên host
      if (isInvalid) {
        this.renderer.setAttribute(this.el.nativeElement, 'disabled', 'true');
        this.renderer.setProperty(this.el.nativeElement, 'disabled', true);
        this.renderer.addClass(this.el.nativeElement, 'p-disabled');
      } else {
        this.renderer.removeAttribute(this.el.nativeElement, 'disabled');
        this.renderer.setProperty(this.el.nativeElement, 'disabled', false);
        this.renderer.removeClass(this.el.nativeElement, 'p-disabled');
      }
    }
  }

  private isFormInvalid(source?: NgForm | FormGroupDirective | any): boolean {
    if (!source) return false;
    // NgForm
    if ('invalid' in source) return !!source.invalid;
    // older shape
    if ('form' in source && source.form && 'invalid' in source.form) return !!source.form.invalid;
    return false;
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }
}
