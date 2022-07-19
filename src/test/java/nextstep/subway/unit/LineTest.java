package nextstep.subway.unit;

import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Line 도메인 객체 테스트")
@SpringBootTest
@Transactional
class LineTest {
    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;

    @BeforeEach
    public void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        삼성역 = new Station("삼성역");
    }

    @Test
    void addSection() {
        // given
        Line 이호선 = new Line("2호선", "green");

        // when
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 삼성역, 5);

        // then
        assertAll(
                () -> assertThat(이호선.getStations()).hasSize(3),
                () -> assertThat(이호선.getStations()).containsExactly(강남역, 역삼역, 삼성역)
        );
    }

    @Test
    void getStations() {
        // given
        Line 이호선 = new Line("2호선", "green");
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 삼성역, 5);

        // when
        List<Station> stations = 이호선.getStations();

        // then
        assertAll(
                () -> assertThat(stations).hasSize(3),
                () -> assertThat(stations).containsExactly(강남역, 역삼역, 삼성역)
        );
    }

    @Test
    void removeSection() {
    }
}
