import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { DepartmentChatService } from './department-chat.service';

describe('DepartmentChatService', () => {
  let service: DepartmentChatService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(DepartmentChatService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('reads the backend AI-enabled state without provider settings', () => {
    service.getConfiguration().subscribe((configuration) => {
      expect(configuration.enabled).toBeFalse();
    });

    const request = httpMock.expectOne('/api/ai-chat/config');
    expect(request.request.method).toBe('GET');
    request.flush({ enabled: false });
  });

  it('creates a department-specific temporary session', () => {
    service.start('PHYSICS').subscribe((session) => {
      expect(session.department).toBe('PHYSICS');
      expect(session.remainingUserMessages).toBe(10);
    });

    const request = httpMock.expectOne('/api/ai-chat/sessions');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ department: 'PHYSICS' });
    request.flush({
      sessionId: 'session-id',
      department: 'PHYSICS',
      remainingUserMessages: 10,
      remainingAiResponses: 10,
    });
  });

  it('sends only the new message and server-issued session id', () => {
    service.send('session-id', 'Explain gravity').subscribe((reply) => {
      expect(reply.response).toBe('Gravity answer');
      expect(reply.remainingUserMessages).toBe(9);
    });

    const request = httpMock.expectOne('/api/ai-chat/sessions/session-id/messages');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ message: 'Explain gravity' });
    request.flush({
      response: 'Gravity answer',
      remainingUserMessages: 9,
      remainingAiResponses: 9,
    });
  });

  it('requests deletion when exiting a session', () => {
    service.exit('session-id').subscribe();

    const request = httpMock.expectOne('/api/ai-chat/sessions/session-id');
    expect(request.request.method).toBe('DELETE');
    request.flush(null);
  });
});
