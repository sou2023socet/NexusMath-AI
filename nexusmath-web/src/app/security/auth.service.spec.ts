import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let auth: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    sessionStorage.clear();
    configureTestBed();
  });

  afterEach(() => {
    http.verify();
    sessionStorage.clear();
  });

  it('restores the signed-in session after the service is recreated', () => {
    const response = {
      token: 'test-jwt',
      username: 'student',
      email: 'student@example.com',
    };

    auth.login(response.email, 'example-password').subscribe();
    http.expectOne('/api/auth/login').flush(response);
    expect(auth.isAuthenticated()).toBeTrue();

    TestBed.resetTestingModule();
    configureTestBed();

    expect(auth.isAuthenticated()).toBeTrue();
    expect(auth.currentUser()).toEqual(response);
  });

  it('clears the stored session on logout', () => {
    auth.login('student@example.com', 'example-password').subscribe();
    http.expectOne('/api/auth/login').flush({
      token: 'test-jwt',
      username: 'student',
      email: 'student@example.com',
    });

    auth.logout().subscribe();
    http.expectOne('/api/auth/logout').flush({ message: 'Logged out' });

    expect(auth.isAuthenticated()).toBeFalse();
    expect(sessionStorage.getItem('nexusmath.auth.session')).toBeNull();
  });

  function configureTestBed(): void {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    auth = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  }
});
