package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService = new LineService(lineRepository, stationService);

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        int distance = 10;

        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");

        Line 이호선 = Line.of("2호선", "green", 강남역, 역삼역, distance);

        setField(이호선, "id", 1L);
        setField(역삼역, "id", 2L);
        setField(선릉역, "id", 3L);

        given(stationService.findById(역삼역.getId())).willReturn(역삼역);
        given(stationService.findById(선릉역.getId())).willReturn(선릉역);
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), distance));

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse lineResponse = lineService.findById(이호선.getId());
        assertThat(lineResponse.getStations().size()).isEqualTo(3);
    }
}
