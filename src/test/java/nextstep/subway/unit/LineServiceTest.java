package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LineService Test")
@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    /**
     * given stationRepository와 lineRepository를 활용하여 초기값을 세팅하고
     * when lineService.addSection 호출하면
     * then 불러온(line.getSections) 구간들에 추가한 구간이 포함되어 있다.
     */
    @DisplayName("지하철 노선 구간 추가")
    @Test
    void addSection() {
        // given
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("양재역"));
        Line line = lineRepository.save(new Line("신분당선", "빨강"));

        // when
        lineService.addSection(line.getId(), new SectionRequest(upStation.getId(), downStation.getId(), 10));

        // then
        assertThat(line.getSections()).contains(new Section(line, upStation, downStation, 10));
    }
}
