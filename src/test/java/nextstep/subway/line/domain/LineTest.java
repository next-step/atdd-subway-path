package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineTest {
    private final Line 신분당선 = new Line("신분당선","bg-red-600");
    private final Station 강남역 = new Station("강남역");
    private final Station 양재역 = new Station("양재역");
    private final List<Station> 테스트역 = Arrays.asList(강남역, 양재역);

    @BeforeEach
    void setUp(){
        신분당선.addSection(테스트역.get(0), 테스트역.get(1), 10);
    }

    @DisplayName("노선의 역들을 확인하기")
    @Test
    void getStations() {
        // when
        List<Station> stations = 신분당선.getStations();

        // then
        assertThat(stations.size()).isEqualTo(테스트역.size());
    }

    @DisplayName("노선에 섹션 추가하기")
    @Test
    void addSection() {
        // given
        final Station 종착역 = 테스트역.get(테스트역.size()-1);
        final Station 정자역 = new Station("정자역");

        // when & then
        신분당선.addSection(종착역, 정자역, 10);
    }

    @DisplayName("중간 섹션에 섹션 추가하기 - 상행선 기준")
    @Test
    void addSectionInMiddleBasedOnUpStation() {
        // given
        final Station 종착역 = 테스트역.get(테스트역.size()-1);
        final Station 광교역 = new Station("광교역");
        신분당선.addSection(종착역, 광교역, 20);

        // when
        final Station 정자역 = new Station("정자역");
        신분당선.addSection(종착역, 정자역, 10);

        // then
        List<Station> resultStations = 신분당선.getStations();
        assertThat(resultStations).containsExactly(강남역, 양재역, 정자역, 광교역);
    }

    @DisplayName("중간 섹션에 섹션 추가하기 - 하행선 기준")
    @Test
    void addSectionInMiddleBasedOnDownStation() {
        // given
        final Station 종착역 = 테스트역.get(테스트역.size()-1);
        final Station 광교역 = new Station("광교역");
        신분당선.addSection(종착역, 광교역, 20);

        // when
        final Station 정자역 = new Station("정자역");
        신분당선.addSection(정자역, 광교역, 10);

        // then
        List<Station> resultStations = 신분당선.getStations();
        assertThat(resultStations).containsExactly(강남역, 양재역, 정자역, 광교역);
    }

    @DisplayName("이미 존재하는 역 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
        // given
        final Station 종착역 = 테스트역.get(테스트역.size()-1);
        final Station 정자역 = new Station("정자역");
        신분당선.addSection(종착역, 정자역, 10);

        // then
        assertThatThrownBy(() -> {
            // when
            신분당선.addSection(종착역, 정자역, 10);
        }).isInstanceOf(InvalidStationException.class);
    }

    @DisplayName("구간 삭제하기")
    @Test
    void removeSection() {
        // given
        final Station 종착역 = 테스트역.get(테스트역.size()-1);
        final Station 정자역 = new Station("정자역");
        신분당선.addSection(종착역, 정자역, 10);

        // when
        신분당선.removeSection(정자역);

        // then
        assertThat(신분당선.getStations().size()).isEqualTo(테스트역.size());
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        // given
        final Station 종착역 = 테스트역.get(테스트역.size()-1);

        // when
        assertThatThrownBy(()->{
            신분당선.removeSection(종착역);
        }).isInstanceOf(InvalidSectionOperationException.class);
    }
}
