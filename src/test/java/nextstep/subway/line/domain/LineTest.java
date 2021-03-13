package nextstep.subway.line.domain;

import nextstep.subway.exceptions.InvalidSectionException;
import nextstep.subway.exceptions.OnlyOneSectionException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@DisplayName("LineService 클래스")
@ExtendWith(SpringExtension.class)
public class LineTest {
    @MockBean
    private LineRepository lineRepository;

    @MockBean
    private StationService stationService;

    private LineService lineService;
    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Line 이호선;

    @BeforeEach
    void setup() {
        lineService = new LineService(lineRepository, stationService);

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
    @DisplayName("getStations 메소드는")
    class Describe_getStations {

        @DisplayName("노선의 포함된 모든 전철역을 조회한다.")
        @Test
        void it_returns_stations_by_line() {
            //given
            when(lineRepository.findById(이호선.getId())).thenReturn(java.util.Optional.ofNullable(이호선));

            //when
            List<StationResponse> stations = lineService.getStations(이호선.getId());

            //then
            assertThat(stations).hasSize(2);
        }
    }

    @Nested
    @DisplayName("addSection 메소드는")
    class Describe_addSection {

        @Nested
        @DisplayName("만약 추가할 구간의 상행역이 기존 구간의 하행역이고")
        class Context_with_equal_station {

            @Nested
            @DisplayName("하행역이 등록되어있지 않다면")
            class Context_with_not_include_down_station {
                @DisplayName("정상적으로 구간이 추가되어야 한다.")
                @Test
                void addSection() {
                    //given
                    when(lineRepository.findById(이호선.getId())).thenReturn(java.util.Optional.ofNullable(이호선));
                    when(stationService.findStationById(역삼역.getId())).thenReturn(역삼역);
                    when(stationService.findStationById(삼성역.getId())).thenReturn(삼성역);

                    //when
                    SectionRequest sectionRequest = new SectionRequest(역삼역.getId(), 삼성역.getId(), 10);
                    lineService.addSection(이호선.getId(), sectionRequest);
                    LineResponse response = lineService.findLineResponseById(이호선.getId());

                    //then
                    assertThat(response.getStations()).hasSize(3);
                }
            }

        }

        @Nested
        @DisplayName("만약 추가할 구간이 이미 등록된 구간과 동일하다면")
        class Context_with_duplicate_section {

            @DisplayName("InvalidSectionException 에러가 발생해야 한다.")
            @Test
            void addSectionAlreadyIncluded() {
                //given
                when(lineRepository.findById(이호선.getId())).thenReturn(java.util.Optional.ofNullable(이호선));
                when(stationService.findStationById(강남역.getId())).thenReturn(강남역);
                when(stationService.findStationById(역삼역.getId())).thenReturn(역삼역);

                //when, then
                SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 역삼역.getId(), 10);
                assertThatThrownBy(() -> lineService.addSection(이호선.getId(), sectionRequest))
                        .isInstanceOf(InvalidSectionException.class);

            }
        }
    }

    @Nested
    @DisplayName("removeSection 메소드는")
    class Describe_removeSection {
        @Nested
        @DisplayName("만약 구간이 하나 이상일때 마지막 구간을 삭제한다면")
        class Context_with_valid_condition_of_remove_section {
            @DisplayName("삭제는 정상적으로 되야한다.")
            @Test
            void removeSection() {
                //given
                when(lineRepository.findById(이호선.getId())).thenReturn(java.util.Optional.ofNullable(이호선));
                when(stationService.findStationById(역삼역.getId())).thenReturn(역삼역);
                when(stationService.findStationById(삼성역.getId())).thenReturn(삼성역);
                SectionRequest sectionRequest = new SectionRequest(역삼역.getId(), 삼성역.getId(), 10);
                lineService.addSection(이호선.getId(), sectionRequest);

                //when
                lineService.removeSection(이호선.getId(), 삼성역.getId());
                LineResponse response = lineService.findLineResponseById(이호선.getId());

                //then
                assertThat(response.getStations()).hasSize(2);
            }
        }

        @Nested
        @DisplayName("만약 구간이 하나뿐일 때 삭제를 시도한다면")
        class Context_with_delete_last_section {
            @DisplayName("OnlyOneSectionException 에러가 발생해야 한다.")
            @Test
            void removeSectionNotEndOfList() {
                //given
                when(lineRepository.findById(이호선.getId())).thenReturn(java.util.Optional.ofNullable(이호선));

                //when,then
                assertThatThrownBy(() -> lineService.removeSection(이호선.getId(), 역삼역.getId()))
                        .isInstanceOf(OnlyOneSectionException.class);

            }
        }


    }


}
