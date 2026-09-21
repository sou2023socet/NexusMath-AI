package mathapp.stats.core;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Central State Container holding all observable data models for real-time UI and Math synchronization.
 */
public class StatisticalState {

    // 1D Univariate Data Store
    private final ObservableList<Double> rawUnivariateData = FXCollections.observableArrayList();
    private final BooleanProperty isSample = new SimpleBooleanProperty(true);
    private final DoubleProperty binWidth = new SimpleDoubleProperty(5.0);

    // 2D Bivariate Data Store (Scatter & Regression)
    private final ObservableList<DataPoint2D> bivariateData = FXCollections.observableArrayList();
    private final StringProperty regressionType = new SimpleStringProperty("LINEAR"); // LINEAR, POLYNOMIAL, EXPONENTIAL, LOGISTIC
    private final IntegerProperty polynomialDegree = new SimpleIntegerProperty(2);
    private final DoubleProperty logisticDecisionThreshold = new SimpleDoubleProperty(0.5);

    // Probability & Sets Store
    private final DoubleProperty probA = new SimpleDoubleProperty(0.5);
    private final DoubleProperty probB = new SimpleDoubleProperty(0.4);
    private final DoubleProperty probC = new SimpleDoubleProperty(0.3);
    private final DoubleProperty probAIntersectB = new SimpleDoubleProperty(0.2);
    private final DoubleProperty probBIntersectC = new SimpleDoubleProperty(0.1);
    private final DoubleProperty probAIntersectC = new SimpleDoubleProperty(0.15);
    private final DoubleProperty probAIntersectBIntersectC = new SimpleDoubleProperty(0.05);

    // Distribution Parameters Store
    private final StringProperty activeDistribution = new SimpleStringProperty("NORMAL");
    private final DoubleProperty distParam1 = new SimpleDoubleProperty(0.0);  // Mean (\mu), n, or \lambda
    private final DoubleProperty distParam2 = new SimpleDoubleProperty(1.0);  // Standard Deviation (\sigma) or p
    private final DoubleProperty distParam3 = new SimpleDoubleProperty(0.0);  // Additional parameter (e.g., Hypergeometric N)
    private final DoubleProperty distBoundLower = new SimpleDoubleProperty(-1.96);
    private final DoubleProperty distBoundUpper = new SimpleDoubleProperty(1.96);

    // Combinatorics Store
    private final IntegerProperty combN = new SimpleIntegerProperty(10);
    private final IntegerProperty combR = new SimpleIntegerProperty(3);

    public StatisticalState() {
        // Initialize default sample data
        rawUnivariateData.addAll(12.0, 15.0, 18.0, 22.0, 22.0, 25.0, 30.0, 31.0, 45.0);
        bivariateData.addAll(
                new DataPoint2D(1.0, 2.1),
                new DataPoint2D(2.0, 3.9),
                new DataPoint2D(3.0, 6.1),
                new DataPoint2D(4.0, 8.2),
                new DataPoint2D(5.0, 9.8),
                new DataPoint2D(6.0, 12.2)
        );
    }

    // Getters & Property Accessors
    public ObservableList<Double> getRawUnivariateData() { return rawUnivariateData; }
    
    public BooleanProperty isSampleProperty() { return isSample; }
    public boolean isSample() { return isSample.get(); }
    public void setSample(boolean sample) { isSample.set(sample); }

    public DoubleProperty binWidthProperty() { return binWidth; }
    public double getBinWidth() { return binWidth.get(); }
    public void setBinWidth(double width) { binWidth.set(width); }

    public ObservableList<DataPoint2D> getBivariateData() { return bivariateData; }

    public StringProperty regressionTypeProperty() { return regressionType; }
    public String getRegressionType() { return regressionType.get(); }
    public void setRegressionType(String type) { regressionType.set(type); }

    public IntegerProperty polynomialDegreeProperty() { return polynomialDegree; }
    public int getPolynomialDegree() { return polynomialDegree.get(); }
    public void setPolynomialDegree(int degree) { polynomialDegree.set(degree); }

    public DoubleProperty logisticDecisionThresholdProperty() { return logisticDecisionThreshold; }
    public double getLogisticDecisionThreshold() { return logisticDecisionThreshold.get(); }
    public void setLogisticDecisionThreshold(double threshold) { logisticDecisionThreshold.set(threshold); }

    public DoubleProperty probAProperty() { return probA; }
    public double getProbA() { return probA.get(); }
    public void setProbA(double val) { probA.set(val); }

    public DoubleProperty probBProperty() { return probB; }
    public double getProbB() { return probB.get(); }
    public void setProbB(double val) { probB.set(val); }

    public DoubleProperty probCProperty() { return probC; }
    public double getProbC() { return probC.get(); }
    public void setProbC(double val) { probC.set(val); }

    public DoubleProperty probAIntersectBProperty() { return probAIntersectB; }
    public double getProbAIntersectB() { return probAIntersectB.get(); }
    public void setProbAIntersectB(double val) { probAIntersectB.set(val); }

    public DoubleProperty probBIntersectCProperty() { return probBIntersectC; }
    public double getProbBIntersectC() { return probBIntersectC.get(); }
    public void setProbBIntersectC(double val) { probBIntersectC.set(val); }

    public DoubleProperty probAIntersectCProperty() { return probAIntersectC; }
    public double getProbAIntersectC() { return probAIntersectC.get(); }
    public void setProbAIntersectC(double val) { probAIntersectC.set(val); }

    public DoubleProperty probAIntersectBIntersectCProperty() { return probAIntersectBIntersectC; }
    public double getProbAIntersectBIntersectC() { return probAIntersectBIntersectC.get(); }
    public void setProbAIntersectBIntersectC(double val) { probAIntersectBIntersectC.set(val); }

    public StringProperty activeDistributionProperty() { return activeDistribution; }
    public String getActiveDistribution() { return activeDistribution.get(); }
    public void setActiveDistribution(String dist) { activeDistribution.set(dist); }

    public DoubleProperty distParam1Property() { return distParam1; }
    public double getDistParam1() { return distParam1.get(); }
    public void setDistParam1(double val) { distParam1.set(val); }

    public DoubleProperty distParam2Property() { return distParam2; }
    public double getDistParam2() { return distParam2.get(); }
    public void setDistParam2(double val) { distParam2.set(val); }

    public DoubleProperty distParam3Property() { return distParam3; }
    public double getDistParam3() { return distParam3.get(); }
    public void setDistParam3(double val) { distParam3.set(val); }

    public DoubleProperty distBoundLowerProperty() { return distBoundLower; }
    public double getDistBoundLower() { return distBoundLower.get(); }
    public void setDistBoundLower(double val) { distBoundLower.set(val); }

    public DoubleProperty distBoundUpperProperty() { return distBoundUpper; }
    public double getDistBoundUpper() { return distBoundUpper.get(); }
    public void setDistBoundUpper(double val) { distBoundUpper.set(val); }

    public IntegerProperty combNProperty() { return combN; }
    public int getCombN() { return combN.get(); }
    public void setCombN(int n) { combN.set(n); }

    public IntegerProperty combRProperty() { return combR; }
    public int getCombR() { return combR.get(); }
    public void setCombR(int r) { combR.set(r); }

    /**
     * Helper record for 2D Coordinate Data Points.
     */
    public record DataPoint2D(double x, double y) {}
}