package nextstep.subway.line.domain;

import nextstep.subway.line.exception.HasNoneOrOneSectionException;
import nextstep.subway.line.exception.InvalidSectionDistanceException;
import nextstep.subway.line.exception.NotLastStationException;
import nextstep.subway.line.exception.SectionDuplicatedException;
import nextstep.subway.line.exception.SectionNotConnectedException;
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
    Station 사당역 = new Station("사당역");
    Line 이호선;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(역삼역, "id", 2L);
        ReflectionTestUtils.setField(삼성역, "id", 3L);
        ReflectionTestUtils.setField(사당역, "id", 4L);
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
        @DisplayName("지하철 구간의 가장 마지막에 추가한다면")
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
        @DisplayName("지하철 구간의 가장 앞에 추가한다면")
        class Context_add_first {
            @Test
            @DisplayName("지하철 구간을 추가한다")
            void it_add_a_section() {
                // when
                이호선.addSection(삼성역, 강남역, 15);

                // then
                assertThat(이호선.getStations()).containsExactly(Arrays.array(삼성역, 강남역, 역삼역));
            }
        }

        @Nested
        @DisplayName("지하철 구간의 중간에 추가(상행역이 존재)한다면")
        class Context_add_in_middle_1 {
            @Test
            @DisplayName("지하철 구간을 추가한다")
            void it_add_a_section() {
                // given
                int distance = 이호선.getSections().getSections().get(0).getDistance();

                // when
                이호선.addSection(강남역, 삼성역, 4);

                // then
                assertThat(이호선.getStations()).containsExactly(Arrays.array(강남역, 삼성역, 역삼역));
                assertThat(이호선.getSections().getTotalDistance()).isEqualTo(distance);
            }
        }

        @Nested
        @DisplayName("지하철 구간의 중간에 추가(하행역이 존재)한다면")
        class Context_add_in_middle_2 {
            @Test
            @DisplayName("지하철 구간을 추가한다")
            void it_add_a_section() {
                // given
                int distance = 이호선.getSections().getSections().get(0).getDistance();

                // when
                이호선.addSection(삼성역, 역삼역, 4);

                // then
                assertThat(이호선.getStations()).containsExactly(Arrays.array(강남역, 삼성역, 역삼역));
                assertThat(이호선.getSections().getTotalDistance()).isEqualTo(distance);
            }
        }

        @Nested
        @DisplayName("지하철 구간의 중간에 추가(상행역이 존재)하면서 기존 역 사이 길이보다 크거나 같으면")
        class Context_add_already_included_with_greater_or_equal_distance_1 {
            @Test
            @DisplayName("예외를 발생시킨다")
            void it_throw_exception() {
                // when, then
                assertThatThrownBy(() -> 이호선.addSection(강남역, 삼성역, 15))
                        .isInstanceOf(InvalidSectionDistanceException.class);
            }
        }

        @Nested
        @DisplayName("지하철 구간의 중간에 추가(하행역이 존재)하면서 기존 역 사이 길이보다 크거나 같으면")
        class Context_add_already_included_with_greater_or_equal_distance_2 {
            @Test
            @DisplayName("예외를 발생시킨다")
            void it_throw_exception() {
                // when, then
                assertThatThrownBy(() -> 이호선.addSection(삼성역, 역삼역, 15))
                        .isInstanceOf(InvalidSectionDistanceException.class);
            }
        }

        @Nested
        @DisplayName("중복된 구간이면")
        class Context_with_duplicate_section {
            @Test
            @DisplayName("예외를 발생시킨다")
            void it_throw_exception() {
                // when, then
                assertThatThrownBy(() -> 이호선.addSection(강남역, 역삼역, 15))
                        .isInstanceOf(SectionDuplicatedException.class);
            }
        }

        @Nested
        @DisplayName("연결되지 않는 구간이면")
        class Context_with_not_connected_section {
            @Test
            @DisplayName("예외를 발생시킨다")
            void it_throw_exception() {
                // when, then
                assertThatThrownBy(() -> 이호선.addSection(삼성역, 사당역, 15))
                        .isInstanceOf(SectionNotConnectedException.class);
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
