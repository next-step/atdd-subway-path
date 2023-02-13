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

    @DisplayName("지하철 노선에 새로운 구간 추가하기")
    @Test
    void addSection() {
        // given
        Station 서울역 = stationRepository.save(new Station("서울역"));
        Station 시청역 = stationRepository.save(new Station("시청역"));
        Line line = lineRepository.save(new Line("1호선", "남색"));

        // when
        lineService.addSection(line.getId(), new SectionRequest(서울역.getId(), 시청역.getId(), 10));

        // then
        assertThat(line.getStations()).containsExactly(서울역, 시청역);
    }
}
