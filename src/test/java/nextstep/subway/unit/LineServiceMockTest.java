package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @InjectMocks
    private LineService lineService;

    @ParameterizedTest
    @CsvSource(value = {"에버라인:red:에버라인:red", "에버라인::에버라인:yellow", ":red:분당선:red"}, delimiter = ':')
    void updateLine(String lineName, String color, String expectLineName, String expectColor) {
        // given
        final Line line = new Line(3L, lineName, color);
        given(lineRepository.findById(3L)).willReturn(Optional.of(line));

        // when
        lineService.updateLine(line.getId(), LineRequest.builder().color(expectColor).name(expectLineName).build());

        // then
        assertAll(
            () -> assertThat(line.getName()).isEqualTo(expectLineName),
            () -> assertThat(line.getColor()).isEqualTo(expectColor)
                 );
    }
}
