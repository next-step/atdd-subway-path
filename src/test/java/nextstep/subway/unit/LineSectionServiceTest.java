package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.common.exception.NoDeleteOneSectionException;
import nextstep.subway.common.exception.NoRegisterStationException;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static nextstep.subway.common.error.SubwayError.NO_DELETE_ONE_SECTION;
import static nextstep.subway.common.error.SubwayError.NO_REMOVE_NOT_REGISTER_LINE_STATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 구간의 대한 테스트")
@SpringBootTest
@Transactional
class LineSectionServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;

    private static Station 강남역;
    private static Station 양재역;
    private static Station 몽촌토성역;
    private static Station 검암역;

    @BeforeEach
    void setUp() {
        강남역 = createStation("강남역");
        양재역 = createStation("양재역");
        몽촌토성역 = createStation("몽촌토성역");
        검암역 = createStation("검암역");
    }

    @DisplayName("노선의 구간을 생성한다.")
    @Test
    void addSection() {
        final Line saveLine = createLine("2호선", "green");
        addSection(saveLine, 강남역, 양재역, 10);

        final List<Station> stations = saveLine.convertToStation();
        assertAll(
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations).containsExactly(강남역, 양재역)
        );
    }

    @DisplayName("지하철 노선에 상행종점역 삭제한다.")
    @Test
    void removeLineUpStation() {
        final Section first = new Section(강남역, 양재역, 10);
        final Section second = new Section(양재역, 몽촌토성역, 5);
        final Line saveLine = createLineBySections("2호선", "green", List.of(first, second));

        lineService.deleteSection(saveLine.getId(), 강남역.getId());

        final List<Station> stations = saveLine.convertToStation();
        assertAll(
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations).containsExactly(양재역, 몽촌토성역)
        );
    }

    @DisplayName("지하철 노선에 중간역 삭제한다.")
    @Test
    void removeLineMiddleStation() {
        final Section first = new Section(강남역, 양재역, 10);
        final Section second = new Section(양재역, 몽촌토성역, 5);
        final Line saveLine = createLineBySections("2호선", "green", List.of(first, second));

        lineService.deleteSection(saveLine.getId(), 양재역.getId());

        final List<Station> stations = saveLine.convertToStation();
        assertAll(
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations).containsExactly(강남역, 몽촌토성역)
        );
    }

    @DisplayName("지하철 노선에 하행종점역 삭제한다.")
    @Test
    void removeLineDownStation() {
        final Section first = new Section(강남역, 양재역, 10);
        final Section second = new Section(양재역, 몽촌토성역, 5);
        final Line saveLine = createLineBySections("2호선", "green", List.of(first, second));

        lineService.deleteSection(saveLine.getId(), 몽촌토성역.getId());

        final List<Station> stations = saveLine.convertToStation();
        assertAll(
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations).containsExactly(강남역, 양재역)
        );
    }

    @DisplayName("노선 구간 제거 시 구간이 하나인 경우 삭제 불가로 구간 삭제가 불가능하다.")
    @Test
    void error_removeSection() {
        final Section first = new Section(강남역, 양재역, 10);
        final Line saveLine = createLineBySections("2호선", "green", List.of(first));

        assertThatThrownBy(() -> lineService.deleteSection(saveLine.getId(), 양재역.getId()))
                .isInstanceOf(NoDeleteOneSectionException.class)
                .hasMessage(NO_DELETE_ONE_SECTION.getMessage());
    }

    @DisplayName("노선 구간 제거 시 노선에 등록되지 않은 역은 삭제가 불가능하다.")
    @Test
    void error_removeSection_2() {
        final Section first = new Section(강남역, 양재역, 10);
        final Section second = new Section(양재역, 몽촌토성역, 5);
        final Line saveLine = createLineBySections("2호선", "green", List.of(first, second));

        assertThatThrownBy(() -> lineService.deleteSection(saveLine.getId(), 검암역.getId()))
                .isInstanceOf(NoRegisterStationException.class)
                .hasMessage(NO_REMOVE_NOT_REGISTER_LINE_STATION.getMessage());
    }

    private void addSection(final Line saveLine, final Station upStation, final Station downStation, final Integer distance) {
        final SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), distance);
        lineService.addSection(saveLine.getId(), sectionRequest);
    }

    private Station createStation(final String station) {
        return stationRepository.save(new Station(station));
    }

    private Line createLine(final String name, final String color) {
        return lineRepository.save(new Line(name, color));
    }

    private Line createLineBySections(final String name, final String color, final List<Section> sections) {
        return lineRepository.save(new Line(name, color, new Sections(sections)));
    }
}
