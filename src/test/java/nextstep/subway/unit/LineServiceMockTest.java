package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.LineUpdateRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.error.exception.BusinessException;
import nextstep.subway.error.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationService);
    }

    @DisplayName("노선 생성")
    @Test
    void saveLine() {
        // given
        final Line 이호선 = 이호선();
        final Station 강남역 = 역_생성(1L, "강남역");
        final Station 역삼역 = 역_생성(2L, "역삼역");
        when(lineRepository.save(any())).thenReturn(이호선);
        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        when(stationService.findById(역삼역.getId())).thenReturn(역삼역);

        // when
        final LineResponse 노선_생성_응답 = lineService.saveLine(new LineRequest("2호선", "green", 강남역.getId(), 역삼역.getId(), 10));

        // then
        assertThat(노선_생성_응답.getId()).isEqualTo(이호선.getId());
    }

    @DisplayName("노선 목록 가져오기")
    @Test
    void showLines() {
        // given
        final Line 이호선 = 이호선();
        when(lineRepository.findAll()).thenReturn(List.of(이호선));

        // when
        final List<LineResponse> 노선_목록_응답 = lineService.showLines();

        // then
        assertThat(노선_목록_응답).hasSize(1);
    }

    @DisplayName("ID 를 통해 노선 정보 가져오기")
    @Test
    void findById() {
        // given
        final Line 이호선 = 이호선();
        when(lineRepository.findById(any())).thenReturn(Optional.of(이호선));

        // when
        final LineResponse 노선_조회_응답 = lineService.findById(이호선.getId());

        // then
        assertThat(노선_조회_응답.getId()).isEqualTo(이호선.getId());
    }

    @DisplayName("노선 수정")
    @Test
    void updateLine() {
        // given
        final Line 이호선 = 이호선();
        when(lineRepository.findById(any())).thenReturn(Optional.of(이호선));

        // when
        lineService.updateLine(이호선.getId(), new LineUpdateRequest("8호선", null));

        // then
        final LineResponse 노선_조회_응답 = lineService.findById(이호선.getId());
        assertThat(노선_조회_응답.getName()).isEqualTo("8호선");
    }

    @DisplayName("노선 삭제 후, 삭제된 노선 조회 실패")
    @Test
    void deleteLine() {
        // given
        final Line 이호선 = 이호선();

        // when
        lineService.deleteLine(이호선.getId());

        // then
        assertThatThrownBy(() -> {
            lineService.findById(이호선.getId());
        }).isInstanceOf(NotFoundException.class);
    }

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        final Station 강남역 = 역_생성(1L, "강남역");
        final Station 역삼역 = 역_생성(2L, "역삼역");
        final Line 이호선 = 이호선();
        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        when(stationService.findById(역삼역.getId())).thenReturn(역삼역);
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10));

        // then
        // line.findLineById 메서드를 통해 검증
        final LineResponse 노선_응답 = lineService.findById(이호선.getId());
        assertThat(노선_응답.getId()).isEqualTo(이호선.getId());
    }

    @DisplayName("구간 삭제")
    @Test
    void deleteSection() {
        // given
        final Station 강남역 = 역_생성(1L, "강남역");
        final Station 역삼역 = 역_생성(2L, "역삼역");
        final Station 삼성역 = 역_생성(3L, "삼성역");
        final Line 이호선 = 이호선();
        이호선.getSections().add(new Section(이호선, 강남역, 역삼역, 10));
        이호선.getSections().add(new Section(이호선, 역삼역, 삼성역, 10));
        when(lineRepository.findById(any())).thenReturn(Optional.of(이호선));
        when(stationService.findById(any())).thenReturn(삼성역);

        // when
        lineService.deleteSection(이호선.getId(), 삼성역.getId());
        when(lineRepository.findById(any())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> {
            lineService.findById(이호선.getId());
        }).isInstanceOf(NotFoundException.class);
    }

    @DisplayName("노선의 하행종점역이 아닌 역을 삭제하려고 할 때 에러 발생")
    @Test
    void deleteSectionWithNonLastDownStation() {
        // given
        final Station 강남역 = 역_생성(1L, "강남역");
        final Station 역삼역 = 역_생성(2L, "역삼역");
        final Station 삼성역 = 역_생성(3L, "삼성역");
        final Line 이호선 = 이호선();
        이호선.getSections().add(new Section(이호선, 강남역, 역삼역, 10));
        이호선.getSections().add(new Section(이호선, 역삼역, 삼성역, 10));
        when(lineRepository.findById(any())).thenReturn(Optional.of(이호선));
        when(stationService.findById(any())).thenReturn(강남역);

        // when
        assertThatThrownBy(() -> {
            lineService.deleteSection(이호선.getId(), 역삼역.getId());
        }).isInstanceOf(BusinessException.class);
    }

    @DisplayName("노선의 마지막 구간을 삭제하려고 할 때 에러 발생")
    @Test
    void deleteSectionWithLastSection() {
        // given
        final Station 강남역 = 역_생성(1L, "강남역");
        final Station 역삼역 = 역_생성(2L, "역삼역");
        final Line 이호선 = 이호선();
        이호선.getSections().add(new Section(이호선, 강남역, 역삼역, 10));
        when(lineRepository.findById(any())).thenReturn(Optional.of(이호선));
        when(stationService.findById(any())).thenReturn(역삼역);

        // when
        assertThatThrownBy(() -> {
            lineService.deleteSection(이호선.getId(), 역삼역.getId());
        }).isInstanceOf(BusinessException.class);
    }

    private Station 역_생성(Long id, String name) {
        return new Station(id, name);
    }

    private Line 이호선() {
        return new Line("2호선", "green");
    }
}
