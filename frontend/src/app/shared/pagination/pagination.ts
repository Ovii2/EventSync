import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-pagination',
  imports: [],
  templateUrl: './pagination.html',
  styleUrl: './pagination.scss'
})
export class Pagination {
  @Input() currentPage: number = 0;
  @Input() totalPages: number = 0;
  @Input() firstPageText: string = '<<';
  @Input() lastPageText: string = '>>';
  @Input() prevText: string = '<';
  @Input() nextText: string = '>';

  @Output() pageChange: EventEmitter<number> = new EventEmitter<number>();

  get canGoToPrevious(): boolean {
    return this.currentPage > 0;
  }

  get canGoToNext(): boolean {
    return this.currentPage < this.totalPages - 1;
  }

  get canGoToFirst(): boolean {
    return this.currentPage > 0;
  }

  get canGoToLast(): boolean {
    return this.currentPage < this.totalPages - 1;
  }

  get isLastPage(): boolean {
    return this.currentPage === this.totalPages - 1;
  }

  onPrevious(): void {
    if (this.canGoToPrevious) {
      this.pageChange.emit(this.currentPage - 1);
    }
  }

  onNext(): void {
    if (this.canGoToNext) {
      this.pageChange.emit(this.currentPage + 1);
    }
  }

  onFirst(): void {
    if (this.canGoToFirst) {
      this.pageChange.emit(0);
    }
  }

  onLast(): void {
    if (this.canGoToLast) {
      this.pageChange.emit(this.totalPages - 1);
    }
  }

}
