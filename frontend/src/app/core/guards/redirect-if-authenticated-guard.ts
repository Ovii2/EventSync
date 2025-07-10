import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {AuthService} from '../services/auth-service';

export const redirectIfAuthenticatedGuard: CanActivateFn = (route, state) => {
  const authService: AuthService = inject(AuthService);
  const router: Router = inject(Router);

  if (authService.isLoggedIn()) {
    void router.navigate(['/events']);
    return false;
  }
  return true;
};
