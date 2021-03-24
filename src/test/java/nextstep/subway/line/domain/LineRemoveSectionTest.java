package nextstep.subway.line.domain;

import nextstep.subway.line.domain.common.LineTDD;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 노선 기능 테스트 - 구간 제거 기능변경 내용 중심")
public class LineRemoveSectionTest extends LineTDD {

    @BeforeEach
    public void setUp(){
        // 역 순서 : 강남역 - [양재역] -(3)- [판교역] -(7)- [정자역] - 광교역    *[] : 기등록역 / () : 거리
        super.setUp();
        신분당선.addSection(양재역, 판교역, 3);
    }

    // Happy Path Test

    @DisplayName("하행 종점역 제거")
    @Test
    void removeSectionInLast() {
        // when
        신분당선.removeSection(정자역.getId());

        // then
        assertThat(신분당선.getStationSize()).isEqualTo(2);

        지하철_노선_역_순서대로_정렬됨(신분당선, Arrays.asList(양재역.getId(), 판교역.getId()));
        // TODO 길이체크
    }

    @DisplayName("상행 종점역 제거")
    @Test
    void removeSectionInFirst() {
        // when
        신분당선.removeSection(양재역.getId());

        // then
        assertThat(신분당선.getStationSize()).isEqualTo(2);
        지하철_노선_역_순서대로_정렬됨(신분당선, Arrays.asList(판교역.getId(), 정자역.getId()));
    }

    @DisplayName("중간역 제거")
    @Test
    void removeSectionInMiddle() {
        // when
        신분당선.removeSection(판교역.getId());

        // then
        assertThat(신분당선.getStationSize()).isEqualTo(2);
        지하철_노선_역_순서대로_정렬됨(신분당선, Arrays.asList(양재역.getId(), 정자역.getId()));
    }

    private ListAssert<Long> 지하철_노선_역_순서대로_정렬됨(Line line, List<Long> expectedIds) {
        return assertThat(line.getStations()
                .stream()
                .map(station -> station.getId())
                .collect(Collectors.toList()))
                .containsExactlyElementsOf(expectedIds);
    }

    // 예외처리 테스트
    @DisplayName("[예외처리] 존재하지 않는 역을 제거하려 했을 시")
    @Test
    void removeSectionWithNotExistsStation(){
        // when + then
        assertThatThrownBy(() ->{
            신분당선.removeSection(광교역.getId());
        }).isInstanceOf(RuntimeException.class);

    }

    @DisplayName("[예외처리] 최소한의 지하철 역의 갯수인 상태라 제거가 불가능 할 시")
    @Test
    void removeSectionWithExceedMinimumStationNum(){
        // given
        신분당선.removeSection(양재역.getId());

        // when + then
        assertThatThrownBy(() ->{
            신분당선.removeSection(판교역.getId());
        }).isInstanceOf(RuntimeException.class);

    }

}
