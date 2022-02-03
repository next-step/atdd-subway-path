package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    final Station 강남역 = Station.of("강남역");
    final Station 역삼역 = Station.of("역삼역");
    final Station 합정역 = Station.of("합정역");

    final Line 이호선 = Line.of("2호선", "bg-green-600", 강남역, 역삼역, 100);

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(역삼역, "id", 2L);
        ReflectionTestUtils.setField(합정역, "id", 3L);

        ReflectionTestUtils.setField(이호선, "id", 1L);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        이호선.addSection(역삼역, 합정역, 50);

        assertThat(이호선.getSections().getStations()).contains(강남역, 역삼역, 합정역);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
    }
}
