package nextstep.subway.line;

import nextstep.subway.line.section.SectionsUpdateRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
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
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 선릉역 = stationRepository.save(new Station("선릉역"));
        Station 교대역 = stationRepository.save(new Station("교대역"));
        Line line = new Line("신분당선",
                "bg-red-600",
                강남역,
                선릉역,
                10L);
        Line 신분당선 = lineRepository.save(line);

        // when
        SectionsUpdateRequest 선릉역_부터_교대역 = new SectionsUpdateRequest(교대역.getId(), 선릉역.getId(), 10L);
        lineService.addSection(신분당선.getId(), 선릉역_부터_교대역);

        // then
        LineResponse actual = lineService.findLine(신분당선.getId());
        Line findLine = lineRepository.findById(신분당선.getId()).orElseThrow(RuntimeException::new);
        LineResponse expected = LineResponseFactory.createLineResponse(findLine);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

}
