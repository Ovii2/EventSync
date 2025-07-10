import {SentimentType} from './enums/sentiment-type';

export interface Feedback {
  id: string,
  content: string,
  createdAt: Date,
  sentimentType: SentimentType
}
