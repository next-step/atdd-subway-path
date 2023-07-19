package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.DuplicateSectionException;
import nextstep.subway.exception.InvalidDistanceException;
import nextstep.subway.exception.NoConnectedSectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {
    @DisplayName("지하철 라인에 구간을 추가한다")
    @Test
    void addSection() {
        // given
        Line line = new Line("1호선", "남색");
        Station upStation = new Station("upStation");
        Station downStation = new Station("downStation");
        int distance = 10;

        // when
        line.addSection(Section.of(line, upStation, downStation, distance));

        // then
        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @DisplayName("지하철 라인 구간에서 가장 앞에 역을 추가한다")
    @Test
    void addSectionAtFirst() {
        // given
        Line line = new Line("1호선", "남색");
        Station upStation = new Station("upStation");
        Station downStation = new Station("downStation");
        Station firstStation = new Station("firstStation");
        int distance = 10;
        line.addSection(Section.of(line, upStation, downStation, distance));

        // when
        line.addSection(Section.of(line, firstStation, upStation, distance));

        // then
        assertThat(CollectionUtils.firstElement(line.getSections()).getUpStation()).isEqualTo(firstStation);
    }

    @DisplayName("지하철 라인 구간에서 가장 마지막에 역을 추가한다")
    @Test
    void addSectionAtLast() {
        // given
        Line line = new Line("1호선", "남색");
        Station upStation = new Station("upStation");
        Station downStation = new Station("downStation");
        Station lastStation = new Station("lastStation");
        int distance = 10;
        line.addSection(Section.of(line, upStation, downStation, distance));

        // when
        line.addSection(Section.of(line, downStation, lastStation, distance));

        // then
        assertThat(CollectionUtils.lastElement(line.getSections()).getDownStation()).isEqualTo(lastStation);
    }

    @DisplayName("지하철 라인 구간에서 상행역 기준으로 다음에 추가한다")
    @Test
    void addSectionAtUpStationNext() {
        // given
        Line line = new Line("1호선", "남색");
        Station upStation = new Station("upStation");
        Station downStation = new Station("downStation");
        Station station = new Station("station");
        int distance = 6;
        line.addSection(Section.of(line, upStation, downStation, 10));

        // when
        line.addSection(Section.of(line, upStation, station, distance));

        // then
        List<Station> stations = line.getSections().stream().map(Section::getUpStation).collect(Collectors.toList());
        stations.add(CollectionUtils.lastElement(line.getSections()).getDownStation());
        assertThat(stations).containsExactly(upStation, station, downStation);
    }

    @DisplayName("지하철 라인 구간에서 하행역 기준으로 이전에 추가한다")
    @Test
    void addSectionAtUpStationPrev() {
        // given
        Line line = new Line("1호선", "남색");
        Station upStation = new Station("upStation");
        Station downStation = new Station("downStation");
        Station station = new Station("station");
        int distance = 6;
        line.addSection(Section.of(line, upStation, downStation, 10));

        // when
        line.addSection(Section.of(line, station, downStation, distance));

        // then
        List<Station> stations = line.getSections().stream().map(Section::getUpStation).collect(Collectors.toList());
        stations.add(CollectionUtils.lastElement(line.getSections()).getDownStation());
        assertThat(stations).containsExactly(upStation, station, downStation);
    }

    @DisplayName("지하철 라인 구간에서 동일한 구간 길이로 등록하면 에러 발생한다")
    @Test
    void addSectionBySameDistance() {
        // given
        Line line = new Line("1호선", "남색");
        Station upStation = new Station("upStation");
        Station downStation = new Station("downStation");
        Station station = new Station("station");
        int distance = 10;
        line.addSection(Section.of(line, upStation, downStation, distance));

        // when & then
        assertThrows(InvalidDistanceException.class,
                () -> line.addSection(Section.of(line, upStation, station, distance)));
    }

    @DisplayName("지하철 라인 구간에서 동일한 역으로 등록하면 에러 발생한다")
    @Test
    void addSectionByDuplicatedStations() {
        // given
        Line line = new Line("1호선", "남색");
        Station upStation = new Station("upStation");
        Station downStation = new Station("downStation");
        int distance = 10;
        line.addSection(Section.of(line, upStation, downStation, distance));

        // when & then
        assertThrows(DuplicateSectionException.class,
                () -> line.addSection(Section.of(line, upStation, downStation, distance)));
    }

    @DisplayName("지하철 라인 구간에서 연결되지 않은 역으로 등록하면 에러 발생한다")
    @Test
    void addSectionByNoConnectedStations() {
        // given
        Line line = new Line("1호선", "남색");
        Station upStation = new Station("upStation");
        Station downStation = new Station("downStation");
        Station noStation1 = new Station("noStation1");
        Station noStation2 = new Station("noStation2");
        int distance = 10;
        line.addSection(Section.of(line, upStation, downStation, distance));

        // when & then
        assertThrows(NoConnectedSectionException.class,
                () -> line.addSection(Section.of(line, noStation1, noStation2, distance)));
    }

    @DisplayName("지하철역을 조회한다")
    @Test
    void getStations() {
        // given
        Line line = new Line("1호선", "남색");
        Station station1 = new Station("station1");
        Station station2 = new Station("station2");
        Station station3 = new Station("station3");
        int distance = 10;
        line.addSection(Section.of(line, station1, station2, distance));
        line.addSection(Section.of(line, station2, station3, distance));

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).containsExactly(station1, station2, station3);
    }

    @DisplayName("지하철 라인에서 역을 제거한다")
    @Test
    void removeSection() {
        // given
        Line line = new Line("1호선", "남색");
        Station station1 = new Station("station1");
        Station station2 = new Station("station2");
        Station station3 = new Station("station3");
        int distance = 10;
        line.addSection(Section.of(line, station1, station2, distance));
        line.addSection(Section.of(line, station2, station3, distance));

        // when
        line.removeStation(station3);

        // then
        assertThat(line.getStations()).containsExactly(station1, station2);
    }
}
