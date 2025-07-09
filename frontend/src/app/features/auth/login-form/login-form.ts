import {Component, inject} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {FormErrors} from '../../../shared/form-errors/form-errors';
import {Router} from '@angular/router';
import {AuthService} from '../../../core/services/auth-service';
import {ToastService} from '../../../core/services/toast-service';
import {LoginRequest} from '../../../core/models/login-request';

@Component({
  selector: 'app-login-form',
  imports: [
    ReactiveFormsModule,
    FormErrors
  ],
  templateUrl: './login-form.html',
  styleUrl: './login-form.scss'
})
export class LoginForm {
  private fb: FormBuilder = inject(FormBuilder);
  private router: Router = inject(Router);
  private authService: AuthService = inject(AuthService);
  private toastService: ToastService = inject(ToastService);

  loginForm: FormGroup = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]]
  });

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    const payload: LoginRequest = {
      username: this.loginForm.value.username.trim(),
      password: this.loginForm.value.password.trim()
    };

    this.authService.loginUser(payload).subscribe({
      next: (response) => {
        const token = response.body?.token;
        if (token) {
          this.authService.setToken(token);
        }

        this.toastService.success("Logged in!")
        this.loginForm.reset();
        void this.router.navigate(['/events']);
      },
      error: (err) => {
        this.toastService.error("Error logging in");
      }
    });
  }
}



