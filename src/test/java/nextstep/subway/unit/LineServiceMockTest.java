package nextstep.subway.unit;

import nextstep.subway.dto.*;
import nextstep.subway.entity.Line;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.service.LineService;
import nextstep.subway.service.StationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    private LineService lineService;

    private static final Long 노선 = 1L;
    private static final Long 첫번째_역 = 1L;
    private static final Long 두번째_역 = 2L;
    private static final Long 세번째_역 = 3L;
    private Line line;

    @BeforeEach
    void setup() {
        lineService = new LineService(lineRepository, stationService);

        line = new Line("노선", "빨강", 첫번째_역, 두번째_역, 1);

        given(lineRepository.findById(노선))
                .willReturn(Optional.ofNullable(line));
        given(stationService.findStationById(첫번째_역))
                .willReturn(new StationResponse(첫번째_역, "첫번째_역"));
        given(stationService.findStationById(세번째_역))
                .willReturn(new StationResponse(세번째_역, "세번째_역"));
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅

        // when
        // lineService.addSection 호출
        lineService.addSection(노선, new SectionRequest(세번째_역, 두번째_역, 1));

        // then
        // lineService.findLineById, findSectionsByLine 메서드를 통해 검증
        LineResponse lineResponse = lineService.findLineById(노선);
        assertThat(lineResponse.getDistance()).isEqualTo(2);

        List<SectionResponse> sectionResponses = lineService.findSectionsByLine(노선);
        assertThat(sectionResponses).hasSize(2);
        assertThat(sectionResponses.stream()
                .map(SectionResponse::getDownStationId)).contains(두번째_역, 세번째_역);
    }
}
