package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.InvalidDistanceException;
import nextstep.subway.exception.NotRegisteredStationException;
import nextstep.subway.exception.NotRegisteredUpStationAndDownStationException;
import nextstep.subway.exception.SectionAlreadyRegisteredException;
import nextstep.subway.exception.SingleSectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.fixture.LineFixture.이호선;
import static nextstep.subway.fixture.SectionFixture.강남_삼성_구간;
import static nextstep.subway.fixture.SectionFixture.강남_역삼_구간;
import static nextstep.subway.fixture.SectionFixture.강남_역삼_구간_비정상_거리;
import static nextstep.subway.fixture.SectionFixture.양재_정자_구간;
import static nextstep.subway.fixture.SectionFixture.역삼_삼성_구간;
import static nextstep.subway.fixture.SectionFixture.역삼_선릉_구간;
import static nextstep.subway.fixture.StationFixture.강남역;
import static nextstep.subway.fixture.StationFixture.범계역;
import static nextstep.subway.fixture.StationFixture.선릉역;
import static nextstep.subway.fixture.StationFixture.역삼역;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DisplayName("지하철 구간 단위 테스트")
class LineTest {

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 지하철_구간_등록 {

        @Nested
        @DisplayName("지하철 노선의 구간을 추가하면")
        class Context_with_add_section {

            private final Line line = 이호선.엔티티_생성();

            @Test
            @DisplayName("구간이 등록된다")
            void it_registered_section() throws Exception {
                line.addSection(강남_역삼_구간.엔티티_생성(line));

                assertThat(line.isEmptySections()).isFalse();
            }
        }

        @Nested
        @DisplayName("지하철 노선의 역 목록을 조회하면")
        class Context_with_select_all_stations {

            private final Line line = 이호선.엔티티_생성();

            @BeforeEach
            void setUp() {
                line.addSection(강남_역삼_구간.엔티티_생성(line));
            }

            @Test
            @DisplayName("지하철 구간에 등록되어 있는 모든 역 정보를 리스트로 반환한다")
            void it_returns_stations_list() throws Exception {
                List<Station> stations = line.getAllStations();

                assertThat(stations).hasSize(2)
                        .map(Station::getName)
                        .containsExactly(강남역.역_이름(), 역삼역.역_이름());
            }
        }

        @Nested
        @DisplayName("지하철 구간 추가 시 상행역과 하행역이 이미 노선에 모두 등록되어 있는 경우")
        class Context_with_already_registered_upstation_and_downstation {

            private final Line line = 이호선.엔티티_생성();

            @BeforeEach
            void setUp() {
                line.addSection(강남_역삼_구간.엔티티_생성(line));
            }

            @Test
            @DisplayName("SectionAlreadyRegisteredException 예외를 던진다")
            void it_returns_exception() throws Exception {
                assertThatThrownBy(() -> line.addSection(강남_역삼_구간.엔티티_생성(line)))
                        .isInstanceOf(SectionAlreadyRegisteredException.class);
            }
        }

        @Nested
        @DisplayName("지하철 구간 추가 시 상행역과 하행역 둘 중 하나도 노선에 포함되어 있지 않은 경우")
        class Context_with_not_registered_upstation_and_downstation {

            private final Line line = 이호선.엔티티_생성();

            @BeforeEach
            void setUp() {
                line.addSection(강남_역삼_구간.엔티티_생성(line));
            }

            @Test
            @DisplayName("NotRegisteredUpStationAndDownStationException 예외를 던진다")
            void it_returns_exception() throws Exception {
                assertThatThrownBy(() -> line.addSection(양재_정자_구간.엔티티_생성(line)))
                        .isInstanceOf(NotRegisteredUpStationAndDownStationException.class);
            }
        }

        @Nested
        @DisplayName("기존 역 사이에 새로운 역을 등록할 때 기존 구간 길이보다 크거나 같은 경우")
        class Context_with_invalid_distance {

            private final Line line = 이호선.엔티티_생성();

            @BeforeEach
            void setUp() {
                line.addSection(강남_삼성_구간.엔티티_생성(line));
            }

            @Test
            @DisplayName("InvalidDistanceException 예외를 던진다")
            void it_returns_exception() throws Exception {
                assertThatThrownBy(() -> line.addSection(강남_역삼_구간_비정상_거리.엔티티_생성(line)))
                        .isInstanceOf(InvalidDistanceException.class);
            }
        }

        /**
         * When 기존 구간 A-C에 신규 구간 A-B를 추가하면
         * Then 기존 구간의 상행역은 B로 수정된다. (A-C -> A-B-C)
         */
        @Nested
        @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
        class Context_with_add_new_final_upstation {

            private final Line line = 이호선.엔티티_생성();
            private final Section 기존_최상위_구간 = 강남_삼성_구간.엔티티_생성(line);

            @BeforeEach
            void setUp() {
                line.addSection(기존_최상위_구간);
            }

            @Test
            @DisplayName("기존 최상위 구간의 상행역은 신규 구간의 하행역으로 수정된다")
            void it_update_upstation() throws Exception {
                line.addSection(강남_역삼_구간.엔티티_생성(line));

                assertThat(기존_최상위_구간.getUpStation().getName())
                        .isEqualTo(역삼역.역_이름());
            }
        }

        /**
         * When 기존 구간 A-C에 신규 구간 B-C를 추가하면
         * Then 기존 구간의 하행역은 B로 수정된다. (A-C -> A-B-C)
         */
        @Nested
        @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
        class Context_with_add_new_final_downstation {

            private final Line line = 이호선.엔티티_생성();
            private final Section 기존_최하위_구간 = 강남_삼성_구간.엔티티_생성(line);

            @BeforeEach
            void setUp() {
                line.addSection(기존_최하위_구간);
            }

            @Test
            @DisplayName("기존 최하위 구간의 하행역은 신규 구간의 상행역으로 수정된다")
            void it_update_downstation() throws Exception {
                line.addSection(역삼_삼성_구간.엔티티_생성(line));

                assertThat(기존_최하위_구간.getDownStation().getName())
                        .isEqualTo(역삼역.역_이름());
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 지하철_구간_삭제 {

        @Nested
        @DisplayName("지하철 노선의 하행 종점역을 삭제하면")
        class Context_with_remove_final_downstation {

            private final Line line = 이호선.엔티티_생성();
            private final Station 하행_종점역 = 선릉역.엔티티_생성();

            @BeforeEach
            void setUp() {
                line.addSection(강남_역삼_구간.엔티티_생성(line));
                line.addSection(역삼_선릉_구간.엔티티_생성(line));
            }

            @Test
            @DisplayName("노선의 역 목록 조회 시 하행 종점역이 삭제되어 반환된다")
            void it_remove_last_section() throws Exception {
                line.removeSection(하행_종점역);

                assertThat(line.getAllStations()).doesNotContain(하행_종점역);
            }
        }

        @Nested
        @DisplayName("지하철 노선의 상행 종점역을 삭제하면")
        class Context_with_remove_final_upstation {

            private final Line line = 이호선.엔티티_생성();
            private final Station 첫번째_역 = 강남역.엔티티_생성();

            @BeforeEach
            void setUp() {
                line.addSection(강남_역삼_구간.엔티티_생성(line));
                line.addSection(역삼_선릉_구간.엔티티_생성(line));
            }

            @Test
            @DisplayName("노선의 역 목록 조회 시 상행 종점역이 삭제되어 반환된다")
            void it_remove_first_section() throws Exception {
                line.removeSection(첫번째_역);

                assertThat(line.getAllStations()).doesNotContain(첫번째_역);
            }
        }

        @Nested
        @DisplayName("기존 구간 사이의 역을 삭제하면")
        class Context_with_remove_middle_station {

            private final Line line = 이호선.엔티티_생성();
            private final Station 중간_역 = 역삼역.엔티티_생성();
            private final Section 기존_상행_구간 = 강남_역삼_구간.엔티티_생성(1L, line);

            @BeforeEach
            void setUp() {
                line.addSection(기존_상행_구간);
                line.addSection(역삼_선릉_구간.엔티티_생성(2L, line));
            }

            @Test
            @DisplayName("기존 상행 구간의 하행역은 기존 하행 구간의 하행역으로, 구간의 거리는 기존 앞 뒤 구간의 합으로 변경된다")
            void it_connecting_before_and_after_stations() throws Exception {
                line.removeSection(중간_역);

                assertThat(기존_상행_구간.getDownStation()).isEqualTo(선릉역.엔티티_생성());
                assertThat(기존_상행_구간.getDistance()).isEqualTo(강남_역삼_구간.구간_거리() + 역삼_선릉_구간.구간_거리());
                assertThat(line.getAllStations()).doesNotContain(중간_역);
            }
        }

        @Nested
        @DisplayName("구간이 하나인 노선에서 역을 삭제하면")
        class Context_with_remove_station_on_line_with_only_one_section {

            private final Line line = 이호선.엔티티_생성();

            @BeforeEach
            void setUp() {
                line.addSection(강남_역삼_구간.엔티티_생성(line));
            }

            @Test
            @DisplayName("SingleSectionException 예외를 던진다")
            void it_returns_exception() throws Exception {
                assertThatThrownBy(() -> line.removeSection(강남역.엔티티_생성()))
                        .isInstanceOf(SingleSectionException.class);

            }
        }

        @Nested
        @DisplayName("등록되지 않은 역을 삭제하면")
        class Context_with_remove_not_registered_station {

            private final Line line = 이호선.엔티티_생성();

            @BeforeEach
            void setUp() {
                line.addSection(강남_역삼_구간.엔티티_생성(line));
                line.addSection(역삼_선릉_구간.엔티티_생성(line));
            }

            @Test
            @DisplayName("NotRegisteredStationException 예외를 던진다")
            void it_returns_exception() throws Exception {
                assertThatThrownBy(() -> line.removeSection(범계역.엔티티_생성()))
                        .isInstanceOf(NotRegisteredStationException.class);
            }
        }
    }
}
