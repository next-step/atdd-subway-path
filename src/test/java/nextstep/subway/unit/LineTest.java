package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    private final Station 강남역 = new Station(1L, "강남역");
    private final Station 양재역 = new Station(2L, "양재역");
    private final Station 판교역 = new Station(3L, "판교역");
    private final Line 신분당선 = new Line("신분당선", "RED", 강남역, 양재역, 10L);

    @DisplayName("노선에 구간을 추가한다.")
    @Nested
    class AddSection {

        @DisplayName("성공")
        @Test
        void success() {
            // when
            신분당선.addNewSection(양재역, 판교역, 10L);

            // then
            assertThat(신분당선.getAllStations()).containsExactly(강남역, 양재역, 판교역);
        }

        @DisplayName("지하철 노선 구간에 이미 등록되어 있는 역을 추가하려 하면 에러가 발생한다.")
        @Test
        void duplicateException() {
            assertThatThrownBy(() -> 신분당선.addNewSection(양재역, 강남역, 10L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("주어진 구간은 이미 노선에 등록되어 있는 구간입니다. upStationId: 2, downStationId: 1");
        }

        @DisplayName("새로 추가하려는 구간의 상행역이 노선의 하행 종착역과 다른 역이라면 에러가 발생한다.")
        @Test
        void notFoundException() {
            assertThatThrownBy(() -> 신분당선.addNewSection(강남역, 판교역, 10L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("새로운 구간을 추가할 수 있는 연결점이 없습니다. upStationId: 1, downStationId: 3");
        }

        @DisplayName("새로 추가하려는 구간의 하행역이 노선의 하행 시작역과 다른 역이라면 에러가 발생한다.")
        @Test
        void notFoundException2() {
            assertThatThrownBy(() -> 신분당선.addNewSection(판교역, 양재역, 10L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("새로운 구간을 추가할 수 있는 연결점이 없습니다. upStationId: 3, downStationId: 2");
        }
    }

    @DisplayName("노선에 속하는 지하철 역 목록을 조회한다.")
    @Test
    void getStations() {
        assertThat(신분당선.getAllStations()).containsExactly(강남역, 양재역);
    }

    @DisplayName("노선에서 구간을 제거한다.")
    @Nested
    class RemoveSection {

        @DisplayName("성공")
        @Test
        void success() {
            // given
            Station 판교역 = new Station(3L, "판교역");
            신분당선.addNewSection(양재역, 판교역, 10L);

            // when
            신분당선.removeStation(판교역);

            // then
            assertThat(신분당선.getAllStations()).containsExactly(강남역, 양재역);
        }

        @DisplayName("노선의 하행 종착역만 삭제할 수 있다.")
        @Test
        void removeException() {
            assertThatThrownBy(() -> 신분당선.removeStation(강남역))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("노선의 하행 종착역만 삭제할 수 있습니다. stationId: 1");
        }

        @DisplayName("노선에 남은 구간이 1개뿐이라 삭제할 수 없다.")
        @Test
        void removeLastSectionException() {
            assertThatThrownBy(() -> 신분당선.removeStation(양재역))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("노선에 남은 구간이 1개뿐이라 삭제할 수 없습니다.");
        }
    }
}
