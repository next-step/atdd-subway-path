package nextstep.subway.line.domain;

import nextstep.subway.line.exception.CannotRemoveSectionException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.StationAlreadyExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("노선 Domain 테스트")
public class LineTest {

    private Station savedStationGyoDae;
    private Station savedStationGangnam;
    private Station savedStationYeoksam;
    private Station savedStationSeolleung;
    private Station savedStationSamseong;
    private Line line2;

    @BeforeEach
    void setUp() {
        savedStationGangnam = new Station(1L, "강남역");
        savedStationYeoksam = new Station(2L, "역삼역");
        savedStationSamseong = new Station(3L, "삼성역");
        savedStationGyoDae = new Station(4L, "교대역");
        savedStationSeolleung = new Station(5L, "선릉역");

        line2 = new Line(1L, "2호선", "bg-green-600");
        line2.addSection(savedStationGangnam, savedStationYeoksam, 10);
    }

    @Test
    @DisplayName("노선의 구간에 있는 역들을 가져오기")
    void getStations() {
        // when
        List<Station> stations = line2.getStations();

        // then
        assertThat(stations).hasSize(2);
    }

    @Test
    @DisplayName("노선에 새로운 상행역 추가. 교대 - 강남 - 역삼")
    void addSectionInUp() {
        // when
        line2.addSection(savedStationGyoDae, savedStationGangnam, 5);

        // then
        assertThat(line2.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationGyoDae, savedStationGangnam, savedStationYeoksam));
    }

    @Test
    @DisplayName("노선 중간에 신규 노선 추가. 강남 - 역삼 - 선릉 - 삼성")
    void addSectionInMiddle() {
        // given
        line2.addSection(savedStationYeoksam, savedStationSamseong, 6);

        // when
        line2.addSection(savedStationYeoksam, savedStationSeolleung, 3);

        // then
        assertThat(line2.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationGangnam, savedStationYeoksam, savedStationSeolleung, savedStationSamseong));
    }

    @Test
    @DisplayName("노선의 하행역에 신규 노선 추가. 강남 - 역삼 - 삼성")
    void addSectionInDown() {
        // when
        line2.addSection(savedStationYeoksam, savedStationSamseong, 6);

        // then
        assertThat(line2.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationGangnam, savedStationYeoksam, savedStationSamseong));
    }

    @Test
    @DisplayName("노선에 존재하는 구간 추가 시 에러 발생")
    void addSectionAlreadyIncluded() {
        assertThatExceptionOfType(StationAlreadyExistException.class)
                .isThrownBy(() -> line2.addSection(savedStationGangnam, savedStationYeoksam, 10));
    }

    @Test
    @DisplayName("노선에 있는 상행 종점역 구간 삭제")
    void removeUpStationSection() {
        // given
        line2.addSection(savedStationYeoksam, savedStationSamseong, 6);

        // when
        line2.deleteSection(savedStationGangnam);

        // then
        assertThat(line2.getSections()).hasSize(1);
        assertThat(line2.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationYeoksam, savedStationSamseong));
    }

    @Test
    @DisplayName("노선에 있는 중간 구간 삭제")
    void removeMiddleSection() {
        // given
        line2.addSection(savedStationYeoksam, savedStationSamseong, 6);
        line2.addSection(savedStationYeoksam, savedStationSeolleung, 3);

        // when
        line2.deleteSection(savedStationYeoksam);

        // then
        assertThat(line2.getSections()).hasSize(2);
        assertThat(line2.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationGangnam, savedStationSeolleung, savedStationSamseong));
    }

    @Test
    @DisplayName("노선에 있는 하행 종점역 구간 삭제")
    void removeDownStationSection() {
        // given
        line2.addSection(savedStationYeoksam, savedStationSamseong, 6);

        // when
        line2.deleteSection(savedStationSamseong);

        // then
        assertThat(line2.getSections()).hasSize(1);
        assertThat(line2.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationGangnam, savedStationYeoksam));
    }

    @Test
    @DisplayName("구간이 하나인 노선 삭제 시 에러 발생")
    void removeSectionNotEndOfList() {
        assertThatExceptionOfType(CannotRemoveSectionException.class)
                .isThrownBy(() -> line2.deleteSection(savedStationYeoksam));
    }
}
