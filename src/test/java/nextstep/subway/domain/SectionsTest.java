package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SectionsTest {

    Sections sections;

    Station 가 = new Station("가");
    Station 나 = new Station("나");
    Station 다 = new Station("다");
    Station 라 = new Station("라");
    Station 마 = new Station("마");

    @BeforeEach
    void setUp() {

        Section section1 = new Section(가, 나, 5);
        Section section2 = new Section(나, 다, 5);
        Section section3 = new Section(다, 라, 5);

        sections = new Sections();
    }

}