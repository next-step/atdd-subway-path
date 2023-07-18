package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.SectionService;
import nextstep.subway.applicaion.dto.request.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 서비스 단위 테스트 without Mock")
@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;
    @Autowired
    private SectionService sectionService;

    private final String COLOR_RED = "bg-red-600";

    private final String COLOR_BLUE = "bg-blue-600";
    private final int DISTANCE = 10;

    private Station 신사역, 광교역, 강남역;
    private Line 신분당선;
    @BeforeEach
    void set(){
        신사역 = stationRepository.save(new Station("신사역"));
        강남역 = stationRepository.save(new Station("강남역"));
        광교역 = stationRepository.save(new Station("광교역"));

        신분당선 = lineRepository.save(new Line("신분당선", COLOR_RED, 신사역, 강남역, DISTANCE));
    }


    @DisplayName("노선에 구간을 추가")
    @Test
    void addSection() {

        // when
        sectionService.saveSection(신분당선.getId(),new SectionRequest(강남역.getId(),광교역.getId(),DISTANCE));

        // then
        assertThat(신분당선.getStations()).contains(광교역);
    }

    @DisplayName("노선에서 구간을 삭제")
    @Test
    void removeSection() {
        // given
        sectionService.saveSection(신분당선.getId(),new SectionRequest(강남역.getId(),광교역.getId(),DISTANCE));

        // when
        sectionService.removeSection(신분당선.getId(),광교역.getId());

        // then
        assertThat(신분당선.getStations()).doesNotContain(광교역);
    }

}
