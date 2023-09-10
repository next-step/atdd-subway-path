package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static nextstep.subway.fixture.LineFixture.*;
import static nextstep.subway.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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

    @Test
    @DisplayName("새로운 노선을 저장")
    void saveLine() {
        // given 역 생성
        Long 강남역_ID = 1L;
        Long 역삼역_ID = 2L;
        given(stationService.findById(강남역_ID)).willReturn(강남역);
        given(stationService.findById(역삼역_ID)).willReturn(역삼역);
        // given 호선 생성
        Line line = new Line(이호선_이름, 이호선_색);
        given(lineRepository.save(any())).willReturn(line);

        // given 생성 요청
        LineRequest request = LineRequest.builder().name(이호선_이름).color(이호선_색)
                .upStationId(강남역_ID).downStationId(역삼역_ID).distance(거리_10).build();

        // when
        LineResponse response = lineService.saveLine(request);

        // then
        assertThat(response.getName()).isNotNull();
        assertThat(response.getName()).isEqualTo(이호선_이름);
        assertThat(response.getStations()).hasSize(2);
    }

    @Test
    @DisplayName("기존 노선을 수정")
    void updateLine() {
        // given 호선 추가
        Long 기존_라인_ID = 1L;
        Line line = new Line(분당선_이름, 분당선_색);
        given(lineRepository.findById(any())).willReturn(
                Optional.of(line));
        // given 수정 요청 생성
        LineRequest request = LineRequest.builder().name(분당선_이름).color(분당선_색).build();

        // when
        lineService.updateLine(기존_라인_ID, request);

        // then
        assertThat(line.getName()).isEqualTo(분당선_이름);
        assertThat(line.getColor()).isEqualTo(분당선_색);
    }

    @Test
    @DisplayName("기존 노선을 삭제")
    void deleteLine() {
        // given
        Long 기존_라인_ID = 1L;

        // when
        lineService.deleteLine(기존_라인_ID);

        // then
        verify(lineRepository).deleteById(기존_라인_ID);
    }


    @Test
    @DisplayName("기존 노선에 구간을 추가")
    void addSection() {
        // given 역 추가
        Long 역삼역_ID = 2L;
        Long 삼성역_ID = 3L;
        given(stationService.findById(역삼역_ID)).willReturn(역삼역);
        given(stationService.findById(삼성역_ID)).willReturn(삼성역);
        // given 호선 추가
        Long 생성_라인_ID = 1L;
        Line line = new Line(이호선_이름, 이호선_색);
        line.addSection(new Section(line, 강남역, 역삼역, 거리_10));
        given(lineRepository.findById(any())).willReturn(Optional.of(line));
        // given 구간 생성 요청
        SectionRequest request = new SectionRequest(역삼역_ID, 삼성역_ID, 거리_10);

        // when
        lineService.addSection(생성_라인_ID, request);

        // then
        assertThat(line).isNotNull();
        assertThat(line.getSections().getSections()).hasSize(2);
    }
}
