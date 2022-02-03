package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {
    private static int DEFAULT_DISTANCE = 5;
    private static String FIRST_STATION_NAME = "강남역";
    private static String SECOND_STATION_NAME = "양재역";
    private static String THIRD_STATION_NAME = "양재시민의숲";
    private static String FOURTH_STATION_NAME = "판교역";

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        // given
        Sections sections = createSections();
        Station upStation = createStation(FIRST_STATION_NAME);
        Station downStation = createStation(SECOND_STATION_NAME);
        Section section = createSection(upStation, downStation);

        // when
        sections.addSection(section);

        // then
        assertThat(sections.getSections()).hasSize(1);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        Sections sections = setUpSections();

        Station upStation = createStation(THIRD_STATION_NAME);
        Station downStation = createStation(FOURTH_STATION_NAME);
        Section section = createSection(upStation, downStation);

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
        Station upStation = createStation(FIRST_STATION_NAME);
        Station downStation = createStation(SECOND_STATION_NAME);
        Section section = createSection(upStation, downStation);

        sections.addSection(section);

        // then
        sections.deleteSection(downStation);

        // when
        assertThat(sections.getSections()).hasSize(0);
    }


    private Station createStation(String name) {
        return new Station(name);
    }

    private Section createSection(Station upStation, Station downStation) {
        return new Section(createLine(), upStation, downStation, DEFAULT_DISTANCE);
    }

    private Line createLine() {
        return new Line("2호선", "bg-green-700");
    }

    private Sections setUpSections() {
        Station station1 = createStation(FIRST_STATION_NAME);
        Station station2 = createStation(SECOND_STATION_NAME);
        Station station3 = createStation(THIRD_STATION_NAME);
        return createSections(createSection(station1, station2), createSection(station2, station3));
    }

    private Sections createSections(Section ...sections) {
        return new Sections(new ArrayList<>(Arrays.asList(sections)));
    }
}
