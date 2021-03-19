package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 노선 기능 테스트")
public class LineTest {

    private Station 강남역, 양재역, 양재시민의숲;
    private Line 신분당선;

    @BeforeEach
    void setUp(){
        // given
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        양재시민의숲 = new Station("양재시민의숲");
        신분당선 = new Line("신분당선", "bg-red-600", 강남역, 양재역, 10 );
    }

    @DisplayName("지하철 노선의 역 조회")
    @Test
    void getStations() {
        // when
        List<Station> stations = 신분당선.getStations();

        // then
        assertThat(stations.size()).isEqualTo(2);
        assertThat(stations.stream()
                .map(s -> s.getId())
                .collect(Collectors.toList()))
                .containsExactlyElementsOf(Arrays.asList(강남역.getId(), 양재역.getId()));
    }

    @DisplayName("지하철 노선에 구간 추가")
    @Test
    void addSection() {
        // when
        신분당선.addSection(양재역, 양재시민의숲, 6);

        // then
        assertThat(신분당선.getStations().size()).isEqualTo(3);
        assertThat(신분당선.getStations().stream()
                .map(s -> s.getId())
                .collect(Collectors.toList()))
                .containsExactlyElementsOf(Arrays.asList(강남역.getId(), 양재역.getId(), 양재시민의숲.getId()));
    }

    /*@DisplayName("목록 중간에 추가할 경우 에러 발생")
    @Test
    void addSectionInMiddle() {
        // when + then
        assertThatThrownBy(() -> {
            신분당선.addSection(강남역, 양재시민의숲, 6);
        }).isInstanceOf(RuntimeException.class);
    }*/

    @DisplayName("이미 존재하는 구간 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
        // when + then
        assertThatThrownBy(()->{
            신분당선.addSection(강남역, 양재역, 10);
        }).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("지하철 노선의 구간 제거")
    @Test
    void removeSection() {
        // given
        신분당선.addSection(양재역, 양재시민의숲, 6);

        // when
        신분당선.removeSection(양재역.getId());

        // then
        assertThat(신분당선.getStations().size()).isEqualTo(2);
        assertThat(신분당선.getStations().stream()
                .map(s -> s.getId())
                .collect(Collectors.toList()))
                .containsExactlyElementsOf(Arrays.asList(강남역.getId(), 양재시민의숲.getId()));
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        // when + then
        assertThatThrownBy(()->{
            신분당선.removeSection(양재역.getId());
        }).isInstanceOf(RuntimeException.class);
    }
}
