package nextstep.subway.line.domain;


import nextstep.subway.exceptions.InvalidSectionException;
import nextstep.subway.exceptions.OnlyOneSectionException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Line 클래스")
public class LineTest {
    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Line 이호선;

    @BeforeEach
    void setup() {
        강남역 = new Station("강남역");
        ReflectionTestUtils.setField(강남역, "id", 1L);
        역삼역 = new Station("역삼역");
        ReflectionTestUtils.setField(역삼역, "id", 2L);
        삼성역 = new Station("삼성역");
        ReflectionTestUtils.setField(삼성역, "id", 3L);
        이호선 = new Line("2호선", "green", 강남역, 역삼역, 10);
        ReflectionTestUtils.setField(이호선, "id", 1L);
    }

    @Nested
    @DisplayName("addSection 메소드는")
    class Describe_addSection {
        @DisplayName("만약 새로운 구간의 상행역이나 하행역이 기존 구간의 하행역이거나 상행역이고")
        @Nested
        class Context_with_valid_UpStationOrDownSection {

            @DisplayName("새로운 구간의 나머지 역이 등록되어 있지 않다면")
            @Nested
            class Context_with_valid_other_section {
                @DisplayName("정상적으로 구간이 추가된다.")
                @Test
                void addSectionWithValidSection() {
                    //given
                    Section section = new Section(이호선, 역삼역, 삼성역, 10);

                    //when
                    이호선.addSection(section);

                    //then
                    assertThat(이호선.getStationsAll()).hasSize(3);
                }
            }

            @DisplayName("새로운 구간의 나머지 역이 등록되어 있다면")
            @Nested
            class Context_with_invalid_other_section {
                @DisplayName("InvalidSectionException 에러가 발생해야 한다.")
                @Test
                void addSectionWithInValidSection() {
                    //given
                    Section section1 = new Section(이호선, 역삼역, 삼성역, 10);
                    이호선.addSection(section1);

                    //when, then
                    Section section2 = new Section(이호선, 강남역, 삼성역, 10);

                    assertThatThrownBy(() -> 이호선.addSection(section2)).isInstanceOf(InvalidSectionException.class);
                }

            }


        }

        @DisplayName("만약 새로운 구간이 등록된 구간과 동일하다면")
        @Nested
        class Context_with_duplicate_section {
            @DisplayName("InvalidSectionException 에러가 발생해야 한다.")
            @Test
            void addSectionAlreadyIncluded() {
                //when, then
                assertThatThrownBy(() -> {
                    Section section1 = new Section(이호선, 강남역, 역삼역, 10);
                    이호선.addSection(section1);
                }).isInstanceOf(InvalidSectionException.class);

            }
        }

    }

    @Nested
    @DisplayName("deleteSection 메소드는")
    class Describe_deleteSection {
        @DisplayName("만약 삭제하려는 역이 등록된 구간에 존재하는 역일 경우에")
        @Nested
        class Context_with_delete_station_is_exists {
            @DisplayName("구간이 2개 이상일 경우")
            @Nested
            class Context_with_section_more_than_two {
                @DisplayName("삭제는 성공적으로 되며 삭제되는 역을 포함 두 구간이 서로 이어진 구간이 생성된다.")
                @Test
                void it_is_delete_and_created_new_section() {
                    //given
                    Section section = new Section(이호선, 역삼역, 삼성역, 10);
                    이호선.addSection(section);

                    //when
                    이호선.deleteSection(역삼역.getId());

                    //then
                    assertThat(이호선.getStationsAll()).hasSize(2);
                    assertThat(이호선.getStationsAll()).containsExactly(강남역, 삼성역);

                }
            }

            @DisplayName("구간이 1개 이하일 경우")
            @Nested
            class Context_with_section_less_than_one {
                @DisplayName("삭제는 실패한다.")
                @Test
                void is_is_deleted_failed() {
                    //when,then
                    assertThatThrownBy(() -> {
                        이호선.deleteSection(역삼역.getId());
                    }).isInstanceOf(OnlyOneSectionException.class);
                }
            }
        }

    }

    @Nested
    @DisplayName("getStationsAll 메소드는")
    class Describe_getStationsAll {

        @DisplayName("등록된 구간의 전철역을 순서를 맞춰 반환한다.")
        @Test
        void it_is_get_station_of_all_section_in_order() {
            //given
            Section section1 = new Section(이호선, 역삼역, 삼성역, 10);
            이호선.addSection(section1);

            //when
            List<Station> stationsAll = 이호선.getStationsAll();

            //then
            assertThat(stationsAll).hasSize(3);
            assertThat(stationsAll).containsExactly(강남역, 역삼역, 삼성역);
        }

    }

}
