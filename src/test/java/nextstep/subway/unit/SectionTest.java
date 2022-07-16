package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionTest {

    @Test
    void 구간을_생성한다() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("암사역");
        Station downStation = new Station("모란역");

        // when
        Section section = new Section(line, upStation, downStation, 10);

        // then
        assertAll(() -> {
            assertThat(section.getUpStation()).isEqualTo(upStation);
            assertThat(section.getDownStation()).isEqualTo(downStation);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -2})
    void 구간을_생성시_거리가_1_미만이면_예외를_일으킨다(int input) {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("암사역");
        Station downStation = new Station("모란역");

        // when
        assertThatIllegalArgumentException().isThrownBy(() ->
                new Section(line, upStation, downStation, input)
        );
    }

    @Test
    void 하행역을_비교하여_같으면_참을_반환한다() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("암사역");
        Station downStation = new Station("모란역");
        Section section = new Section(line, upStation, downStation, 10);

        // when
        boolean matchDownStation = section.matchDownStation(downStation);

        // then
        assertThat(matchDownStation).isTrue();
    }

    @Test
    void 하행역을_비교하여_다르면_거짓을_반환한다() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("암사역");
        Station downStation = new Station("모란역");
        Section section = new Section(line, upStation, downStation, 10);

        // when
        boolean matchDownStation = section.matchDownStation(upStation);

        // then
        assertThat(matchDownStation).isFalse();
    }

}
