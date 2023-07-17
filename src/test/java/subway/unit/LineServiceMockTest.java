package subway.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.line.dto.LineModifyRequest;
import subway.line.dto.SectionCreateRequest;
import subway.line.dto.SectionDeleteRequest;
import subway.line.model.Line;
import subway.line.repository.LineRepository;
import subway.line.service.LineService;
import subway.line.service.SectionService;
import subway.station.model.Station;
import subway.station.service.StationService;

import javax.persistence.EntityNotFoundException;
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

    @Mock
    private SectionService sectionService;

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
        final long distance = 10;

        when(stationService.findStationById(upStationId)).thenReturn(new Station(1L, "강남역"));
        when(stationService.findStationById(downStationId)).thenReturn(new Station(2L, "역삼역"));
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(Line.builder().name("2호선").color("bg-green-600").build()));

        // when
        LineService lineService = new LineService(lineRepository, stationService, sectionService);

        SectionCreateRequest createRequest = SectionCreateRequest.builder()
                .distance(distance)
                .downStationId(downStationId)
                .upStationId(upStationId)
                .build();
        lineService.appendSection(lineId, createRequest);

        // then
        assertThat(lineService.findLineById(lineId).getLineSections().getSectionsCount()).isEqualTo(1);
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
        final long distance = 10;

        when(stationService.findStationById(firstStationId)).thenReturn(new Station(1L, "강남역"));
        when(stationService.findStationById(middleStationId)).thenReturn(new Station(2L, "역삼역"));
        when(stationService.findStationById(lastStationId)).thenReturn(new Station(3L, "선릉역"));
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(Line.builder().name("2호선").color("bg-green-600").build()));
        LineService lineService = new LineService(lineRepository, stationService, sectionService);

        SectionCreateRequest 첫번째_구간_요청 = SectionCreateRequest.builder()
                .upStationId(firstStationId)
                .downStationId(middleStationId)
                .distance(distance)
                .build();
        lineService.appendSection(lineId, 첫번째_구간_요청);

        SectionCreateRequest 두번째_구간_요청 = SectionCreateRequest.builder()
                .upStationId(middleStationId)
                .downStationId(lastStationId)
                .distance(distance)
                .build();
        lineService.appendSection(lineId, 두번째_구간_요청);

        // when
        SectionDeleteRequest 구간_삭제_요청 = SectionDeleteRequest.builder()
                .lineId(lineId)
                .stationId(lastStationId)
                .build();
        lineService.deleteSection(구간_삭제_요청);

        // then
        long sectionsSize = lineService.findLineById(lineId).getLineSections().getSectionsCount();
        assertThat(sectionsSize).isEqualTo(1);

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
        final long distance = 10;

        when(stationService.findStationById(upStationId)).thenReturn(new Station(1L, "강남역"));
        when(stationService.findStationById(downStationId)).thenReturn(new Station(2L, "역삼역"));
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(Line.builder().name("2호선").color("bg-green-600").build()));
        LineService lineService = new LineService(lineRepository, stationService, sectionService);

        SectionCreateRequest 구간_요청 = SectionCreateRequest.builder()
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();
        lineService.appendSection(lineId, 구간_요청);

        // when
        final String changeName = "3호선";
        final String changeColor = "bg-amber-600";
        LineModifyRequest modifyRequest = LineModifyRequest.builder()
                .name(changeName)
                .color(changeColor)
                .build();
        lineService.updateLine(lineId, modifyRequest);

        // then
        Line line = lineService.findLineById(lineId);
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
        final long distance = 10;

        when(stationService.findStationById(upStationId)).thenReturn(new Station(1L, "강남역"));
        when(stationService.findStationById(downStationId)).thenReturn(new Station(2L, "역삼역"));
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(Line.builder().name("2호선").color("bg-green-600").build()));
        LineService lineService = new LineService(lineRepository, stationService, sectionService);

        SectionCreateRequest 구간_요청 = SectionCreateRequest.builder()
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();
        lineService.appendSection(lineId, 구간_요청);

        // when
        lineService.deleteById(lineId);
        when(lineRepository.findById(lineId)).thenThrow(new EntityNotFoundException("노선이 존재하지 않습니다."));

        // then
        assertThatThrownBy(() -> lineService.findLineById(lineId)).isInstanceOf(EntityNotFoundException.class);
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
        final long distance = 10;

        when(stationService.findStationById(upStationId)).thenReturn(new Station(1L, "강남역"));
        when(stationService.findStationById(downStationId)).thenReturn(new Station(2L, "역삼역"));
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(Line.builder().name("2호선").color("bg-green-600").build()));
        LineService lineService = new LineService(lineRepository, stationService, sectionService);

        SectionCreateRequest 구간_요청 = SectionCreateRequest.builder()
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();
        lineService.appendSection(lineId, 구간_요청);

        // then
        Line foundLine = lineService.findLineById(lineId);
        assertThat(foundLine).isNotNull();
    }
}
