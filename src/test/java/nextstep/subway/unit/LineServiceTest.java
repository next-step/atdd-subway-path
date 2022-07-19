package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
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

    private Station 영등포역;
    private Station 신도림역;
    private Station 구로역;

    @BeforeEach
    void setUp() {
        영등포역 = stationRepository.save(new Station("영등포역"));
        신도림역 = stationRepository.save(new Station("신도림역"));
        구로역 = stationRepository.save(new Station("구로역"));
    }

    @Test
    void addSection() {
        // given
        Line 일호선 = lineRepository.save(createLine(영등포역, 신도림역));

        // when
        lineService.addSection(일호선.getId(), new SectionRequest(신도림역.getId(), 구로역.getId(), 20));

        // then
        LineResponse lineResponse = lineService.findById(일호선.getId());
        assertAll(
                () -> assertThat(일호선.getSections().size()).isEqualTo(2),
                () -> assertThat(lineResponse.getStations()).extracting("name")
                        .containsExactly(영등포역.getName(), 신도림역.getName(), 구로역.getName())
        );
    }

    private Line createLine(Station upStation, Station downStation) {
        Line line = new Line("1호선", "blue");
        line.addSection(upStation, downStation, 10);
        return line;
    }
}
