package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
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
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station upStation = stationRepository.save(new Station("서울역"));
        Station downStation = stationRepository.save(new Station("용산역"));
        Line line = lineRepository.save(new Line("1호선", "bg-color-blue"));

        SectionRequest sectionRequest = getSectionRequest(upStation, downStation, 5);

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        Assertions.assertThat(line.getSections()).containsExactly(new Section(line, upStation, downStation, 5));
    }

    private SectionRequest getSectionRequest(Station upStation, Station downStation, int distance) {
        return SectionRequest.builder()
                .upStationId(upStation.getId())
                .downStationId(downStation.getId())
                .distance(distance)
                .build();
    }
}
