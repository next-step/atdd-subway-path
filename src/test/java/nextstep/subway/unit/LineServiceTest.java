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

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @DisplayName("지하철 라인에 구간을 추가한다")
    @Test
    void addSection() {
        // given
        Line line = lineRepository.save(new Line("1호선", "남색"));
        Station station1 = stationRepository.save(new Station("station1"));
        Station station2 = stationRepository.save(new Station("station2"));
        Station station3 = stationRepository.save(new Station("station3"));
        int distance = 10;
        line.addSection(Section.of(line, station1, station2, distance));
        SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(), distance);


        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), sectionRequest);

        // then
        assertThat(line.getSections().size()).isEqualTo(2);
    }
}
