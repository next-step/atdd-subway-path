package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 비즈니스 로직 테스트")
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
    @DisplayName("노선에 구간 추가 단위 테스트")
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Station 삼성역 = stationRepository.save(new Station("삼성역"));
        Line 이호선 = new Line("2호선", "bg-green-600", 강남역, 역삼역, 10);
        Line savedLine = lineRepository.save(이호선);

        SectionRequest sectionRequest = new SectionRequest(역삼역.getId(), 삼성역.getId(), 6);

        // when
        // lineService.addSection 호출
        lineService.addSection(savedLine.getId(),sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        Line line = lineService.findLineById(savedLine.getId());
        assertThat(lineService.getStations(line)).containsExactlyElementsOf(Arrays.asList(강남역, 역삼역, 삼성역));
    }
}
