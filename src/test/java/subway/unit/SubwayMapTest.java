package subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Line;
import subway.domain.Path;
import subway.domain.Section;
import subway.domain.Station;
import subway.domain.SubwayMap;
import subway.exception.error.SubwayErrorCode;
import subway.exception.impl.CannotFindPath;

public class SubwayMapTest {

    private SubwayMap 지하철_지도;
    private Station 논현역;
    private Station 신논현역;
    private Station 반포역;
    private Station 사평역;
    private Station 고속터미널역;
    private Station 옥수역;
    private Station 압구정역;

    @BeforeEach
    void setUp() {
        this.논현역 = Station.builder().name("논현역").build();
        this.신논현역 = Station.builder().name("신논현역").build();
        this.반포역 = Station.builder().name("반포역").build();
        this.사평역 = Station.builder().name("사평역").build();
        this.고속터미널역 = Station.builder().name("고속터미널역").build();
        this.옥수역 = Station.builder().name("옥수역").build();
        this.압구정역 = Station.builder().name("압구정역").build();

        Line 신분당선 = Line.builder()
            .name("신분당선")
            .color("bg-red-600")
            .upStation(논현역)
            .downStation(신논현역)
            .distance(2L)
            .build();

        Line 칠호선 = Line.builder()
            .name("7호선")
            .color("bg-green-600")
            .upStation(반포역)
            .downStation(논현역)
            .distance(1L)
            .build();
        Section 고속터미널_반포_구간 = Section.builder()
            .line(칠호선)
            .upStation(고속터미널역)
            .downStation(반포역)
            .distance(1L)
            .build();
        칠호선.addSection(고속터미널_반포_구간);

        Line 구호선 = Line.builder()
            .name("9호선")
            .color("bg-brown-600")
            .upStation(사평역)
            .downStation(신논현역)
            .distance(1L)
            .build();
        Section 고속터미널_사평_구간 = Section.builder()
            .line(구호선)
            .upStation(고속터미널역)
            .downStation(사평역)
            .distance(1L)
            .build();
        구호선.addSection(고속터미널_사평_구간);

        Line 삼호선 = Line.builder()
            .name("3호선")
            .color("bg-orange-600")
            .upStation(옥수역)
            .downStation(압구정역)
            .distance(1L)
            .build();

        this.지하철_지도 = new SubwayMap(List.of(신분당선, 칠호선, 구호선, 삼호선));
    }


    @Test
    @DisplayName("[성공] 조회된 경로가 있는 경우")
    void 경로_조회() {
        // When
        Path 조회한_경로 = this.지하철_지도.findPath(논현역, 고속터미널역);

        // Then
        assertThat(조회한_경로.getSections().getSize()).isEqualTo(2);
        assertThat(조회한_경로.getSections().getStations().stream().map(Station::getName))
            .containsAll(List.of("고속터미널역", "반포역", "논현역"));
    }

    @Test
    @DisplayName("[실패] 조회된 경로가 없는 경우 (출발역과 도착역이 연결되어 있지 않은 경우)")
    void 조회된_경로가_없는_경우() {
        // When
        assertThatThrownBy(() -> { this.지하철_지도.findPath(옥수역, 사평역); })
            .isInstanceOf(CannotFindPath.class)
            .hasMessage(SubwayErrorCode.CANNOT_FIND_PATH.getMessage());
    }

}
