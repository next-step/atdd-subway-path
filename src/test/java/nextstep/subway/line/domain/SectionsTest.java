package nextstep.subway.line.domain;

import nextstep.subway.line.exception.InvalidSectionOperationException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {
    private Line 신분당선 = new Line("신분당선","bg-red-600");
    private Sections 신분당선_구간집합 = new Sections();
    private final Station 강남역 = new Station("강남역");
    private final Station 양재역 = new Station("양재역");

    @BeforeEach
    void setUp(){
        신분당선_구간집합.add(new Section(신분당선, 강남역, 양재역, 10));
    }

    @DisplayName("구간에서 역들 확인하기")
    @Test
    void getStations() {
        // when
        List<Station> stations = 신분당선_구간집합.getStations();

        // then
        assertThat(stations.size()).isEqualTo(2);
    }

    @DisplayName("해당역을 상행역으로 가진 구간 찾기")
    @Test
    void findSectionByUpStation() {
        // given
        final Station 정자역 = new Station("정자역");
        신분당선_구간집합.add(new Section(신분당선, 양재역, 정자역, 8));

        // when
        final Section resultSection = 신분당선_구간집합.findSectionByUpStation(양재역).get();

        // then
        assertThat(resultSection).isNotNull();
        assertThat(resultSection.getUpStation()).isEqualTo(양재역);
    }


    @DisplayName("해당역을 하행역으로 가진 구간 찾기")
    @Test
    void findSectionByDownStation() {
        // given
        final Station 정자역 = new Station("정자역");
        신분당선_구간집합.add(new Section(신분당선, 양재역, 정자역, 8));

        // when
        final Section resultSection = 신분당선_구간집합.findSectionByDownStation(정자역).get();

        // then
        assertThat(resultSection).isNotNull();
        assertThat(resultSection.getDownStation()).isEqualTo(정자역);
    }

    @DisplayName("노션에서 가장 앞에 있는 역 삭제하기")
    @Test
    void removeFrontStation() {
        // given
        final Station 정자역 = new Station("정자역");
        final Station 광교역 = new Station("광교역");
        신분당선_구간집합.add(new Section(신분당선, 양재역, 정자역, 8));
        신분당선_구간집합.add(new Section(신분당선, 정자역, 광교역, 20));

        // when
        신분당선_구간집합.remove(강남역);

        // then
        List<Station> resultStations = 신분당선_구간집합.getStations();
        assertThat(resultStations).isNotNull();
        assertThat(resultStations.get(0)).isEqualTo(양재역);
    }

    @DisplayName("노선에서 가장 뒤 있는 역 삭제하기")
    @Test
    void removeRearmostStation() {
        // given
        final Station 정자역 = new Station("정자역");
        final Station 광교역 = new Station("광교역");
        신분당선_구간집합.add(new Section(신분당선, 양재역, 정자역, 8));
        신분당선_구간집합.add(new Section(신분당선, 정자역, 광교역, 20));

        // when
        신분당선_구간집합.remove(광교역);

        // then
        List<Station> resultStations = 신분당선_구간집합.getStations();
        assertThat(resultStations).isNotNull();
        assertThat(resultStations.get(resultStations.size()-1)).isEqualTo(정자역);
    }

    @DisplayName("노션에서 중간 역 삭제하기")
    @Test
    void removeMiddleStation() {
        // given
        final Station 정자역 = new Station("정자역");
        final Station 광교역 = new Station("광교역");
        신분당선_구간집합.add(new Section(신분당선, 양재역, 정자역, 8));
        신분당선_구간집합.add(new Section(신분당선, 정자역, 광교역, 20));

        // when
        신분당선_구간집합.remove(양재역);

        // then
        List<Station> resultStations = 신분당선_구간집합.getStations();
        assertThat(resultStations).isNotNull();
        assertThat(resultStations.get(1)).isEqualTo(정자역);
    }

    @DisplayName("노션에서 한 구간만 존재할 경우 삭제 실")
    @Test
    void removeLeftOneSection() {
        // then
        assertThatThrownBy(()->{
            // when
            신분당선_구간집합.remove(양재역);
        }).isInstanceOf(InvalidSectionOperationException.class);

    }
}
