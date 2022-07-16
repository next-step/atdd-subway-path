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
import static org.junit.jupiter.api.Assertions.assertAll;

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
        Line line = lineRepository.save(new Line("8호선", "bg-pink-500"));
        Station upStation = stationRepository.save(new Station("암사역"));
        Station downStation = stationRepository.save(new Station("모란역"));

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), new SectionRequest(upStation.getId(), downStation.getId(), 20));

        // then
        // line.getSections 메서드를 통해 검증
        assertAll(() -> {
            assertThat(line.getSections().get(0).getUpStation()).isEqualTo(upStation);
            assertThat(line.getSections().get(0).getDownStation()).isEqualTo(downStation);
        });
    }

    @Test
    void 노선을_저장한다() {
        // given
        Line line = lineRepository.save(new Line("8호선", "bg-pink-500"));
        Station upStation = stationRepository.save(new Station("암사역"));
        Station downStation = stationRepository.save(new Station("모란역"));

        // when
        LineResponse response = lineService.saveLine(new LineRequest("8호선", "bg-pink-500", upStation.getId(), downStation.getId(), 20));

        // then
        assertAll(() -> {
            assertThat(response.getName()).isEqualTo("8호선");
            assertThat(response.getColor()).isEqualTo("bg-pink-500");
            assertThat(response.getStations()).hasSize(2);
        });
    }
}
