import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HistoryService } from './history.service';
import { RouterService } from './router.service';

@Injectable({
  providedIn: 'root'
})
export class NavigationService {
  constructor(
    private router: Router,
    private historyService: HistoryService,
    private routerService: RouterService
  ) {}

  public back(): void {
    const previousUrl = this.historyService.getPreviousUrl();
    if (previousUrl) {
      this.router.navigateByUrl(previousUrl);
    } else {
      const currentUrl = this.router.url;
      const fallbackUrl = this.routerService.getFallbackRoute(currentUrl);

      if (fallbackUrl) {
        this.router.navigateByUrl(fallbackUrl);
      } else {
        this.router.navigateByUrl('/');
      }
    }
  }
}