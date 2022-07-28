package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    private Station 선릉역;

    private Station 삼성역;

    private Station 종합운동장역;

    private Line _2호선;

    @BeforeEach
    void setUp() {
        선릉역 = new Station("선릉역");
        삼성역 = new Station("삼성역");
        종합운동장역 = new Station("종합운동장역");
        _2호선 = new Line("2호선", "bg-green-600");
    }

    @DisplayName("구간 등록을 할 수 있다.")
    @Test
    void addSection() {
        // given
        final var section = new Section(_2호선, 선릉역, 삼성역, 10);

        // when
        _2호선.addSection(section);

        // then
        assertThat(_2호선.findAllStations()).containsExactly(선릉역, 삼성역);
    }

    @DisplayName("구간 등록 후 모든 역을 조회할 수 있다.")
    @Test
    void getStations() {
        // given
        final var section = new Section(_2호선, 선릉역, 삼성역, 10);
        _2호선.addSection(section);

        // when
        final var stations = _2호선.findAllStations();

        // then
        assertThat(stations).containsExactly(선릉역, 삼성역);

    }

    @DisplayName("하행 종착역 구간을 삭제할 수 있다.")
    @Test
    void removeSection() {
        // given
        final var firstSection = new Section(_2호선, 선릉역, 삼성역, 10);
        final var secondSection = new Section(_2호선, 삼성역, 종합운동장역, 5);

        _2호선.addSection(firstSection);
        _2호선.addSection(secondSection);

        // when
        _2호선.removeSection(종합운동장역);

        // then
        assertThat(_2호선.findAllStations()).containsExactly(선릉역, 삼성역);

    }

    @DisplayName("삭제하고자 하는 구간이 상행 종착역과 하행 종착역만이 있다면 삭제 시 에러가 발생한다.")
    @Test
    void removeExceptionWhenOnlyOneSectionExist() {
        // given
        final var section = new Section(_2호선, 선릉역, 삼성역, 10);
        _2호선.addSection(section);

        // when, then
        assertThatThrownBy(() -> _2호선.removeSection(삼성역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역만 존재하기 때문에 삭제할 수 없습니다.");

    }

}
