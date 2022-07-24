package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.restassured.RestAssured;
import java.util.List;
import java.util.NoSuchElementException;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
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
    @DisplayName("지하철 노선 생성 정상 동작")
    void saveLine() {
        // given
        stationRepository.save(new Station("염창역"));
        stationRepository.save(new Station("당산역"));

        // when
        LineResponse response = lineService.saveLine(new LineRequest("9호선", "YELLOW", 1L, 2L, 10));

        // then
        assertThat(response.getName()).isEqualTo("9호선");
        assertThat(response.getColor()).isEqualTo("YELLOW");
        assertThat(response.getStations().get(0).getName()).isEqualTo("염창역");
        assertThat(response.getStations().get(1).getName()).isEqualTo("당산역");
    }

    @Test
    @DisplayName("구간 생성 정상 동작")
    void addSection() {
        // given
        Line line = lineRepository.save(new Line("9호선", "YELLOW"));
        Station station1 = stationRepository.save(new Station("염창역"));
        Station station2 = stationRepository.save(new Station("당산역"));

        // when
        lineService.addSection(line.getId(),new SectionRequest(station1.getId(),station2.getId(),10));

        // then
        assertThat(line.getSections()).hasSize(1);
        assertThat(line.getSections().get(0).getUpStation().getName()).isEqualTo("염창역");
        assertThat(line.getSections().get(0).getDownStation().getName()).isEqualTo("당산역");
    }

    @Test
    @DisplayName("지하철 노선 호출 정상 동작")
    void showLines() {
        // given
        Line line1 = lineRepository.save(new Line("9호선", "YELLOW"));
        Line line2 = lineRepository.save(new Line("2호선", "GREEN"));
        Station station1 = stationRepository.save(new Station("염창역"));
        Station station2 = stationRepository.save(new Station("당산역"));
        Station station3 = stationRepository.save(new Station("잠실역"));
        lineService.addSection(line1.getId(), new SectionRequest(station1.getId(),station2.getId(),10));
        lineService.addSection(line2.getId(), new SectionRequest(station2.getId(),station3.getId(),10));

        // when
        List<LineResponse> responses = lineService.showLines();

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getName()).isEqualTo("9호선");
        assertThat(responses.get(0).getColor()).isEqualTo("YELLOW");
        assertThat(responses.get(0).getStations().get(0).getName()).isEqualTo("염창역");
        assertThat(responses.get(0).getStations().get(1).getName()).isEqualTo("당산역");
        assertThat(responses.get(1).getName()).isEqualTo("2호선");
        assertThat(responses.get(1).getColor()).isEqualTo("GREEN");
        assertThat(responses.get(1).getStations().get(0).getName()).isEqualTo("당산역");
        assertThat(responses.get(1).getStations().get(1).getName()).isEqualTo("잠실역");
    }

    @Test
    @DisplayName("지하철 단일 노선 호출 정상 동작")
    void findByLineId() {
        // given
        Line line = lineRepository.save(new Line("9호선", "YELLOW"));
        Station station1 = stationRepository.save(new Station("염창역"));
        Station station2 = stationRepository.save(new Station("당산역"));
        lineService.addSection(line.getId(),new SectionRequest(station1.getId(),station2.getId(),10));

        // when
        LineResponse response = lineService.findById(line.getId());

        // then
        assertThat(response.getName()).isEqualTo("9호선");
        assertThat(response.getColor()).isEqualTo("YELLOW");
        assertThat(response.getStations().get(0).getName()).isEqualTo("염창역");
        assertThat(response.getStations().get(1).getName()).isEqualTo("당산역");
    }

    @Test
    @DisplayName("지하철 노선 수정 정상 동작")
    void updateLine() {
        // given
        Line line = lineRepository.save(new Line("9호선", "YELLOW"));
        Station station1 = stationRepository.save(new Station("염창역"));
        Station station2 = stationRepository.save(new Station("당산역"));
        lineService.addSection(line.getId(),new SectionRequest(station1.getId(),station2.getId(),10));

        // when
        lineService.updateLine(line.getId(), new LineRequest("2호선","GREEN",station1.getId(),station2.getId(),10));

        // then
        Line actual = lineRepository.findById(line.getId()).orElseThrow(NoSuchElementException::new);
        assertThat(actual.getName()).isEqualTo("2호선");
        assertThat(actual.getColor()).isEqualTo("GREEN");
    }

    @Test
    @DisplayName("지하철 노선 삭제 정상 동작")
    void deleteLine() {
        // given
        Line line = lineRepository.save(new Line("9호선", "YELLOW"));
        Station station1 = stationRepository.save(new Station("염창역"));
        Station station2 = stationRepository.save(new Station("당산역"));
        lineService.addSection(line.getId(),new SectionRequest(station1.getId(),station2.getId(),10));

        // when
        lineService.deleteLine(line.getId());

        // then
        assertThatThrownBy(()->lineRepository.findById(line.getId()).orElseThrow()).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("구간 제거 정상 동작")
    void deleteSection() {
        // given
        Line line = lineRepository.save(new Line("9호선", "YELLOW"));
        Station station1 = stationRepository.save(new Station("염창역"));
        Station station2 = stationRepository.save(new Station("당산역"));
        Station station3 = stationRepository.save(new Station("여의도역"));
        lineService.addSection(line.getId(),new SectionRequest(station1.getId(),station2.getId(),10));
        lineService.addSection(line.getId(),new SectionRequest(station2.getId(),station3.getId(),10));
        assertThat(line.getSections()).hasSize(2);

        // when
        lineService.deleteSection(line.getId(), station3.getId());

        // then
        assertThat(line.getSections()).hasSize(1);
        assertThat(line.getSections().get(0).getUpStation().getName()).isEqualTo("염창역");
        assertThat(line.getSections().get(0).getDownStation().getName()).isEqualTo("당산역");
    }
}
