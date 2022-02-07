package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.fixture.LineFixture.*;
import static nextstep.subway.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Station upStation;
    private Station downStation;
    private Section section;
    private Line line;

    @BeforeEach
    void setUp() {
        upStation = new Station(강남역);
        downStation = new Station(판교역);
        line = new Line(신분당선, 빨강색);
        section = new Section(line, upStation, downStation, 강남_판교_거리);
        line.addSection(section);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        // given
        final Station extraStation = new Station(정자역);
        final Section section = new Section(line, downStation, extraStation, 판교_정자_거리);

        // when
        line.addSection(section);

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation, extraStation);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        assertThat(line.getStations()).containsExactly(upStation, downStation);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        // given
        final Station extraStation = new Station(정자역);
        final Section section = new Section(line, downStation, extraStation, 판교_정자_거리);
        line.addSection(section);

        // when
        line.removeSection(extraStation);

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation);
    }

    @DisplayName("노선 구간들을 반환")
    @Test
    void getSections() {
        // then
        assertThat(line.getSections()).containsExactly(section);
    }
}
