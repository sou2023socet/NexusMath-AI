import { Component } from '@angular/core';

@Component({
  selector: 'app-agents',
  standalone: true,
  template: `
    <section class="page-shell">
      <header class="page-header">
        <div>
          <p class="eyebrow">AI workspace</p>
          <h1>AI agents are not connected yet</h1>
        </div>
      </header>
      <article class="panel">
        <p>
          The existing Spring backend does not currently expose agent or LLM endpoints.
          This workspace is intentionally unavailable until a provider, server-side secret
          configuration, request contract, and safety behavior are implemented.
        </p>
        <p class="muted">
          Current mathematics operations continue to use the existing deterministic math APIs.
        </p>
      </article>
    </section>
  `,
})
export class AgentsComponent {}
