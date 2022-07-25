package nextstep.subway.unit;

import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
    @DisplayName("이미 존재하는 구간 사이에 새로운 구간을 추가한다.")
    @Test
    void addSectionToExistSection() {
        Sections sections = new Sections();
        sections.add(null, new Station("강남"), new Station("잠실"), 10);
        sections.add(null, new Station("강남"), new Station("선릉"), 5);

        List<Station> stations = sections.getStations();

        assertThat(stations).containsExactly(new Station("강남"), new Station("선릉"), new Station("잠실"));
    }

    @DisplayName("상행 종점역을 하행역으로 하는 구간 추가한다.")
    @Test
    void addSectionAtUpStation() {
        Sections sections = new Sections();
        sections.add(null, new Station("잠실"), new Station("강남"), 10);
        sections.add(null, new Station("교대"), new Station("잠실"), 10);

        List<Station> stations = sections.getStations();

        assertThat(stations).containsExactly(new Station("교대"), new Station("잠실"), new Station("강남"));
    }

    @DisplayName("하행 종점역을 상행역으로 하는 구간 추가한다.")
    @Test
    void addSectionAtDownStation() {
        Sections sections = new Sections();
        sections.add(null, new Station("잠실"), new Station("강남"), 10);
        sections.add(null, new Station("강남"), new Station("선릉"), 10);

        List<Station> stations = sections.getStations();

        assertThat(stations).containsExactly(new Station("잠실"), new Station("강남"), new Station("선릉"));
    }

    @DisplayName("이미 존재하는 구간 사이에 더 긴 구간을 추가할 수 없다.")
    @Test
    void addLongSection() {
        Sections sections = new Sections();
        sections.add(null, new Station("잠실"), new Station("강남"), 10);

        assertThatThrownBy(() -> sections.add(null, new Station("잠실"), new Station("선릉"), 15))
                .isInstanceOf(SectionException.class);
    }

    @DisplayName("추가하려는 구간의 역 전부가 이미 존재하면 구간을 추가할 수 없다.")
    @Test
    void addSectionWithExistStations() {
        Sections sections = new Sections();
        sections.add(null, new Station("잠실"), new Station("강남"), 10);
        sections.add(null, new Station("강남"), new Station("선릉"), 10);

        assertThatThrownBy(() -> sections.add(null, new Station("잠실"), new Station("선릉"), 15))
                .isInstanceOf(SectionException.class);
    }
}
