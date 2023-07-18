package nextstep.subway.unit.application.path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import static nextstep.subway.unit.LineFixture.makeLine;
import static nextstep.subway.unit.StationFixture.강남역;
import static nextstep.subway.unit.StationFixture.교대역;
import static nextstep.subway.unit.StationFixture.선릉역;
import static nextstep.subway.unit.StationFixture.역삼역;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.applicaion.path.PathService;
import nextstep.subway.applicaion.station.response.StationResponse;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.domain.station.exception.NoSuchStationException;
import nextstep.subway.global.SubwayException;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {
    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private PathService pathService;

    private static final Long SOURCE_ID = 1L;
    private static final Long TARGET_ID = 2L;

    @DisplayName("최단경로를 조회한다")
    @Nested
    class FindShortestPathTest {

        @Nested
        class Success {

            @Test
            void 최단경로를_조회한다() {
                // given
                given(stationRepository.getById(SOURCE_ID)).willReturn(교대역);
                given(stationRepository.getById(TARGET_ID)).willReturn(선릉역);
                given(stationRepository.findAll()).willReturn(List.of(교대역, 강남역, 역삼역, 선릉역));
                given(lineRepository.findAll()).willReturn(List.of(
                        makeLine(교대역, 강남역, 역삼역, 선릉역),
                        makeLine(교대역, 역삼역, 1)
                ));

                // when
                final var response = pathService.findShortestPath(SOURCE_ID, TARGET_ID);

                //then
                final var stations = response.getStations().stream()
                        .map(StationResponse::getId).collect(Collectors.toUnmodifiableList());
                final var expected = Stream.of(교대역, 역삼역, 선릉역)
                        .map(Station::getId).collect(Collectors.toUnmodifiableList());
                assertThat(stations).isEqualTo(expected);
            }
        }

        @Nested
        class Fail {

            @Test
            void 출발역과_도착역이_동일한_경우() {
                assertThatThrownBy(() -> pathService.findShortestPath(1L, 1L))
                        .isInstanceOf(SubwayException.class);
            }

            @Test
            void 출발역에서_도착역까지의_경로가_이어지지_않은_경우() {
                // given
                given(stationRepository.getById(SOURCE_ID)).willReturn(교대역);
                given(stationRepository.getById(TARGET_ID)).willReturn(선릉역);
                given(stationRepository.findAll()).willReturn(List.of(교대역, 강남역, 역삼역, 선릉역));
                given(lineRepository.findAll()).willReturn(List.of(makeLine(교대역, 강남역, 역삼역)));

                // when
                assertThatThrownBy(() -> pathService.findShortestPath(SOURCE_ID, TARGET_ID))
                        .isInstanceOf(SubwayException.class);
            }

            @Test
            void 출발역이_존재하지_않는_경우() {
                // given
                given(stationRepository.getById(SOURCE_ID)).willThrow(new NoSuchStationException(""));

                // when
                assertThatThrownBy(() -> pathService.findShortestPath(SOURCE_ID, TARGET_ID))
                        .isInstanceOf(NoSuchStationException.class);
            }

            @Test
            void 도착역이_존재하지_않는_경우() {
                // given
                given(stationRepository.getById(SOURCE_ID)).willReturn(역삼역);
                given(stationRepository.getById(TARGET_ID)).willThrow(new NoSuchStationException(""));

                // when
                assertThatThrownBy(() -> pathService.findShortestPath(SOURCE_ID, TARGET_ID))
                        .isInstanceOf(NoSuchStationException.class);
            }
        }
    }
}
