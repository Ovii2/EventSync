import {Component, inject, OnInit} from '@angular/core';
import {EventDetails} from '../../features/events/event-details/event-details';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-event-details-page',
  imports: [
    EventDetails
  ],
  templateUrl: './event-details-page.html',
  styleUrl: './event-details-page.scss'
})
export class EventDetailsPage implements OnInit {

  eventId: string | null = '';

  private activatedRoute: ActivatedRoute = inject(ActivatedRoute);

  ngOnInit(): void {
    this.eventId = this.activatedRoute.snapshot.paramMap.get('id');
  }
}
