package nextstep.subway.unit;

import nextstep.marker.MockitoUnitTest;
import nextstep.subway.common.NotFoundLineException;
import nextstep.subway.controller.request.LineCreateRequest;
import nextstep.subway.controller.request.LineModifyRequest;
import nextstep.subway.controller.request.SectionAddRequest;
import nextstep.subway.controller.resonse.LineResponse;
import nextstep.subway.controller.resonse.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.LineService;
import nextstep.subway.service.command.SectionAddCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.thenCode;
import static org.mockito.BDDMockito.*;

@MockitoUnitTest
class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private LineService lineService;

    @Captor
    private ArgumentCaptor<Section> sectionArgumentCaptor;


    @Test
    void 노선_생성() {
        // given
        Station upStation = getStation(1L);
        given(stationRepository.getReferenceById(1L)).willReturn(upStation);

        Station downStation = getStation(2L);
        given(stationRepository.getReferenceById(2L)).willReturn(downStation);

        Line savedLine = getSavedLine(upStation, downStation);
        given(lineRepository.save(any(Line.class))).willReturn(savedLine);

        LineCreateRequest lineCreateRequest = getLineCreateRequest("2호선", "bg-green-300", 1L, 2L);
        // when
        LineResponse response = lineService.saveLine(lineCreateRequest);

        // then
        Assertions.assertEquals("2호선", response.getName());
        Assertions.assertEquals("bg-green-300", response.getColor());

        List<StationResponse> stations = response.getStations();
        assertThat(stations).hasSize(2)
                .map(StationResponse::getId)
                .containsExactly(1L, 2L);
    }

    private LineCreateRequest getLineCreateRequest(String name, String color, Long upStationId, Long downStationId) {
        LineCreateRequest request = mock(LineCreateRequest.class);
        given(request.getName()).willReturn(name);
        given(request.getColor()).willReturn(color);
        given(request.getUpStationId()).willReturn(upStationId);
        given(request.getDownStationId()).willReturn(downStationId);
        given(request.getDistance()).willReturn(10L);
        return request;
    }

    @Test
    void 노선_목록_조회() {
        // given
        Station upStation = getStation(1L);
        Station downStation = getStation(2L);
        Line savedLine = getSavedLine(upStation, downStation);
        given(lineRepository.findAll()).willReturn(Collections.singletonList(savedLine));

        // when
        List<LineResponse> lines = lineService.findAllLines();

        // then
        assertThat(lines).hasSize(1);
        LineResponse response = lines.get(0);
        Assertions.assertEquals("2호선", response.getName());
        Assertions.assertEquals("bg-green-300", response.getColor());

        List<StationResponse> stations = response.getStations();
        assertThat(stations).hasSize(2)
                .map(StationResponse::getId)
                .containsExactly(1L, 2L);
    }

    @Test
    void 노선_단건_조회() {
        // given
        Station upStation = getStation(1L);
        Station downStation = getStation(2L);
        Line savedLine = getSavedLine(upStation, downStation);
        given(lineRepository.findById(1L)).willReturn(Optional.of(savedLine));

        // when
        LineResponse response = lineService.findLineById(1L);

        // then
        Assertions.assertEquals("2호선", response.getName());
        Assertions.assertEquals("bg-green-300", response.getColor());

        List<StationResponse> stations = response.getStations();
        assertThat(stations).hasSize(2)
                .map(StationResponse::getId)
                .containsExactly(1L, 2L);
    }

    @Test
    void 노선_단건_조회_했으나_찾지_못한_경우() {
        // given
        given(lineRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        thenCode(() -> lineService.findLineById(1L)).isInstanceOf(NotFoundLineException.class);
    }

    @Test
    void 노선_정보_수정() {
        // given
        Line line = mock(Line.class);
        given(lineRepository.findById(1L)).willReturn(Optional.of(line));
        LineModifyRequest request = getLineModifyRequest("3호선", "bg-red-700");

        // when
        lineService.modifyLine(1L, request);

        // then
        then(line).should().modify("3호선", "bg-red-700");
    }

    private LineModifyRequest getLineModifyRequest(String name, String color) {
        LineModifyRequest request = mock(LineModifyRequest.class);
        given(request.getName()).willReturn(name);
        given(request.getColor()).willReturn(color);
        return request;
    }

    @Test
    void 노선_제거() {
        // given
        Line line = mock(Line.class);
        given(lineRepository.findById(1L)).willReturn(Optional.of(line));

        // when
        lineService.deleteLineById(1L);

        // then
        then(lineRepository).should().delete(line);
    }

    @Test
    void 구간_추가() {
        // given
        SectionAddCommand sectionAddCommand = getSectionAddRequest(2L, 3L);

        Station downStation = mock(Station.class);
        given(stationRepository.getReferenceById(2L)).willReturn(downStation);

        Station newStation = mock(Station.class);
        given(stationRepository.getReferenceById(3L)).willReturn(newStation);

        Line line = mock(Line.class);
        given(lineRepository.findById(1L)).willReturn(Optional.of(line));


        // when
        lineService.addSection(1L, sectionAddCommand);

        // then
        then(line).should().expandLine(sectionArgumentCaptor.capture());
        Section newSection = sectionArgumentCaptor.getValue();
        Assertions.assertEquals(downStation, newSection.getUpStation());
        Assertions.assertEquals(newStation, newSection.getDownStation());
    }

    private SectionAddRequest getSectionAddRequest(Long downStationId, Long newStationId) {
        SectionAddRequest request = mock(SectionAddRequest.class);
        given(request.getUpStationId()).willReturn(downStationId);
        given(request.getDownStationId()).willReturn(newStationId);
        given(request.getDistance()).willReturn(10L);
        return request;
    }

    @Test
    void 구간_제거() {
        Station targetStation = mock(Station.class);
        given(stationRepository.getReferenceById(1L)).willReturn(targetStation);

        Line line = mock(Line.class);
        given(lineRepository.findById(1L)).willReturn(Optional.of(line));


        // when
        lineService.deleteStationAtLine(1L, 1L);

        // then
        then(line).should().shorten(targetStation);
    }

    private Line getSavedLine(Station upStation, Station downStation) {
        Line line = mock(Line.class);
        given(line.getName()).willReturn("2호선");
        given(line.getColor()).willReturn("bg-green-300");
        given(line.getUpStation()).willReturn(upStation);
        given(line.getDownStation()).willReturn(downStation);
        return line;
    }

    private Station getStation(long id) {
        Station station = mock(Station.class);
        given(station.getId()).willReturn(id);
        return station;
    }
}
