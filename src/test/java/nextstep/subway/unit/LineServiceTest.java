package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
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
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("양재역"));

        Station addStation = stationRepository.save(new Station("판교역"));
        LineResponse lineResponse = lineService.saveLine(new LineRequest("신분당선", "노랑", upStation.getId(), downStation.getId(), 10));

        // when
        lineService.addSection(lineResponse.getId(), new SectionRequest(upStation.getId(), addStation.getId(), 2));
        Line line = lineRepository.findById(lineResponse.getId()).get();
        // then

        assertThat(line.getSections().stream().anyMatch(section -> section.isUp(upStation) && section.isDown(addStation)))
                .isEqualTo(true);
    }
}
