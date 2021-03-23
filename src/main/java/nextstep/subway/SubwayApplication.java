package nextstep.subway;

import nextstep.subway.path.domain.DijkstraStrategy;
import nextstep.subway.path.domain.PathStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@EnableJpaAuditing
@SpringBootApplication
public class SubwayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubwayApplication.class, args);
    }

    @Bean
    public PathStrategy pathStrategy(){
        return new DijkstraStrategy();
    }
}
