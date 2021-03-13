package nextstep.subway.line.domain;

import nextstep.subway.line.exception.DownStationExistedException;
import nextstep.subway.line.exception.HasNoneOrOneSectionException;
import nextstep.subway.line.exception.NotLastStationException;
import nextstep.subway.line.exception.NotValidUpStationException;
import nextstep.subway.station.domain.Station;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Line 클래스")
class LineTest {

    Station 강남역 = new Station("강남역");
    Station 역삼역 = new Station("역삼역");
    Station 삼성역 = new Station("삼성역");
    Line 이호선;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(역삼역, "id", 2L);
        ReflectionTestUtils.setField(삼성역, "id", 3L);
        이호선 = new Line("2호선", "green", 강남역, 역삼역, 10);

    }

    @Nested
    @DisplayName("getStation 메소드는")
    class Describe_getStation {
        @Nested
        @DisplayName("지하철 노선이 생성되어 있다면")
        class Context_with_line {
            @Test
            @DisplayName("지하철역 목록을 리턴한다")
            void it_return_sections() {
                // when
                List<Station> stations = 이호선.getStations();

                // then
                assertThat(stations).containsExactly(Arrays.array(강남역, 역삼역));
            }
        }
    }

    @Nested
    @DisplayName("addSection 메소드는")
    class Describe_addSection {
        @Nested
        @DisplayName("지하철 구간 마지막에 추가한다면")
        class Context_add_last {
            @Test
            @DisplayName("지하철 구간을 추가한다")
            void it_add_a_section() {
                // when
                이호선.addSection(역삼역, 삼성역, 15);

                // then
                assertThat(이호선.getStations()).containsExactly(Arrays.array(강남역, 역삼역, 삼성역));
            }
        }

        @Nested
        @DisplayName("지하철 중간에 추가한다면")
        class Context_add_in_middle {
            @Test
            @DisplayName("예외를 발생시킨다")
            void it_throw_exception() {
                // when, then
                assertThatThrownBy(() -> 이호선.addSection(강남역, 삼성역, 15))
                        .isInstanceOf(NotValidUpStationException.class);
            }
        }

        @Nested
        @DisplayName("이미 존재하는 역을 추가한다면")
        class Context_add_already_included {
            @Test
            @DisplayName("예외를 발생시킨다")
            void it_throw_exception() {
                // when, then
                assertThatThrownBy(() -> 이호선.addSection(역삼역, 강남역, 15))
                        .isInstanceOf(DownStationExistedException.class);
            }
        }
    }

    @Nested
    @DisplayName("removeSection 메소드는")
    class Describe_removeSection {
        @Nested
        @DisplayName("지하철 구간이 2개 이상일 때 마지막 역을 제거한다면")
        class Context_line_has_sections_and_remove_last {
            @Test
            @DisplayName("지하철 구간을 제거한다")
            void it_remove_a_section() {
                // given
                이호선.addSection(역삼역, 삼성역, 15);

                // when
                이호선.removeSection(삼성역.getId());

                //then
                assertThat(이호선.getStations()).containsExactly(Arrays.array(강남역, 역삼역));
            }
        }

        @Nested
        @DisplayName("지하철 구간이 1개일 때 역을 제거한다면")
        class Context_line_has_a_section {
            @Test
            @DisplayName("예외를 발생시킨다")
            void it_throw_exception() {
                // given
                Long stationId = 역삼역.getId();

                // when, then
                assertThatThrownBy(() -> 이호선.removeSection(stationId))
                        .isInstanceOf(HasNoneOrOneSectionException.class);
            }
        }

        @Nested
        @DisplayName("하행 종점역이 아닌 역을 제거한다면")
        class Context_delete_not_last_station {
            @Test
            @DisplayName("예외를 발생시킨다")
            void it_throw_exception() {
                // given
                이호선.addSection(역삼역, 삼성역, 15);
                Long stationId = 역삼역.getId();

                // when, then
                assertThatThrownBy(() -> 이호선.removeSection(stationId))
                        .isInstanceOf(NotLastStationException.class);
            }
        }
    }
}
