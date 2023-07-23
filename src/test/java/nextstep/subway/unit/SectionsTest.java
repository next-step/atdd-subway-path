package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import nextstep.subway.line.AlreadyConnectedException;
import nextstep.subway.line.InvalidDistanceException;
import nextstep.subway.line.MissingStationException;
import nextstep.subway.line.Section;
import nextstep.subway.line.SectionNotFoundException;
import nextstep.subway.line.Sections;
import nextstep.subway.line.SingleSectionRemovalException;
import nextstep.subway.line.StationNotIncludedException;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    public static final int SADANG_TO_YANGJAE_DISTANCE = 4;
    public static final int GANGNAM_TO_YANGJAE_DISTANCE = 1;
    public static final int YANGJAE_TO_PANGYO_DISTANCE = 9;
    public static final int GANGNAM_TO_PANGYO_DISTANCE = 10;
    public static final int YANGJAE_TO_YEOKSAM_DISTANCE = 3;
    public static final int YANGJAE_TO_SEOLLEUNG_DISTANCE = 1;
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
        sections.addSection(new Section(gangnamStation, pangyoStation, GANGNAM_TO_PANGYO_DISTANCE));
        Station sadangStation = new Station("사당역");

        // when
        sections.addSection(new Section(sadangStation, gangnamStation, SADANG_TO_YANGJAE_DISTANCE));

        // then
        assertThat(sections.getStations()).containsExactly(sadangStation, gangnamStation, pangyoStation);
    }

    @DisplayName("추가할 세션의 상행 스테이션이 추가될 노선에 포함되고 추가될 위치의 하행스테이션과 똑같이 아니면 그사이에 추가된다")
    @Test
    void insertSectionSuccessBySameUpStationOfSection() {
        // given
        sections.addSection(new Section(gangnamStation, pangyoStation, GANGNAM_TO_PANGYO_DISTANCE));
        Station yangjaeStation = new Station("양재역");

        // when
        sections.addSection(new Section(gangnamStation, yangjaeStation, SADANG_TO_YANGJAE_DISTANCE));

        // then
        assertThat(sections.getStations())
                .containsExactly(gangnamStation, yangjaeStation, pangyoStation);
    }

    @DisplayName("추가할 세션의 하행 스테이션이 추가될 노선에 포함되고 추가될 위치의 상행스테이션과 똑같이 아니면 그사이에 추가된다")
    @Test
    void insertSectionSuccessBySameDownStationOfSection() {
        // given
        sections.addSection(new Section(gangnamStation, pangyoStation, GANGNAM_TO_PANGYO_DISTANCE));
        Station yangjaeStation = new Station("양재역");

        // when
        sections.addSection(new Section(yangjaeStation, pangyoStation, YANGJAE_TO_PANGYO_DISTANCE));

        // then
        List<Station> stations = sections.getStations();
        assertThat(stations).containsExactly(gangnamStation, yangjaeStation, pangyoStation);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void insertSectionSuccessBetweenSectionFailedByDistance() {
        // given
        sections.addSection(new Section(gangnamStation, pangyoStation, GANGNAM_TO_PANGYO_DISTANCE));
        Station yangjaeStation = new Station("양재역");

        // when,then
        Section tooLongDistanceSection = new Section(gangnamStation, yangjaeStation, GANGNAM_TO_PANGYO_DISTANCE + 1);
        assertThatThrownBy(() -> sections.addSection(tooLongDistanceSection))
                .isInstanceOf(InvalidDistanceException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void insertSectionSuccessBetweenSectionFailedByExists() {
        // given
        sections.addSection(new Section(gangnamStation, pangyoStation, GANGNAM_TO_PANGYO_DISTANCE));
        Station yangjaeStation = new Station("양재역");
        sections.addSection(new Section(gangnamStation, yangjaeStation, 4));

        // when,then
        Section existsSection = new Section(gangnamStation, pangyoStation, YANGJAE_TO_YEOKSAM_DISTANCE);
        assertThatThrownBy(() -> sections.addSection(existsSection))
                .isInstanceOf(AlreadyConnectedException.class);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void insertSectionSuccessBetweenSectionFailedByNotExists() {
        // given
        sections.addSection(new Section(gangnamStation, pangyoStation, GANGNAM_TO_PANGYO_DISTANCE));
        Station yangjaeStation = new Station("양재역");
        Station sadangStation = new Station("사당역");

        // when,then
        Section sectionsNotContainsStationOfSection = new Section(yangjaeStation, sadangStation,
                SADANG_TO_YANGJAE_DISTANCE);
        assertThatThrownBy(() -> sections.addSection(sectionsNotContainsStationOfSection))
                .isInstanceOf(MissingStationException.class);
    }

    @DisplayName("노선에 포함한 스테이션을 가져온다")
    @Test
    void getStations() {
        // given
        Station yangjaeStation = new Station("양재역");
        Station seolleungStation = new Station("선릉역");
        Station yeoksamStation = new Station("역삼역");
        sections.addSection(new Section(gangnamStation, pangyoStation, GANGNAM_TO_PANGYO_DISTANCE));
        sections.addSection(new Section(gangnamStation, yangjaeStation, GANGNAM_TO_YANGJAE_DISTANCE));
        sections.addSection(new Section(yangjaeStation, yeoksamStation, YANGJAE_TO_YEOKSAM_DISTANCE));
        sections.addSection(new Section(yangjaeStation, seolleungStation, YANGJAE_TO_SEOLLEUNG_DISTANCE));

        // when
        List<Station> stations = sections.getStations();

        // then
        assertThat(stations).containsExactly(gangnamStation, yangjaeStation, seolleungStation, yeoksamStation,
                pangyoStation);
    }

    @DisplayName("노선에서 등록된 하행 종점 station을 성공적으로 삭제한다")
    @Test
    void deleteBottomSectionSuccess() {
        // given
        Station yangjaeStation = new Station("양재역");
        sections.addSection(new Section(gangnamStation, yangjaeStation, GANGNAM_TO_YANGJAE_DISTANCE));
        sections.addSection(new Section(yangjaeStation, pangyoStation, YANGJAE_TO_PANGYO_DISTANCE));

        // when
        sections.deleteSection(pangyoStation);

        // then
        assertThat(sections.getStations()).doesNotContain(pangyoStation);
    }

    @DisplayName("노선에서 등록된 상행 종점 station을 성공적으로 삭제한다")
    @Test
    void deleteTopSectionSuccess() {
        // given
        Station yangjaeStation = new Station("양재역");
        sections.addSection(new Section(gangnamStation, yangjaeStation, GANGNAM_TO_YANGJAE_DISTANCE));
        sections.addSection(new Section(yangjaeStation, pangyoStation, YANGJAE_TO_PANGYO_DISTANCE));

        // when
        sections.deleteSection(gangnamStation);

        // then
        assertThat(sections.getStations()).doesNotContain(gangnamStation);
    }

    @DisplayName("노선에서 중간에 등록된 station을 성공적으로 삭제하면 재비치되고 거리는 두 구간의 거리의 합이다.")
    @Test
    void deleteSectionSuccessOnCenter() {
        // given
        Station yangjaeStation = new Station("양재역");
        sections.addSection(new Section(gangnamStation, yangjaeStation, GANGNAM_TO_YANGJAE_DISTANCE));
        sections.addSection(new Section(yangjaeStation, pangyoStation, YANGJAE_TO_PANGYO_DISTANCE));

        // when
        sections.deleteSection(yangjaeStation);

        // then
        Section sameUpStationSection = sections.getSameUpStationSection(gangnamStation);
        Assertions.assertAll(
                () -> assertThat(sections.getStations())
                        .containsExactly(gangnamStation, pangyoStation),
                () -> assertThat(sameUpStationSection.getDistance())
                        .isEqualTo(GANGNAM_TO_PANGYO_DISTANCE)
        );
    }

    @DisplayName("노선의 section이 하나 뿐이면 station를 삭제 할 때 예외를 던진다")
    @Test
    void deleteSectionFailedBySingleSection() {
        // given
        sections.addSection(new Section(gangnamStation, pangyoStation, GANGNAM_TO_PANGYO_DISTANCE));

        // when,then
        assertThatThrownBy(() -> sections.deleteSection(pangyoStation))
                .isInstanceOf(SingleSectionRemovalException.class);
    }

    @DisplayName("해당 노선에 포함되어 있지 않는 스테이션을 삭제 할 때 예외를 던진다")
    @Test
    void deleteSectionFailedByNotIncluded() {
        // given
        Station yangjaeStation = new Station("양재역");
        sections.addSection(new Section(gangnamStation, yangjaeStation, GANGNAM_TO_YANGJAE_DISTANCE));
        sections.addSection(new Section(yangjaeStation, pangyoStation, YANGJAE_TO_PANGYO_DISTANCE));

        // when,then
        Station notExistsStation = new Station("교대역");
        assertThatThrownBy(() -> sections.deleteSection(notExistsStation))
                .isInstanceOf(StationNotIncludedException.class);
    }

    @DisplayName("targetStation과 같은 upStation을 가지고 있는 section을 가져온다")
    @Test
    void getSameUpStationSectionSuccess() {
        // given
        sections.addSection(new Section(gangnamStation, pangyoStation, GANGNAM_TO_PANGYO_DISTANCE));

        // when
        Section sameUpStationSection = sections.getSameUpStationSection(gangnamStation);

        // then
        assertThat(sameUpStationSection.getUpStation()).isEqualTo(gangnamStation);
    }

    @DisplayName("targetStation과 같은 upStation을 가지고 있는 section을 가져올 때 존재하지 않으면 예외를 던진다")
    @Test
    void getSameUpStationSectionFailedByNotExists() {
        // given
        sections.addSection(new Section(gangnamStation, pangyoStation, GANGNAM_TO_PANGYO_DISTANCE));

        // when/then
        assertThatThrownBy(() -> sections.getSameUpStationSection(pangyoStation))
                .isInstanceOf(SectionNotFoundException.class);
    }


    @DisplayName("targetStation과 같은 DownStation을 가지고 있는 section을 가져온다")
    @Test
    void getSameDownStationSectionSuccess() {
        // given
        sections.addSection(new Section(gangnamStation, pangyoStation, GANGNAM_TO_PANGYO_DISTANCE));

        // when
        Section sameUpStationSection = sections.getSameDownStationSection(pangyoStation);

        // then
        assertThat(sameUpStationSection.getDownStation()).isEqualTo(pangyoStation);
    }

    @DisplayName("targetStation과 같은 DownStation을 가지고 있는 section을 가져올 때 존재하지 않으면 예외를 던진다")
    @Test
    void getSameDownStationSectionFailedByNotExists() {
        // given
        Station yangjaeStation = new Station("양재역");
        sections.addSection(new Section(gangnamStation, yangjaeStation, GANGNAM_TO_YANGJAE_DISTANCE));

        // when/then
        assertThatThrownBy(() -> sections.getSameDownStationSection(gangnamStation))
                .isInstanceOf(SectionNotFoundException.class);
    }
}
