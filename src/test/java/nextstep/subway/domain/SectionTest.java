package nextstep.subway.domain;

import nextstep.subway.exception.SubwayRuntimeException;
import nextstep.subway.exception.message.SubwayErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 구간 테스트")
class SectionTest {

    Station 신논현;
    Station 강남역;
    Station 양재역;
    Station 판교역;
    Station 정자역;
    Station 미금역;
    Line 신분당선;

    @BeforeEach
    void setup() {
        신논현 = new Station("신논현");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        판교역 = new Station("판교역");
        정자역 = new Station("정자역");
        미금역 = new Station("미금역");
        신분당선 = new Line("신분당선", "bg-red-600");
        신분당선.addSection(강남역, 판교역, 10);
    }

    @DisplayName("지하철 구간 추가 테스트")
    @Test
    void addSection() {
        // When
        신분당선.addSection(판교역, 정자역, 10);

        // Then
        assertThat(신분당선.getStations()).extracting("name").containsExactly("강남역", "판교역", "정자역");
    }

    @DisplayName("지하철 구간 추가 테스트 - 상행 종점역에 구간 추가")
    @Test
    void addSection1() {
        // When
        신분당선.addSection(신논현, 강남역, 10);

        // Then
        assertThat(신분당선.getStations()).extracting("name").containsExactly("신논현", "강남역", "판교역");
    }

    @DisplayName("지하철 구간 추가 테스트 - 중간역 추가")
    @Test
    void addSection2() {
        // When
        신분당선.addSection(판교역, 양재역, 10);

        // Then
        assertThat(신분당선.getStations()).extracting("name").containsExactly("강남역", "판교역", "양재역");
    }

    @DisplayName("지하철 구간 예외 테스트 - [새로 등록할 구간 길이가 이전에 등록한 구간의 길이와 같거나 큰경우]")
    @Test
    void addSectionException() {
        // When & Then
        assertThatThrownBy(() -> 신분당선.addSection(강남역, 정자역, 10))
                .isInstanceOf(SubwayRuntimeException.class)
                .hasMessage(SubwayErrorCode.INVALID_SECTION_DISTANCE.getMessage());
    }

    @DisplayName("지하철 구간 예외 테스트 - [이미 등록된 구간일 경우]")
    @Test
    void addSectionException2() {
        // When & Then
        assertThatThrownBy(() -> 신분당선.addSection(강남역, 판교역, 10))
                .isInstanceOf(SubwayRuntimeException.class)
                .hasMessage(SubwayErrorCode.DUPLICATE_SECTION.getMessage());
    }

    @DisplayName("지하철 구간 예외 테스트 - [추가 할 구간의 상행/종점 역이 모두 기존 구간에 존재하지 않을 경우]")
    @Test
    void addSectionException3() {
        // When & Then
        assertThatThrownBy(() -> 신분당선.addSection(미금역, 정자역, 10))
                .isInstanceOf(SubwayRuntimeException.class)
                .hasMessage(SubwayErrorCode.NOT_CONTAIN_STATION.getMessage());
    }
}
