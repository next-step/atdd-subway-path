package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static nextstep.subway.domain.factory.DtoFactory.createLineRequest;
import static nextstep.subway.domain.factory.DtoFactory.createSectionRequest;
import static nextstep.subway.domain.factory.EntityFactory.createLine;
import static nextstep.subway.domain.factory.EntityFactory.createStation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private LineService lineService;

    private Station 강남역;
    private Station 선릉역;
    private Line 이호선;

    @BeforeEach
    void init() {
        lineService = new LineService(lineRepository, stationRepository);

        강남역 = createStation(1L, "강남역");
        선릉역 = createStation(2L, "선릉역");
        이호선 = createLine(1L, "2호선", "green", 강남역, 선릉역, 10);
    }

    @Test
    void 노선_생성() {
        // given
        when(stationRepository.findById(1L)).thenReturn(Optional.of(강남역));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(선릉역));
        when(lineRepository.save(any())).thenReturn(이호선);

        // when
        LineResponse lineResponse = lineService.saveLine(createLineRequest("2호선", "green", 강남역.getId(), 선릉역.getId(), 10));

        // then
        assertThat(lineResponse.getName()).isEqualTo("2호선");
        assertThat(lineResponse.getColor()).isEqualTo("green");
    }

    @Test
    void 노선_정보가_담긴_리스트() {
        // given
        Station 용산역 = createStation(3L, "용산역");
        Station 공덕역 = createStation(4L, "공덕역");
        Line 경의선 = createLine(2L, "경의선", "blue", 용산역, 공덕역, 8);

        when(lineRepository.findAll()).thenReturn(Arrays.asList(이호선, 경의선));

        // when
        List<LineResponse> lineResponses = lineService.showLines();

        // then
        assertThat(lineResponses.size()).isEqualTo(2);
    }

    @Test
    void 단일_구간_정보() {
        when(lineRepository.findById(any())).thenReturn(Optional.of(이호선));

        // when
        LineResponse lineResponse = lineService.findById(1L);

        // then
        assertThat(lineResponse.getName()).isEqualTo("2호선");
        assertThat(lineResponse.getColor()).isEqualTo("green");
    }

    @Test
    void 노선_정보_변경() {
        when(lineRepository.findById(any())).thenReturn(Optional.of(이호선));

        // when
        lineService.updateLine(1L, createLineRequest("3호선", "orange", null, null, 0));

        // then
        assertThat(이호선.getName()).isEqualTo("3호선");
        assertThat(이호선.getColor()).isEqualTo("orange");
    }

    @Test
    void 노선_삭제() {
        // when
        lineService.deleteLine(1L);
    }

    @Test
    void 노선_구간_추가() {
        // given
        Station 용산역 = createStation(3L, "용산역");

        when(stationRepository.findById(2L)).thenReturn(Optional.of(선릉역));
        when(stationRepository.findById(3L)).thenReturn(Optional.of(용산역));
        when(lineRepository.findById(any())).thenReturn(Optional.of(이호선));

        // when
        lineService.addSection(1L, createSectionRequest(2L, 3L, 5));

        // then
        assertThat(이호선.getSectionSize()).isEqualTo(2);
        assertThat(이호선.isUpStation(강남역)).isTrue();
        assertThat(이호선.isDownStation(용산역)).isTrue();
    }

}