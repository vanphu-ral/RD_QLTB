import { Directive, Input, TemplateRef } from '@angular/core';

@Directive({
  selector: '[appColumnTemplate]',
})
export class CustomColumnDirective {
  @Input('appColumnTemplate') field!: string; 
  constructor(public template: TemplateRef<any>) {}
}
