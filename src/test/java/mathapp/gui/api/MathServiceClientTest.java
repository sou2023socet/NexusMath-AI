package mathapp.gui.api;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MathServiceClientTest {

    private HttpServer server;
    private MathServiceClient client;
    private final AtomicReference<String> requestBody = new AtomicReference<>();
    private final AtomicReference<String> authorization = new AtomicReference<>();

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/math/arithmetic", exchange -> {
            requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            byte[] response = "5.0".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.createContext("/api/math/statistics/regression", exchange -> {
            requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            byte[] response = "{\"report\":\"linear fit\",\"predictedProbabilities\":[{\"x\":4.0,\"y\":8.0}]}"
                    .getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.createContext("/api/math/error", exchange -> {
            byte[] response = "{\"error\":\"invalid input\"}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(400, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
        client = new MathServiceClient(new ApiClient("http://127.0.0.1:" + server.getAddress().getPort()));
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    @Test
    void sendsAuthenticatedArithmeticRequestAndParsesResult() throws Exception {
        assertEquals(5.0, client.arithmetic("ADD", 2.0, 3.0, "test-token"));
        assertEquals("Bearer test-token", authorization.get());
        assertTrue(requestBody.get().contains("\"operation\":\"ADD\""));
        assertTrue(requestBody.get().contains("\"firstOperand\":2.0"));
    }

    @Test
    void serializesRegressionRequestAndDeserializesResponse() throws Exception {
        MathServiceClient.RegressionResponse response = client.fitRegression(
                "LINEAR", List.of(new MathServiceClient.DataPoint(2.0, 4.0),
                        new MathServiceClient.DataPoint(3.0, 6.0)),
                null, null, "test-token");

        assertTrue(requestBody.get().contains("\"operation\":\"LINEAR\""));
        assertTrue(requestBody.get().contains("\"x\":2.0"));
        assertEquals("linear fit", response.report());
        assertEquals(new MathServiceClient.DataPoint(4.0, 8.0), response.predictedProbabilities().get(0));
    }

    @Test
    void reportsBackendHttpErrorsToTheFrontend() {
        ApiClient apiClient = new ApiClient("http://127.0.0.1:" + server.getAddress().getPort());
        IOException exception = assertThrows(IOException.class,
                () -> apiClient.postJson("/api/math/error", "{}", "test-token"));
        assertTrue(exception.getMessage().contains("HTTP 400"));
        assertTrue(exception.getMessage().contains("invalid input"));
    }
}
