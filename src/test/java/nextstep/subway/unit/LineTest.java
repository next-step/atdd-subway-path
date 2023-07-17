package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.line.DownstreamStationIncludedException;
import nextstep.subway.line.Line;
import nextstep.subway.line.MismatchedUpstreamStationException;
import nextstep.subway.line.NonDownstreamTerminusException;
import nextstep.subway.line.Section;
import nextstep.subway.line.SingleSectionRemovalException;
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
        assertThat(line.getSections()).contains(section);
    }

    @DisplayName("추가할 세션의 상행 스테이션이 추가될 노선에 포함되고 추가될 위치의 하행스테이션과 똑같이 아니면 그사이에 추가된다")
    @Test
    void insertSectionSuccessBetweenSection() {
        // given
        Line line = new Line();
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        Section targetSection = new Section(강남역, 판교역, 1);
        line.addSection(targetSection);
        Station 양재역 = new Station("양재역");
        Section insertSection = new Section(강남역, 양재역, 1);

        // when
        line.addSection(insertSection);

        // then
        List<Section> sections = line.getSections();
        assertThat(sections).containsOnly(new Section(강남역, 양재역, 1), new Section(양재역, 판교역, 1));
    }


    @DisplayName("추가할 세션의 하행 스테이션이 Line에 포함 되여 있으면 예외를 던진다")
    @Test
    void addSectionFailedByUpStationOfNewSection() {
        // given
        Line line = new Line();
        Section savedSection = new Section(new Station("강남역"), new Station("양재역"), 1);
        line.addSection(savedSection);

        // when,then
        Section newSection = new Section(new Station("양재역"), new Station("강남역"), 1);
        assertThatThrownBy(() -> line.addSection(newSection)).isInstanceOf(DownstreamStationIncludedException.class);
    }

    @DisplayName("노선 하행 종점역과 추가할 세션의 상행역이 일치하지 않아면 예외를 던진다")
    @Test
    void addSectionFailed() {
        // given
        Line line = new Line();
        Section savedSection = new Section(new Station("강남역"), new Station("양재역"), 1);
        line.addSection(savedSection);

        // when,then
        Section newSection = new Section(new Station("교대역"), new Station("서초역"), 1);
        assertThatThrownBy(() -> line.addSection(newSection)).isInstanceOf(MismatchedUpstreamStationException.class);
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
        line.deleteStation(pangyoStation);

        // then
        boolean exists = line.getSections().stream()
                .anyMatch(savedSection -> savedSection.containsStation(pangyoStation));
        assertThat(exists).isFalse();
    }

    @DisplayName("노선의 section이 하나 뿐이면 station를 삭제 할 때 예외를 던진다")
    @Test
    void deleteSectionFailedBySingleSection() {
        // given
        Station gangnamStation = new Station("강남역");
        Station yangjaeStation = new Station("양재역");
        Line line = new Line();
        Section section = new Section(gangnamStation, yangjaeStation, 10);
        line.addSection(section);

        // when,then
        assertThatThrownBy(() -> line.deleteStation(yangjaeStation))
                .isInstanceOf(SingleSectionRemovalException.class);
    }

    @DisplayName("제거할 Station이 노선의 하행 종점역이 아니면 예외를 던진다")
    @Test
    void deleteSectionFailed() {
        // given
        Station gangnamStation = new Station("강남역");
        Station yangjaeStation = new Station("양재역");
        Station pangyoStation = new Station("판교역");
        Line line = new Line();
        Section section = new Section(gangnamStation, yangjaeStation, 10);
        line.addSection(section);
        section = new Section(yangjaeStation, pangyoStation, 10);
        line.addSection(section);

        // when,then
        assertThatThrownBy(() -> line.deleteStation(yangjaeStation))
                .isInstanceOf(NonDownstreamTerminusException.class);
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
