package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    private Station 사당역;
    private Station 수원역;
    private Station 화서역;

    @BeforeEach
    void init() {
        사당역 = stationRepository.save(new Station("사당역"));
        수원역 = stationRepository.save(new Station("수원역"));
        화서역 = stationRepository.save(new Station("화서역"));
    }

    @DisplayName("Line을 생성하다.")
    @Test
    void addLine() {
        // when
        LineResponse result =
                lineService.saveLine(new LineRequest("1호선", "파란색", 사당역.getId(), 수원역.getId(), 10));

        // then
        assertThat(result.getName()).isEqualTo("1호선");
    }

    @DisplayName("특정 Line을 가져오다.")
    @Test
    void findLineById() {
        // given
        LineResponse lineResponse =
                lineService.saveLine(new LineRequest("1호선", "파란색", 1L, 2L, 10));

        // when
        LineResponse result = lineService.findById(lineResponse.getId());

        // then
        assertThat(result.getName()).isEqualTo("1호선");
    }

    @DisplayName("구간을 생성하다.")
    @Test
    void addSection() {
        // given
        Line line = new Line("1호선", "파란색", 사당역, 수원역, 10);
        lineRepository.save(line);

        // when
        lineService.addSection(line.getId(),new SectionRequest(사당역.getId(), 화서역.getId(), 5));

        // then
        assertThat(line.getSections()).hasSize(2);
    }

    @DisplayName("구간을 삭제하다.")
    @Test
    void deleteSection() {

        // given
        Line line = new Line("1호선", "파란색", 사당역, 수원역, 10);
        lineRepository.save(line);
        lineService.addSection(line.getId(),new SectionRequest(사당역.getId(), 화서역.getId(), 5));

        // when
        lineService.deleteSection(line.getId(), 수원역.getId());

        // then
        assertThat(line.getSections()).hasSize(1);
    }

    @AfterEach
    void deleteRepo() {
        lineRepository.deleteAll();
        stationRepository.deleteAll();
    }
}

