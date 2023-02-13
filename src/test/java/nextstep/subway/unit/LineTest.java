package nextstep.subway.unit;


import nextstep.subway.common.ErrorMessage;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 노선도 테스트")
class LineTest {

    Line line;
    Station firstStation;
    Station secondStation;
    Section section;


    @BeforeEach
    void init() {
        line = new Line("신분당선", "red");
        firstStation = new Station("강남역");
        secondStation = new Station("판교역");
        section = new Section(line, firstStation, secondStation, 10);

        line.addSections(section);
    }

    @Test
    @DisplayName("지하철노선을 추가할 수 있다.")
    void addSection() {
        Section retriveSection = line.getSections().get(0);
        assertThat(retriveSection).isEqualTo(section);
    }

    @Test
    @DisplayName("강남 - 판교 사이에 양재역을 추가하여서 강남 - 양재 -판교 구간을 만들 수 있다.")
    void addSectionCase_1() {
        Station lastStation = new Station("양재역");
        section = new Section(line, firstStation, lastStation, 5);
        line.addSections(section);

        List<String> names = line.getStations().stream().map(Station::getName).collect(Collectors.toList());
        assertThat(names).containsExactly("강남역","양재역","판교역");
    }

    @Test
    @DisplayName("서초 - 강남 구간을 추가해서 서초 - 강남 -판교 구간을 만들 수 있다.")
    void addSectionCase_2() {
        Station addStation = new Station("서초역");
        section = new Section(line, addStation, firstStation, 5);
        line.addSections(section);

        List<String> names = line.getStations().stream().map(Station::getName).collect(Collectors.toList());
        assertThat(names).containsExactly("서초역", "강남역","판교역");
    }

    @Test
    @DisplayName("판교 - 청계산 구간을 추가해서 강남 - 판교 - 청계산 구간을 만들 수 있다.")
    void addSectionCase_3() {
        Station addStation = new Station("청계산역");
        section = new Section(line, secondStation, addStation, 5);
        line.addSections(section);

        List<String> names = line.getStations().stream().map(Station::getName).collect(Collectors.toList());
        assertThat(names).containsExactly( "강남역","판교역","청계산역");
    }

    @Test
    @DisplayName("역사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
    void addSectionFail_1() {
        Station addStation = new Station("청계산역");
        section = new Section(line, firstStation, addStation, 10);
        assertThatThrownBy(() -> line.addSections(section))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.INVALID_DISTANCE.toString());
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 등록되있으면 등록할 수 없다.")
    void addSectionFail_2() {
        section = new Section(line, secondStation, firstStation, 10);
        assertThatThrownBy(() -> line.addSections(section))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.DUPLICATED_STATION.toString());
    }

    @Test
    @DisplayName("상행역과 하행역 중 하나라도 노선에 포함되어 있지 않으면 추가할 수 없다.")
    void addSectionFail_3() {
        Station addStation = new Station("청계산역");
        Station addTwoStation = new Station("양재역");
        section = new Section(line, addTwoStation, addStation, 10);
        assertThatThrownBy(() -> line.addSections(section))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.NOT_CONNECT_STATION.toString());
    }


    @Test
    @DisplayName("지하철 노선을 조회할 수 있따.")
    void getStations() {
        List<Station> stations = line.getStations();

        assertThat(stations).hasSize(2);
        assertThat(stations).containsAnyOf(firstStation);
        assertThat(stations).containsAnyOf(secondStation);
    }

    @Test
    @DisplayName("지하철 노선의 구간은 1개 이상 유지되어야 한다.")
    void removeSectionFail() {
        assertThatThrownBy(() -> line.removeStation(secondStation))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.ENOUGH_NOT_SECTION_SIZE.toString());
    }

    @Test
    @DisplayName("지하철 노선의 구간은 하행종점역이 아닌 역은 삭제할 수 없다.")
    void removeSectionFail2() {
        Station lastStation = new Station("청계산역");
        section = new Section(line, secondStation, lastStation, 10);
        line.addSections(section);

        assertThatThrownBy(() -> line.removeStation(secondStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.ENOUGH_REMOVE_DOWN.toString());
    }

    @Test
    @DisplayName("지하철 노선의 하행종점역을 삭제할 수 있다.")
    void removeSection() {
        Station lastStation = new Station("청계산역");
        section = new Section(line, secondStation, lastStation, 10);
        line.addSections(section);

        line.removeStation(lastStation);

        List<Station> stations = line.getStations();
        assertThat(stations).hasSize(2);
        assertThat(stations).containsAnyOf(firstStation);
        assertThat(stations).containsAnyOf(secondStation);
        assertThat(stations).doesNotContain(lastStation);
    }
}
