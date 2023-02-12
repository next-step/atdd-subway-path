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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    @DisplayName("성공: 구간추가")
    void addSection() {
        // given
        Station upStation = stationRepository.save(new Station("강남역"));
        Station downStation = stationRepository.save(new Station("삼성역"));
        Line line = lineRepository.save(new Line("이호선", "green"));
        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), 5);
        // when
        lineService.addSection(line.getId(), sectionRequest);
        // then
        assertSoftly(softly -> {
            softly.assertThat(lineService.findLineById(line.getId()).getSections()).hasSize(1);
            softly.assertThat(lineService.findLineById(line.getId()).getStations())
                    .extracting(Station::getName)
                    .containsExactlyInAnyOrder("삼성역","강남역");
        });
    }
}
