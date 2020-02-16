package atdd.line.service;

import atdd.line.domain.Line;
import atdd.line.domain.TimeTable;
import atdd.line.dto.LineResponseDto;
import atdd.line.repository.LineRepository;
import atdd.station.domain.Station;
import atdd.station.service.StationService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class LineServiceTest {

    private static final String NAME_1 = "name111";
    private static final String NAME_2 = "name222";
    private static final TimeTable TIME_TABLE = new TimeTable(LocalTime.MIN, LocalTime.MAX);;

    private final Line line1 = Line.of(143L, NAME_1, TIME_TABLE, 0);
    private final Line line2 = Line.of(144L, NAME_2, TIME_TABLE, 0);

    private LineService lineService;

    private LineAssembler lineAssembler;
    private LineRepository lineRepository;
    private StationService stationService;

    @BeforeEach
    void setup() {
        this.lineAssembler = mock(LineAssembler.class);
        this.lineRepository = mock(LineRepository.class);
        this.stationService = mock(StationService.class);

        this.lineService = new LineService(lineAssembler, lineRepository, stationService);
    }

    @DisplayName("findAll - name 이 null 이거나 빈 값이면 전체를 반환한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void findAll(String name) throws Exception {
        final List<Line> lines = Lists.list(line1, line2);

        given(lineRepository.findAll()).willReturn(lines);
        given(lineAssembler.convertToResponseDto(line1)).willReturn(convertDto(line1));
        given(lineAssembler.convertToResponseDto(line2)).willReturn(convertDto(line2));


        final List<LineResponseDto> responseDtos = lineService.findAll(name);


        assertThat(responseDtos).hasSize(lines.size());

        final LineResponseDto responseDto1 = responseDtos.get(0);
        assertThat(responseDto1.getId()).isEqualTo(line1.getId());
        assertThat(responseDto1.getName()).isEqualTo(line1.getName());

        final LineResponseDto responseDto2 = responseDtos.get(1);
        assertThat(responseDto2.getId()).isEqualTo(line2.getId());
        assertThat(responseDto2.getName()).isEqualTo(line2.getName());
    }

    @DisplayName("findAll - name 과 일치하는 값을 반환한다.")
    @ParameterizedTest
    @MethodSource("findAllByNameArguments")
    void findAllByName(String name) throws Exception {
        final List<Line> lines = Lists.list(line1, line2);

        given(lineRepository.findAll()).willReturn(lines);
        given(lineAssembler.convertToResponseDto(line1)).willReturn(convertDto(line1));
        given(lineAssembler.convertToResponseDto(line2)).willReturn(convertDto(line2));


        final List<LineResponseDto> responseDtos = lineService.findAll(name);


        assertThat(responseDtos).hasSize(1);
        assertThat(responseDtos.get(0).getName()).isEqualTo(name);
    }

    private static Stream<Arguments> findAllByNameArguments() {
        return Stream.of(
                Arguments.of(NAME_1),
                Arguments.of(NAME_2)
        );
    }

    private LineResponseDto convertDto(Line line) {
        return new LineResponseDto(line.getId(), line.getName(), line.getTimeTable(), line.getIntervalTime(), new ArrayList<>());
    }

    @Test
    void delete() {
        given(lineRepository.findById(line1.getId())).willReturn(Optional.of(line1));

        lineService.delete(line1.getId());

        verify(lineRepository, times(1)).delete(line1);
    }

    @Test
    void addStation() {
        final Station station = Station.of(4156L, "stationName!!");

        given(lineRepository.findById(line1.getId())).willReturn(Optional.of(line1));
        given(stationService.findById(station.getId())).willReturn(station);


        lineService.addStation(line1.getId(), station.getId());


        final List<Station> stations = line1.getStations();
        assertThat(stations).hasSize(1);

        final Station addedStation = stations.get(0);
        assertThat(addedStation.getId()).isEqualTo(station.getId());
        assertThat(addedStation.getName()).isEqualTo(station.getName());
    }

}