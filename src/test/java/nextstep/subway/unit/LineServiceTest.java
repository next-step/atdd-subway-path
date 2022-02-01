package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Station 가양역;
    private Station 증미역;
    private Line 구호선;

    @BeforeEach
    void setUp() {
        가양역 = new Station("가양역");
        증미역 = new Station("증미역");
        구호선 = new Line("9호선", "갈색");
    }

    @Test
    void addSection() {
        // given
        Station upStation = stationRepository.save(가양역);
        Station downStation = stationRepository.save(증미역);
        Line line = lineRepository.save(구호선);

        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), 10);

        // when
        lineService.addSection(line.getId(), sectionRequest);

        // then
        assertThat(line.getSections().size()).isEqualTo(1);
        LineResponse response = lineService.findById(line.getId());
        List<StationResponse> stations = response.getStations();
        assertThat(stations.get(0).getId()).isEqualTo(upStation.getId());
        assertThat(stations.get(1).getId()).isEqualTo(downStation.getId());
    }
}
