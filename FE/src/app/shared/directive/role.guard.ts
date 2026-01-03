import { Injectable } from '@angular/core';
import { 
  CanActivate, 
  ActivatedRouteSnapshot, 
  RouterStateSnapshot, 
  Router 
} from '@angular/router';
import { AccountService } from '../core/auth/account/account.service'; // Đường dẫn tới service của bạn
import _ from 'lodash';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(
    private accountService: AccountService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    // 1. Lấy danh sách roles được phép truy cập từ data của router
    const expectedRoles = route.data['roles'] as Array<string>;

    // Nếu router không yêu cầu role nào (không khai báo data.roles), cho phép đi qua
    if (!expectedRoles || expectedRoles.length === 0) {
      return true;
    }

    // 2. Lấy roles hiện tại của user (giống logic trong Directive của bạn)
    const userRoles = _.get(this.accountService.getUser(), 'attributes.roles') || [];

    // 3. Kiểm tra user có quyền không
    const hasPermission = expectedRoles.some(role => _.includes(userRoles, role));

    if (hasPermission) {
      return true;
    }

    // 4. Nếu không có quyền: Chặn và chuyển hướng (VD: về trang 403 hoặc trang chủ)
    // Bạn có thể tạo thêm trang 'access-denied' hoặc bắn thông báo
    this.router.navigate(['/']); 
    // Hoặc this.router.navigate(['/access-denied']);
    
    return false;
  }
}