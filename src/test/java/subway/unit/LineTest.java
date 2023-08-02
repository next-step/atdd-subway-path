package subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.exception.error.SubwayErrorCode;
import subway.exception.impl.CannotDeleteSectionException;

class LineTest {

    private Station 강남역;

    private Station 논현역;

    private Station 광교역;

    private Line 신분당선;

    private Section 논현역_광교역_구간;

    @BeforeEach
    void setUp() {
        this.강남역 = Station.builder().name("강남역").build();
        this.논현역 = Station.builder().name("논현역").build();
        this.광교역 = Station.builder().name("광교역").build();

        this.신분당선 = Line.builder()
            .name("신분당선")
            .color("bg-red-600")
            .distance(30L)
            .upStation(강남역)
            .downStation(논현역)
            .build();

        this.논현역_광교역_구간 = Section.builder()
            .line(신분당선)
            .distance(10L)
            .upStation(논현역)
            .downStation(광교역)
            .build();
    }

    @Test
    void addSection() {
        // When
        신분당선.addSection(논현역_광교역_구간);

        // Then
        Sections 신분당선_구간_리스트 = 신분당선.getSections();
        List<Station> 신분당선_지하철역_리스트 = 신분당선.getSections().getStations();

        assertThat(신분당선_구간_리스트.getLastSection().getDownStation().getName()).isEqualTo("광교역");
        assertThat(신분당선_지하철역_리스트.size()).isEqualTo(3L);
        assertThat(신분당선.getDistance()).isEqualTo(40L);
    }

    @Test
    void getStations() {
        // When
        List<Station> 신분당선_지하철역_리스트 = 신분당선.getSections().getStations();

        // Then
        assertThat(신분당선_지하철역_리스트.size()).isEqualTo(2);
        assertThat(
            신분당선_지하철역_리스트.stream().map(Station::getName).collect(Collectors.toList()))
            .containsAll(List.of("강남역", "논현역"));
    }

    @Test
    @DisplayName("[성공] 첫 번째 구간 삭제")
    void removeFirstSection() {
        // Given
        신분당선.addSection(논현역_광교역_구간);

        // When
        신분당선.removeSection(강남역);

        // Then
        구간_검증(List.of("논현역", "광교역"), 10L);
    }

    @Test
    @DisplayName("[성공] 중간 구간 삭제")
    void removeMiddleSection() {
        // Given
        신분당선.addSection(논현역_광교역_구간);

        // When
        신분당선.removeSection(논현역);

        // Then
        구간_검증(List.of("강남역", "광교역"), 40L);
    }

    @Test
    @DisplayName("[실패] 단일 구간 삭제")
    void removeSingleSection() {
        // Given
        // When
        assertThatThrownBy(() -> 신분당선.removeSection(강남역))
            .isInstanceOf(CannotDeleteSectionException.class)
            .hasMessage(SubwayErrorCode.CANNOT_DELETE_SECTION.getMessage());
    }

    @Test
    @DisplayName("[실패] 노선에 포함되지 않은 지하철역 삭제")
    void removeNotExistsStationSection() {
        // Given
        신분당선.addSection(논현역_광교역_구간);
        Station 정자역 = Station.builder().name("정자역").build();

        // When
        assertThatThrownBy(() -> 신분당선.removeSection(정자역))
            .isInstanceOf(CannotDeleteSectionException.class)
            .hasMessage(SubwayErrorCode.CANNOT_DELETE_SECTION.getMessage());
    }

    void 구간_검증(List<String> stationNames, Long distance) {
        List<String> stations = 신분당선.getStations().stream()
            .map(Station::getName)
            .collect(Collectors.toList());
        for (int i=0; i< stations.size(); i++) {
            assertThat(stations.get(i)).isEqualTo(stationNames.get(i));
        }

        assertThat(신분당선.getDistance()).isEqualTo(distance);
    }
}
