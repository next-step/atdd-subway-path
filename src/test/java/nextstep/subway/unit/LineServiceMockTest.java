package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.SectionService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.request.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.unit.fixture.StationFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Optional;

import static nextstep.subway.unit.fixture.StationFixture.지하철역_생성;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("구간 서비스 단위 테스트 with Mock")
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private SectionService sectionService;

    private final String COLOR_RED = "bg-red-600";
    private final int DISTANCE = 10;

    private Station 신사역, 논현역, 신논현역, 광교역, 강남역;
    private Line 신분당선;

    @BeforeEach
    void setUp(){
        신사역 = 지하철역_생성("신사역");
        논현역 = 지하철역_생성("논현역");
        신논현역 = 지하철역_생성("신논현역");
        강남역 = 지하철역_생성("강남역");
        광교역 = 지하철역_생성("광교역");

        신분당선 = new Line(1L,"신분당선", COLOR_RED, 논현역, 강남역, 30);
    }

    @DisplayName("노선의 상행 종점역 구간을 등록")
    @Test
    void addFirstSection() {
        // given
        given(lineRepository.findById(신분당선.getId())).willReturn(Optional.of(신분당선));
        given(stationService.getStations(신사역.getId())).willReturn(신사역);
        given(stationService.getStations(논현역.getId())).willReturn(논현역);

        // when
        sectionService.saveSection(신분당선.getId(), new SectionRequest(신사역.getId(), 논현역.getId(), DISTANCE));

        // then
        assertThat(신분당선.getStations()).containsExactly(신사역,논현역,강남역);
    }

    @DisplayName("기존 구간 중간에 신규 구간을 등록")
    @Test
    void addMiddleSection() {
        // given
        given(lineRepository.findById(신분당선.getId())).willReturn(Optional.of(신분당선));
        given(stationService.getStations(논현역.getId())).willReturn(논현역);
        given(stationService.getStations(신논현역.getId())).willReturn(신논현역);

        // when
        sectionService.saveSection(신분당선.getId(), new SectionRequest(논현역.getId(), 신논현역.getId(), DISTANCE));

        // then
        assertThat(신분당선.getStations()).containsExactly(논현역,신논현역,강남역);
    }

    @DisplayName("노선의 하행 종점역 구간을 등록")
    @Test
    void addLastSection() {
        // given
        given(lineRepository.findById(신분당선.getId())).willReturn(Optional.of(신분당선));
        given(stationService.getStations(강남역.getId())).willReturn(강남역);
        given(stationService.getStations(광교역.getId())).willReturn(광교역);

        // when
        sectionService.saveSection(신분당선.getId(), new SectionRequest(강남역.getId(), 광교역.getId(), DISTANCE));

        // then
        assertThat(신분당선.getStations()).containsExactly(논현역,강남역,광교역);
    }

    @DisplayName("노선 구간 삭제")
    @Test
    void deleteSection() {
        // given
        given(lineRepository.findById(신분당선.getId())).willReturn(Optional.of(신분당선));
        given(stationService.getStations(강남역.getId())).willReturn(강남역);
        given(stationService.getStations(광교역.getId())).willReturn(광교역);
        sectionService.saveSection(신분당선.getId(), new SectionRequest(강남역.getId(), 광교역.getId(), DISTANCE));

        sectionService.removeSection(신분당선.getId(),광교역.getId());

        assertThat(신분당선.getStations()).doesNotContain(광교역);
    }
}
