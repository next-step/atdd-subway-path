package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class PathServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private PathService pathService;

    @Test
    void 경로_조회_기능() {
        final Station 강남역 = stationRepository.save(new Station("강남역"));
        final Station 역삼역 = stationRepository.save(new Station("역삼역"));
        final Station 선릉역 = stationRepository.save(new Station("선릉역"));

        final Line line = lineRepository.save(new Line("2호선", "green", 강남역, 역삼역, 10));
        line.addSection(역삼역, 선릉역, 10);

        final PathResponse response = pathService.getPath(
            new PathRequest(강남역.getId(), 선릉역.getId()));

        assertThat(response.getDistance()).isEqualTo(20);
        assertThat(response.getStations().stream().map(StationResponse::getId).collect(Collectors.toList()))
            .contains(강남역.getId(), 역삼역.getId());
    }
}
