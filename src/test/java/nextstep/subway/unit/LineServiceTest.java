package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("호선의 대한 테스트")
@SpringBootTest
@Transactional
class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @DisplayName("호선의 구간을 생성한다.")
    @Test
    void addSection() {

        final Station upStation = new Station("강남역");
        final Station saveUpStation = stationRepository.save(upStation);

        final Station downStation = new Station("잠실역");
        final Station saveDownStation = stationRepository.save(downStation);

        final Line line = new Line(new LineInfo("2호선", "green"));
        final Line saveLine = lineRepository.save(line);

        final SectionRequest sectionRequest = new SectionRequest(saveUpStation.getId(), saveDownStation.getId(), 10);

        lineService.addSection(saveLine.getId(), sectionRequest);

        assertAll(
                () -> assertThat(line.getSections()).hasSize(1),
                () -> assertThat(line.getSections().get(0).getLine().getLineInfo()).isEqualTo(new LineInfo("2호선", "green")),
                () -> assertThat(line.getSections().get(0).getUpStation().getName()).isEqualTo("강남역"),
                () -> assertThat(line.getSections().get(0).getDownStation().getName()).isEqualTo("잠실역"),
                () -> assertThat(line.getSections().get(0).getDistance()).isEqualTo(10)
        );
    }
}
