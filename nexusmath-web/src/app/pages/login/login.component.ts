import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../security/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="auth-page">
      <form class="auth-card" (ngSubmit)="submit()">
        <p class="eyebrow">NexusMath AI</p>
        <h1>{{ forgotMode ? 'Reset password' : registering ? 'Create account' : 'Sign in' }}</h1>
        <p class="muted">Use your account to access the math workbench.</p>

        <label *ngIf="registering && !forgotMode" for="username">Username</label>
        <input
          *ngIf="registering && !forgotMode"
          id="username"
          name="username"
          [(ngModel)]="username"
          autocomplete="username"
          required
          maxlength="80"
        />

        <label for="email">Email</label>
        <input
          id="email"
          name="email"
          type="email"
          [(ngModel)]="email"
          autocomplete="email"
          required
          maxlength="254"
        />

        <label *ngIf="!forgotMode" for="password">Password</label>
        <input
          *ngIf="!forgotMode"
          id="password"
          name="password"
          type="password"
          [(ngModel)]="password"
          [attr.autocomplete]="registering ? 'new-password' : 'current-password'"
          [minlength]="registering ? 8 : null"
          required
        />

        <p class="success-message" role="status" *ngIf="successMessage">{{ successMessage }}</p>
        <p class="error-message" role="alert" *ngIf="errorMessage">{{ errorMessage }}</p>
        <button class="primary" type="submit" [disabled]="submitting || !email || (!forgotMode && (!password || (registering && (!username || password.length < 8))))">
          {{ submitting ? 'Please wait…' : forgotMode ? 'Send reset link' : registering ? 'Create account' : 'Sign in' }}
        </button>
        <button class="text-button" *ngIf="!forgotMode && !registering" type="button" (click)="forgotMode = true; errorMessage = ''; successMessage = ''" [disabled]="submitting">
          Forgot password?
        </button>
        <button class="text-button" type="button" (click)="toggleMode()" [disabled]="submitting">
          {{ forgotMode || registering ? 'Back to sign in' : 'Need an account? Register' }}
        </button>
      </form>
    </section>
  `,
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  email = '';
  username = '';
  password = '';
  registering = false;
  forgotMode = false;
  submitting = false;
  errorMessage = '';
  successMessage = '';

  toggleMode(): void {
    if (this.forgotMode || this.registering) {
      this.forgotMode = false;
      this.registering = false;
    } else {
      this.registering = true;
    }
    this.errorMessage = '';
    this.successMessage = '';
  }

  submit(): void {
    if (this.submitting || !this.email.trim() || (!this.forgotMode && !this.password)) {
      return;
    }
    if (!this.forgotMode && this.registering && (!this.username.trim() || this.password.length < 8)) {
      return;
    }

    this.submitting = true;
    this.errorMessage = '';
    if (this.forgotMode) {
      this.auth.forgotPassword(this.email.trim()).subscribe({
        next: (response) => this.successMessage = response.message,
        error: (error: unknown) => {
          this.errorMessage = getErrorMessage(error);
          this.submitting = false;
        },
        complete: () => this.submitting = false,
      });
      return;
    }

    const request = this.registering
      ? this.auth.register(this.email.trim(), this.username.trim(), this.password)
      : this.auth.login(this.email.trim(), this.password);

    request.subscribe({
      next: () => {
        const requestedUrl = this.route.snapshot.queryParamMap.get('returnUrl');
        const returnUrl = requestedUrl?.startsWith('/') && !requestedUrl.startsWith('//')
          ? requestedUrl
          : '/dashboard';
        void this.router.navigateByUrl(returnUrl);
      },
      error: (error: unknown) => {
        this.errorMessage = getErrorMessage(error);
        this.submitting = false;
      },
      complete: () => this.submitting = false,
    });
  }
}

function getErrorMessage(error: unknown): string {
  if (typeof error === 'object' && error !== null && 'error' in error) {
    const body = error.error;
    if (typeof body === 'object' && body !== null && 'error' in body && typeof body.error === 'string') {
      return body.error;
    }
    if (typeof body === 'object' && body !== null && 'message' in body && typeof body.message === 'string') {
      return body.message;
    }
  }
  return 'Unable to authenticate. Check the service connection and try again.';
}
