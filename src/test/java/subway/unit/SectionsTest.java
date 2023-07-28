package subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;

public class SectionsTest {

    @Test
    @DisplayName("추가하려는 구간과 연결되는 기존 구간을 가져온다.")
    void getConnectedSection() {
        // Given
        Station 강남역 = Station.builder().name("강남역").build();
        Station 논현역 = Station.builder().name("논현역").build();

        Line 신분당선 = Line.builder()
            .name("신분당선")
            .color("bg-red-600")
            .distance(30L)
            .upStation(강남역)
            .downStation(논현역)
            .build();

        Section 강남역_논현역_구간 = 신분당선.getSections().getLastSection();

        // When
        Station 신논현역 = Station.builder().name("신논현역").build();
        Section 신규구간 = Section.builder()
            .line(신분당선)
            .upStation(강남역)
            .downStation(신논현역)
            .distance(7L)
            .build();
        Sections 구간리스트 = 신분당선.getSections();
        Section 연결된구간 = 구간리스트.getConnectedSection(신규구간);

        // Then
        assertThat(연결된구간).isEqualTo(강남역_논현역_구간);
    }

    @Test
    @DisplayName("기존 구간 중간에 새로운 역을 추가하는 경우 기존 구간을 분리한다.")
    void getDivideSection() {
        // Given
        Station 강남역 = Station.builder().name("강남역").build();
        Station 논현역 = Station.builder().name("논현역").build();

        Line 신분당선 = Line.builder()
            .name("신분당선")
            .color("bg-red-600")
            .distance(30L)
            .upStation(강남역)
            .downStation(논현역)
            .build();

        Sections 구간리스트 = 신분당선.getSections();
        Section 강남역_논현역_구간 = 신분당선.getSections().getLastSection();

        // When
        Station 신논현역 = Station.builder().name("신논현역").build();
        Section 신규구간 = Section.builder()
            .line(신분당선)
            .upStation(강남역)
            .downStation(신논현역)
            .distance(7L)
            .build();
        Section 분리된_구간 = 구간리스트.getDividedSection(강남역_논현역_구간, 신규구간);

        // Then
        Section expected = Section.builder()
            .line(신분당선)
            .upStation(신논현역)
            .downStation(논현역)
            .distance(23L)
            .build();
        assertThat(분리된_구간).isEqualTo(expected);
    }

}
