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

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.unit.LineServiceTest.SubwayInfo.*;
import static org.assertj.core.api.Assertions.assertThat;

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

    private SubwayInfo subwayInfo;

    @AfterEach
    void cleanData() {
        databaseCleanup.execute();
    }

    @BeforeEach
    void createStations() {
        stationRepository.save(subwayInfo.구로역);
        stationRepository.save(subwayInfo.신도림역);
        stationRepository.save(subwayInfo.영등포역);
    }

    public LineServiceTest() {
        this.subwayInfo = new SubwayInfo();
    }

    @DisplayName("지하철 노선에 구간 추가")
    @Test
    void addSection() {
        // given
        Line 일호선 = new Line("1호선", "blue");
        lineRepository.save(일호선);

        // when
        // lineService.addSection 호출
        lineService.addSection(일호선.getId(), new SectionRequest(subwayInfo.구로역.getId(), subwayInfo.신도림역.getId(), subwayInfo.구로역_신도림역_거리));
        lineService.addSection(일호선.getId(), new SectionRequest(subwayInfo.신도림역.getId(), subwayInfo.영등포역.getId(), subwayInfo.신도림역_영등포역_거리));

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(일호선.getSections().count()).isEqualTo(2);
    }

    @DisplayName("지하철 노선에 등록된 역 목록 조회")
    @Test
    void getStations() {
        // given
        Line 일호선 = new Line("1호선", "blue");
        lineRepository.save(일호선);

        // when
        // lineService.addSection 호출
        lineService.addSection(일호선.getId(), new SectionRequest(subwayInfo.구로역.getId(), subwayInfo.신도림역.getId(), subwayInfo.구로역_신도림역_거리));
        lineService.addSection(일호선.getId(), new SectionRequest(subwayInfo.신도림역.getId(), subwayInfo.영등포역.getId(), subwayInfo.신도림역_영등포역_거리));

        // then
        // line.getSections 메서드를 통해 검증
        List<String> stationNames = 일호선.getStations().stream().map(Station::getName).collect(Collectors.toList());
        assertThat(stationNames).contains("구로역", "신도림역", "영등포역");
    }


    public class SubwayInfo {
        public final Station 구로역 = new Station("구로역");
        public final Station 신도림역 = new Station("신도림역");
        public final Station 영등포역 = new Station("영등포역");

        public final int 구로역_신도림역_거리 = 10;
        public final int 신도림역_영등포역_거리 = 15;
    }

}
