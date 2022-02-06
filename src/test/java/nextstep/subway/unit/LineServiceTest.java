package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

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
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        Station 정자역 = new Station("정자역");
        stationRepository.save(강남역);
        stationRepository.save(판교역);
        stationRepository.save(정자역);
        LineResponse lineResponse = lineService.saveLine(
                new LineRequest("신분당선", "red", 강남역.getId(), 판교역.getId(), 8));
        SectionRequest sectionRequest = new SectionRequest(판교역.getId(), 정자역.getId(), 4);

        // when
        lineService.addSection(sectionRequest, lineResponse.getId());

        // then
        Line line = lineRepository.getById(lineResponse.getId());
        assertThat(line.getAllStations()).containsExactly(강남역, 판교역, 정자역);
    }
}
