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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

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

    @Test
    void 노선목록을_조회한다() {
        // given
        lineRepository.save(new Line("8호선", "bg-pink-500"));
        lineRepository.save(new Line("2호선", "bg-lime-300"));

        // when
        List<LineResponse> lineResponses = lineService.showLines();

        // then
        assertAll(() -> {
            assertThat(lineResponses).hasSize(2);
            assertThat(lineResponses.stream().map(LineResponse::getName)).containsExactly("8호선", "2호선");
            assertThat(lineResponses.stream().map(LineResponse::getColor)).containsExactly("bg-pink-500", "bg-lime-300");
        });
    }

    @Test
    void 노선을_조회한다() {
        // given
        Line line = lineRepository.save(new Line("8호선", "bg-pink-500"));

        // when
        LineResponse response = lineService.findById(line.getId());

        // then
        assertAll(() -> {
            assertThat(response.getName()).isEqualTo("8호선");
            assertThat(response.getColor()).isEqualTo("bg-pink-500");
        });
    }

    @Test
    void 노선을_정보를_수정하라() {
        // given
        Line line = lineRepository.save(new Line("8호선", "bg-pink-500"));

        // when
        lineService.updateLine(line.getId(), new LineRequest("2호선", "bg-lime-300", 1L, 2L, 10));

        // then
        assertAll(() -> {
            assertThat(line.getName()).isEqualTo("2호선");
            assertThat(line.getColor()).isEqualTo("bg-lime-300");
        });
    }
}
