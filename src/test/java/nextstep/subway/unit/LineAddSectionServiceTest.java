package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.addsection.LineAddSectionRequest;
import nextstep.subway.line.addsection.LineAddSectionService;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LineAddSectionServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineAddSectionService lineAddSectionService;

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSection() {
        // given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 양재역 = stationRepository.save(new Station("양재역"));
        Station 판교역 = stationRepository.save(new Station("판교역"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "RED", 강남역, 양재역, 10L));

        LineAddSectionRequest request = new LineAddSectionRequest(양재역.getId(), 판교역.getId(), 10L);

        // when
        lineAddSectionService.addSection(신분당선.getId(), request);

        // then
        assertThat(신분당선.getAllStations()).containsExactly(강남역, 양재역, 판교역);
    }
}
