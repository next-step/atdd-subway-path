package nextstep.subway.unit;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.line.Line;
import nextstep.subway.linesection.LineSection;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 단위테스트")
public class LineTest {
    private Station 노원역;
    private Station 창동역;
    private Station 총신대입구역;
    private Station 사당역;
    private Line line;

    @BeforeEach
    void setUp() {
        노원역 = new Station("노원역");
        창동역 = new Station("창동역");
        총신대입구역 = new Station("총신대입구역");
        사당역 = new Station("사당역");
        line = Line.of("4호선", "light-blue", 노원역, 창동역, 3);
    }

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        //when
        line.addSection(LineSection.of(line, 창동역, 사당역, 3));
        //then
        Assertions.assertEquals(사당역, line.getSections().getEndStation());
    }

    @DisplayName("구간 추가 - 예외 발생")
    @Test
    void addSection_exception() {
        //when
        //then
        Assertions.assertThrows(BadRequestException.class,
                () -> line.addSection(LineSection.of(line, 총신대입구역, 사당역, 3)));
    }
    @DisplayName("구간 조회")
    @Test
    void getStations() {
        //given
        line.addSection(LineSection.of(line, 창동역, 사당역, 3));
        //when
        //then
        assertThat(line.getStations()).containsExactly(노원역, 창동역, 사당역);
    }

    @DisplayName("구간 제거")
    @Test
    void removeSection() {
        //given
        line.addSection(LineSection.of(line, 창동역, 사당역, 3));
        //when
        line.removeSection(사당역);
        //then
        assertThat(line.getStations()).containsExactly(노원역, 창동역);
    }
    @DisplayName("구간 제거 - 예외")
    @Test
    void removeSection_exception() {
        //given
        line.addSection(LineSection.of(line, 창동역, 사당역, 3));
        //when
        //then
        Assertions.assertThrows(BadRequestException.class,
                () -> line.removeSection(창동역));
    }
}
