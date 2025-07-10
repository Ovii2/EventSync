import {Component, Input} from '@angular/core';
import {Feedback} from '../../../core/models/feedback';
import {DatePipe} from '@angular/common';
import {SentimentType} from '../../../core/models/enums/sentiment-type';

@Component({
  selector: 'app-feedback-card',
  imports: [
    DatePipe
  ],
  templateUrl: './feedback-card.html',
  styleUrl: './feedback-card.scss'
})
export class FeedbackCard {

  @Input() feedback!: Feedback;
  protected readonly SentimentType = SentimentType;
}
