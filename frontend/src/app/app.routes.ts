import {Routes} from '@angular/router';
import {RegisterPage} from './pages/register-page/register-page';
import {LoginPage} from './pages/login-page/login-page';
import {EventsPage} from './pages/events-page/events-page';
import {EventDetailsPage} from './pages/event-details-page/event-details-page';
import {NotFoundPage} from './pages/not-found-page/not-found-page';
import {redirectIfAuthenticatedGuard} from './core/guards/redirect-if-authenticated-guard';
import {redirectIfNotAuthenticatedGuard} from './core/guards/redirect-if-not-authenticated-guard';

export const routes: Routes = [
  {path: 'register', component: RegisterPage, canActivate: [redirectIfAuthenticatedGuard]},
  {path: 'login', component: LoginPage, canActivate: [redirectIfAuthenticatedGuard]},
  {path: 'events', component: EventsPage, canActivate: [redirectIfNotAuthenticatedGuard]},
  {path: 'events/event-details/:id', component: EventDetailsPage, canActivate: [redirectIfNotAuthenticatedGuard]},
  {path: '**', component: NotFoundPage},
];
