package mathapp.gui.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class MathServiceClient {

    private final ApiClient apiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MathServiceClient() {
        this(new ApiClient());
    }

    public MathServiceClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public double arithmetic(String operation, double first, Double second, String token)
            throws IOException, InterruptedException {
        String response = post("/api/math/arithmetic", new ArithmeticRequest(operation, first, second), token);
        return Double.parseDouble(response);
    }

    public String solveSystem(List<String> equations, String variableValues, String token)
            throws IOException, InterruptedException {
        return post("/api/math/equations/solve", new EquationRequest(equations, variableValues), token);
    }

    public String evaluateSequence(String operation, String input, Integer targetN, String token)
            throws IOException, InterruptedException {
        return post("/api/math/sequences/evaluate", new SequenceRequest(operation, input, targetN), token);
    }

    public String analyzeSequence(List<Double> terms, String token) throws IOException, InterruptedException {
        return post("/api/math/sequences/analyze", new SequenceTermsRequest(terms), token);
    }

    public String evaluateNumberProperty(String operation, String input, String token)
            throws IOException, InterruptedException {
        return post("/api/math/number-properties/evaluate",
                new NumberPropertyRequest(operation, input), token);
    }

    public String evaluateMatrix(String operation, double[][] values, String token)
            throws IOException, InterruptedException {
        return post("/api/math/matrices/evaluate", new MatrixRequest(operation, values), token);
    }

    public String analyzeBivariate(double[] x, double[] y, Double targetX, String token)
            throws IOException, InterruptedException {
        return post("/api/math/statistics/correlation", new CorrelationRequest(x, y, targetX), token);
    }

    public String calculateCombinations(int n, int r, String token)
            throws IOException, InterruptedException {
        return post("/api/math/combinatorics", new CombinatoricsRequest(n, r), token);
    }

    public RegressionResponse fitRegression(
            String operation, List<DataPoint> points, Integer polynomialDegree, Double threshold, String token)
            throws IOException, InterruptedException {
        String response = post("/api/math/statistics/regression",
                new RegressionRequest(operation, points, polynomialDegree, threshold), token);
        return readJson(response, RegressionResponse.class);
    }

    public String solveVenn2(double probabilityA, double probabilityB, double intersection, String token)
            throws IOException, InterruptedException {
        return post("/api/math/statistics/venn2",
                new Venn2Request(probabilityA, probabilityB, intersection), token);
    }

    public String updateBayes(List<String> names, List<Double> priors, List<Double> likelihoods, String token)
            throws IOException, InterruptedException {
        return post("/api/math/statistics/bayes", new BayesRequest(names, priors, likelihoods), token);
    }

    public String analyzeContingency(double[][] counts, String token)
            throws IOException, InterruptedException {
        return post("/api/math/statistics/contingency", new ContingencyRequest(counts), token);
    }

    public DistributionResponse evaluateDistribution(DistributionRequest request, String token)
            throws IOException, InterruptedException {
        String response = post("/api/math/distributions/evaluate", request, token);
        return readJson(response, DistributionResponse.class);
    }

    private String post(String path, Object request, String token) throws IOException, InterruptedException {
        try {
            return apiClient.postJson(path, objectMapper.writeValueAsString(request), token);
        } catch (JsonProcessingException exception) {
            throw new IOException("Could not serialize the math-service request.", exception);
        }
    }

    private <T> T readJson(String json, Class<T> responseType) throws IOException {
        try {
            return objectMapper.readValue(json, responseType);
        } catch (JsonProcessingException exception) {
            throw new IOException("Could not parse the math-service response.", exception);
        }
    }

    public record RegressionResponse(String report, List<DataPoint> predictedProbabilities) {
    }

    public record DataPoint(double x, double y) {
    }

    public record DistributionResponse(String report, List<DistributionPoint> points) {
    }

    public record DistributionPoint(double x, double pdfOrPmf, double cdf) {
    }

    public record DistributionRequest(
            String distribution, Double mean, Double standardDeviation, Double x,
            Integer trials, Double probability, Integer successes) {
    }

    public record ArithmeticRequest(String operation, double firstOperand, Double secondOperand) {
    }

    public record EquationRequest(List<String> equations, String variableValues) {
    }

    public record SequenceRequest(String operation, String input, Integer targetN) {
    }

    public record NumberPropertyRequest(String operation, String input) {
    }

    public record SequenceTermsRequest(List<Double> terms) {
    }

    public record MatrixRequest(String operation, double[][] values) {
    }

    public record CorrelationRequest(double[] x, double[] y, Double targetX) {
    }

    public record CombinatoricsRequest(int n, int r) {
    }

    public record RegressionRequest(
            String operation, List<DataPoint> points, Integer polynomialDegree, Double threshold) {
    }

    public record Venn2Request(double probabilityA, double probabilityB, double intersection) {
    }

    public record BayesRequest(List<String> hypothesisNames, List<Double> priors, List<Double> likelihoods) {
    }

    public record ContingencyRequest(double[][] counts) {
    }
}
