import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {ToastMessage} from '../models/toast-message';
import {ToastType} from '../models/enums/toast-type';

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toastSubject: Subject<ToastMessage> = new Subject<ToastMessage>();
  private clearSubject: Subject<void> = new Subject<void>();

  toast$: Observable<ToastMessage> = this.toastSubject.asObservable();
  clear$: Observable<void> = this.clearSubject.asObservable();

  constructor() {
  }

  show(toast: ToastMessage): void {
    this.toastSubject.next(toast);
  }

  success(text: string): void {
    this.show({type: ToastType.SUCCESS, text});
  }

  info(text: string): void {
    this.show({type: ToastType.INFO, text});
  }

  error(text: string): void {
    this.show({type: ToastType.ERROR, text});
  }

  clear(): void {
    this.clearSubject.next();
  }
}
