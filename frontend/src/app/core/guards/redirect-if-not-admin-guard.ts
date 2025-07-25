import {CanActivateFn, Router} from '@angular/router';
import {AuthService} from '../services/auth-service';
import {inject} from '@angular/core';

export const redirectIfNotAdminGuard: CanActivateFn = (route, state) => {
  const authService: AuthService = inject(AuthService);
  const router: Router = inject(Router);

  if (!authService.isLoggedIn() || !authService.isAdmin()) {
    void router.navigate(['/login']);
    return false;
  }
  return true;
};
