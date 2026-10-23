import { Component } from '@angular/core';

@Component({
  selector: 'app-guide',
  standalone: true,
  template: `
    <section class="page-shell">
      <header class="page-header">
        <div>
          <p class="eyebrow">Guide</p>
          <h1>Using the math workbench</h1>
        </div>
      </header>
      <div class="guide-list">
        <article class="card">
          <h2>1. Sign in</h2>
          <p>Register or sign in with the backend account. Your sign-in lasts for this browser tab and survives page reloads.</p>
        </article>
        <article class="card">
          <h2>2. Select an operation</h2>
          <p>Choose a tool and enter inputs in the format shown beside its fields. Lists accept comma- or space-separated values; matrices and data point rows are separated with semicolons.</p>
        </article>
        <article class="card">
          <h2>3. Calculate and review</h2>
          <p>The Angular client sends requests to the existing Spring API. The server validates and computes results; request errors appear in the workspace.</p>
        </article>
        <article class="card">
          <h2>Session and data</h2>
          <p>The JWT remains in memory and is removed on sign out. The web client does not persist calculations or account data locally.</p>
        </article>
      </div>
    </section>
  `,
})
export class GuideComponent {}
