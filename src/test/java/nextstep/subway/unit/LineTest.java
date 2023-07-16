package nextstep.subway.unit;

import static nextstep.subway.utils.LineFixture.*;
import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

class LineTest {

    @DisplayName("지하철 노선에 구간을 추가한다.")
    @Test
    void addSection() {
        // given
        Line line = 라인_생성하고_구간을_등록한다();
        Section section = new Section(2L, line, 논현역(), 신논현역(), 10);

        // when
        line.addSection(section);

        // then
        assertThat(line.getStations()).usingRecursiveComparison()
            .isEqualTo(List.of(신사역(), 논현역(), 신논현역()));
    }

    @DisplayName("지하철 노선에 등록된 지하철역들을 반환한다.")
    @Test
    void getStations() {
        // given
        Line line = 라인_생성하고_구간을_등록한다();

        // when
        List<Station> actual = line.getStations();

        // then
        assertThat(actual).usingRecursiveComparison()
            .isEqualTo(List.of(신사역(), 논현역()));
    }

    @DisplayName("지하철 노선에 해당 구간을 삭제한다.")
    @Test
    void removeSection() {
        // given
        Line line = 라인_생성하고_구간을_등록한다();
        line.addSection(new Section(2L, line, 논현역(), 신논현역(), 15));

        // when
        line.removeSection(신논현역().getId());

        // then
        assertThat(line.getSectionSize()).isEqualTo(1);
    }

    private Line 라인_생성하고_구간을_등록한다() {
        Line line = new Line(1L, 신분당선_이름, 신분당선_이름);
        Section section = new Section(1L, line, 신사역(), 논현역(), 10);
        line.addSection(section);
        return line;
    }

    private Station 신사역() {
        return new Station(1L, 신사역);
    }

    private Station 논현역() {
        return new Station(2L, 논현역);
    }

    private Station 신논현역() {
        return new Station(3L, 신논현역);
    }
}
