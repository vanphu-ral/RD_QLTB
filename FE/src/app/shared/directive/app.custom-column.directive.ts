import { Directive, Input, TemplateRef } from '@angular/core';

@Directive({
  selector: '[appColumnTemplate]',
  standalone: true,
})
export class CustomColumnDirective {
  @Input('appColumnTemplate') field!: string; 
  constructor(public template: TemplateRef<any>) {}
}
