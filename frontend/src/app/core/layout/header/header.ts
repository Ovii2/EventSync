import {Component, ElementRef, HostListener, inject} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import {AuthService} from '../../services/auth-service';
import {ToastService} from '../../services/toast-service';

@Component({
  selector: 'app-header',
  imports: [
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss'
})
export class Header {
  isMenuOpen: boolean = false;

  private eRef: ElementRef = inject(ElementRef);
  protected authService: AuthService = inject(AuthService);
  private toastService: ToastService = inject(ToastService);
  private router: Router = inject(Router);

  closeMenu(): void {
    this.isMenuOpen = false;
  }

  @HostListener('document:click', ['$event.target'])
  clickOut(targetElement: EventTarget | null): void {
    if (!this.eRef.nativeElement.contains(targetElement) && this.isMenuOpen) {
      this.closeMenu();
    }
  }

  @HostListener('document:keydown', ['$event'])
  onEscape(event: KeyboardEvent): void {
    if (event.key === 'Escape' && this.isMenuOpen) {
      this.closeMenu();
    }
  }

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
  }

  onLogout(): void {
    this.closeMenu();

    this.authService.logoutUser().subscribe({
      next: (response) => {
        this.authService.logout();
        this.toastService.success('Logged out');
        void this.router.navigate(['/login']);
      },
      error: (error) => {
        this.authService.logout();
        this.toastService.error('Error during logout');
        void this.router.navigate(['/login']);
      }
    });
  }
}
