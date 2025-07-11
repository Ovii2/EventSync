import {Component, inject} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {FormErrors} from '../../../shared/form-errors/form-errors';
import {Router} from '@angular/router';
import {ToastService} from '../../../core/services/toast-service';
import {EventRequest} from '../../../core/models/event-request';
import {EventService} from '../../../core/services/event-service';

@Component({
  selector: 'app-event-create-form',
  imports: [
    ReactiveFormsModule,
    FormErrors
  ],
  templateUrl: './event-create-form.html',
  styleUrl: './event-create-form.scss'
})
export class EventCreateForm {

  private fb: FormBuilder = inject(FormBuilder);
  private router: Router = inject(Router);
  private toastService: ToastService = inject(ToastService);
  private eventService: EventService = inject(EventService);

  eventForm: FormGroup = this.fb.group({
    title: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(100)]],
    description: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(500)]]
  });

  onSubmit(): void {
    if (this.eventForm.invalid) {
      return;
    }

    const payload: EventRequest = {
      title: this.eventForm.value.title.trim(),
      description: this.eventForm.value.description.trim()
    };

    this.eventService.createEvent(payload).subscribe({
      next: () => {
        this.toastService.success("Event created!")
        this.eventForm.reset();
        void this.router.navigate(['/events']);
      },
      error: (err) => {
        this.toastService.error("Error creating event");
      }
    });
  }
}
