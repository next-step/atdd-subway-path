package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import nextstep.subway.line.AlreadyConnectedException;
import nextstep.subway.line.InvalidDistanceException;
import nextstep.subway.line.MissingStationException;
import nextstep.subway.line.NonDownstreamTerminusException;
import nextstep.subway.line.Section;
import nextstep.subway.line.Sections;
import nextstep.subway.line.SingleSectionRemovalException;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    Sections sections;
    Station gangnamStation;
    Station pangyoStation;

    @BeforeEach
    void setUp() {
        sections = new Sections();
        gangnamStation = new Station("강남역");
        pangyoStation = new Station("판교역");
    }

    @DisplayName("추가할 세션의 하행 스테이션이 노선의 상행 종점일 때 추가할 세션이 상행 좀점 세션으로 등록된다")
    @Test
    void insertSectionSuccessToTop() {
        // given
        sections.addSection(new Section(gangnamStation, pangyoStation, 1));
        Station sadangStation = new Station("사당역");

        // when
        sections.addSection(new Section(sadangStation, gangnamStation, 1));

        // then
        assertThat(sections.getStations()).containsExactly(sadangStation, gangnamStation, pangyoStation);
    }

    @DisplayName("추가할 세션의 상행 스테이션이 추가될 노선에 포함되고 추가될 위치의 하행스테이션과 똑같이 아니면 그사이에 추가된다")
    @Test
    void insertSectionSuccessBySameUpStationOfSection() {
        // given
        sections.addSection(new Section(gangnamStation, pangyoStation, 10));
        Station yangjaeStation = new Station("양재역");

        // when
        sections.addSection(new Section(gangnamStation, yangjaeStation, 1));

        // then
        assertThat(sections.getStations())
                .containsExactly(gangnamStation, yangjaeStation, pangyoStation);
    }

    @DisplayName("추가할 세션의 하행 스테이션이 추가될 노선에 포함되고 추가될 위치의 상행스테이션과 똑같이 아니면 그사이에 추가된다")
    @Test
    void insertSectionSuccessBySameDownStationOfSection() {
        // given
        sections.addSection(new Section(gangnamStation, pangyoStation, 10));
        Station yangjaeStation = new Station("양재역");

        // when
        sections.addSection(new Section(yangjaeStation, pangyoStation, 9));

        // then
        assertThat(sections.getStations()).containsExactly(gangnamStation, yangjaeStation, pangyoStation);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void insertSectionSuccessBetweenSectionFailedByDistance() {
        // given
        sections.addSection(new Section(gangnamStation, pangyoStation, 1));
        Station yangjaeStation = new Station("양재역");

        // when,then
        assertThatThrownBy(() -> sections.addSection(new Section(gangnamStation, yangjaeStation, 1)))
                .isInstanceOf(InvalidDistanceException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void insertSectionSuccessBetweenSectionFailedByExists() {
        // given
        sections.addSection(new Section(gangnamStation, pangyoStation, 10));
        Station yangjaeStation = new Station("양재역");
        sections.addSection(new Section(gangnamStation, yangjaeStation, 4));

        // when,then
        assertThatThrownBy(() -> sections.addSection(new Section(gangnamStation, pangyoStation, 3)))
                .isInstanceOf(AlreadyConnectedException.class);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void insertSectionSuccessBetweenSectionFailedByNotExists() {
        // given
        sections.addSection(new Section(gangnamStation, pangyoStation, 10));
        Station yangjaeStation = new Station("양재역");
        Station sadangStation = new Station("사당역");

        // when,then
        assertThatThrownBy(() -> sections.addSection(new Section(yangjaeStation, sadangStation, 3)))
                .isInstanceOf(MissingStationException.class);
    }

    @DisplayName("노선에 포함한 스테이션을 가져온다")
    @Test
    void getStations() {
        // given
        Station yangjaeStation = new Station("양재역");
        Station seolleungStation = new Station("선릉역");
        Station yeoksamStation = new Station("역삼역");
        sections.addSection(new Section(gangnamStation, pangyoStation, 10));
        sections.addSection(new Section(gangnamStation, yangjaeStation, 1));
        sections.addSection(new Section(yangjaeStation, yeoksamStation, 3));
        sections.addSection(new Section(yangjaeStation, seolleungStation, 1));

        // when
        List<Station> stations = sections.getStations();

        // then
        assertThat(stations).containsExactly(gangnamStation, yangjaeStation, seolleungStation, yeoksamStation,
                pangyoStation);
    }

    @DisplayName("노선에서 등록된 station을 성공적으로 삭제한다")
    @Test
    void deleteSectionSuccess() {
        // given
        Station yangjaeStation = new Station("양재역");
        sections.addSection(new Section(gangnamStation, yangjaeStation, 10));
        sections.addSection(new Section(yangjaeStation, pangyoStation, 10));

        // when
        sections.deleteSection(pangyoStation);

        // then
        assertThat(sections.getStations()).doesNotContain(pangyoStation);
    }

    @DisplayName("노선의 section이 하나 뿐이면 station를 삭제 할 때 예외를 던진다")
    @Test
    void deleteSectionFailedBySingleSection() {
        // given
        sections.addSection(new Section(gangnamStation, pangyoStation, 10));

        // when,then
        assertThatThrownBy(() -> sections.deleteSection(pangyoStation))
                .isInstanceOf(SingleSectionRemovalException.class);
    }

    @DisplayName("제거할 Station이 노선의 하행 종점역이 아니면 예외를 던진다")
    @Test
    void deleteSectionFailed() {
        // given
        Station yangjaeStation = new Station("양재역");
        sections.addSection(new Section(gangnamStation, yangjaeStation, 10));
        sections.addSection(new Section(yangjaeStation, pangyoStation, 10));

        // when,then
        assertThatThrownBy(() -> sections.deleteSection(yangjaeStation))
                .isInstanceOf(NonDownstreamTerminusException.class);
    }

}
