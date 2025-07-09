import {Component, inject, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, Validators} from '@angular/forms';
import {FormErrors} from '../../../shared/form-errors/form-errors';
import {Router} from '@angular/router';
import {AuthService} from '../../../core/services/auth-service';
import {ToastService} from '../../../core/services/toast-service';
import {Register} from '../../../core/models/register';

@Component({
  selector: 'app-register-form',
  imports: [
    ReactiveFormsModule,
    FormErrors
  ],
  templateUrl: './register-form.html',
  styleUrl: './register-form.scss'
})
export class RegisterForm implements OnInit {

  registerForm!: FormGroup;

  private fb: FormBuilder = inject(FormBuilder);
  private router: Router = inject(Router);
  private authService: AuthService = inject(AuthService);
  private toastService: ToastService = inject(ToastService);

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email, Validators.minLength(4), Validators.maxLength(100)]],
      password: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(100)]],
      repeatPassword: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(100)]]
    }, { validators: this.passwordMatchValidator });
  }

  private passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password');
    const repeatPassword = control.get('repeatPassword');

    if (password && repeatPassword && password.value !== repeatPassword.value) {
      return { passwordsMismatch: true };
    }
    return null;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      return;
    }

    const payload: Register = {
      username: this.registerForm.value.username.trim(),
      email: this.registerForm.value.email.trim(),
      password: this.registerForm.value.password.trim()
    };

    this.authService.createUser(payload).subscribe({
      next: () => {
        this.toastService.success("User registered successfully!")
        this.registerForm.reset();
        void this.router.navigate(['/login']);
      },
      error: (err) => {
        this.toastService.error("Error registering user");
      }
    });
  }
}
