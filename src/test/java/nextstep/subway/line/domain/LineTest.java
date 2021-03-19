package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

@DisplayName("지하철 노선 기능 테스트")
public class LineTest {

    private Station 강남역, 양재역, 양재시민의숲;
    private Line 신분당선;

    @BeforeEach
    void setUp(){
        // given
        강남역 = new Station("강남역");
        ReflectionTestUtils.setField(강남역, "id", 1L);
        양재역 = new Station("양재역");
        ReflectionTestUtils.setField(양재역, "id", 2L);
        양재시민의숲 = new Station("양재시민의숲");
        ReflectionTestUtils.setField(양재시민의숲, "id", 3L);
        신분당선 = new Line("신분당선", "bg-red-600", 강남역, 양재역, 10 );
        ReflectionTestUtils.setField(신분당선, "id", 1L);
    }

    @DisplayName("지하철 노선의 역 조회")
    @Test
    void getStations() {
        // when
        List<Station> stations = 신분당선.getStations();

        // then
        assertThat(stations.size()).isEqualTo(2);
        assertThat(stations).extracting("id")
                .containsExactlyElementsOf(Arrays.asList(강남역.getId(), 양재역.getId()));
    }

    @DisplayName("지하철 노선에 구간 추가")
    @Test
    void addSection() {
        // when
        신분당선.addSection(양재역, 양재시민의숲, 6);

        // then
        assertThat(신분당선.getStations().size()).isEqualTo(3);
        assertThat(신분당선.getStations()).extracting("id", "name")
                .contains(tuple(양재시민의숲.getId(), 양재시민의숲.getName()));
    }

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
        신분당선.removeSection(양재시민의숲.getId());

        // then
        assertThat(신분당선.getStations().size()).isEqualTo(2);
        assertThat(신분당선.getStations())
                .extracting("name")
                .doesNotContain(양재시민의숲.getName());
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
