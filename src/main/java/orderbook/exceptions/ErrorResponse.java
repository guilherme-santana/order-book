package orderbook.exceptions;

import io.micrometer.tracing.Tracer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;


@Component
public class ErrorResponse {

    private Tracer tracer;

    public ErrorResponse(Tracer tracer) {
        this.tracer = tracer;
    }

    public ResponseEntity<Map<String, Object>> generateErrorResponse(String message, HttpStatus status) {
        String traceId = tracer.currentSpan() != null ? tracer.currentSpan().context().traceId() : "N/A";

        Map<String, Object> errorResponse = Map.of(
                "Error", new ErrorDetails(message, status.value(), traceId)
        );

        return new ResponseEntity<>(errorResponse, status);
    }

    @JsonPropertyOrder({"message", "status code", "traceId"})
        public record ErrorDetails(@JsonProperty("message") String message, @JsonProperty("status code") int statusCode,
                                   @JsonProperty("traceId") String traceId) {
            public ErrorDetails(String message, int statusCode, String traceId) {
                this.statusCode = statusCode;
                this.traceId = traceId;
                this.message = message;
            }

            @Override
            public int statusCode() {
                return statusCode;
            }

            @Override
            public String traceId() {
                return traceId;
            }

            @Override
            public String message() {
                return message;
            }
        }
}
