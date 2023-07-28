package subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Section;
import subway.domain.Station;

public class SectionTest {

    @Test
    @DisplayName("구간의 상하행종점이 노선 지하철역 집합에 포함되는 경우")
    void isIncludeStationsTrue() {
        // Given
        Station 강남역 = Station.builder().name("강남역").build();
        Station 논현역 = Station.builder().name("논현역").build();
        Station 신논현역 = Station.builder().name("신논현역").build();

        Set<Station> 노선의_지하철역_집합 = new HashSet<>() {{
           add(강남역);
           add(논현역);
           add(신논현역);
        }};

        // When
        Section 신규구간 = Section.builder()
            .upStation(강남역)
            .downStation(신논현역)
            .distance(7L)
            .build();
        boolean actual = 신규구간.isIncludeStations(노선의_지하철역_집합);

        // Then
        assertThat(actual).isEqualTo(true);
    }

    @Test
    @DisplayName("구간의 상하행종점이 노선 지하철역 집합에 포함되지 않는 경우")
    void isExcludeStations() {
        // Given
        Station 강남역 = Station.builder().name("강남역").build();
        Station 논현역 = Station.builder().name("논현역").build();

        Set<Station> 노선의_지하철역_집합 = new HashSet<>() {{
            add(강남역);
            add(논현역);
        }};

        // When
        Station 신논현역 = Station.builder().name("신논현역").build();
        Station 정자역 = Station.builder().name("정자역").build();

        Section 신규구간 = Section.builder()
            .upStation(신논현역)
            .downStation(정자역)
            .distance(7L)
            .build();
        boolean actual = 신규구간.isExcludeStations(노선의_지하철역_집합);

        // Then
        assertThat(actual).isEqualTo(true);
    }

    @Test
    @DisplayName("구간의 지하철역들 중 하나의 역에만 포함되는 구간을 추가하는 경우")
    void cannotDuplicateStation() {
        // Given
        Station 강남역 = Station.builder().name("강남역").build();
        Station 논현역 = Station.builder().name("논현역").build();

        Set<Station> 노선의_지하철역_집합 = new HashSet<>() {{
            add(강남역);
            add(논현역);
        }};

        // When
        Station 신논현역 = Station.builder().name("신논현역").build();

        Section 신규구간 = Section.builder()
            .upStation(강남역)
            .downStation(신논현역)
            .distance(7L)
            .build();
        boolean includeActual = 신규구간.isIncludeStations(노선의_지하철역_집합);
        boolean excludeActual = 신규구간.isExcludeStations(노선의_지하철역_집합);

        // Then
        assertThat(includeActual).isEqualTo(false);
        assertThat(excludeActual).isEqualTo(false);
    }

    @Test
    @DisplayName("기존 구간의 상행종점역 다음에 새로운 역을 추가하는 경우")
    void isInsertedAfterUpStation() {
        // Given
        Station 강남역 = Station.builder().name("강남역").build();
        Station 논현역 = Station.builder().name("논현역").build();

        Section 강남역_논현역_구간 = Section.builder()
            .upStation(강남역)
            .downStation(논현역)
            .build();

        // When
        Station 신논현역 = Station.builder().name("신논현역").build();
        Section 강남역_신논현역_구간 = Section.builder()
            .upStation(강남역)
            .downStation(신논현역)
            .build();
        boolean actual = 강남역_신논현역_구간.isInsertedBetween(강남역_논현역_구간);

        // Then
        assertThat(actual).isEqualTo(true);
    }

    @Test
    @DisplayName("기존 구간의 하행종점역 전에 새로운 역을 추가하는 경우")
    void isInsertedBeforeDownStation() {
        // Given
        Station 강남역 = Station.builder().name("강남역").build();
        Station 논현역 = Station.builder().name("논현역").build();

        Section 강남역_논현역_구간 = Section.builder()
            .upStation(강남역)
            .downStation(논현역)
            .build();

        // When
        Station 신논현역 = Station.builder().name("신논현역").build();
        Section 신논현역_논현역_구간 = Section.builder()
            .upStation(신논현역)
            .downStation(논현역)
            .build();
        boolean actual = 신논현역_논현역_구간.isInsertedBetween(강남역_논현역_구간);

        // Then
        assertThat(actual).isEqualTo(true);
    }


    @Test
    @DisplayName("기존 구간에 새로운 상행종점역을 추가하는 경우")
    void isAppendToFirst() {
        // Given
        Station 강남역 = Station.builder().name("강남역").build();
        Station 논현역 = Station.builder().name("논현역").build();

        Section 강남역_논현역_구간 = Section.builder()
            .upStation(강남역)
            .downStation(논현역)
            .build();

        // When
        Station 신사역 = Station.builder().name("신사역").build();
        Section 신사역_강남역_구간 = Section.builder()
            .upStation(신사역)
            .downStation(강남역)
            .build();
        boolean actual = 신사역_강남역_구간.isAppendedToEnds(강남역_논현역_구간);

        // Then
        assertThat(actual).isEqualTo(true);
    }

    @Test
    @DisplayName("기존 구간에 새로운 하행종점역을 추가하는 경우")
    void isAppendToFinal() {
        // Given
        Station 강남역 = Station.builder().name("강남역").build();
        Station 논현역 = Station.builder().name("논현역").build();

        Section 강남역_논현역_구간 = Section.builder()
            .upStation(강남역)
            .downStation(논현역)
            .build();

        // When
        Station 신사역 = Station.builder().name("신사역").build();
        Section 신사역_강남역_구간 = Section.builder()
            .upStation(신사역)
            .downStation(강남역)
            .build();
        boolean actual = 신사역_강남역_구간.isAppendedToEnds(강남역_논현역_구간);

        // Then
        assertThat(actual).isEqualTo(true);
    }
}
