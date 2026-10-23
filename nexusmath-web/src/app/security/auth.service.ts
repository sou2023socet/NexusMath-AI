import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, finalize, tap } from 'rxjs';

export interface AuthResponse {
  token: string;
  username: string;
  email: string;
}

export interface AuthMessage {
  message: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly session = signal<AuthResponse | null>(null);

  readonly currentUser = this.session.asReadonly();

  isAuthenticated(): boolean {
    return this.session() !== null;
  }

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/login', { email, password }).pipe(
      tap((response) => this.session.set(response)),
    );
  }

  register(email: string, username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/register', { email, username, password }).pipe(
      tap((response) => this.session.set(response)),
    );
  }

  forgotPassword(email: string): Observable<AuthMessage> {
    return this.http.post<AuthMessage>('/api/auth/forgot-password', { email });
  }

  logout(): Observable<unknown> {
    return this.http.post('/api/auth/logout', {}).pipe(
      finalize(() => this.session.set(null)),
    );
  }

  clearSession(): void {
    this.session.set(null);
  }
}
