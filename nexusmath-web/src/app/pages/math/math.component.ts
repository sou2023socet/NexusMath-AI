import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DataPoint2D, DistributionPoint, MathApiService } from '../../services/math-api.service';

type Tool =
  | 'arithmetic' | 'equations' | 'sequence' | 'number-property' | 'matrix'
  | 'correlation' | 'combinatorics' | 'regression' | 'venn' | 'bayes'
  | 'contingency' | 'distribution';

@Component({
  selector: 'app-math',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="page-shell">
      <header class="page-header">
        <div>
          <p class="eyebrow">Mathematics workbench</p>
          <h1>Calculate and explore</h1>
        </div>
      </header>

      <section class="panel tool-panel">
        <label for="tool">Calculation</label>
        <select id="tool" name="tool" [(ngModel)]="tool" (ngModelChange)="clearResult()">
          <option *ngFor="let item of tools" [value]="item.id">{{ item.label }}</option>
        </select>

        <form class="tool-form" (ngSubmit)="execute()">
          <ng-container [ngSwitch]="tool">
            <ng-container *ngSwitchCase="'arithmetic'">
              <label for="arithmetic-operation">Operation</label>
              <select id="arithmetic-operation" name="arithmetic-operation" [(ngModel)]="operation">
                <option value="ADD">Add</option><option value="SUBTRACT">Subtract</option>
                <option value="MULTIPLY">Multiply</option><option value="DIVIDE">Divide</option>
                <option value="REMAINDER">Remainder</option><option value="POWER">Power</option>
                <option value="SQUARE">Square</option>
              </select>
              <div class="field-row">
                <div><label for="first-value">First value</label><input id="first-value" name="first-value" type="number" step="any" [(ngModel)]="firstValue" required /></div>
                <div *ngIf="operation !== 'SQUARE'"><label for="second-value">Second value</label><input id="second-value" name="second-value" type="number" step="any" [(ngModel)]="secondValue" [required]="operation !== 'SQUARE'" /></div>
              </div>
            </ng-container>

            <ng-container *ngSwitchCase="'equations'">
              <label for="equations">Equations, one per line</label>
              <textarea id="equations" name="equations" rows="4" [(ngModel)]="equationsText" required></textarea>
              <label for="variables">Known variable values</label>
              <input id="variables" name="variables" [(ngModel)]="variableValues" placeholder="auto or x=2,y=3" required />
            </ng-container>

            <ng-container *ngSwitchCase="'sequence'">
              <label for="sequence-operation">Sequence operation</label>
              <select id="sequence-operation" name="sequence-operation" [(ngModel)]="sequenceOperation">
                <option *ngFor="let item of sequenceOperations" [value]="item.id">{{ item.label }}</option>
              </select>
              <label for="sequence-input">Input terms or parameters</label>
              <textarea id="sequence-input" name="sequence-input" rows="3" [(ngModel)]="sequenceInput" required></textarea>
              <label for="target-n">Target n (when required)</label>
              <input id="target-n" name="target-n" type="number" min="0" step="1" [(ngModel)]="targetN" />
            </ng-container>

            <ng-container *ngSwitchCase="'number-property'">
              <label for="property-operation">Property</label>
              <select id="property-operation" name="property-operation" [(ngModel)]="propertyOperation">
                <option *ngFor="let item of propertyOperations" [value]="item.id">{{ item.label }}</option>
              </select>
              <label for="property-input">Number or comma-separated values</label>
              <input id="property-input" name="property-input" [(ngModel)]="propertyInput" required />
            </ng-container>

            <ng-container *ngSwitchCase="'matrix'">
              <label for="matrix-operation">Matrix operation</label>
              <select id="matrix-operation" name="matrix-operation" [(ngModel)]="matrixOperation">
                <option value="DETERMINANT">Determinant</option><option value="INVERSE">Inverse</option>
                <option value="INVERSE_WITH_DETERMINANT">Inverse and determinant</option>
              </select>
              <label for="matrix-input">Square matrix (rows separated by semicolons)</label>
              <textarea id="matrix-input" name="matrix-input" rows="3" [(ngModel)]="matrixInput" required></textarea>
            </ng-container>

            <ng-container *ngSwitchCase="'correlation'">
              <div class="field-row">
                <div><label for="x-values">X values</label><input id="x-values" name="x-values" [(ngModel)]="xValues" placeholder="1, 2, 3" required /></div>
                <div><label for="y-values">Y values</label><input id="y-values" name="y-values" [(ngModel)]="yValues" placeholder="2, 4, 5" required /></div>
              </div>
              <label for="target-x">Optional target X</label>
              <input id="target-x" name="target-x" type="number" step="any" [(ngModel)]="targetX" />
            </ng-container>

            <ng-container *ngSwitchCase="'combinatorics'">
              <div class="field-row">
                <div><label for="n-value">n</label><input id="n-value" name="n-value" type="number" min="0" step="1" [(ngModel)]="nValue" required /></div>
                <div><label for="r-value">r</label><input id="r-value" name="r-value" type="number" min="0" step="1" [(ngModel)]="rValue" required /></div>
              </div>
            </ng-container>

            <ng-container *ngSwitchCase="'regression'">
              <label for="regression-operation">Model</label>
              <select id="regression-operation" name="regression-operation" [(ngModel)]="regressionOperation">
                <option value="LINEAR">Linear</option><option value="POLYNOMIAL">Polynomial</option>
                <option value="EXPONENTIAL">Exponential</option><option value="LOGISTIC">Logistic</option>
              </select>
              <label for="regression-points">Data points as x,y pairs separated by semicolons</label>
              <textarea id="regression-points" name="regression-points" rows="3" [(ngModel)]="regressionPoints" required></textarea>
              <div class="field-row" *ngIf="regressionOperation === 'POLYNOMIAL' || regressionOperation === 'LOGISTIC'">
                <div *ngIf="regressionOperation === 'POLYNOMIAL'"><label for="polynomial-degree">Polynomial degree</label><input id="polynomial-degree" name="polynomial-degree" type="number" min="1" step="1" [(ngModel)]="polynomialDegree" /></div>
                <div *ngIf="regressionOperation === 'LOGISTIC'"><label for="threshold">Decision threshold</label><input id="threshold" name="threshold" type="number" min="0" max="1" step="any" [(ngModel)]="threshold" /></div>
              </div>
            </ng-container>

            <ng-container *ngSwitchCase="'venn'">
              <div class="field-row">
                <div><label for="probability-a">P(A)</label><input id="probability-a" name="probability-a" type="number" min="0" max="1" step="any" [(ngModel)]="probabilityA" required /></div>
                <div><label for="probability-b">P(B)</label><input id="probability-b" name="probability-b" type="number" min="0" max="1" step="any" [(ngModel)]="probabilityB" required /></div>
                <div><label for="intersection">P(A ∩ B)</label><input id="intersection" name="intersection" type="number" min="0" max="1" step="any" [(ngModel)]="intersection" required /></div>
              </div>
            </ng-container>

            <ng-container *ngSwitchCase="'bayes'">
              <label for="hypothesis-names">Hypothesis names (comma separated)</label>
              <input id="hypothesis-names" name="hypothesis-names" [(ngModel)]="hypothesisNames" required />
              <label for="priors">Prior probabilities (matching order)</label>
              <input id="priors" name="priors" [(ngModel)]="priors" required />
              <label for="likelihoods">Likelihoods (matching order)</label>
              <input id="likelihoods" name="likelihoods" [(ngModel)]="likelihoods" required />
            </ng-container>

            <ng-container *ngSwitchCase="'contingency'">
              <label for="contingency-counts">Count matrix (rows separated by semicolons)</label>
              <textarea id="contingency-counts" name="contingency-counts" rows="3" [(ngModel)]="contingencyCounts" required></textarea>
            </ng-container>

            <ng-container *ngSwitchCase="'distribution'">
              <label for="distribution">Distribution</label>
              <select id="distribution" name="distribution" [(ngModel)]="distribution">
                <option value="NORMAL">Normal</option><option value="BINOMIAL">Binomial</option>
              </select>
              <div *ngIf="distribution === 'NORMAL'" class="field-row">
                <div><label for="mean">Mean</label><input id="mean" name="mean" type="number" step="any" [(ngModel)]="mean" /></div>
                <div><label for="stddev">Standard deviation</label><input id="stddev" name="stddev" type="number" min="0" step="any" [(ngModel)]="standardDeviation" /></div>
                <div><label for="normal-x">Evaluation x</label><input id="normal-x" name="normal-x" type="number" step="any" [(ngModel)]="distributionX" /></div>
              </div>
              <div *ngIf="distribution === 'BINOMIAL'" class="field-row">
                <div><label for="trials">Trials</label><input id="trials" name="trials" type="number" min="1" step="1" [(ngModel)]="trials" /></div>
                <div><label for="success-probability">Success probability</label><input id="success-probability" name="success-probability" type="number" min="0" max="1" step="any" [(ngModel)]="successProbability" /></div>
                <div><label for="successes">Successes</label><input id="successes" name="successes" type="number" min="0" step="1" [(ngModel)]="successes" /></div>
              </div>
            </ng-container>
          </ng-container>

          <p class="error-message" role="alert" *ngIf="errorMessage">{{ errorMessage }}</p>
          <button class="primary" type="submit" [disabled]="loading">
            {{ loading ? 'Calculating…' : 'Calculate' }}
          </button>
        </form>
      </section>

      <section class="panel result-panel" *ngIf="resultText">
        <h2>Result</h2>
        <pre>{{ resultText }}</pre>
        <svg *ngIf="plotPath" class="result-plot" viewBox="0 0 640 240" role="img" aria-label="Plot of backend calculation results">
          <line x1="24" y1="216" x2="624" y2="216" class="plot-axis" />
          <line x1="24" y1="16" x2="24" y2="216" class="plot-axis" />
          <polyline [attr.points]="plotPath" class="plot-line" />
        </svg>
        <p class="muted" *ngIf="plotPath">Plot generated from numeric points returned by the math service.</p>
      </section>
    </section>
  `,
})
export class MathComponent {
  private readonly mathApi = inject(MathApiService);

  readonly tools: { id: Tool; label: string }[] = [
    { id: 'arithmetic', label: 'Arithmetic' },
    { id: 'equations', label: 'Equation systems' },
    { id: 'sequence', label: 'Sequences' },
    { id: 'number-property', label: 'Number properties' },
    { id: 'matrix', label: 'Matrices' },
    { id: 'correlation', label: 'Correlation' },
    { id: 'combinatorics', label: 'Combinatorics' },
    { id: 'regression', label: 'Regression' },
    { id: 'venn', label: 'Two-set probability' },
    { id: 'bayes', label: 'Bayes update' },
    { id: 'contingency', label: 'Contingency table' },
    { id: 'distribution', label: 'Distributions' },
  ];
  readonly sequenceOperations = [
    { id: 'ANALYZE_SEQUENCE_PATTERN', label: 'Analyze pattern' },
    { id: 'PREDICT_NEXT_TERMS', label: 'Predict next terms' },
    { id: 'GENERATE_DIFFERENCE_TABLE', label: 'Difference table' },
    { id: 'FIND_NTH_TERM', label: 'Find nth term' },
    { id: 'SUM_SERIES_TO_N', label: 'Sum series through n' },
    { id: 'ANALYZE_TERMS', label: 'Detailed sequence analysis' },
    { id: 'POLYGONAL_NUMBERS', label: 'Polygonal numbers' },
    { id: 'FIBONACCI_SEQUENCE', label: 'Fibonacci sequence' },
    { id: 'CATALAN_NUMBERS', label: 'Catalan numbers' },
    { id: 'PASCALS_TRIANGLE', label: 'Pascal’s triangle' },
    { id: 'CHECK_MONOTONIC_AND_BOUNDS', label: 'Monotonicity and bounds' },
    { id: 'FREQUENCY_TABLE', label: 'Frequency table' },
    { id: 'STEM_AND_LEAF_PLOT', label: 'Stem and leaf plot' },
  ];
  readonly propertyOperations = [
    { id: 'CHECK_EVEN_NUMBER', label: 'Check even' },
    { id: 'CHECK_ODD_NUMBER', label: 'Check odd' },
    { id: 'CHECK_POSITIVE_NUMBER', label: 'Check positive' },
    { id: 'CHECK_NEGATIVE_NUMBER', label: 'Check negative' },
    { id: 'CHECK_PRIME_NUMBER', label: 'Check prime' },
    { id: 'CHECK_COMPOSITE_NUMBER', label: 'Check composite' },
    { id: 'CHECK_COPRIME_NUMBER', label: 'Check coprime (two values)' },
    { id: 'CHECK_PERFECT_NUMBER', label: 'Check perfect number' },
    { id: 'PRIME_FACTORISATION', label: 'Prime factorisation' },
    { id: 'FIND_FACTORS', label: 'Find factors' },
    { id: 'FIND_HCF', label: 'Find HCF' },
    { id: 'FIND_LCM', label: 'Find LCM' },
    { id: 'EUCLIDEAN_ALGORITHM', label: 'Euclidean algorithm (two values)' },
    { id: 'EXTENDED_EUCLIDEAN_ALGORITHM', label: 'Extended Euclidean algorithm (two values)' },
    { id: 'PLACE_VALUE', label: 'Place value' },
    { id: 'MULTIPLES', label: 'Multiples' },
    { id: 'CHECK_NATURAL_NUMBERS_LIST', label: 'Check natural-number list' },
    { id: 'CHECK_WHOLE_NUMBERS_LIST', label: 'Check whole-number list' },
    { id: 'CHECK_REAL_NUMBER_LIST', label: 'Check real-number list' },
  ];

  tool: Tool = 'arithmetic';
  operation = 'ADD';
  firstValue = '12';
  secondValue = '4';
  equationsText = 'x + y = 10\n2x - y = 4';
  variableValues = 'auto';
  sequenceOperation = 'ANALYZE_SEQUENCE_PATTERN';
  sequenceInput = '2, 5, 10, 17, 26';
  targetN = '';
  propertyOperation = 'CHECK_PRIME_NUMBER';
  propertyInput = '29';
  matrixOperation = 'DETERMINANT';
  matrixInput = '1, 2; 3, 4';
  xValues = '1, 2, 3, 4';
  yValues = '2, 4, 5, 8';
  targetX = '';
  nValue = '5';
  rValue = '2';
  regressionOperation = 'LINEAR';
  regressionPoints = '1, 2; 2, 4; 3, 5; 4, 8';
  polynomialDegree = '2';
  threshold = '0.5';
  probabilityA = '0.4';
  probabilityB = '0.5';
  intersection = '0.2';
  hypothesisNames = 'H1, H2';
  priors = '0.6, 0.4';
  likelihoods = '0.8, 0.3';
  contingencyCounts = '20, 10; 15, 25';
  distribution = 'NORMAL';
  mean = '0';
  standardDeviation = '1';
  distributionX = '1';
  trials = '10';
  successProbability = '0.5';
  successes = '5';
  loading = false;
  errorMessage = '';
  resultText = '';
  plotPath = '';

  clearResult(): void {
    this.errorMessage = '';
    this.resultText = '';
    this.plotPath = '';
  }

  execute(): void {
    this.clearResult();
    this.loading = true;

    try {
      switch (this.tool) {
        case 'arithmetic':
          this.run(this.mathApi.arithmetic(this.operation, this.requiredNumber(this.firstValue, 'First value'), this.operation === 'SQUARE' ? undefined : this.requiredNumber(this.secondValue, 'Second value')));
          return;
        case 'equations': {
          const equations = this.equationsText.split(/\r?\n/).map((line) => line.trim()).filter(Boolean);
          if (!equations.length) throw new Error('Enter at least one equation.');
          if (!this.variableValues.trim()) throw new Error('Enter variable values or use "auto".');
          this.run(this.mathApi.solveEquation(equations, this.variableValues.trim()));
          return;
        }
        case 'sequence':
          if (!this.sequenceInput.trim()) throw new Error('Enter sequence terms or parameters.');
          if (this.sequenceOperation === 'ANALYZE_TERMS') {
            this.run(this.mathApi.analyzeSequence(this.parseNumberList(this.sequenceInput, 'Sequence terms')));
            return;
          }
          this.run(this.mathApi.evaluateSequence(this.sequenceOperation, this.sequenceInput.trim(), this.optionalInteger(this.targetN)));
          return;
        case 'number-property':
          if (!this.propertyInput.trim()) throw new Error('Enter a number or list of values.');
          this.run(this.mathApi.evaluateNumberProperty(this.propertyOperation, this.propertyInput.trim()));
          return;
        case 'matrix':
          this.run(this.mathApi.evaluateMatrix(this.matrixOperation, this.parseMatrix(this.matrixInput)));
          return;
        case 'correlation': {
          const x = this.parseNumberList(this.xValues, 'X values');
          const y = this.parseNumberList(this.yValues, 'Y values');
          if (x.length !== y.length || x.length < 2) throw new Error('Enter matching X and Y lists with at least two points.');
          this.run(this.mathApi.correlation(x, y, this.optionalNumber(this.targetX)));
          return;
        }
        case 'combinatorics': {
          const n = this.requiredInteger(this.nValue, 'n');
          const r = this.requiredInteger(this.rValue, 'r');
          if (n < 0 || r < 0 || r > n) throw new Error('Use integers satisfying 0 ≤ r ≤ n.');
          this.run(this.mathApi.combinatorics(n, r));
          return;
        }
        case 'regression': {
          const points = this.parsePoints(this.regressionPoints);
          if (points.length < 2) throw new Error('Enter at least two x,y data points.');
          const polynomialDegree = this.regressionOperation === 'POLYNOMIAL'
            ? this.requiredInteger(this.polynomialDegree, 'Polynomial degree')
            : undefined;
          if (polynomialDegree !== undefined && polynomialDegree < 1) {
            throw new Error('Polynomial degree must be at least 1.');
          }
          const threshold = this.regressionOperation === 'LOGISTIC'
            ? this.boundedProbability(this.threshold, 'Decision threshold')
            : undefined;
          this.run(this.mathApi.regression(
            this.regressionOperation,
            points,
            polynomialDegree,
            threshold,
          ), (response) => {
            this.resultText = response.report;
            this.plotPath = this.createPlot(response.predictedProbabilities);
          });
          return;
        }
        case 'venn': {
          const a = this.boundedProbability(this.probabilityA, 'P(A)');
          const b = this.boundedProbability(this.probabilityB, 'P(B)');
          const intersection = this.boundedProbability(this.intersection, 'P(A ∩ B)');
          if (intersection > Math.min(a, b)) throw new Error('The intersection probability cannot exceed either set probability.');
          this.run(this.mathApi.venn2(a, b, intersection));
          return;
        }
        case 'bayes': {
          const names = this.hypothesisNames.split(',').map((name) => name.trim()).filter(Boolean);
          const priors = this.parseNumberList(this.priors, 'Prior probabilities');
          const likelihoods = this.parseNumberList(this.likelihoods, 'Likelihoods');
          if (names.length < 2 || names.length !== priors.length || names.length !== likelihoods.length) {
            throw new Error('Enter matching hypothesis names, priors, and likelihoods (at least two of each).');
          }
          priors.forEach((value) => this.assertProbability(value, 'Each prior'));
          likelihoods.forEach((value) => this.assertProbability(value, 'Each likelihood'));
          this.run(this.mathApi.bayes(names, priors, likelihoods));
          return;
        }
        case 'contingency':
          {
            const counts = this.parseMatrix(this.contingencyCounts, false);
            if (counts.some((row) => row.some((value) => value < 0))) {
              throw new Error('Contingency counts cannot be negative.');
            }
            this.run(this.mathApi.contingency(counts));
          }
          return;
        case 'distribution':
          if (this.distribution === 'NORMAL') {
            const standardDeviation = this.requiredNumber(this.standardDeviation, 'Standard deviation');
            if (standardDeviation <= 0) throw new Error('Standard deviation must be greater than zero.');
            this.run(this.mathApi.distribution({
              distribution: 'NORMAL',
              mean: this.requiredNumber(this.mean, 'Mean'),
              standardDeviation,
              x: this.requiredNumber(this.distributionX, 'Evaluation x'),
            }), (response) => {
              this.resultText = response.report;
              this.plotPath = this.createDistributionPlot(response.points);
            });
          } else {
            const trials = this.requiredInteger(this.trials, 'Trials');
            const successes = this.requiredInteger(this.successes, 'Successes');
            if (trials < 1 || successes > trials) throw new Error('Trials must be positive and successes cannot exceed trials.');
            this.run(this.mathApi.distribution({
              distribution: 'BINOMIAL',
              trials,
              probability: this.boundedProbability(this.successProbability, 'Success probability'),
              successes,
            }), (response) => {
              this.resultText = response.report;
              this.plotPath = this.createDistributionPlot(response.points);
            });
          }
      }
    } catch (error: unknown) {
      this.errorMessage = error instanceof Error ? error.message : 'The request could not be prepared.';
      this.loading = false;
    }
  }

  private run<T>(request: import('rxjs').Observable<T>, onSuccess?: (value: T) => void): void {
    request.subscribe({
      next: (value) => {
        onSuccess?.(value);
        if (typeof value === 'string' || typeof value === 'number') {
          this.resultText = String(value);
        }
      },
      error: (error: unknown) => {
        this.errorMessage = getApiErrorMessage(error);
        this.loading = false;
      },
      complete: () => this.loading = false,
    });
  }

  private requiredNumber(value: string, label: string): number {
    const parsed = Number(value);
    if (!value.trim() || !Number.isFinite(parsed)) throw new Error(`${label} must be a finite number.`);
    return parsed;
  }

  private optionalNumber(value: string): number | undefined {
    return value.trim() ? this.requiredNumber(value, 'Optional value') : undefined;
  }

  private requiredInteger(value: string, label: string): number {
    const parsed = this.requiredNumber(value, label);
    if (!Number.isInteger(parsed)) throw new Error(`${label} must be a whole number.`);
    return parsed;
  }

  private optionalInteger(value: string): number | undefined {
    return value.trim() ? this.requiredInteger(value, 'Target n') : undefined;
  }

  private boundedProbability(value: string, label: string): number {
    const parsed = this.requiredNumber(value, label);
    this.assertProbability(parsed, label);
    return parsed;
  }

  private assertProbability(value: number, label: string): void {
    if (value < 0 || value > 1) throw new Error(`${label} must be between 0 and 1.`);
  }

  private parseNumberList(input: string, label: string): number[] {
    const values = input.trim().split(/[,\s]+/).filter(Boolean).map(Number);
    if (!values.length || values.some((value) => !Number.isFinite(value))) {
      throw new Error(`${label} must contain finite numbers separated by commas or spaces.`);
    }
    return values;
  }

  private parseMatrix(input: string, square = true): number[][] {
    const rows = input.split(';').map((row) => this.parseNumberList(row, 'Each matrix row'));
    const width = rows[0]?.length ?? 0;
    if (!width || rows.some((row) => row.length !== width)) throw new Error('All matrix rows must have the same non-zero number of values.');
    if (square && rows.length !== width) throw new Error('Matrix operations require a square matrix.');
    return rows;
  }

  private parsePoints(input: string): DataPoint2D[] {
    return input.split(';').map((pair) => {
      const values = this.parseNumberList(pair, 'Each data point');
      if (values.length !== 2) throw new Error('Each data point must contain exactly x,y.');
      return { x: values[0], y: values[1] };
    });
  }

  private createPlot(points: DataPoint2D[]): string {
    if (points.length < 2 || points.some((point) => !Number.isFinite(point.x) || !Number.isFinite(point.y))) return '';
    return this.scalePoints(points.map(({ x, y }) => ({ x, y })));
  }

  private createDistributionPlot(points: DistributionPoint[]): string {
    return this.scalePoints(points.map(({ x, pdfOrPmf }) => ({ x, y: pdfOrPmf })));
  }

  private scalePoints(points: DataPoint2D[]): string {
    if (points.length < 2) return '';
    const xs = points.map((point) => point.x);
    const ys = points.map((point) => point.y);
    const minX = Math.min(...xs), maxX = Math.max(...xs);
    const minY = Math.min(...ys), maxY = Math.max(...ys);
    const width = maxX - minX || 1, height = maxY - minY || 1;
    return points.map((point) => {
      const x = 24 + ((point.x - minX) / width) * 600;
      const y = 216 - ((point.y - minY) / height) * 200;
      return `${x},${y}`;
    }).join(' ');
  }
}

function getApiErrorMessage(error: unknown): string {
  if (typeof error === 'object' && error !== null && 'error' in error) {
    const body = error.error;
    if (typeof body === 'string' && body.trim()) return body;
    if (typeof body === 'object' && body !== null && 'error' in body && typeof body.error === 'string') return body.error;
  }
  return 'The math service request failed. Check your connection and input, then try again.';
}
