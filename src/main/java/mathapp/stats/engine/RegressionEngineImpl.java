package mathapp.stats.engine;

import mathapp.stats.core.StatisticalState.DataPoint2D;
import mathapp.stats.dto.LogisticRegressionResult;
import mathapp.stats.dto.RegressionResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of IRegressionEngine providing linear, polynomial,
 * exponential, and logistic regression fits.
 */
public class RegressionEngineImpl implements IRegressionEngine {

    @Override
    public RegressionResult fitLinearRegression(List<DataPoint2D> points) {
        if (points == null || points.size() < 2) {
            throw new IllegalArgumentException("At least 2 data points are required for linear regression.");
        }

        int n = points.size();
        double sumX = 0, sumY = 0;
        for (DataPoint2D p : points) {
            sumX += p.x();
            sumY += p.y();
        }
        double meanX = sumX / n;
        double meanY = sumY / n;

        double cov = CorrelationAndRegressionCalculator.calculateCovariance(points, meanX, meanY);
        double pearsonR = CorrelationAndRegressionCalculator.calculatePearsonR(points, meanX, meanY);
        double spearmanRho = CorrelationAndRegressionCalculator.calculateSpearmanRho(points);

        double varX = 0.0;
        for (DataPoint2D p : points) {
            double dx = p.x() - meanX;
            varX += dx * dx;
        }
        varX /= (n - 1);

        double slope = (varX == 0) ? 0.0 : cov / varX;
        double intercept = meanY - (slope * meanX);

        double sst = 0.0; // Total sum of squares
        double sse = 0.0; // Sum of squared errors
        double ssr = 0.0; // Regression sum of squares

        List<Double> residuals = new ArrayList<>();
        List<DataPoint2D> fittedPoints = new ArrayList<>();

        for (DataPoint2D p : points) {
            double yHat = intercept + slope * p.x();
            double residual = p.y() - yHat;

            residuals.add(residual);
            fittedPoints.add(new DataPoint2D(p.x(), yHat));

            sse += residual * residual;
            sst += Math.pow(p.y() - meanY, 2);
            ssr += Math.pow(yHat - meanY, 2);
        }

        double rSquared = (sst == 0) ? 0.0 : 1.0 - (sse / sst);
        double adjustedRSquared = (n <= 2) ? rSquared : 1.0 - ((1.0 - rSquared) * (n - 1) / (n - 2));
        double standardErrorEst = (n > 2) ? Math.sqrt(sse / (n - 2)) : 0.0;

        String formula = String.format("\\hat{y} = %.4f %s %.4fx",
                intercept, (slope >= 0 ? "+" : "-"), Math.abs(slope));

        String report = buildStepByStepLinearReport(
                n, meanX, meanY, cov, pearsonR, spearmanRho,
                slope, intercept, rSquared, adjustedRSquared, sse, sst, standardErrorEst
        );

        return new RegressionResult(
            n, cov, cov * (n - 1.0) / n, pearsonR, spearmanRho,
            rSquared, slope, intercept, standardErrorEst,
            residuals, null, null, sse / Math.max(1, n - 2), 0.0, 0.0,
            formula, formula, report
        );
    }

