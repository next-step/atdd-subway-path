package nextstep.subway.line.domain;

import nextstep.subway.line.exception.CannotAddSectionException;
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

    private Station savedStationGangnam;
    private Station savedStationYeoksam;
    private Station savedStationSamseong;
    private Station savedStationGyoDae;
    private Line line2;

    @BeforeEach
    void setUp() {
        savedStationGangnam = new Station(1L, "강남역");
        savedStationYeoksam = new Station(2L, "역삼역");
        savedStationSamseong = new Station(3L, "삼성역");
        savedStationGyoDae = new Station(4L, "교대역");
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
    @DisplayName("노선에 하행역 추가")
    void addSection() {
        // when
        line2.addSection(savedStationYeoksam, savedStationSamseong, 6);

        // then
        assertThat(line2.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationGangnam, savedStationYeoksam, savedStationSamseong));
    }

    @Test
    @DisplayName("노선에 상행역 추가")
    void addSectionInMiddle() {
        // when
        line2.addSection(savedStationGyoDae, savedStationGangnam, 5);

        // then
        assertThat(line2.getStations()).containsExactlyElementsOf(Arrays.asList(savedStationGyoDae, savedStationGangnam, savedStationYeoksam));
    }

    @Test
    @DisplayName("노선에 존재하는 구간 추가 시 에러 발생")
    void addSectionAlreadyIncluded() {
        assertThatExceptionOfType(StationAlreadyExistException.class)
                .isThrownBy(() -> line2.addSection(savedStationGangnam, savedStationYeoksam, 10));
    }

    @Test
    @DisplayName("노선에 있는 하행 종점역 구간 삭제")
    void removeSection() {
        // given
        line2.addSection(savedStationYeoksam, savedStationSamseong, 6);

        // when
        line2.deleteLastDownStation(savedStationSamseong.getId());

        // then
        assertThat(line2.getSections()).hasSize(1);
    }

    @Test
    @DisplayName("구간이 하나인 노선에서 역(하행 종점역) 삭제 시 에러 발생")
    void removeSectionNotEndOfList() {
        assertThatExceptionOfType(CannotRemoveSectionException.class)
                .isThrownBy(() -> line2.deleteLastDownStation(savedStationYeoksam.getId()));
    }
}
