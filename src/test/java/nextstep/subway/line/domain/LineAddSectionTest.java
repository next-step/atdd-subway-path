package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 노선 기능 테스트 - 구간 추가 기능변경 내용 중심")
public class LineAddSectionTest {

    private Station 강남역, 양재역, 판교역, 정자역, 광교역;
    private Line 신분당선;

    @BeforeEach
    void setUp(){
        // given
        // 역 순서 : 강남역 - [양재역] - 판교역 - [정자역] - 광교역
        강남역 = new Station("강남역");
        ReflectionTestUtils.setField(강남역, "id", 1L);
        양재역 = new Station("양재역");
        ReflectionTestUtils.setField(양재역, "id", 2L);
        판교역 = new Station("판교역");
        ReflectionTestUtils.setField(판교역, "id", 3L);
        정자역 = new Station("정자역");
        ReflectionTestUtils.setField(정자역, "id", 4L);
        광교역 = new Station("광교역");
        ReflectionTestUtils.setField(광교역, "id", 5L);
        신분당선 = new Line("신분당선", "bg-red-600", 양재역, 정자역, 10 );
        ReflectionTestUtils.setField(신분당선, "id", 1L);
    }

    // Happy Path Test

    @DisplayName("역 사이에 새로운 역을 등록")
    @Test
    void addSectionInMiddle() {
        // when
        신분당선.addSection(양재역, 판교역, 3);

        // then
        assertThat(신분당선.getStationSize()).isEqualTo(3);
        지하철_노선_역_순서대로_정렬됨(신분당선, Arrays.asList(양재역.getId(), 판교역.getId(), 정자역.getId()));
    }

    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void addSectionInFirst() {
        // when
        신분당선.addSection(강남역, 양재역, 3);

        // then
        assertThat(신분당선.getStationSize()).isEqualTo(3);
        지하철_노선_역_순서대로_정렬됨(신분당선, Arrays.asList(강남역.getId(), 양재역.getId(), 정자역.getId()));
    }

    @DisplayName("새로운 역을 하행 종점으로 등록")
    @Test
    void addSectionInLast() {
        // when
        신분당선.addSection(정자역, 광교역, 3);

        // then
        assertThat(신분당선.getStationSize()).isEqualTo(3);
        지하철_노선_역_순서대로_정렬됨(신분당선, Arrays.asList(양재역.getId(), 정자역.getId(), 광교역.getId()));
    }

    // 예외처리 테스트
    @DisplayName("[예외처리] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addSectionWithExceedDistance(){
        // when + then
        assertThatThrownBy(() ->{
            신분당선.addSection(양재역, 판교역, 15);
        }).isInstanceOf(RuntimeException.class);

    }

    @DisplayName("[예외처리] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionWithAlreadyAdded(){
        // when + then
        assertThatThrownBy(() ->{
            신분당선.addSection(양재역, 정자역, 5);
        }).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("[예외처리] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionWithCantConnectable(){
        // when + then
        assertThatThrownBy(() ->{
                    신분당선.addSection(강남역, 광교역, 5);
        }).isInstanceOf(RuntimeException.class);
    }

    private ListAssert<Long> 지하철_노선_역_순서대로_정렬됨(Line line, List<Long> expectedIds) {
        return assertThat(line.getStations()
                .stream()
                .map(station -> station.getId())
                .collect(Collectors.toList()))
                .containsExactlyElementsOf(expectedIds);
    }

}
