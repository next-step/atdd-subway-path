package nextstep.subway.unit;

import lombok.val;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.domain.object.Distance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static nextstep.subway.unit.test.utils.Stations.삼성역;
import static nextstep.subway.unit.test.utils.Stations.역_초기화;
import static org.assertj.core.api.Assertions.assertThat;
import static nextstep.subway.unit.test.utils.Lines.GTXA_연신내_서울역;
import static nextstep.subway.unit.test.utils.Lines.노선_초기화;
import static nextstep.subway.unit.test.utils.Stations.서울역;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @BeforeEach
    public void setUp() {
        역_초기화();
        노선_초기화();
    }

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station upStation = stationRepository.save(서울역);
        Station downStation = stationRepository.save(삼성역);
        Line line = lineRepository.save(GTXA_연신내_서울역);
        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), new Distance(10));

        // when
        // lineService.addSection 호출
        lineService.saveSection(line.getId(), sectionRequest);

        // then
        val stations = line.getAllStations();

        assertThat(stations.size()).isEqualTo(3);
        assertThat(stations.get(1)).isEqualTo(upStation);
        assertThat(stations.get(2)).isEqualTo(downStation);
    }
}
