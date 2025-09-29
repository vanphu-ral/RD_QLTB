import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'customDate'
})
export class CustomDatePipe implements PipeTransform {
  transform(value: string | Date | null | undefined, format: string = 'yy/mm/dd'): string {
    if (!value) return '';

    const date = new Date(value);
    if (isNaN(date.getTime())) return '';

    const yy = date.getFullYear().toString().slice(-2);
    const yyyy = date.getFullYear().toString();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');

    return format
      .replace(/yyyy/g, yyyy)
      .replace(/yy/g, yy)
      .replace(/mm/g, mm)
      .replace(/dd/g, dd);
  }
}
