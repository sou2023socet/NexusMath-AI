import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export type ChatDepartment = 'MATHEMATICS' | 'PHYSICS' | 'DSA';

export interface ChatSession {
  sessionId: string;
  department: ChatDepartment;
  remainingUserMessages: number;
  remainingAiResponses: number;
}

export interface ChatReply {
  response: string;
  remainingUserMessages: number;
  remainingAiResponses: number;
}

export interface ChatConfiguration {
  enabled: boolean;
}

@Injectable({ providedIn: 'root' })
export class DepartmentChatService {
  private readonly http = inject(HttpClient);
  private readonly sessionsUrl = '/api/ai-chat/sessions';

  getConfiguration(): Observable<ChatConfiguration> {
    return this.http.get<ChatConfiguration>('/api/ai-chat/config');
  }

  start(department: ChatDepartment): Observable<ChatSession> {
    return this.http.post<ChatSession>(this.sessionsUrl, { department });
  }

  send(sessionId: string, message: string): Observable<ChatReply> {
    return this.http.post<ChatReply>(
      `${this.sessionsUrl}/${encodeURIComponent(sessionId)}/messages`,
      { message },
    );
  }

  exit(sessionId: string): Observable<void> {
    return this.http.delete<void>(`${this.sessionsUrl}/${encodeURIComponent(sessionId)}`);
  }
}
