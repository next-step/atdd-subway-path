package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    @Test
    @DisplayName("지하철노선에 구간을 추가한다")
    void addSection() {
        // given
        Line line = new Line("신분당선", "reg-bg-500");
        Station upStation = new Station("지하철역");
        Station downStation = new Station("또다른지하철역");

        // when
        line.addSection(new Section(line, upStation, downStation, 10));

        // then
        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("지하철 노선의 역목록을 조회한다")
    void getStations() {
        // given
        Line line = new Line("신분당선", "reg-bg-500");
        Station upStation = new Station("지하철역");
        Station downStation = new Station("또다른지하철역");

        // when
        line.addSection(new Section(line, upStation, downStation, 10));

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation);
    }

    @Test
    @DisplayName("지하철 노선에 구간을 제거한다")
    void removeSection() {
        // given
        Line line = new Line("신분당선", "reg-bg-500");
        Station upStation = new Station("지하철역");
        Station downStation = new Station("또다른지하철역");
        line.addSection(new Section(line, upStation, downStation, 10));

        // when
        line.removeSection();

        // then
        assertThat(line.getSections().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 역목록 조회")
    void getStationsExistStation() {
        Line line = new Line("신분당선", "bg-red-600");
        Station 지하철역1 = new Station("지하철역1");
        Station 지하철역2 = new Station("지하철역2");
        Station 지하철역3 = new Station("지하철역3");
        Station 지하철역4 = new Station("지하철역4");

        line.addSection(new Section(line, 지하철역1, 지하철역2, 3));
        line.addSection(new Section(line, 지하철역2, 지하철역3, 10));
        line.addSection(new Section(line, 지하철역1, 지하철역4, 10));

        List<Station> stations = line.getStations();
        assertThat(stations.stream().map(Station::getName).collect(Collectors.toList()))
                .containsExactly("지하철역1", "지하철역4", "지하철역2", "지하철역3");
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 구간보다 같거나 큰 경우 에러 발생한다")
    void addSectionDistanceGreaterOrEqual() {
        // given
        Line line = new Line("신분당선", "reg-bg-500");
        Station 지하철역 = new Station("지하철역");
        Station 또다른_지하철역 = new Station("또다른지하철역");
        Station 다른_지하철역 = new Station("다른지하철역");

        line.addSection(new Section(line, 지하철역, 또다른_지하철역, 10));
        assertThatThrownBy(() -> line.addSection(new Section(line, 지하철역, 다른_지하철역, 10)))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> line.addSection(new Section(line, 지하철역, 다른_지하철역, 11)))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    @DisplayName("이미 등록된 상행역과 하행역을 가진 구간을 등록하는 경우 에러가 발생한다")
    void addSectionExistUpStationAndDownStation() {
        // given
        Line line = new Line("신분당선", "reg-bg-500");
        Station 지하철역 = new Station("지하철역");
        Station 또다른_지하철역 = new Station("또다른지하철역");
        line.addSection(new Section(line, 지하철역, 또다른_지하철역, 7));

        // when, then
        assertThatThrownBy(() -> line.addSection(new Section(line, 지하철역, 또다른_지하철역, 6)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message().isEqualTo("상행역과 하행역이 이미 등록된 구간입니다.");
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되지 않은 구간을 노선에 등록하면 에러가 발생한다")
    void addIncompleteLineSection() {
        Line line = new Line("신분당선", "reg-bg-500");
        Station 지하철역 = new Station("지하철역");
        Station 또다른_지하철역 = new Station("또다른지하철역");
        line.addSection(new Section(line, 지하철역, 또다른_지하철역, 7));

        // given
        Station 불광역 = new Station("불광역");
        Station 독바위역 = new Station("독바위역");

        // when, then
        assertThatThrownBy(() -> line.addSection(new Section(line, 불광역, 독바위역, 6)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message().isEqualTo("잘못된 지하철 구간 등록입니다.");
    }
}
