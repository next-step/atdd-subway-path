package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.line.Line;
import nextstep.subway.line.Section;
import nextstep.subway.line.Sections;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("세션을 성공적으로 추가한다")
    @Test
    void addSectionSuccess() {
        // given
        Line line = new Line();
        Section section = new Section();

        // when
        line.addSection(section);

        // then
        assertThat(line.getSections().getSections().contains(section)).isTrue();
    }

    @DisplayName("추가할 세션의 하행 스테이션이 노선의 상행 종점일 때 추가할 세션이 상행 좀점 세션으로 등록된다")
    @Test
    void insertSectionSuccessToTop() {
        // given
        Line line = new Line();
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        Section targetSection = new Section(강남역, 판교역, 1);
        line.addSection(targetSection);
        Station 교대역 = new Station("교대역");
        Section insertSection = new Section(교대역, 강남역, 1);

        // when
        line.addSection(insertSection);

        // then
        Assertions.assertAll(
                () -> assertThat(line.getSections().getSections().get(0)).isEqualTo(insertSection),
                () -> assertThat(line.getSections().getSections().get(1)).isEqualTo(targetSection)
        );
    }

    @DisplayName("추가할 세션의 상행 스테이션이 추가될 노선에 포함되고 추가될 위치의 하행스테이션과 똑같이 아니면 그사이에 추가된다")
    @Test
    void insertSectionSuccessBySameUpStationOfSection() {
        // given
        Line line = new Line();
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        Section targetSection = new Section(강남역, 판교역, 10);
        line.addSection(targetSection);
        Station 양재역 = new Station("양재역");
        Section insertSection = new Section(강남역, 양재역, 1);

        // when
        line.addSection(insertSection);

        // then
        Sections sections = line.getSections();
        assertThat(sections.getSections())
                .containsOnly(new Section(강남역, 양재역, 1), new Section(양재역, 판교역, 9));
    }

    @DisplayName("노선에 포함한 스테이션을 가져온다")
    @Test
    void getStations() {
        // given
        Station gangnamStation = new Station("강남역");
        Station yangjaeStation = new Station("양재역");
        Line line = new Line();
        Section section = new Section(gangnamStation, yangjaeStation, 10);
        line.addSection(section);

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).containsExactly(gangnamStation, yangjaeStation);
    }

    @DisplayName("노선에서 등록된 station을 성공적으로 삭제한다")
    @Test
    void deleteSectionSuccess() {
        // given
        Station gangnamStation = new Station("강남역");
        Station yangjaeStation = new Station("양재역");
        Station pangyoStation = new Station("판교역");
        Line line = new Line();
        Section section = new Section(gangnamStation, yangjaeStation, 10);
        line.addSection(section);
        section = new Section(yangjaeStation, pangyoStation, 10);
        line.addSection(section);

        // when
        line.deleteSection(pangyoStation);

        // then
        boolean exists = line.getSections().getSections().stream()
                .anyMatch(savedSection -> savedSection.containsStation(pangyoStation));
        assertThat(exists).isFalse();
    }

    @DisplayName("노선의 이름과 컬러를 업데이트 한다")
    @Test
    void update() {
        // given
        Line line = new Line("신분당선", "#D31145", new ArrayList<>());

        // when
        line.update("강남선", "#D31146");

        // then
        Assertions.assertAll(
                () -> assertThat(line.getName()).isEqualTo("강남선"),
                () -> assertThat(line.getColor()).isEqualTo("#D31146")
        );
    }

}