    @Override
    public RegressionResult fitPolynomialRegression(List<DataPoint2D> points, int degree) {
        if (degree < 1) {
            throw new IllegalArgumentException("Polynomial degree must be at least 1.");
        }
        if (points == null || points.size() <= degree) {
            throw new IllegalArgumentException("Points count must be greater than polynomial degree.");
        }

        int n = points.size();
        double[] coeffs = CorrelationAndRegressionCalculator.fitPolynomialCoefficients(points, degree);

        double sumY = 0.0;
        for (DataPoint2D p : points) sumY += p.y();
        double meanY = sumY / n;

        double meanX = points.stream().mapToDouble(DataPoint2D::x).average().orElse(0.0);
        double cov = CorrelationAndRegressionCalculator.calculateCovariance(points, meanX, meanY);
        double pearsonR = CorrelationAndRegressionCalculator.calculatePearsonR(points, meanX, meanY);
        double spearmanRho = CorrelationAndRegressionCalculator.calculateSpearmanRho(points);

        double sst = 0.0;
        double sse = 0.0;
        double ssr = 0.0;

        List<Double> residuals = new ArrayList<>();
        List<DataPoint2D> fittedPoints = new ArrayList<>();

        for (DataPoint2D p : points) {
            double yHat = 0.0;
            for (int deg = 0; deg <= degree; deg++) {
                yHat += coeffs[deg] * Math.pow(p.x(), deg);
            }

            double residual = p.y() - yHat;
            residuals.add(residual);
            fittedPoints.add(new DataPoint2D(p.x(), yHat));

            sse += residual * residual;
            sst += Math.pow(p.y() - meanY, 2);
            ssr += Math.pow(yHat - meanY, 2);
        }

        double rSquared = (sst == 0) ? 0.0 : 1.0 - (sse / sst);
        int df = n - (degree + 1);
        double adjustedRSquared = (df <= 0) ? rSquared : 1.0 - ((1.0 - rSquared) * (n - 1) / df);
        double standardErrorEst = (df > 0) ? Math.sqrt(sse / df) : 0.0;

        StringBuilder formulaBuilder = new StringBuilder("\\hat{y} = ");
        for (int i = 0; i <= degree; i++) {
            if (i == 0) {
                formulaBuilder.append(String.format("%.4f", coeffs[i]));
            } else {
                double c = coeffs[i];
                formulaBuilder.append(String.format(" %s %.4fx", (c >= 0 ? "+" : "-"), Math.abs(c)));
                if (i > 1) formulaBuilder.append("^").append(i);
            }
        }

        String report = String.format("### Polynomial Regression Fit (Degree %d)\n\n", degree) +
                String.format("* R-Squared ($R^2$): `%.4f`\n", rSquared) +
                String.format("* Adjusted $R^2$: `%.4f`\n", adjustedRSquared) +
                String.format("* Standard Error of Estimate: `%.4f`\n", standardErrorEst) +
                String.format("* Fitted Formula: $%s$\n", formulaBuilder.toString());

        return new RegressionResult(
            n, cov, cov * (n - 1.0) / n, pearsonR, spearmanRho,
            rSquared, coeffs.length > 1 ? coeffs[1] : 0.0, coeffs[0], standardErrorEst,
            residuals, null, null, sse / Math.max(1, df), 0.0, 0.0,
            formulaBuilder.toString(), formulaBuilder.toString(), report
        );
    }

    @Override
    public RegressionResult fitExponentialRegression(List<DataPoint2D> points) {
        List<DataPoint2D> logTransformedPoints = new ArrayList<>();
        for (DataPoint2D p : points) {
            if (p.y() <= 0) {
                throw new IllegalArgumentException("Exponential regression requires all Y values to be strictly positive (> 0).");
            }
            logTransformedPoints.add(new DataPoint2D(p.x(), Math.log(p.y())));
        }

        RegressionResult linearFit = fitLinearRegression(logTransformedPoints);
        double a = Math.exp(linearFit.intercept());
        double b = linearFit.slope();

        List<Double> residuals = new ArrayList<>();
        List<DataPoint2D> fittedPoints = new ArrayList<>();
        double sse = 0.0, sst = 0.0;
        double meanY = points.stream().mapToDouble(DataPoint2D::y).average().orElse(0.0);

        for (DataPoint2D p : points) {
            double yHat = a * Math.exp(b * p.x());
            double residual = p.y() - yHat;
            residuals.add(residual);
            fittedPoints.add(new DataPoint2D(p.x(), yHat));

            sse += residual * residual;
            sst += Math.pow(p.y() - meanY, 2);
        }

        double rSquared = (sst == 0) ? 0.0 : 1.0 - (sse / sst);
        String formula = String.format("\\hat{y} = %.4f \\cdot e^{%.4fx}", a, b);

        return new RegressionResult(
                points.size(), linearFit.sampleCovariance(), linearFit.populationCovariance(),
                linearFit.pearsonR(), linearFit.spearmanRho(), rSquared, b, a,
                Math.sqrt(sse / Math.max(1, points.size() - 2)), residuals, null, null,
                sse / Math.max(1, points.size() - 2), 0.0, 0.0, formula, formula,
                "### Exponential Model Fit\n\nModel Equation: $" + formula + "$\n"
        );
    }

