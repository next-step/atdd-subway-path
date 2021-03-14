package nextstep.subway.line.application;

import nextstep.repository.MemoryLineRepository;
import nextstep.repository.MemoryStationRepository;
import nextstep.subway.exceptions.InvalidSectionException;
import nextstep.subway.exceptions.OnlyOneSectionException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DisplayName("LineService 클래스")
@ExtendWith(SpringExtension.class)
public class LineServiceTest {
    private final LineRepository lineRepository = new MemoryLineRepository();

    private StationService stationService = new StationService(new MemoryStationRepository());

    private LineService lineService;
    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Line 이호선;

    @BeforeEach
    void setup() {
        lineService = new LineService(lineRepository, stationService);

        강남역 = new Station("강남역");
        stationService.saveStation(new StationRequest(강남역.getName()));
        ReflectionTestUtils.setField(강남역, "id", 1L);

        역삼역 = new Station("역삼역");
        ReflectionTestUtils.setField(역삼역, "id", 2L);
        stationService.saveStation(new StationRequest(역삼역.getName()));

        삼성역 = new Station("삼성역");
        ReflectionTestUtils.setField(삼성역, "id", 3L);
        stationService.saveStation(new StationRequest(삼성역.getName()));

        이호선 = new Line("2호선", "green", 강남역, 역삼역, 10);
        ReflectionTestUtils.setField(이호선, "id", 1L);
        lineService.saveLine(new LineRequest(이호선.getName(), 이호선.getColor(), 강남역.getId(), 역삼역.getId(), 10));
    }


    @Nested
    @DisplayName("getStations 메소드는")
    class Describe_getStations {

        @DisplayName("노선의 포함된 모든 전철역을 조회한다.")
        @Test
        void it_returns_stations_by_line() {
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

                //when,then
                assertThatThrownBy(() -> lineService.removeSection(이호선.getId(), 역삼역.getId()))
                        .isInstanceOf(OnlyOneSectionException.class);

            }
        }
    }

}
