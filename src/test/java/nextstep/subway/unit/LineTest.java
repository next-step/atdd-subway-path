package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        // given
        Line line = 지하철_라인_역_샘플();
        Station newUpStation = new Station( "사당역");
        Station newDownStation = new Station("신도림역");
        line.addSection(newUpStation, newDownStation, 30);

        // when
        Sections sections = line.getSections();

        // then
        assertThat(sections.count()).isEqualTo(2);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        Line line = 지하철_라인_역_샘플();
        Station newUpStation = new Station( "사당역");
        Station newDownStation = new Station("왕심리역");
        line.addSection(newUpStation, newDownStation, 50);

        // when
        Sections sections = line.getSections();

        // then
        assertThat(sections.count()).isEqualTo(2);

    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        // given
        Line line = 지하철_라인_역_샘플();
        Station newUpStation = new Station( "사당역");
        Station newDownStation = new Station("왕심리역");

        line.addSection(newUpStation, newDownStation, 50);

        // when
        line.removeSection(newDownStation);

        // then
        assertThat(line.getSections().count()).isEqualTo(1);
    }

    private Line 지하철_라인_역_샘플() {
        Station upStation = new Station( "강남역");
        Station downStation = new Station( "사당역");
        Line line = new Line("2호선", "green");
        line.addSection(upStation, downStation, 30);
        return line;
    }
}
