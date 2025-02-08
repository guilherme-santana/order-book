package orderbook.dataprovider.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("Error", Map.of(
                "message", ex.getMessage(),
                "status code", HttpStatus.BAD_REQUEST.value()
        ));
        log.error("M=handleIllegalArgument, error={}", response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        errorDetails.put("Error", Map.of(
                "message", fieldErrors,
                "status code", HttpStatus.BAD_REQUEST.value()
        ));
        log.error("M=handleValidationExceptions, error={}", errorDetails);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String detailedMessage = ex.getMostSpecificCause().getMessage();
        Map<String, Object> response = new HashMap<>();
        response.put("Error", Map.of(
                "message", detailedMessage,
                "status code", HttpStatus.BAD_REQUEST.value()
        ));
        log.error("M=handleHttpMessageNotReadable, error={}", response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("Error", Map.of(
                "message", errors,
                "status code", HttpStatus.BAD_REQUEST.value()
        ));
        log.error("M=handleConstraintViolation, error={}", response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("Error", Map.of(
                "message", ex.getMessage(),
                "status code", HttpStatus.INTERNAL_SERVER_ERROR.value()
        ));
        log.error("M=handleGeneralException, error={}", errorDetails);
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("Error", Map.of(
                "message", ex.getMessage(),
                "status code", HttpStatus.UNPROCESSABLE_ENTITY.value()
        ));
        log.error("M=handleBusinessException, error={}", response);
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }


}
