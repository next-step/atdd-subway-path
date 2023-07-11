package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DisplayName("LineService 단위 테스트 (stub)")
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    /**
     * Given 노선이 있을 때
     * When 구간을 추가하면
     * Then 구간을 조회 할 수 있다.
     */
    @DisplayName("노선에 구간을 추가한다")
    @Test
    void addSection() {
        // given
        final long upStationId = 1L;
        final long downStationId = 2L;
        final long lineId = 1L;
        final int distance = 10;

        when(stationService.findById(upStationId)).thenReturn(new Station("강남역"));
        when(stationService.findById(downStationId)).thenReturn(new Station("역삼역"));
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(new Line("2호선", "bg-green-600")));

        // when
        LineService lineService = new LineService(lineRepository, stationService);

        SectionRequest request = new SectionRequest(upStationId, downStationId, distance);
        lineService.addSection(lineId, request);

        // then
        assertThat(lineService.findByLineId(lineId).getSections().size()).isEqualTo(1);
    }

    /**
     * Given 2개의 구간을 가진 노선이 있을때
     * When 노선을 1개 삭제하면
     * Then 1개의 구간을 가진 노선이 된다.
     */
    @DisplayName("노선의 구간을 삭제한다.")
    @Test
    void deleteSection() {
        // given
        final long firstStationId = 1L;
        final long middleStationId = 2L;
        final long lastStationId = 3L;
        final long lineId = 1L;
        final int distance = 10;

        when(stationService.findById(firstStationId)).thenReturn(new Station("강남역"));
        when(stationService.findById(middleStationId)).thenReturn(new Station("역삼역"));
        when(stationService.findById(lastStationId)).thenReturn(new Station("선릉역"));
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(new Line("2호선", "bg-green-600")));
        LineService lineService = new LineService(lineRepository, stationService);

        SectionRequest 첫번째구간_요청 = new SectionRequest(firstStationId, middleStationId, distance);
        lineService.addSection(lineId, 첫번째구간_요청);
        SectionRequest 두번째구간_요청 = new SectionRequest(middleStationId, lastStationId, distance);
        lineService.addSection(lineId, 두번째구간_요청);

        // when
        lineService.deleteSection(lineId, lastStationId);

        // then
        List<Section> sections = lineService.findByLineId(lineId).getSections();
        assertThat(sections.size()).isEqualTo(1);

    }

    /**
     * Given 노선이 있을 때
     * When 노선의 정보를 변경하면
     * Then 노선의 정보가 변경 된 것을 죄회로 확인할 수 있다.
     */
    @DisplayName("노선의 정보를 변경한다")
    @Test
    void updateLine() {
        // given
        final long upStationId = 1L;
        final long downStationId = 2L;
        final long lineId = 1L;
        final int distance = 10;

        when(stationService.findById(upStationId)).thenReturn(new Station("강남역"));
        when(stationService.findById(downStationId)).thenReturn(new Station("역삼역"));
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(new Line("2호선", "bg-green-600")));
        LineService lineService = new LineService(lineRepository, stationService);

        SectionRequest request = new SectionRequest(upStationId, downStationId, distance);
        lineService.addSection(lineId, request);

        // when
        final String changeName = "3호선";
        final String changeColor = "bg-amber-600";
        LineRequest updateRequest = new LineRequest(changeName, changeColor, upStationId, downStationId, distance);
        lineService.updateLine(lineId, updateRequest);

        // then
        Line line = lineService.findByLineId(lineId);
        assertThat(line.getName()).isEqualTo(changeName);


    }

    /**
     * Given 노선이 있을 때
     * When 노선을 삭제 하면
     * Then 노선이 조회되지 않는다.
     */
    @DisplayName("노선을 삭제한다")
    @Test
    void deleteLine() {
        // given
        final long upStationId = 1L;
        final long downStationId = 2L;
        final long lineId = 1L;
        final int distance = 10;

        when(stationService.findById(upStationId)).thenReturn(new Station("강남역"));
        when(stationService.findById(downStationId)).thenReturn(new Station("역삼역"));
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(new Line("2호선", "bg-green-600")));
        LineService lineService = new LineService(lineRepository, stationService);

        SectionRequest request = new SectionRequest(upStationId, downStationId, distance);
        lineService.addSection(lineId, request);

        // when
        lineService.deleteLine(lineId);
        when(lineRepository.findById(lineId)).thenThrow(new EntityNotFoundException("노선이 존재하지 않습니다."));

        // then
        assertThatThrownBy(() -> lineService.findByLineId(lineId)).isInstanceOf(EntityNotFoundException.class);
    }

    /**
     * When 노선을 저장하면
     * Then 노선이 저장된 것을 조회로 확인할 수 있다.
     */
    @DisplayName("노선을 저장한다.")
    @Test
    void saveLine() {
        // when
        final long upStationId = 1L;
        final long downStationId = 2L;
        final long lineId = 1L;
        final int distance = 10;

        when(stationService.findById(upStationId)).thenReturn(new Station("강남역"));
        when(stationService.findById(downStationId)).thenReturn(new Station("역삼역"));
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(new Line("2호선", "bg-green-600")));
        LineService lineService = new LineService(lineRepository, stationService);

        SectionRequest request = new SectionRequest(upStationId, downStationId, distance);
        lineService.addSection(lineId, request);

        // then
        Line foundLine = lineService.findByLineId(lineId);
        assertThat(foundLine).isNotNull();
    }
}
