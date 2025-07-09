import {ToastType} from './enums/toast-type';

export interface ToastMessage {
  type: ToastType;
  text: string;
}
