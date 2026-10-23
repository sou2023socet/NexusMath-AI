import { Component } from '@angular/core';

@Component({
  selector: 'app-theory',
  standalone: true,
  template: `
    <section class="page-shell">
      <header class="page-header">
        <div>
          <p class="eyebrow">Theory</p>
          <h1>Topics covered by current calculation engines</h1>
        </div>
      </header>
      <p class="muted">
        The backend does not currently provide a browsable theory-content API. These categories
        describe the capabilities already implemented in its math services; they are not a
        generated textbook or a live content catalogue.
      </p>
      <div class="topic-grid">
        <article class="card">
          <h2>Algebra and number theory</h2>
          <p>Arithmetic, equation systems, matrix determinants and inverses, integer properties, factors, HCF, and LCM.</p>
        </article>
        <article class="card">
          <h2>Sequences</h2>
          <p>Pattern analysis, nth terms, series sums, polygonal numbers, Fibonacci and Catalan sequences, and Pascal’s triangle.</p>
        </article>
        <article class="card">
          <h2>Statistics</h2>
          <p>Correlation, regression, contingency analysis, and statistical distributions.</p>
        </article>
        <article class="card">
          <h2>Probability</h2>
          <p>Combinatorics, two-set probability, Bayes updates, and normal and binomial distributions.</p>
        </article>
      </div>
    </section>
  `,
})
export class TheoryComponent {}
