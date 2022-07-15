package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 without Mock")
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @AfterEach
    void tearDown() {
        databaseCleanup.execute();
    }

    @DisplayName("지하철 구간 등록")
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 구로디지털단지역 = stationRepository.save(new Station("구로디지털단지역"));
        Station 신대방역 = stationRepository.save(new Station("신대방역"));
        Station 신림역 = stationRepository.save(new Station("신림역"));
        Line 이호선 = lineRepository.save(new Line("2호선", "green", 구로디지털단지역, 신대방역, 10));

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), new SectionRequest(신대방역.getId(), 신림역.getId(), 8));

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(이호선.getStations()).contains(구로디지털단지역, 신대방역, 신림역);
    }
}
