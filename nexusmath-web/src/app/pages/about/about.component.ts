import { Component } from '@angular/core';

@Component({
  selector: 'app-about',
  standalone: true,
  template: `
    <section class="page-shell">
      <header class="page-header">
        <div>
          <p class="eyebrow">About</p>
          <h1>NexusMath AI</h1>
        </div>
      </header>
      <article class="panel">
        <p>
          NexusMath is a mathematics workbench with a JavaFX desktop client, a Spring
          authentication and calculation API, and this Angular web client.
        </p>
        <p class="muted">
          Calculation requests are processed by the existing backend. JavaFX remains
          available as a separate client.
        </p>
      </article>
    </section>
  `,
})
export class AboutComponent {}
