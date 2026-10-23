import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import {
  ChatDepartment,
  ChatSession,
  DepartmentChatService,
} from '../../services/department-chat.service';

interface DepartmentCard {
  id: ChatDepartment;
  title: string;
  description: string;
}

interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
}

@Component({
  selector: 'app-agents',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="page-shell">
      <header class="page-header">
        <div>
          <p class="eyebrow">AI workspace</p>
          <h1>{{ session ? departmentTitle(session.department) : 'Department AI chatbots' }}</h1>
        </div>
      </header>

      <ng-container *ngIf="!session; else activeChat">
        <p *ngIf="configurationLoading" class="muted" role="status">Checking AI chat availability…</p>
        <p *ngIf="!configurationLoading && !backendAiEnabled" class="chat-disabled" role="status">
          AI chat is disabled. Set GEMINI_API_KEY in the backend environment and restart the backend.
        </p>
        <p class="muted">
          Choose a department to start a private, temporary chat. Sessions may be lost if the
          backend restarts and are never saved to your account.
        </p>
        <p *ngIf="error" class="chat-error" role="alert">{{ error }}</p>
        <div class="agent-list">
          <article class="card" *ngFor="let department of departments">
            <h2>{{ department.title }}</h2>
            <p>{{ department.description }}</p>
            <button type="button" (click)="startChat(department.id)"
              [disabled]="configurationLoading || !backendAiEnabled || starting || exiting">
              {{ starting ? 'Starting…' : 'Start Chat' }}
            </button>
          </article>
        </div>
      </ng-container>

      <ng-template #activeChat>
        <section class="panel department-chat" aria-label="Department chat">
          <div class="chat-toolbar">
            <div>
              <p class="muted">{{ departmentDescription(session!.department) }}</p>
              <p class="chat-count" aria-live="polite">
                {{ session!.remainingUserMessages }} user messages and
                {{ session!.remainingAiResponses }} AI responses remaining
              </p>
            </div>
            <button type="button" class="secondary-button" (click)="exitChat()" [disabled]="exiting">
              {{ exiting ? 'Exiting…' : 'Exit / New Chat' }}
            </button>
          </div>

          <p class="session-notice">
            This conversation is temporary and may be lost if the backend restarts.
          </p>
          <p *ngIf="!backendAiEnabled" class="chat-disabled" role="status">
            AI chat has been disabled by the backend. Exit this session to clear it.
          </p>

          <div class="chat-transcript" aria-live="polite" aria-relevant="additions text">
            <p *ngIf="messages.length === 0" class="chat-empty">
              Ask a question about {{ departmentTitle(session!.department) }} to begin.
            </p>
            <article
              *ngFor="let message of messages"
              class="chat-message"
              [class.user-message]="message.role === 'user'"
            >
              <strong>{{ message.role === 'user' ? 'You' : departmentTitle(session!.department) }}</strong>
              <p>{{ message.content }}</p>
            </article>
            <p *ngIf="loading" class="chat-loading" role="status">Thinking…</p>
          </div>

          <p *ngIf="error" class="chat-error" role="alert">{{ error }}</p>
          <form class="chat-composer" (ngSubmit)="sendMessage()">
            <label for="chat-message">Your question</label>
            <textarea
              id="chat-message"
              name="chat-message"
              [(ngModel)]="draft"
              maxlength="4000"
              rows="3"
              required
              [disabled]="!backendAiEnabled || loading || exiting || session!.remainingUserMessages === 0"
              placeholder="Type a text-only question…"
            ></textarea>
            <div class="composer-footer">
              <span class="muted">{{ draft.length }}/4000 characters</span>
              <button
                type="submit"
                [disabled]="!backendAiEnabled || loading || exiting || session!.remainingUserMessages === 0 || !draft.trim()"
              >
                {{ loading ? 'Sending…' : 'Send' }}
              </button>
            </div>
          </form>
          <p *ngIf="session!.remainingUserMessages === 0" class="muted">
            This chat has reached its 10-message limit. Exit to start a new chat.
          </p>
        </section>
      </ng-template>
    </section>
  `,
  styles: [`
    .department-chat { display: grid; gap: 1rem; }
    .chat-toolbar, .composer-footer { display: flex; align-items: center; justify-content: space-between; gap: 1rem; }
    .chat-toolbar p { margin-bottom: 0.35rem; }
    .chat-count { font-weight: 600; }
    .session-notice, .chat-empty { color: var(--muted); }
    .session-notice { margin: 0; font-size: 0.9rem; }
    .chat-transcript { display: grid; gap: 0.75rem; min-height: 12rem; max-height: 55vh; overflow-y: auto; padding: 1rem; border: 1px solid var(--border); border-radius: 0.5rem; background: var(--background); }
    .chat-message { max-width: 90%; padding: 0.8rem 1rem; border: 1px solid var(--border); border-radius: 0.5rem; background: var(--surface); }
    .chat-message p { margin: 0.4rem 0 0; white-space: pre-wrap; overflow-wrap: anywhere; }
    .user-message { justify-self: end; background: var(--surface-raised); }
    .chat-empty, .chat-loading { margin: auto; text-align: center; }
    .chat-composer { display: grid; gap: 0.6rem; }
    .chat-composer textarea { width: 100%; resize: vertical; padding: 0.75rem; color: var(--text); background: var(--surface); border: 1px solid var(--border); border-radius: 0.4rem; }
    .secondary-button { color: var(--text); background: var(--surface-raised); border: 1px solid var(--border); }
    .chat-error { color: var(--danger); }
    .chat-disabled { color: var(--muted); font-weight: 600; }
    @media (max-width: 560px) {
      .chat-toolbar { align-items: flex-start; flex-direction: column; }
      .chat-message { max-width: 100%; }
    }
  `],
})
export class AgentsComponent implements OnDestroy, OnInit {
  private readonly chatService = inject(DepartmentChatService);
  private activeRequest?: Subscription;
  private destroyed = false;

  readonly departments: DepartmentCard[] = [
    { id: 'MATHEMATICS', title: 'Mathematics AI', description: 'Explore mathematical concepts, problem solving, and reasoning.' },
    { id: 'PHYSICS', title: 'Physics AI', description: 'Ask about physics concepts, laws, and applications.' },
    { id: 'DSA', title: 'DSA AI', description: 'Learn data structures, algorithms, complexity, and implementation.' },
  ];

  session: ChatSession | null = null;
  messages: ChatMessage[] = [];
  draft = '';
  error = '';
  starting = false;
  loading = false;
  exiting = false;
  configurationLoading = true;
  backendAiEnabled = false;

  ngOnInit(): void {
    this.refreshBackendConfiguration();
  }

  private refreshBackendConfiguration(): void {
    this.chatService.getConfiguration().subscribe({
      next: (configuration) => {
        if (this.destroyed) {
          return;
        }
        this.backendAiEnabled = configuration.enabled;
        this.configurationLoading = false;
        if (!configuration.enabled && this.session) {
          this.exitChat();
        }
      },
      error: (error: HttpErrorResponse) => {
        if (this.destroyed) {
          return;
        }
        this.error = this.errorMessage(error);
        this.configurationLoading = false;
        this.backendAiEnabled = false;
      },
    });
  }

  startChat(department: ChatDepartment): void {
    if (!this.backendAiEnabled || this.configurationLoading || this.starting || this.exiting) {
      return;
    }
    this.error = '';
    this.starting = true;
    this.chatService.start(department).subscribe({
      next: (session) => {
        if (this.destroyed) {
          this.chatService.exit(session.sessionId).subscribe({
            error: () => console.error('Unable to clear the temporary AI chat session.'),
          });
          return;
        }
        this.session = session;
        this.messages = [];
        this.draft = '';
        this.starting = false;
      },
      error: (error: HttpErrorResponse) => {
        if (this.destroyed) {
          return;
        }
        this.error = this.errorMessage(error);
        this.starting = false;
        if (error.status === 503) {
          this.refreshBackendConfiguration();
        }
      },
    });
  }

  sendMessage(): void {
    const message = this.draft.trim();
    if (!this.backendAiEnabled || !this.session || !message || this.loading || this.exiting
        || this.session.remainingUserMessages === 0) {
      return;
    }

    this.error = '';
    this.loading = true;
    this.activeRequest = this.chatService.send(this.session.sessionId, message).subscribe({
      next: (reply) => {
        this.messages = [
          ...this.messages,
          { role: 'user', content: message },
          { role: 'assistant', content: reply.response },
        ];
        this.session = {
          ...this.session!,
          remainingUserMessages: reply.remainingUserMessages,
          remainingAiResponses: reply.remainingAiResponses,
        };
        this.draft = '';
        this.loading = false;
      },
      error: (error: HttpErrorResponse) => {
        this.error = this.errorMessage(error);
        this.loading = false;
        if (error.status === 503) {
          this.refreshBackendConfiguration();
        }
      },
    });
  }

  exitChat(): void {
    if (this.exiting) {
      return;
    }
    const sessionId = this.session?.sessionId;
    this.activeRequest?.unsubscribe();
    this.session = null;
    this.messages = [];
    this.draft = '';
    this.loading = false;
    this.error = '';
    if (!sessionId) {
      return;
    }

    this.exiting = true;
    this.chatService.exit(sessionId).subscribe({
      next: () => {
        this.exiting = false;
      },
      error: (error: HttpErrorResponse) => {
        this.error = this.errorMessage(error);
        this.exiting = false;
      },
    });
  }

  departmentTitle(department: ChatDepartment): string {
    return this.departments.find((item) => item.id === department)?.title ?? 'Department AI';
  }

  departmentDescription(department: ChatDepartment): string {
    return this.departments.find((item) => item.id === department)?.description ?? '';
  }

  ngOnDestroy(): void {
    this.destroyed = true;
    this.activeRequest?.unsubscribe();
    const sessionId = this.session?.sessionId;
    if (sessionId) {
      this.chatService.exit(sessionId).subscribe({
        error: () => console.error('Unable to clear the temporary AI chat session.'),
      });
    }
  }

  private errorMessage(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'Could not reach the AI service. Check your connection and try again.';
    }
    const serverMessage = error.error?.error;
    if (typeof serverMessage === 'string' && serverMessage.trim()) {
      return serverMessage;
    }
    return 'The chat request could not be completed. Please try again.';
  }
}
