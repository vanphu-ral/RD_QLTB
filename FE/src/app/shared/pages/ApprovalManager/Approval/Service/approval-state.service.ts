import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApprovalStateService {
  private approval$ = new BehaviorSubject<any | null>(null);

  setApproval(a: any) {
    this.approval$.next(a);

    if (a?.id) {
      localStorage.setItem('approvalId', a.id.toString());
    } else {
      localStorage.removeItem('approvalId');
    }
  }

  getApproval$() {
    return this.approval$.asObservable();
  }

  clear() {
    this.approval$.next(null);
    localStorage.removeItem('approvalId');
  }

  /** fallback khi reload trang */
  getApprovalIdFromStorage(): number | null {
    const id = localStorage.getItem('approvalId');
    return id ? +id : null;
  }
}