    @Override
    public LogisticRegressionResult fitLogisticRegression(List<DataPoint2D> points, double threshold) {
        // Simple Gradient Descent fit for binary logistic regression: P(Y=1|X) = 1 / (1 + exp(-(beta0 + beta1*x)))
        double beta0 = 0.0;
        double beta1 = 0.0;
        double lr = 0.01;
        int epochs = 1000;

        for (int epoch = 0; epoch < epochs; epoch++) {
            double grad0 = 0.0;
            double grad1 = 0.0;
            for (DataPoint2D p : points) {
                double pPred = 1.0 / (1.0 + Math.exp(-(beta0 + beta1 * p.x())));
                double err = pPred - p.y();
                grad0 += err;
                grad1 += err * p.x();
            }
            beta0 -= lr * (grad0 / points.size());
            beta1 -= lr * (grad1 / points.size());
        }

        int tp = 0, fp = 0, tn = 0, fn = 0;
        for (DataPoint2D p : points) {
            double prob = 1.0 / (1.0 + Math.exp(-(beta0 + beta1 * p.x())));
            int predictedClass = prob >= threshold ? 1 : 0;
            int actualClass = (int) p.y();

            if (predictedClass == 1 && actualClass == 1) tp++;
            else if (predictedClass == 1 && actualClass == 0) fp++;
            else if (predictedClass == 0 && actualClass == 0) tn++;
            else if (predictedClass == 0 && actualClass == 1) fn++;
        }

        double accuracy = (double) (tp + tn) / points.size();
        double precision = (tp + fp == 0) ? 0.0 : (double) tp / (tp + fp);
        double recall = (tp + fn == 0) ? 0.0 : (double) tp / (tp + fn);
        double specificity = (tn + fp == 0) ? 0.0 : (double) tn / (tn + fp);
        double f1 = (precision + recall == 0) ? 0.0 : 2.0 * (precision * recall) / (precision + recall);

        String report = String.format("### Logistic Regression Classification Report\n\n" +
                "* Model Equation: $P(Y=1\\vert{}X) = \\frac{1}{1 + e^{-(%.4f %s %.4fx)}}$\n" +
                "* Decision Threshold: `%.2f`\n" +
                "* Accuracy: `%.2f%%` | Precision: `%.4f` | Recall: `%.4f` | F1-Score: `%.4f`\n",
                beta0, (beta1 >= 0 ? "+" : "-"), Math.abs(beta1), threshold, accuracy * 100.0, precision, recall, f1);

        return new LogisticRegressionResult(
                beta0, beta1, Math.exp(beta1), 0.0, threshold,
                tp, fp, tn, fn, accuracy, precision, recall, specificity, f1,
                List.of(), 0.0, report
        );
    }

    private String buildStepByStepLinearReport(
            int n, double meanX, double meanY, double cov, double r, double rho,
            double slope, double intercept, double r2, double adjR2, double sse, double sst, double se
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("### Step-by-Step Bivariate Linear Regression Report\n\n");
        sb.append("**1. Descriptive Metrics & Correlation:**\n");
        sb.append(String.format("* Points ($n$): %d | Mean X: `%.4f` | Mean Y: `%.4f`\n", n, meanX, meanY));
        sb.append(String.format("* Covariance (\\text{Cov}(X,Y)): `%.4f`\n", cov));
        sb.append(String.format("* Pearson Correlation ($r$): `%.4f` (%s)\n",
                r, Math.abs(r) > 0.7 ? "Strong linear association" : Math.abs(r) > 0.3 ? "Moderate linear association" : "Weak/No linear association"));
        sb.append(String.format("* Spearman Rank Correlation ($\\rho$): `%.4f`\n\n", rho));

        sb.append("**2. Least-Squares Regression Model:**\n");
        sb.append(String.format("* Slope ($\\beta_1$): `%.4f` = $\\frac{\\text{Cov}(X,Y)}{\\text{Var}(X)}$\n", slope));
        sb.append(String.format("* Intercept ($\\beta_0$): `%.4f` = $\\bar{y} - \\beta_1 \\bar{x}$\n", intercept));
        sb.append(String.format("* Fitted Line: $\\hat{y} = %.4f %s %.4fx$\n\n", intercept, (slope >= 0 ? "+" : "-"), Math.abs(slope)));

        sb.append("**3. Goodness of Fit & Errors:**\n");
        sb.append(String.format("* Coefficient of Determination ($R^2$): `%.4f` (Explains `%.2f%%` of Y variance)\n", r2, r2 * 100.0));
        sb.append(String.format("* Adjusted $R^2$: `%.4f`\n", adjR2));
        sb.append(String.format("* Sum of Squared Errors (SSE): `%.4f` | Total Sum of Squares (SST): `%.4f`\n", sse, sst));
        sb.append(String.format("* Standard Error of Estimate ($s_e$): `%.4f`\n", se));

        return sb.toString();
    }
}