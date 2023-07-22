package nextstep.subway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import nextstep.subway.path.ShortedPathFinderConfiguration;

@Import({
        ShortedPathFinderConfiguration.class
})
@SpringBootApplication
public class SubwayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubwayApplication.class, args);
    }

}
