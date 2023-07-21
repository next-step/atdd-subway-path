package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    void addSection() {
        // given
        Station 봉천역 = stationRepository.save(new Station("봉천역"));
        Station 신림역 = stationRepository.save(new Station("신림역"));
        Line line = lineRepository.save(new Line("2호선", "#00FF00"));

        SectionRequest request = SectionRequest.builder()
            .upStationId(봉천역.getId())
            .downStationId(신림역.getId())
            .distance(10)
            .build();

        // when
        lineService.addSection(line.getId(), request);

        // then
        Assertions.assertThat(line.getStations())
            .asList()
            .containsExactly(봉천역, 신림역);
    }
}
