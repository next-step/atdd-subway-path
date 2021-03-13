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
        Line savedLine = lineRepository.save(new Line("2호선", "bg-green-600"));

        lineService.addSectionToLine(savedLine.getId(), 구간_추가_요청(강남역, 역삼역, 10));

        // when
        // lineService.addSection 호출
        lineService.addSectionToLine(savedLine.getId(), 구간_추가_요청(역삼역, 삼성역, 6));

        // then
        // line.getSections 메서드를 통해 검증
        Line line = lineService.findLineById(savedLine.getId());
        assertThat(line.getStations()).containsExactlyElementsOf(Arrays.asList(강남역, 역삼역, 삼성역));
    }

    private SectionRequest 구간_추가_요청(Station upStation, Station downStation, int distance) {
        return new SectionRequest(upStation.getId(), downStation.getId(), distance);
    }
}
