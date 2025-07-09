import {Routes} from '@angular/router';
import {RegisterPage} from './pages/register-page/register-page';
import {LoginPage} from './pages/login-page/login-page';

export const routes: Routes = [
  {path: 'register', component: RegisterPage},
  {path: 'login', component: LoginPage},
];
