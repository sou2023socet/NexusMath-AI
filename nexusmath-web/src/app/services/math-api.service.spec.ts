import { TestBed } from '@angular/core/testing';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from '../security/auth.service';
import { authInterceptor } from '../security/auth.interceptor';
import { MathApiService } from './math-api.service';

describe('MathApiService', () => {
  let api: MathApiService;
  let auth: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        MathApiService,
        AuthService,
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    api = TestBed.inject(MathApiService);
    auth = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('sends authenticated arithmetic requests using the backend request contract', () => {
    auth.login('student@example.com', 'example-password').subscribe();
    http.expectOne('/api/auth/login').flush({
      token: 'test-jwt',
      username: 'student',
      email: 'student@example.com',
    });

    api.arithmetic('ADD', 4, 7).subscribe((result) => expect(result).toBe(11));

    const request = http.expectOne('/api/math/arithmetic');
    expect(request.request.method).toBe('POST');
    expect(request.request.headers.get('Authorization')).toBe('Bearer test-jwt');
    expect(request.request.body).toEqual({
      operation: 'ADD',
      firstOperand: 4,
      secondOperand: 7,
    });
    request.flush(11);
  });

  it('uses the backend equation contract and reads its plain-text report', () => {
    api.solveEquation(['x + y = 3'], 'auto').subscribe((report) => expect(report).toContain('Resolved'));

    const request = http.expectOne('/api/math/equations/solve');
    expect(request.request.body).toEqual({
      equations: ['x + y = 3'],
      variableValues: 'auto',
    });
    request.flush('Resolved variables:\nx = 1\ny = 2');
  });

  it('clears the saved session after logout', () => {
    auth.login('student@example.com', 'example-password').subscribe();
    http.expectOne('/api/auth/login').flush({
      token: 'test-jwt',
      username: 'student',
      email: 'student@example.com',
    });
    expect(auth.isAuthenticated()).toBeTrue();

    auth.logout().subscribe();
    http.expectOne('/api/auth/logout').flush({ message: 'Logged out' });
    expect(auth.isAuthenticated()).toBeFalse();
  });

  it('requests password-reset email using the backend auth contract', () => {
    auth.forgotPassword('student@example.com').subscribe((response) => {
      expect(response.message).toContain('reset link');
    });

    const request = http.expectOne('/api/auth/forgot-password');
    expect(request.request.body).toEqual({ email: 'student@example.com' });
    request.flush({ message: 'If the email exists, a reset link has been sent.' });
  });
});
