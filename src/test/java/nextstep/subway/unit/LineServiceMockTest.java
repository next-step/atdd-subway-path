package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    private Line 경강선;
    private Station 부발역;
    private Station 여주역;
    private Long 경강선_Id;
    private Long 부발역_Id;
    private Long 여주역_Id;

    @BeforeEach
    void setUp() {
        경강선 = new Line("경강선", "blue");
        부발역 = new Station("부발역");
        여주역 = new Station("여주역");
        경강선_Id = 1L;
        부발역_Id = 1L;
        여주역_Id = 2L;
    }


    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        given(lineRepository.findById(경강선_Id)).willReturn(Optional.of(경강선));
        given(stationService.findById(여주역_Id)).willReturn(여주역);
        given(stationService.findById(부발역_Id)).willReturn(부발역);

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(여주역_Id, 부발역_Id, 10);
        lineService.addSection(경강선_Id, sectionRequest);


        // then
        // lineService.findLineById 메서드를 통해 검증
        Line line = lineService.findLineById(경강선_Id);
        assertThat(line.getStations()).containsExactly(여주역, 부발역);
    }
}
