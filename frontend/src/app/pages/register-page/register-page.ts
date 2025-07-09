import { Component } from '@angular/core';
import {RegisterForm} from '../../features/auth/register-form/register-form';

@Component({
  selector: 'app-register-page',
  imports: [
    RegisterForm
  ],
  templateUrl: './register-page.html',
  styleUrl: './register-page.scss'
})
export class RegisterPage {

}
