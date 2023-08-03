package nextstep.subway.section.config;

import nextstep.subway.section.policy.add.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BeanConfig {
    @Bean
    AddSectionPolicy addSectionPolicy() {
        return new IntegrationAddSectionPolicy(List.of(
                new CheckRegisterPolicy(),
                new CheckNotRegisterPolicy(),
                new CheckDistancePolicy()
        ));
    }
}
