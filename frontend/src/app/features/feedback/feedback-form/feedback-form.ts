import {Component, EventEmitter, inject, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {FormErrors} from '../../../shared/form-errors/form-errors';
import {ActivatedRoute} from '@angular/router';
import {FeedbackService} from '../../../core/services/feedback-service';
import {ToastService} from '../../../core/services/toast-service';

@Component({
  selector: 'app-feedback-form',
  imports: [
    ReactiveFormsModule,
    FormErrors
  ],
  templateUrl: './feedback-form.html',
  styleUrl: './feedback-form.scss'
})
export class FeedbackForm implements OnInit {

  @Input() eventId!: string;
  @Output() feedbackAdded: EventEmitter<void> = new EventEmitter<void>();
  feedbackForm!: FormGroup;
  id: string | null | undefined = '';
  private fb: FormBuilder = inject(FormBuilder);

  private feedbackService: FeedbackService = inject(FeedbackService);
  private toastService: ToastService = inject(ToastService);

  ngOnInit() {
    this.feedbackForm = this.fb.group({
      text: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(100)]]
    });
  }

  onSubmit() {
    if (this.feedbackForm.invalid) {
      return;
    }

    const payload = {
      content: this.feedbackForm.value.text.trim(),

    }

    this.feedbackService.createFeedback(this.eventId!, payload).subscribe({
      next: (): void => {
        this.toastService.success("Feedback was added!");
        this.feedbackForm.reset();
        this.feedbackAdded.emit();
      },
      error: (): void => {
        this.toastService.error("Error adding feedback");
      }
    })
  }
}
