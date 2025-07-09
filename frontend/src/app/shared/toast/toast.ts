import {Component, inject, OnInit} from '@angular/core';
import {ToastService} from '../../core/services/toast-service';
import {ToastMessage} from '../../core/models/toast-message';
import {ToastType} from '../../core/models/enums/toast-type';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-toast',
  imports: [
    NgClass
  ],
  templateUrl: './toast.html',
  styleUrl: './toast.scss'
})
export class Toast implements OnInit {
  message: ToastMessage | null = null;
  private timeout: any;

  private toastService: ToastService = inject(ToastService);

  ngOnInit(): void {
    this.toastService.toast$.subscribe((msg: ToastMessage): void => {
      this.message = msg;
      clearTimeout(this.timeout);
      this.timeout = setTimeout((): void => {
        this.message = null;
      }, 3000);
    });

    this.toastService.clear$.subscribe(() => {
      this.message = null;
      clearTimeout(this.timeout);
    });
  }

  onMouseEnter(): void {
    clearTimeout(this.timeout);
  }

  onMouseLeave(): void {
    this.timeout = setTimeout((): void => {
      this.message = null;
    }, 2000);
  }

  onClose(): void {
    this.toastService.clear();
  }

  getIconName(type: ToastType): string {
    const iconMap: Record<string, string> = {
      [ToastType.SUCCESS]: 'check_circle',
      [ToastType.ERROR]: 'error',
      [ToastType.INFO]: 'warning',
    };

    return iconMap[type.toLowerCase()] || 'notifications';
  }
}
