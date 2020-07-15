package nextstep.subway.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

public class ETagHeaderFilter {

	public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
		FilterRegistrationBean<ShallowEtagHeaderFilter> filter = new FilterRegistrationBean<>(
			new ShallowEtagHeaderFilter());
		filter.addUrlPatterns("/maps");
		filter.setName("etagFilter");
		return filter;
	}
}
