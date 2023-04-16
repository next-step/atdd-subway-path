package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PathServiceTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineService lineService;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private PathService pathService;

    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Station 청명역;
    private Station 영통역;

    private Line 강남_2호선;
    private Line 수인_분당선;

    @BeforeEach
    void setUp() {
        강남_2호선 = lineRepository.save(new Line("강남_2호선", "green"));
        강남역 = stationRepository.save(new Station("강남역"));
        역삼역 = stationRepository.save(new Station("역삼역"));
        삼성역 = stationRepository.save(new Station("삼성역"));

        lineService.addSection(강남_2호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10));
        lineService.addSection(강남_2호선.getId(), new SectionRequest(역삼역.getId(), 삼성역.getId(), 15));

        수인_분당선 = lineRepository.save(new Line("수인_분당선", "yellow"));
        청명역 = stationRepository.save(new Station("청명역"));
        영통역 = stationRepository.save(new Station("영통역"));

        lineService.addSection(수인_분당선.getId(), new SectionRequest(청명역.getId(), 영통역.getId(), 20));
    }

    @Test
    void 경로를_조회한다() {
        PathResponse response = pathService.findPath(강남역.getId(), 삼성역.getId());
        List<String> stationNames = response.getStations().stream().map(station -> station.getName()).collect(Collectors.toList());

        assertThat(stationNames).containsExactly(강남역.getName(), 역삼역.getName(), 삼성역.getName());
        assertThat(response.getDistance()).isEqualTo(25);
    }
}
