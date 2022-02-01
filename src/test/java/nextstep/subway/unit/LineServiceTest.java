package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
    private Station 방배역;
    private Line 이호선;
    private Section 사당_방배_구간;


    @BeforeEach
    void setUp() {
        사당역 = new Station("사당");
        방배역 = new Station("방배");
        이호선 = new Line("2호선", "green");
        사당_방배_구간 = new Section(사당역, 방배역, 5);
        이호선.addSection(사당_방배_구간);

        stationRepository.save(사당역);
        stationRepository.save(방배역);
        lineRepository.save(이호선);
    }

    @DisplayName("요청한 구간이 마지막 구간과 연결되면 구간 등록 성공")
    @Test
    void addSection() {
        // given
        Station 서초역 = new Station("서초");
        stationRepository.save(서초역);

        Long lineId = 이호선.getId();
        SectionRequest sectionRequest = new SectionRequest(방배역.getId(), 서초역.getId(), 3);

        // when
        lineService.addSection(lineId, sectionRequest);

        // then
        Line line = lineRepository.findById(lineId).get();
        assertThat(line.getStations()).contains(서초역);
    }

}
