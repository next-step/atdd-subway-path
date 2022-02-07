package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("구간들 관리")
public class SectionsTest {
    private static final int DEFAULT_DISTANCE = 5;
    private static final String FIRST_STATION_NAME = "강남역";
    private static final String SECOND_STATION_NAME = "역삼역";
    private static final String THIRD_STATION_NAME = "삼성역";
    private static final String FOURTH_STATION_NAME = "잠실역";

    private static final Line DEFAULT_LINE = new Line("2호선", "bg-green-700");

    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Station 잠실역;

    @BeforeEach
    void setUp() {
        강남역 = createStation(FIRST_STATION_NAME);
        역삼역 = createStation(SECOND_STATION_NAME);
        삼성역 = createStation(THIRD_STATION_NAME);
        잠실역 = createStation(FOURTH_STATION_NAME);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가한다.")
    @Test
    void addSection() {
        // given
        Sections sections = createSections();
        Section section = createSection(강남역, 역삼역);

        // when
        sections.addSection(section);
        sections.addSection(createSection(역삼역, 삼성역));

        // then
        List<Station> stations = sections.getStations();
        assertAll(
                () -> assertThat(sections.getSections()).hasSize(2),
                () -> assertThat(stations).hasSize(3),
                () -> assertThat(stations.get(0)).isEqualTo(강남역),
                () -> assertThat(stations.get(1)).isEqualTo(역삼역),
                () -> assertThat(stations.get(2)).isEqualTo(삼성역)
        );
    }

    @DisplayName("구간을 추가할 때 상행역과 하행역 모두 등록되어있지 않으면 예외 처리")
    @Test
    void addSectionNoneMatchStationException() {
        // given
        Sections sections = createSections(createSection(강남역, 역삼역), createSection(역삼역, 삼성역));

        Station 동인천역 = createStation("동인천역");
        Station 춘천역 = createStation("춘천역");
        Section section = createSection(동인천역, 춘천역);

        // when, then
        assertThatThrownBy(() -> sections.addSection(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 모두 등록되지 않았습니다.");
    }

    @DisplayName("구간을 추가할 때 상행역과 하행역 모두 등록된 상태면 예외 처리")
    @Test
    void addSectionAllMatchStationException() {
        // given
        Sections sections = createSections(createSection(강남역, 역삼역), createSection(역삼역, 삼성역));
        Section section = createSection(강남역, 역삼역);

        // when, then
        assertThatThrownBy(() -> sections.addSection(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 모두 등록된 상태입니다.");
    }

    @DisplayName("기존 구간(A-C) 중간에 새로운 구간(A-B)을 추가할 경우, A-B-C로 정렬이 되어야한다.")
    @Test
    void addSectionInTheMiddle() {
        // given
        Sections sections = createSections();
        sections.addSection(createSection(강남역, 삼성역));

        Section newSection = createSection(강남역, 역삼역, 2);

        // when
        sections.addSection(newSection);

        // then
        List<Station> stations = sections.getStations();
        assertAll(
                () -> assertThat(stations).hasSize(3),
                () -> assertThat(stations.get(0)).isEqualTo(강남역),
                () -> assertThat(stations.get(1)).isEqualTo(역삼역),
                () -> assertThat(stations.get(2)).isEqualTo(삼성역),
                () -> assertThat(sections.getSections().get(0).getDistance()).isEqualTo(2),
                () -> assertThat(sections.getSections().get(1).getDistance()).isEqualTo(3)
        );
    }

    @DisplayName("기존 구간(A-B) 앞에 새로운 구간(C-A)을 추가할 경우, C-A-B로 정렬이 되어야한다.")
    @Test
    void addFirstSection() {
        // given
        Sections sections = createSections();
        sections.addSection(createSection(역삼역, 삼성역));

        Section newSection = createSection(강남역, 역삼역, 2);

        // when
        sections.addSection(newSection);

        // then
        List<Station> stations = sections.getStations();
        assertAll(
                () -> assertThat(stations).hasSize(3),
                () -> assertThat(stations.get(0)).isEqualTo(강남역),
                () -> assertThat(stations.get(1)).isEqualTo(역삼역),
                () -> assertThat(stations.get(2)).isEqualTo(삼성역),
                () -> assertThat(sections.getSections().get(0).getDistance()).isEqualTo(2),
                () -> assertThat(sections.getSections().get(1).getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        Sections sections = createSections(createSection(강남역, 역삼역), createSection(역삼역, 삼성역));
        Section section = createSection(삼성역, 잠실역);

        sections.addSection(section);

        // when
        List<Station> stations = sections.getStations();

        // then
        assertThat(stations).hasSize(4);
    }

    @DisplayName("구간 목록에서 마지막 구간 삭제")
    @Test
    void removeLastSection() {
        // given
        Sections sections = createSections(createSection(강남역, 역삼역), createSection(역삼역, 삼성역));

        // then
        sections.deleteSection(삼성역);

        // when
        assertAll(
                () -> assertThat(sections.getSections()).hasSize(1),
                () -> assertThat(sections.getStations()).containsExactly(강남역, 역삼역),
                () -> assertThat(sections.getSections().get(0).getDistance()).isEqualTo((DEFAULT_DISTANCE))
        );
    }

    @DisplayName("구간 목록에서 첫번째 구간 삭제")
    @Test
    void removeFirstSection() {
        // given
        Sections sections = createSections(createSection(강남역, 역삼역), createSection(역삼역, 삼성역));

        // then
        sections.deleteSection(강남역);

        // when
        assertAll(
                () -> assertThat(sections.getSections()).hasSize(1),
                () -> assertThat(sections.getStations()).containsExactly(역삼역, 삼성역),
                () -> assertThat(sections.getSections().get(0).getDistance()).isEqualTo((DEFAULT_DISTANCE))
        );
    }

    @DisplayName("구간 목록에서 중간 구간 삭제")
    @Test
    void removeMiddleSection() {
        // given
        Sections sections = createSections(createSection(강남역, 역삼역), createSection(역삼역, 삼성역));

        // then
        sections.deleteSection(역삼역);

        // when
        Section result = sections.getSections().get(0);
        assertAll(
                () -> assertThat(sections.getSections()).hasSize(1),
                () -> assertThat(sections.getStations()).containsExactly(강남역, 삼성역),
                () -> assertThat(result.getDistance()).isEqualTo((DEFAULT_DISTANCE * 2))
        );
    }

    @DisplayName("지하철 노선의 구간이 1개 이하일 때 제거 요청하면 예외처리")
    @Test
    void removeSectionMinimumSizeException() {
        // given
        Sections sections = createSections(createSection(강남역, 역삼역));

        // then, when
        assertThatThrownBy(() -> sections.deleteSection(역삼역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 적어 삭제할 수 없습니다.");
    }

    @DisplayName("지하철 노선에 등록되지 않은 구간을 제거 요청하면 예외처리")
    @Test
    void removeSectionNoneStationException() {
        // given
        Sections sections = createSections(createSection(강남역, 역삼역), createSection(역삼역, 삼성역));

        // then, when
        assertThatThrownBy(() -> sections.deleteSection(잠실역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않은 구간입니다.");
    }

    private Station createStation(String name) {
        return new Station(name);
    }

    private Section createSection(Station upStation, Station downStation) {
        return createSection(upStation, downStation, DEFAULT_DISTANCE);
    }

    private Section createSection(Station upStation, Station downStation, int distance) {
        return createSection(DEFAULT_LINE, upStation, downStation, distance);
    }

    private Section createSection(Line line, Station upStation, Station downStation, int distance) {
        return new Section(line, upStation, downStation, distance);
    }

    private Sections createSections(Section ...sections) {
        return new Sections(new ArrayList<>(Arrays.asList(sections)));
    }
}
