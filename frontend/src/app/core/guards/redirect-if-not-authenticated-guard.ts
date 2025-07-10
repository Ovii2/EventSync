import {CanActivateFn, Router} from '@angular/router';
import {AuthService} from '../services/auth-service';
import {inject} from '@angular/core';

export const redirectIfNotAuthenticatedGuard: CanActivateFn = (route, state) => {
  const authService: AuthService = inject(AuthService);
  const router: Router = inject(Router);

  if (!authService.isLoggedIn()) {
    void router.navigate(['/login']);
    return false;
  }
  return true;
};
