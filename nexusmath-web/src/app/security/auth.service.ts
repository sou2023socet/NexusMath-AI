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
  private readonly sessionStorageKey = 'nexusmath.auth.session';
  private readonly http = inject(HttpClient);
  private readonly session = signal<AuthResponse | null>(this.restoreSession());

  readonly currentUser = this.session.asReadonly();

  isAuthenticated(): boolean {
    return this.session() !== null;
  }

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/login', { email, password }).pipe(
      tap((response) => this.setSession(response)),
    );
  }

  register(email: string, username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/register', { email, username, password }).pipe(
      tap((response) => this.setSession(response)),
    );
  }

  forgotPassword(email: string): Observable<AuthMessage> {
    return this.http.post<AuthMessage>('/api/auth/forgot-password', { email });
  }

  logout(): Observable<unknown> {
    return this.http.post('/api/auth/logout', {}).pipe(
      finalize(() => this.clearSession()),
    );
  }

  clearSession(): void {
    sessionStorage.removeItem(this.sessionStorageKey);
    this.session.set(null);
  }

  private restoreSession(): AuthResponse | null {
    const storedSession = sessionStorage.getItem(this.sessionStorageKey);
    if (!storedSession) {
      return null;
    }

    try {
      const parsed: unknown = JSON.parse(storedSession);
      if (isAuthResponse(parsed)) {
        return parsed;
      }
    } catch (error) {
      console.error('Unable to restore the saved sign-in session.', error);
    }

    sessionStorage.removeItem(this.sessionStorageKey);
    return null;
  }

  private setSession(response: AuthResponse): void {
    sessionStorage.setItem(this.sessionStorageKey, JSON.stringify(response));
    this.session.set(response);
  }
}

function isAuthResponse(value: unknown): value is AuthResponse {
  return typeof value === 'object'
    && value !== null
    && 'token' in value
    && typeof value.token === 'string'
    && value.token.length > 0
    && 'username' in value
    && typeof value.username === 'string'
    && 'email' in value
    && typeof value.email === 'string';
}
