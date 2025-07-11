import {Routes} from '@angular/router';
import {RegisterPage} from './pages/register-page/register-page';
import {LoginPage} from './pages/login-page/login-page';
import {NotFoundPage} from './pages/not-found-page/not-found-page';
import {redirectIfAuthenticatedGuard} from './core/guards/redirect-if-authenticated-guard';
import {redirectIfNotAuthenticatedGuard} from './core/guards/redirect-if-not-authenticated-guard';
import {EventCreatePage} from './pages/event-create-page/event-create-page';
import {redirectIfNotAdminGuard} from './core/guards/redirect-if-not-admin-guard';

export const routes: Routes = [
  {path: '', component: LoginPage, canActivate: [redirectIfAuthenticatedGuard]},
  {path: 'register', component: RegisterPage, canActivate: [redirectIfAuthenticatedGuard]},
  {path: 'login', component: LoginPage, canActivate: [redirectIfAuthenticatedGuard]},
  {
    path: 'events',
    canActivate: [redirectIfNotAuthenticatedGuard],
    loadComponent: () => import('./pages/events-page/events-page').then(m => m.EventsPage)
  },
  {
    path: 'events/details/:id',
    canActivate: [redirectIfNotAuthenticatedGuard],
    loadComponent: () => import('./pages/event-details-page/event-details-page').then(m => m.EventDetailsPage)
  },
  {path: 'events/new', component: EventCreatePage, canActivate: [redirectIfNotAdminGuard]},
  {path: '**', component: NotFoundPage},
];
