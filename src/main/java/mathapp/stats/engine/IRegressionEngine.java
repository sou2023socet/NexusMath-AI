package mathapp.stats.engine;

import mathapp.stats.core.StatisticalState.DataPoint2D;
import mathapp.stats.dto.LogisticRegressionResult;
import mathapp.stats.dto.RegressionResult;

import java.util.List;

/**
 * Service contract for fitting parametric, non-parametric, and classification regression models.
 */
public interface IRegressionEngine {

    /** Fits an Ordinary Least Squares (OLS) bivariate linear model. */
    RegressionResult fitLinearRegression(List<DataPoint2D> points);

    /** Fits an OLS polynomial regression model of the specified degree. */
    RegressionResult fitPolynomialRegression(List<DataPoint2D> points, int degree);

    /** Fits an exponential regression model (\hat{y} = a \cdot e^{bx}) via log-transformation. */
    RegressionResult fitExponentialRegression(List<DataPoint2D> points);

    /** Fits a binary logistic regression model using gradient descent. */
    LogisticRegressionResult fitLogisticRegression(List<DataPoint2D> points, double threshold);
}