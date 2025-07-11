import {Component, inject, OnInit} from '@angular/core';
import {EventsList} from '../../features/events/events-list/events-list';
import {EventService} from '../../core/services/event-service';
import {Event} from '../../core/models/event';
import {ToastService} from '../../core/services/toast-service';
import {ActivatedRoute, Router} from '@angular/router';
import {Pagination} from '../../shared/pagination/pagination';
import {PageResponse} from '../../core/models/page-response';

@Component({
  selector: 'app-events-page',
  standalone: true,
  imports: [
    EventsList,
    Pagination
  ],
  templateUrl: './events-page.html',
  styleUrl: './events-page.scss'
})
export class EventsPage implements OnInit {
  events: Event[] = [];
  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;

  private eventService: EventService = inject(EventService);
  private toastService: ToastService = inject(ToastService);
  private router: Router = inject(Router);
  private activatedRoute: ActivatedRoute = inject(ActivatedRoute);

  ngOnInit(): void {
    this.initPagination();
  }

  initPagination(): void {
    this.activatedRoute.queryParamMap.subscribe(params => {
      const pageParam: string | null = params.get('page');
      const parsedPage: number = pageParam ? parseInt(pageParam, 10) : 1;

      if (parsedPage < 1) {
        this.redirectToPage(1);
        return;
      }

      this.currentPage = parsedPage - 1;
      this.loadPaginatedEvents();
    });
  }

  loadPaginatedEvents(): void {
    this.eventService.getAllEventsPaginated(this.currentPage, this.pageSize).subscribe({
      next: (data: PageResponse<Event>): void => {
        this.totalPages = data.totalPages;

        if (this.currentPage >= this.totalPages && this.totalPages > 0) {
          this.redirectToPage(this.totalPages);
          return;
        }
        this.events = data.content;
      },
      error: (): void => {
        this.toastService.error("Failed to load events");
        this.events = [];
      }
    });
  }

  handlePageChange(newPage: number): void {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: {page: newPage + 1},
      queryParamsHandling: 'merge'
    }).then((): void => {
      this.loadPaginatedEvents();
    });
  }

  redirectToPage(page: number): void {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: {page},
      queryParamsHandling: 'merge'
    }).then((): void => {
      this.currentPage = page - 1;
      this.loadPaginatedEvents();
    });
  }
}
