package nextstep.subway.unit;

import nextstep.subway.dto.*;
import nextstep.subway.entity.Line;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.service.LineService;
import nextstep.subway.service.StationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    private static final Long 노선_id = 1L;
    private static final Long 첫번째_역_id = 1L;
    private static final Long 두번째_역_id = 2L;
    private static final Long 세번째_역_id = 3L;
    private Line 노선;

    @BeforeEach
    void setup() {
        lineService = new LineService(lineRepository, stationService);

        노선 = new Line("노선", "빨강", 첫번째_역_id, 두번째_역_id, 1);

        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        given(lineRepository.findById(노선_id))
                .willReturn(Optional.ofNullable(노선));
        given(stationService.findStationById(첫번째_역_id))
                .willReturn(new StationResponse(첫번째_역_id, "첫번째_역"));
    }

    @Test
    @DisplayName("지하철 노선의 구간을 등록한다.")
    void addSection() {
        given(stationService.findStationById(세번째_역_id))
                .willReturn(new StationResponse(세번째_역_id, "세번째_역"));
        // when
        // lineService.addSection 호출
        lineService.addSection(노선_id, new SectionRequest(세번째_역_id, 두번째_역_id, 1));

        // then
        // lineService.findLineById, findSectionsByLine 메서드를 통해 검증
        LineResponse lineResponse = lineService.findLineById(노선_id);
        assertThat(lineResponse.getDistance()).isEqualTo(2);

        List<SectionResponse> sectionResponses = lineService.findSectionsByLine(노선_id);
        assertThat(sectionResponses).hasSize(2);
        assertThat(sectionResponses.stream()
                .map(SectionResponse::getDownStationId)).contains(두번째_역_id, 세번째_역_id);
    }

    @Test
    @DisplayName("지하철 노선의 구간을 제거한다.")
    void deleteSection() {
        // given
        // lineService.addSection 호출을 통해 삭제할 section 등록
        given(stationService.findStationById(두번째_역_id))
                .willReturn(new StationResponse(두번째_역_id, "두번째_역"));
        lineService.addSection(노선_id, new SectionRequest(세번째_역_id, 두번째_역_id, 1));

        // when
        // lineService.deleteSection 호출
        lineService.deleteSection(노선_id, 세번째_역_id);

        // then
        // lineService.findLineById, findSectionsByLine 메서드를 통해 검증
        LineResponse lineResponse = lineService.findLineById(노선_id);
        assertThat(lineResponse.getDistance()).isEqualTo(1);

        List<SectionResponse> sectionResponses = lineService.findSectionsByLine(노선_id);
        assertThat(sectionResponses).hasSize(1);
        assertThat(sectionResponses.stream()
                .map(SectionResponse::getDownStationId)).doesNotContain(세번째_역_id);
    }

}
