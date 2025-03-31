//package orderbook.dataprovider.exceptions;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class CustomErrorInterceptor implements HandlerInterceptor {
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//        if (ex != null) {
//            try {
//                HttpStatus status = defineHttpStatus(ex);
//                response.setStatus(status.value());
//
//                Map<String, Object> errorResponse = new HashMap<>();
//                errorResponse.put("error", Map.of(
//                        "message", ex.getMessage(),
//                        "statusCode", status.value()
//                ));
//
//                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//
//                ObjectMapper mapper = new ObjectMapper();
//                response.getWriter().write(mapper.writeValueAsString(errorResponse));
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private HttpStatus defineHttpStatus(Exception ex) {
//        if (ex instanceof IllegalArgumentException) {
//            return HttpStatus.BAD_REQUEST;
//        } else if (ex instanceof jakarta.validation.ConstraintViolationException) {
//            return HttpStatus.BAD_REQUEST;
//        } else if (ex instanceof org.springframework.web.bind.MethodArgumentNotValidException) {
//            return HttpStatus.BAD_REQUEST;
//        } else if (ex instanceof BusinessException) {
//            return HttpStatus.UNPROCESSABLE_ENTITY;
//        }
//        return HttpStatus.INTERNAL_SERVER_ERROR;
//    }
//}