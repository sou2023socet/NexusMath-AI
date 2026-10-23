import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './security/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'NexusMath AI';
  readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  navItems = [
    { path: '/dashboard', label: 'Dashboard' },
    { path: '/math', label: 'Math' },
    { path: '/theory', label: 'Theory' },
    { path: '/guide', label: 'Guide' },
  ];
  lightTheme = localStorage.getItem('nexusmath.theme') === 'light';

  constructor() {
    document.documentElement.classList.toggle('theme-light', this.lightTheme);
  }

  toggleTheme(): void {
    this.lightTheme = !this.lightTheme;
    localStorage.setItem('nexusmath.theme', this.lightTheme ? 'light' : 'dark');
    document.documentElement.classList.toggle('theme-light', this.lightTheme);
  }

  logout(): void {
    this.auth.logout().subscribe({
      next: () => void this.router.navigate(['/login']),
      error: () => {
        this.auth.clearSession();
        void this.router.navigate(['/login']);
      },
    });
  }
}
