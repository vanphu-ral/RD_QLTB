import { Directive, Input, TemplateRef } from '@angular/core';

@Directive({
  selector: '[appCustomFilter]',
  standalone: true,
})
export class CustomFilterDirective {
  @Input('appCustomFilter') field!: string; 
  constructor(public template: TemplateRef<any>) {}
}
