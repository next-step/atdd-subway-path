package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    @DisplayName("지하철 구간 중간에 새로운 역을 추가")
    @Test
    void addSection() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(Stub.구로디지털단지역, Stub.신림역, 10));

        // when
        sections.add(new Section(Stub.구로디지털단지역, Stub.신대방역, 3));

        // then
        assertThat(sections.getStations()).contains(Stub.구로디지털단지역, Stub.신대방역, Stub.신림역);
    }

    @DisplayName("새로운 상행 종점역 추가")
    @Test
    void addNewUpStation() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(Stub.구로디지털단지역, Stub.신대방역, 10));

        // when
        sections.add(new Section(Stub.대림역, Stub.구로디지털단지역, 4));

        // then
        assertThat(sections.getStations()).contains(Stub.대림역, Stub.구로디지털단지역, Stub.신대방역);
    }

    private static class Stub {
        public static final Station 대림역 = new Station("대림역");
        public static final Station 구로디지털단지역 = new Station("구로디지털단지역");
        public static final Station 신대방역 = new Station("신대방역");
        public static final Station 신림역 = new Station("신림역");
    }
}