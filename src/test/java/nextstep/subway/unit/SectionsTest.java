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

public class SectionsTest {
    private static final int DEFAULT_DISTANCE = 5;
    private static final String FIRST_STATION_NAME = "강남역";
    private static final String SECOND_STATION_NAME = "역삼역";
    private static final String THIRD_STATION_NAME = "삼성역";
    private static final String FOURTH_STATION_NAME = "잠실역";

    private Line line;

    @BeforeEach
    void setUp() {
        line = createLine();
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        // given
        Sections sections = createSections();
        Station 강남역 = createStation(FIRST_STATION_NAME);
        Station 역삼역 = createStation(SECOND_STATION_NAME);
        Section section = createSection(강남역, 역삼역);

        // when
        sections.addSection(section);

        // then
        assertThat(sections.getSections()).hasSize(1);
    }

    @DisplayName("구간을 추가할 때 상행역과 하행역 모두 등록되어있지 않으면 예외 처리")
    @Test
    void addSectionNoneMatchStationException() {
        // given
        Station 강남역 = createStation(FIRST_STATION_NAME);
        Station 역삼역 = createStation(SECOND_STATION_NAME);
        Station 삼성역 = createStation(THIRD_STATION_NAME);
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
        Station 강남역 = createStation(FIRST_STATION_NAME);
        Station 역삼역 = createStation(SECOND_STATION_NAME);
        Station 삼성역 = createStation(THIRD_STATION_NAME);
        Sections sections = createSections(createSection(강남역, 역삼역), createSection(역삼역, 삼성역));
        Section section = createSection(강남역, 역삼역);

        // when, then
        assertThatThrownBy(() -> sections.addSection(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 모두 등록된 상태입니다.");
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        Station 강남역 = createStation(FIRST_STATION_NAME);
        Station 역삼역 = createStation(SECOND_STATION_NAME);
        Station 삼성역 = createStation(THIRD_STATION_NAME);
        Station 잠실역 = createStation(FOURTH_STATION_NAME);
        Sections sections = createSections(createSection(강남역, 역삼역), createSection(역삼역, 삼성역));
        Section section = createSection(삼성역, 잠실역);

        sections.addSection(section);

        // when
        List<Station> stations = sections.getStations();

        // then
        assertThat(stations).hasSize(4);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        // given
        Sections sections = createSections();
        Station 강남역 = createStation(FIRST_STATION_NAME);
        Station 역삼역 = createStation(SECOND_STATION_NAME);
        Section section = createSection(강남역, 역삼역);

        sections.addSection(section);

        // then
        sections.deleteSection(역삼역);

        // when
        assertThat(sections.getSections()).hasSize(0);
    }


    private Station createStation(String name) {
        return new Station(name);
    }

    private Section createSection(Station upStation, Station downStation) {
        return new Section(line, upStation, downStation, DEFAULT_DISTANCE);
    }

    private Line createLine() {
        return new Line("2호선", "bg-green-700");
    }

    private Sections setUpSections() {
        Station 강남역 = createStation(FIRST_STATION_NAME);
        Station 역삼역 = createStation(SECOND_STATION_NAME);
        Station 잠실역 = createStation(THIRD_STATION_NAME);
        return createSections(createSection(강남역, 역삼역), createSection(역삼역, 잠실역));
    }

    private Sections createSections(Section ...sections) {
        return new Sections(new ArrayList<>(Arrays.asList(sections)));
    }
}
