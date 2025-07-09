import {Component, Input} from '@angular/core';
import {AbstractControl, FormControl} from '@angular/forms';

@Component({
  selector: 'app-form-errors',
  standalone: true,
  imports: [],
  templateUrl: './form-errors.html',
  styleUrl: './form-errors.scss'
})
export class FormErrors {

  @Input() fieldName: string = '';
  @Input() control!: AbstractControl | null;

  private ERROR_MESSAGES: Record<string, (params?: any) => string> = {
    required: () => `${this.fieldName} is required.`,
    minlength: ({requiredLength}) => `${this.fieldName} must be at least ${requiredLength} characters long.`,
    maxlength: ({requiredLength}) => `${this.fieldName} must not exceed ${requiredLength} characters.`,
    email: () => `Please enter a valid email address.`,
    passwordsMismatch: () => `Passwords do not match.`
  };

  shouldShowErrors(): boolean {
    if (!this.control) return false;

    if (this.control.errors?.['passwordsMismatch']) {
      const repeatPasswordControl = this.control.get?.('repeatPassword');
      return repeatPasswordControl?.dirty || repeatPasswordControl?.touched || false;
    }

    return !!this.control && this.control.invalid && (this.control.dirty || this.control.touched);
  }

  listOfErrors(): string[] {
    if (!this.control?.errors) return [];

    return Object.keys(this.control.errors)
      .map((err) => this.ERROR_MESSAGES[err]?.(this.control!.getError(err)))
      .filter((msg): msg is string => !!msg);
  }
}
