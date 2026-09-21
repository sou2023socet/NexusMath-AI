package mathapp.stats.facade;

import mathapp.stats.core.StatisticalState.DataPoint2D;
import mathapp.stats.dto.RegressionResult;
import mathapp.stats.engine.DistributionEngineImpl.DistributionEvaluationResult;
import mathapp.stats.engine.HypothesisEngineImpl.HypothesisTestReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Stats Service Facade Integration Tests")
class StatsServiceFacadeTest {

    private StatsServiceFacade statsFacade;

    @BeforeEach
    void setUp() {
        statsFacade = new StatsServiceFacade();
    }

    @Test
    @DisplayName("Facade Orchestration: End-to-End Regression & Report Generation")
    void testCompositeBivariateReportGeneration() {
        List<DataPoint2D> dataset = List.of(
                new DataPoint2D(10.0, 25.0),
                new DataPoint2D(20.0, 45.0),
                new DataPoint2D(30.0, 65.0),
                new DataPoint2D(40.0, 85.0)
        );

        String compositeReport = statsFacade.generateCompositeBivariateReport(dataset, "Study Hours", "Exam Score");

        assertNotNull(compositeReport, "Composite report must not be null");
        assertTrue(compositeReport.contains("Executive Bivariate Summary Report"), "Report must contain header");
        assertTrue(compositeReport.contains("Pearson Correlation ($r$)"), "Report must include correlation stats");
        assertTrue(compositeReport.contains("Study Hours"), "Report must reference custom X label");
    }

    @Test
    @DisplayName("Facade Probability & Distributions Integration")
    void testFacadeDistributionEvaluation() {
        DistributionEvaluationResult normResult = statsFacade.evaluateNormalDistribution(0.0, 1.0, 1.96);

        assertEquals(0.0, normResult.mean(), 1e-6);
        assertEquals(0.975, normResult.evaluatedCdf(), 1e-2, "N(0,1) at z=1.96 should correspond to ~97.5th percentile");

        HypothesisTestReport tTest = statsFacade.performOneSampleTTest(new double[]{1.0, 2.0, 3.0, 4.0, 5.0}, 3.0, 0.05);
        assertFalse(tTest.rejectNull(), "Sample with mean 3.0 should fail to reject H0: mu = 3.0");
    }
}