package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
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
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Station 선릉역 = stationRepository.save(new Station("선릉역"));
        Line 이호선 = lineRepository.save(Line.of("2호선", "green", 역삼역, 선릉역, 10));

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), 5));

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(이호선.getSections().getStations()).contains(역삼역, 선릉역);
    }
}
