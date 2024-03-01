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

        @DisplayName("노선의 끝에 구간을 추가한다.")
        @Test
        void success() {
            // when
            신분당선.addNewSection(양재역, 판교역, 10L);

            // then
            assertThat(신분당선.getAllStations()).containsExactly(강남역, 양재역, 판교역);
        }

        @DisplayName("노선의 중간에 구간을 추가한다. - 상행 겹침")
        @Test
        void success2() {
            // when
            신분당선.addNewSection(강남역, 판교역, 5L);

            // then
            assertThat(신분당선.getAllStations()).containsExactly(강남역, 판교역, 양재역);
        }

        @DisplayName("노선의 중간에 구간을 추가한다. - 하행 겹침")
        @Test
        void success3() {
            // when
            신분당선.addNewSection(판교역, 양재역, 5L);

            // then
            assertThat(신분당선.getAllStations()).containsExactly(강남역, 판교역, 양재역);
        }

        @DisplayName("지하철 노선 구간에 이미 등록되어 있는 구간을 추가하려 하면 에러가 발생한다.")
        @Test
        void duplicateException() {
            assertThatThrownBy(() -> 신분당선.addNewSection(양재역, 강남역, 10L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("주어진 구간은 이미 노선에 등록되어 있는 구간입니다. upStationId: 2, downStationId: 1");
        }

        @DisplayName("지하철 노선에 연결할 수 있는 역이 없다면 에러가 발생한다.")
        @Test
        void notConnectException() {
            Station 신논현역 = new Station(4L, "신논현역");

            assertThatThrownBy(() -> 신분당선.addNewSection(신논현역, 판교역, 10L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("새로운 구간을 추가할 수 있는 연결점이 없습니다. upStationId: 4, downStationId: 3");
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

        @DisplayName("노선의 하행 종착역을 제거할 수 있다.")
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

        @DisplayName("노선의 하행 시작역을 제거할 수 있다.")
        @Test
        void success2() {
            // given
            Station 판교역 = new Station(3L, "판교역");
            신분당선.addNewSection(양재역, 판교역, 10L);

            // when
            신분당선.removeStation(강남역);

            // then
            assertThat(신분당선.getAllStations()).containsExactly(양재역, 판교역);
        }

        @DisplayName("노선의 중간역을 제거할 수 있다.")
        @Test
        void removeException() {
            // given
            신분당선.addNewSection(양재역, 판교역, 10L);

            // when
            신분당선.removeStation(양재역);

            // then
            assertThat(신분당선.getAllStations()).containsExactly(강남역, 판교역);
        }

        @DisplayName("노선에 남은 구간이 1개뿐이라 제거할 수 없다.")
        @Test
        void removeLastSectionException() {
            assertThatThrownBy(() -> 신분당선.removeStation(양재역))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("노선에 남은 구간이 1개뿐이라 제거할 수 없습니다.");
        }

        @DisplayName("노선에 구간(역)이 존재하지 않으면 에러가 발생한다.")
        @Test
        void removeSectionException() {
            // given
            신분당선.addNewSection(양재역, 판교역, 10L);
            Station 신논현역 = new Station(4L, "신논현역");

            assertThatThrownBy(() -> 신분당선.removeStation(신논현역))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("노선에 구간이 존재하지 않습니다. stationId: 4");
        }
    }
}
