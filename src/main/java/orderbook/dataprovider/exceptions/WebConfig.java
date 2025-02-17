package orderbook.dataprovider.exceptions;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CustomErrorInterceptor customErrorInterceptor;

    public WebConfig(CustomErrorInterceptor customErrorInterceptor) {
        this.customErrorInterceptor = customErrorInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customErrorInterceptor);
    }
}

