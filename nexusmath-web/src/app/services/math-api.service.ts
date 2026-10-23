import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export interface DataPoint2D {
  x: number;
  y: number;
}

export interface RegressionResult {
  report: string;
  predictedProbabilities: DataPoint2D[];
}

export interface DistributionPoint {
  x: number;
  pdfOrPmf: number;
  cdf: number;
}

export interface DistributionResult {
  report: string;
  points: DistributionPoint[];
}

@Injectable({ providedIn: 'root' })
export class MathApiService {
  private readonly http = inject(HttpClient);

  arithmetic(operation: string, firstOperand: number, secondOperand?: number): Observable<number> {
    return this.http.post<number>('/api/math/arithmetic', {
      operation,
      firstOperand,
      secondOperand: secondOperand ?? null,
    });
  }

  solveEquation(equations: string[], variableValues = 'auto'): Observable<string> {
    return this.http.post('/api/math/equations/solve', { equations, variableValues }, { responseType: 'text' });
  }

  evaluateSequence(operation: string, input: string, targetN?: number): Observable<string> {
    return this.http.post('/api/math/sequences/evaluate', { operation, input, targetN }, { responseType: 'text' });
  }

  analyzeSequence(terms: number[]): Observable<string> {
    return this.http.post('/api/math/sequences/analyze', { terms }, { responseType: 'text' });
  }

  evaluateNumberProperty(operation: string, input: string): Observable<string> {
    return this.http.post('/api/math/number-properties/evaluate', { operation, input }, { responseType: 'text' });
  }

  evaluateMatrix(operation: string, values: number[][]): Observable<string> {
    return this.http.post('/api/math/matrices/evaluate', { operation, values }, { responseType: 'text' });
  }

  correlation(x: number[], y: number[], targetX?: number): Observable<string> {
    return this.http.post('/api/math/statistics/correlation', { x, y, targetX }, { responseType: 'text' });
  }

  combinatorics(n: number, r: number): Observable<string> {
    return this.http.post('/api/math/combinatorics', { n, r }, { responseType: 'text' });
  }

  regression(
    operation: string,
    points: DataPoint2D[],
    polynomialDegree?: number,
    threshold?: number,
  ): Observable<RegressionResult> {
    return this.http.post<RegressionResult>('/api/math/statistics/regression', {
      operation,
      points,
      polynomialDegree,
      threshold,
    });
  }

  venn2(probabilityA: number, probabilityB: number, intersection: number): Observable<string> {
    return this.http.post('/api/math/statistics/venn2', {
      probabilityA,
      probabilityB,
      intersection,
    }, { responseType: 'text' });
  }

  bayes(hypothesisNames: string[], priors: number[], likelihoods: number[]): Observable<string> {
    return this.http.post('/api/math/statistics/bayes', {
      hypothesisNames,
      priors,
      likelihoods,
    }, { responseType: 'text' });
  }

  contingency(counts: number[][]): Observable<string> {
    return this.http.post('/api/math/statistics/contingency', { counts }, { responseType: 'text' });
  }

  distribution(body: {
    distribution: string;
    mean?: number;
    standardDeviation?: number;
    x?: number;
    trials?: number;
    probability?: number;
    successes?: number;
  }): Observable<DistributionResult> {
    return this.http.post<DistributionResult>('/api/math/distributions/evaluate', body);
  }
}
