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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    Sections sections;
    Station 강남역;
    Station 판교역;

    @BeforeEach
    void setUp() {
        sections = new Sections();
        강남역 = new Station("강남역");
        판교역 = new Station("판교역");
    }

    @DisplayName("추가할 세션의 하행 스테이션이 노선의 상행 종점일 때 추가할 세션이 상행 좀점 세션으로 등록된다")
    @Test
    void insertSectionSuccessToTop() {
        // given
        Section targetSection = new Section(강남역, 판교역, 1);
        sections.addSection(targetSection);
        Station 교대역 = new Station("교대역");
        Section insertSection = new Section(교대역, 강남역, 1);

        // when
        sections.addSection(insertSection);

        // then
        Assertions.assertAll(
                () -> assertThat(sections.getSections().get(0)).isEqualTo(insertSection),
                () -> assertThat(sections.getSections().get(1)).isEqualTo(targetSection)
        );
    }

    @DisplayName("추가할 세션의 상행 스테이션이 추가될 노선에 포함되고 추가될 위치의 하행스테이션과 똑같이 아니면 그사이에 추가된다")
    @Test
    void insertSectionSuccessBySameUpStationOfSection() {
        // given
        Section targetSection = new Section(강남역, 판교역, 10);
        sections.addSection(targetSection);
        Station 양재역 = new Station("양재역");
        Section insertSection = new Section(강남역, 양재역, 1);

        // when
        sections.addSection(insertSection);

        // then
        assertThat(sections.getSections())
                .containsOnly(new Section(강남역, 양재역, 1), new Section(양재역, 판교역, 9));
    }

    @DisplayName("추가할 세션의 하행 스테이션이 추가될 노선에 포함되고 추가될 위치의 상행스테이션과 똑같이 아니면 그사이에 추가된다")
    @Test
    void insertSectionSuccessBySameDownStationOfSection() {
        // given
        Section targetSection = new Section(강남역, 판교역, 10);
        sections.addSection(targetSection);
        Station 양재역 = new Station("양재역");
        Section insertSection = new Section(양재역, 판교역, 9);

        // when
        sections.addSection(insertSection);

        // then
        assertThat(sections.getSections()).containsOnly(new Section(강남역, 양재역, 1), new Section(양재역, 판교역, 9));
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void insertSectionSuccessBetweenSectionFailedByDistance() {
        // given
        Section targetSection = new Section(강남역, 판교역, 1);
        sections.addSection(targetSection);
        Station 양재역 = new Station("양재역");
        Section insertSection = new Section(강남역, 양재역, 1);

        // when,then
        assertThatThrownBy(() -> sections.addSection(insertSection))
                .isInstanceOf(InvalidDistanceException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void insertSectionSuccessBetweenSectionFailedByExists() {
        // given
        Section targetSection = new Section(강남역, 판교역, 10);
        sections.addSection(targetSection);
        Station 양재역 = new Station("양재역");
        Section exampleSection = new Section(강남역, 양재역, 4);
        sections.addSection(exampleSection);
        Section insertSection = new Section(강남역, 판교역, 3);

        // when,then
        assertThatThrownBy(() -> sections.addSection(insertSection))
                .isInstanceOf(AlreadyConnectedException.class);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void insertSectionSuccessBetweenSectionFailedByNotExists() {
        // given
        Section targetSection = new Section(강남역, 판교역, 10);
        sections.addSection(targetSection);
        Station 양재역 = new Station("양재역");
        Station 교대역 = new Station("교대역");

        Section insertSection = new Section(양재역, 교대역, 3);

        // when,then
        assertThatThrownBy(() -> sections.addSection(insertSection))
                .isInstanceOf(MissingStationException.class);
    }

    @DisplayName("노선에 포함한 스테이션을 가져온다")
    @Test
    void getStations() {
        // given
        Station 양재역 = new Station("양재역");
        Station 선릉역 = new Station("선릉역");
        Station 역삼역 = new Station("역삼역");
        Section section = new Section(강남역, 판교역, 10);
        sections.addSection(section);
        section = new Section(강남역, 양재역, 1);
        sections.addSection(section);
        section = new Section(양재역, 역삼역, 3);
        sections.addSection(section);
        section = new Section(양재역, 선릉역, 1);
        sections.addSection(section);

        // when
        List<Station> stations = sections.getStations();

        // then
        assertThat(stations).containsExactly(강남역, 양재역, 선릉역, 역삼역, 판교역);
    }

    @DisplayName("노선에서 등록된 station을 성공적으로 삭제한다")
    @Test
    void deleteSectionSuccess() {
        // given
        Station 양재역 = new Station("양재역");
        Section section = new Section(강남역, 양재역, 10);
        sections.addSection(section);
        section = new Section(양재역, 판교역, 10);
        sections.addSection(section);

        // when
        sections.deleteSection(판교역);

        // then
        boolean exists = sections.getSections().stream()
                .anyMatch(savedSection -> savedSection.containsStation(판교역));
        assertThat(exists).isFalse();
    }

    @DisplayName("노선의 section이 하나 뿐이면 station를 삭제 할 때 예외를 던진다")
    @Test
    void deleteSectionFailedBySingleSection() {
        // given
        Section section = new Section(강남역, 판교역, 10);
        sections.addSection(section);

        // when,then
        assertThatThrownBy(() -> sections.deleteSection(판교역))
                .isInstanceOf(SingleSectionRemovalException.class);
    }

    @DisplayName("제거할 Station이 노선의 하행 종점역이 아니면 예외를 던진다")
    @Test
    void deleteSectionFailed() {
        // given
        Station 양재역 = new Station("양재역");
        Section section = new Section(강남역, 양재역, 10);
        sections.addSection(section);
        section = new Section(양재역, 판교역, 10);
        sections.addSection(section);

        // when,then
        assertThatThrownBy(() -> sections.deleteSection(양재역))
                .isInstanceOf(NonDownstreamTerminusException.class);
    }

}
