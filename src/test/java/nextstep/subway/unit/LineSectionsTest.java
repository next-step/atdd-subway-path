package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineSections;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineSectionsTest {
    private Line 신분당선;
    private Station 지하철역;
    private Station 또다른_지하철역;
    private LineSections 지하철노선_구간;

    // given 지하철역과 노선, 지하철구간을 생성
    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "reg-bg-500");
        지하철역 = new Station("지하철역");
        또다른_지하철역 = new Station("또다른지하철역");
        지하철노선_구간 = new LineSections(신분당선);
    }

    @Test
    @DisplayName("지하철 노선 구간에 구간을 추가한다")
    void addSection() {
        // when
        지하철노선_구간.addSection(new Section(신분당선, 지하철역, 또다른_지하철역, 10));

        // then
        assertThat(지하철노선_구간.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("지하철 노선 구간에 새로운역을 하행종점으로 등록한다")
    void addSectionDownStation() {
        // given
        지하철노선_구간.addSection(new Section(신분당선, 지하철역, 또다른_지하철역, 10));
        Station 다른지하철역 = new Station("다른지하철역");

        // when
        지하철노선_구간.addSection(new Section(신분당선, 또다른_지하철역, 다른지하철역, 5));

        // then
        assertThat(지하철노선_구간.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("지하철 노선 구간에 새로운역을 상행종점으로 등록한다")
    void addSectionUpStation() {
        // given
        지하철노선_구간.addSection(new Section(신분당선, 지하철역, 또다른_지하철역, 10));
        Station 다른_지하철역 = new Station("다른지하철역");

        // when
        지하철노선_구간.addSection(new Section(신분당선, 다른_지하철역, 지하철역, 15));

        // then
        assertThat(지하철노선_구간.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("지하철 노선의 역목록을 조회한다")
    void getStations() {
        // when
        지하철노선_구간.addSection(new Section(신분당선, 지하철역, 또다른_지하철역, 10));

        // then
        assertThat(지하철노선_구간.getStations()).containsExactly(지하철역, 또다른_지하철역);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 구간보다 같거나 큰 경우 에러 발생한다")
    void addSectionDistanceGreaterOrEqual() {
        // given
        Station 다른_지하철역 = new Station("다른지하철역");

        // when, then
        지하철노선_구간.addSection(new Section(신분당선, 지하철역, 또다른_지하철역, 10));
        assertThatThrownBy(() -> 지하철노선_구간.addSection(new Section(신분당선, 지하철역, 다른_지하철역, 10)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message().isEqualTo("기존 역 사이 길이보다 크거나 같을 수 없습니다.");
        assertThatThrownBy(() -> 지하철노선_구간.addSection(new Section(신분당선, 지하철역, 다른_지하철역, 11)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message().isEqualTo("기존 역 사이 길이보다 크거나 같을 수 없습니다.");
    }

    @Test
    @DisplayName("이미 등록된 상행역과 하행역을 가진 구간을 등록하는 경우 에러가 발생한다")
    void addSectionExistUpStationAndDownStationCaseOne() {
        // given
        지하철노선_구간.addSection(new Section(신분당선, 지하철역, 또다른_지하철역, 7));

        // when, then
        assertThatThrownBy(() -> 지하철노선_구간.addSection(new Section(신분당선, 지하철역, 또다른_지하철역, 6)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message().isEqualTo("상행역과 하행역이 이미 등록된 구간입니다.");
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되지 않은 구간을 지하철 노선 구간에 등록하면 에러가 발생한다")
    void addSectionUpStationOrDownStationNotContain() {
        // given
        지하철노선_구간.addSection(new Section(신분당선, 지하철역, 또다른_지하철역, 7));
        Station 불광역 = new Station("불광역");
        Station 독바위역 = new Station("독바위역");

        // when, then
        assertThatThrownBy(() -> 지하철노선_구간.addSection(new Section(신분당선, 불광역, 독바위역, 6)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message().isEqualTo("잘못된 지하철 구간 등록입니다.");
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 지하철 노선 구간에 등록되어 있는 경우 에러 발생한다")
    void addSectionExistUpStationAndDownStationCaseTwo() {
        // given
        Station 다른_지하철역 = new Station("다른지하철역");
        지하철노선_구간.addSection(new Section(신분당선, 지하철역, 또다른_지하철역, 4));
        지하철노선_구간.addSection(new Section(신분당선, 또다른_지하철역, 다른_지하철역, 3));

        // when, then
        assertThatThrownBy(() -> 지하철노선_구간.addSection(new Section(신분당선, 지하철역, 다른_지하철역, 3)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message().isEqualTo("상행역과 하행역이 이미 등록된 구간입니다.");
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 역목록 조회")
    void getStationsExistStation() {
        // given
        Line 신분당선 = new Line("신분당선", "bg-red-600");
        Station 지하철역1 = new Station("지하철역1");
        Station 지하철역2 = new Station("지하철역2");
        Station 지하철역3 = new Station("지하철역3");
        Station 지하철역4 = new Station("지하철역4");

        // when
        지하철노선_구간.addSection(new Section(신분당선, 지하철역1, 지하철역2, 13));
        지하철노선_구간.addSection(new Section(신분당선, 지하철역2, 지하철역3, 10));
        지하철노선_구간.addSection(new Section(신분당선, 지하철역1, 지하철역4, 10));

        // then
        List<Station> stations = 지하철노선_구간.getStations();
        assertThat(stations.stream().map(Station::getName).collect(Collectors.toList()))
                .containsExactly("지하철역1", "지하철역4", "지하철역2", "지하철역3");
    }

    @Test
    @DisplayName("지하철 노선 마지막 구간을 제거한다")
    void removeLastLineSection() {
        // given
        Station 다른_지하철역 = new Station("다른지하철역");
        지하철노선_구간.addSection(new Section(신분당선, 지하철역, 또다른_지하철역, 10));
        지하철노선_구간.addSection(new Section(신분당선, 또다른_지하철역, 다른_지하철역, 10));

        // when
        지하철노선_구간.removeStation(다른_지하철역);

        // then
        assertThat(지하철노선_구간.size()).isEqualTo(1);
    }

    @DisplayName("지하철 노선에 중간에 위치한 구간을 제거한다")
    @Test
    void removeMiddleLineSection() {
        // given
        Station 다른_지하철역 = new Station("다른지하철역");
        지하철노선_구간.addSection(new Section(신분당선, 지하철역, 또다른_지하철역, 10));
        지하철노선_구간.addSection(new Section(신분당선, 또다른_지하철역, 다른_지하철역, 10));

        // when
        지하철노선_구간.removeStation(또다른_지하철역);

        // then
        List<Station> stations = 지하철노선_구간.getStations();
        assertThat(stations.stream().map(Station::getName).collect(Collectors.toList()))
                .containsExactly("지하철역", "다른지하철역");
        assertThat(지하철노선_구간.getLineSectionsTotalDistance()).isEqualTo(20);
    }

    @DisplayName("지하철 노선에 처음에 위치한 구간을 제거한다")
    @Test
    void removeFirstLineSection() {
        // given
        Station 다른_지하철역 = new Station("다른지하철역");
        지하철노선_구간.addSection(new Section(신분당선, 지하철역, 또다른_지하철역, 10));
        지하철노선_구간.addSection(new Section(신분당선, 또다른_지하철역, 다른_지하철역, 10));

        // when
        지하철노선_구간.removeStation(지하철역);

        // then
        List<Station> stations = 지하철노선_구간.getStations();
        assertThat(stations.stream().map(Station::getName).collect(Collectors.toList()))
                .containsExactly("또다른지하철역", "다른지하철역");
    }

    @DisplayName("구간이 하나인 지하철 노선에 구간을 제거할때 에러가 발생한다")
    @Test
    void removeOneLineSectionException() {
        // given
        지하철노선_구간.addSection(new Section(신분당선, 지하철역, 또다른_지하철역, 10));

        // when, then
        assertThatThrownBy(() -> 지하철노선_구간.removeStation(또다른_지하철역))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("지하철 노선에 등록되지 않은 구간을 제거하면 에러가 발생한다")
    @Test
    void removeNonexistentLineSection() {
        // given
        Station 없는_지하철역 = new Station("없는지하철역");
        Station 다른_지하철역 = new Station("다른지하철역");
        지하철노선_구간.addSection(new Section(신분당선, 지하철역, 또다른_지하철역, 10));
        지하철노선_구간.addSection(new Section(신분당선, 또다른_지하철역, 다른_지하철역, 10));

        // when, then
        assertThatThrownBy(() -> 지하철노선_구간.removeStation(없는_지하철역))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
