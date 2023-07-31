package subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;

class LineTest {

    private Line 신분당선;

    private Section 논현역_광교역_구간;

    @BeforeEach
    void setUp() {
        Station 강남역 = Station.builder()
            .id(1L)
            .name("강남역")
            .build();

        Station 논현역 = Station.builder()
            .id(2L)
            .name("논현역")
            .build();

        Station 광교역 = Station.builder()
            .id(3L)
            .name("광교역")
            .build();

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

    @Disabled
    @Test
    void removeSection() {
        // Given
        신분당선.addSection(논현역_광교역_구간);

        // When
        신분당선.removeSection(null);

        // Then
        assertThat(신분당선.getSections().getLastSection().getDownStation().getName()).isEqualTo("논현역");
        assertThat(신분당선.getSections().getStations().size()).isEqualTo(2);
    }
}
