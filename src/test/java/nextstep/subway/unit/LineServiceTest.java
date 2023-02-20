package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.Test;

public class LineServiceTest {

    private StationRepository stationRepository = new InMemoryStationRepository();
    private StationService stationService = new StationService(stationRepository);
    private LineRepository lineRepository = new InMemoryLineRepository();
    private LineService lineService = new LineService(lineRepository, stationService);

    @Test
    void addSection() {
        // given
        final Line line = new Line(1L, "이호선", "초록색");
        final Station 강남역 = new Station(1L, "강남역");
        final Station 역삼역 = new Station(2L, "역삼역");

        lineRepository.save(line);
        stationRepository.save(강남역);
        stationRepository.save(역삼역);

        // when
        final SectionRequest sectionRequest = new SectionRequest(
            강남역.getId(),
            역삼역.getId(),
            10
        );
        lineService.addSection(line.getId(), sectionRequest);

        // then
        final LineResponse lineResponse = lineService.findById(line.getId());
        assertThat(lineResponse.getStations()).hasSize(2);
    }
}
