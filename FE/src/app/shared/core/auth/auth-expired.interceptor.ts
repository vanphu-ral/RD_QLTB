import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { LoginService } from './login/login.service';

export const authExpiredInterceptor: HttpInterceptorFn = (req, next) => {
  const loginService = inject(LoginService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        console.warn('Session đã hết hạn, đang chuyển hướng đăng nhập...');
        loginService.login(); 
      }
      return throwError(() => error);
    })
  );
};