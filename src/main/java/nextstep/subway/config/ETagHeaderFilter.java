package nextstep.subway.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class ETagHeaderFilter {
    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> filter = new FilterRegistrationBean<>(new ShallowEtagHeaderFilter());
        filter.addUrlPatterns("/maps");
        filter.setName("etagFilter");
        return filter;
    }
}
