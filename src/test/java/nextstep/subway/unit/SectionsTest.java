package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {
    private Line 신분당선;

    @BeforeEach
    void init() {
        신분당선 = new Line("2호선", "red");
    }

    @DisplayName("이미 존재하는 구간 사이에 새로운 구간을 추가한다.")
    @Test
    void addSectionToExistSection() {
        Sections sections = new Sections();
        sections.addSection(신분당선, new Station("강남"), new Station("양재"), 10);
        sections.addSection(신분당선, new Station("강남"), new Station("정자"), 5);

        List<Station> stations = sections.getStationsInOrder();
        assertThat(stations).containsExactly(new Station("강남"), new Station("정자"), new Station("양재"));
    }

    @DisplayName("상행 종점역을 하행역으로 하는 구간 추가한다.")
    @Test
    void addSectionAtUpStation() {
        Sections sections = new Sections();
        sections.addSection(신분당선, new Station("양재"), new Station("정자"), 10);
        sections.addSection(신분당선, new Station("강남"), new Station("양재"), 10);

        List<Station> stations = sections.getStationsInOrder();

        assertThat(stations).containsExactly(new Station("강남"), new Station("양재"), new Station("정자"));
    }

    @DisplayName("하행 종점역을 상행역으로 하는 구간 추가한다.")
    @Test
    void addSectionAtDownStation() {
        Sections sections = 강남양재구간_양재정자구간_10거리씩_순서대로_포함();

        List<Station> stations = sections.getStationsInOrder();

        assertThat(stations).containsExactly(new Station("강남"), new Station("양재"), new Station("정자"));
    }

    @DisplayName("이미 존재하는 구간 사이에 더 긴 구간을 추가할 수 없다.")
    @Test
    void addLongSection() {
        Sections sections = new Sections();
        sections.addSection(신분당선, new Station("강남"), new Station("양재"), 10);

        assertThatThrownBy(() -> sections.addSection(신분당선, new Station("강남"), new Station("정자"), 15))
                .isInstanceOf(BusinessException.class);
    }

    @DisplayName("추가하려는 구간의 역 전부가 이미 존재하면 구간을 추가할 수 없다.")
    @Test
    void addSectionWithExistStations() {
        Sections sections = 강남양재구간_양재정자구간_10거리씩_순서대로_포함();

        assertThatThrownBy(() -> sections.addSection(신분당선, new Station("강남"), new Station("정자"), 15))
                .isInstanceOf(BusinessException.class);
    }

    @DisplayName("추가하려는 구간의 역 전부가 노선에 존재하지 않는다")
    @Test
    void addSectionWithNotExistStations() {
        Sections sections = 강남양재구간_양재정자구간_10거리씩_순서대로_포함();

        assertThatThrownBy(() -> sections.addSection(신분당선, new Station("판교"), new Station("광교"), 15))
                .isInstanceOf(BusinessException.class);
    }

    @DisplayName("상행 종점 구간을 제거한다.")
    @Test
    void removeUpSection() {
        Sections sections = 강남양재구간_양재정자구간_10거리씩_순서대로_포함();

        sections.deleteStation(new Station("강남"));

        List<Station> stations = sections.getStations();
        assertThat(stations).containsExactly(new Station("양재"), new Station("정자"));
    }

    @DisplayName("하행 종점 구간을 제거한다.")
    @Test
    void removeDownSection() {
        Sections sections = 강남양재구간_양재정자구간_10거리씩_순서대로_포함();

        sections.deleteStation(new Station("정자"));

        List<Station> stations = sections.getStations();
        assertThat(stations).containsExactly(new Station("강남"), new Station("양재"));
    }

    @DisplayName("중간 구간을 제거한다.")
    @Test
    void removeMiddleSection() {
        Sections sections = 강남양재구간_양재정자구간_10거리씩_순서대로_포함();

        sections.deleteStation(new Station("양재"));

        List<Station> stations = sections.getStations();
        assertThat(stations).containsExactly(new Station("강남"), new Station("정자"));
    }

    @DisplayName("하나의 구간만 존재할 때 역을 삭제할 수 없다.")
    @Test
    void removeAtOneSection() {
        Sections sections = new Sections();
        sections.addSection(신분당선, new Station("강남"), new Station("양재"), 10);

        assertThatThrownBy(() -> sections.deleteStation(new Station("강남")))
                .isInstanceOf(BusinessException.class);
    }

    @DisplayName("구간에 존재하지 않는 역을 삭제할 수 없다.")
    @Test
    void removeNonExistentSection() {
        Sections sections = 강남양재구간_양재정자구간_10거리씩_순서대로_포함();

        assertThatThrownBy(() -> sections.deleteStation(new Station("판교")))
                .isInstanceOf(BusinessException.class);
    }

    private Sections 강남양재구간_양재정자구간_10거리씩_순서대로_포함() {
        Sections sections = new Sections();
        sections.addSection(신분당선, new Station("강남"), new Station("양재"), 10);
        sections.addSection(신분당선, new Station("양재"), new Station("정자"), 10);
        return sections;
    }
}
