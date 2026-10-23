import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../security/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink],
  template: `
    <section class="page-shell">
      <header class="page-header">
        <div>
          <p class="eyebrow">NexusMath AI</p>
          <h1>{{ auth.currentUser()?.username ? 'Welcome, ' + auth.currentUser()?.username : 'Mathematics workbench' }}</h1>
        </div>
      </header>

      <p class="muted">
        Explore the existing calculation services. Sign in to run authenticated operations.
      </p>

      <div class="topic-grid">
        <article class="card">
          <h2>Calculate</h2>
          <p>Use existing backend engines for arithmetic, equation systems, sequences, matrices, and number properties.</p>
          <a routerLink="/math">Open mathematics workspace</a>
        </article>
        <article class="card">
          <h2>Statistics and probability</h2>
          <p>Evaluate correlations, regression models, distributions, combinatorics, and probability analyses.</p>
          <a routerLink="/math">Open statistics tools</a>
        </article>
        <article class="card">
          <h2>Learn and explore</h2>
          <p>Review the current feature roadmap and find instructions for using the available calculations.</p>
          <a routerLink="/theory">View theory topics</a>
        </article>
      </div>
    </section>
  `,
})
export class DashboardComponent {
  readonly auth = inject(AuthService);
}
