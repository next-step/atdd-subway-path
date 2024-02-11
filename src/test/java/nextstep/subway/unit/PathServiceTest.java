package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PathServiceTest {
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    @Mock
    private LineRepository lineRepository;

    private PathService pathService;

    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        선릉역 = new Station(3L, "선릉역");
        Line 이호선 = new Line("2호선", "green", 강남역, 역삼역, 10);

        이호선.addSection(역삼역, 선릉역, 10);

        when(lineRepository.findAll()).thenReturn(List.of(이호선));
        pathService = new PathService(lineRepository);
    }

    @Test
    void 경로_조회_기능() {
        final PathResponse response = pathService.getPath(
            new PathRequest(강남역.getId(), 선릉역.getId()));

        assertThat(response.getDistance()).isEqualTo(20);
        assertThat(response.getStations().stream().map(StationResponse::getId).collect(Collectors.toList()))
            .contains(강남역.getId(), 역삼역.getId());
    }
}
