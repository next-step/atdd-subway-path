package atdd.line.service;

import atdd.line.domain.Line;
import atdd.line.domain.TimeTable;
import atdd.line.dto.LineResponseDto;
import atdd.line.repository.LineRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class LineServiceTest {

    private static final TimeTable TIME_TABLE = new TimeTable(LocalTime.MIN, LocalTime.MAX);;
    private static final Line LINE_1 = Line.of(143L, "name111", TIME_TABLE, 0);
    private static final Line LINE_2 = Line.of(144L, "name222", TIME_TABLE, 0);

    private LineService lineService;

    private LineAssembler lineAssembler;
    private LineRepository lineRepository;

    @BeforeEach
    void setup() {
        this.lineAssembler = mock(LineAssembler.class);
        this.lineRepository = mock(LineRepository.class);
        this.lineService = new LineService(lineAssembler, lineRepository);
    }

    @DisplayName("findAll - name 이 null 이거나 빈 값이면 전체를 반환한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void findAll(String name) throws Exception {
        final List<Line> lines = Lists.list(LINE_1, LINE_2);

        given(lineRepository.findAll()).willReturn(lines);
        given(lineAssembler.convertToResponseDto(LINE_1)).willReturn(convertDto(LINE_1));
        given(lineAssembler.convertToResponseDto(LINE_2)).willReturn(convertDto(LINE_2));


        final List<LineResponseDto> responseDtos = lineService.findAll(name);


        assertThat(responseDtos).hasSize(lines.size());

        final LineResponseDto responseDto1 = responseDtos.get(0);
        assertThat(responseDto1.getId()).isEqualTo(LINE_1.getId());
        assertThat(responseDto1.getName()).isEqualTo(LINE_1.getName());

        final LineResponseDto responseDto2 = responseDtos.get(1);
        assertThat(responseDto2.getId()).isEqualTo(LINE_2.getId());
        assertThat(responseDto2.getName()).isEqualTo(LINE_2.getName());
    }

    @DisplayName("findAll - name 과 일치하는 값을 반환한다.")
    @ParameterizedTest
    @MethodSource("findAllByNameArguments")
    void findAllByName(String name) throws Exception {
        final List<Line> lines = Lists.list(LINE_1, LINE_2);

        given(lineRepository.findAll()).willReturn(lines);
        given(lineAssembler.convertToResponseDto(LINE_1)).willReturn(convertDto(LINE_1));
        given(lineAssembler.convertToResponseDto(LINE_2)).willReturn(convertDto(LINE_2));


        final List<LineResponseDto> responseDtos = lineService.findAll(name);


        assertThat(responseDtos).hasSize(1);
        assertThat(responseDtos.get(0).getName()).isEqualTo(name);
    }

    private static Stream<Arguments> findAllByNameArguments() {
        return Stream.of(
                Arguments.of(LINE_1.getName()),
                Arguments.of(LINE_2.getName())
        );
    }

    private LineResponseDto convertDto(Line line) {
        return new LineResponseDto(line.getId(), line.getName(), line.getTimeTable(), line.getIntervalTime(), new ArrayList<>());
    }

}