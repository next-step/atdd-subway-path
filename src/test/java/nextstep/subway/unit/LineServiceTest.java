package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
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

    /**
     * Given 노선이 있을 때
     * When 구간을 추가하면
     * Then 구간을 조회 할 수 있다.
     */
    @DisplayName("노선에 구간을 추가한다")
    @Test
    void addSection() {
        // given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Line 이호선 = lineRepository.save(new Line("2호선", "bg-green-600"));
        final int distance = 10;

        // when
        SectionRequest request = new SectionRequest(강남역.getId(), 역삼역.getId(), 10);
        lineService.addSection(이호선.getId(), request);

        // then
        assertThat(이호선.getSections().size()).isEqualTo(1);
    }
}
