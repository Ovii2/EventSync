import {Component, inject, Input, OnDestroy, OnInit} from '@angular/core';
import {FeedbackCard} from '../feedback-card/feedback-card';
import {FeedbackService} from '../../../core/services/feedback-service';
import {Feedback} from '../../../core/models/feedback';
import {FeedbackForm} from '../feedback-form/feedback-form';
import {WebsocketService} from '../../../core/services/websocket-service';
import {Subject, takeUntil} from 'rxjs';

@Component({
  selector: 'app-feedback-list',
  imports: [
    FeedbackCard,
  ],
  templateUrl: './feedback-list.html',
  styleUrl: './feedback-list.scss'
})
export class FeedbackList implements OnInit, OnDestroy {

  @Input() eventId: string | null | undefined = '';
  @Input() feedbackList: Feedback[] = [];


  private websocketService: WebsocketService = inject(WebsocketService);
  private feedbackService: FeedbackService = inject(FeedbackService);
  private destroy$: Subject<void> = new Subject<void>();

  ngOnInit(): void {
    if (this.eventId) {
      this.fetchFeedback(this.eventId);
      this.websocketService.connect();
      this.fetchSentimentUpdates();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  fetchFeedback(eventId: string): void {
    this.feedbackService.getFeedbackByEventId(eventId).subscribe({
      next: (data: Feedback[]): void => {
        this.feedbackList = data;
      },
      error: () => {
        throw new Error("Failed to load feedback");
      }
    })
  }

  refreshFeedback(): void {
    if (this.eventId) {
      this.fetchFeedback(this.eventId);
    }
  }


  fetchSentimentUpdates(): void {
    this.websocketService.feedbackUpdates$
      .pipe(takeUntil(this.destroy$))
      .subscribe(updatedFeedback => {
        const index: number = this.feedbackList.findIndex(fb => fb.id === updatedFeedback.id);
        if (index !== -1) {
          this.feedbackList[index] = updatedFeedback;
        }
      });
  }
}
