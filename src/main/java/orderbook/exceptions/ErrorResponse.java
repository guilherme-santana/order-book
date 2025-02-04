package orderbook.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ErrorResponse {

    public static ResponseEntity<Map<String, Object>> generateErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = Map.of(
                "Error", Map.of(
                        "message", message,
                        "status code", status.value()
                )
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}
