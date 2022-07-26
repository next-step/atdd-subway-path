package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
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
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;

    @Test
    void addSection() {
        // given
        final Station 기흥역 = new Station(1L, "기흥역");
        final Station 신갈역 = new Station(2L, "신갈역");
        given(stationService.findById(1L)).willReturn(기흥역);
        given(stationService.findById(2L)).willReturn(신갈역);

        final Line line = new Line(3L, "분당선", "yellow");
        given(lineRepository.findById(3L)).willReturn(Optional.of(line));

        // when
        lineService.addSection(line.getId(), new SectionRequest(기흥역.getId(), 신갈역.getId(), 10));

        // then
        assertThat(line.getSections().size()).isEqualTo(1);
        assertThat(line.getSections().getStationNames()).containsExactlyInAnyOrder("기흥역", "신갈역");
    }

    @ParameterizedTest
    @CsvSource(value = {"에버라인:red:에버라인:red", "에버라인::에버라인:yellow", ":red:분당선:red"}, delimiter = ':')
    void updateLine(String lineName, String color, String expectLineName, String expectColor) {
        // given
        final Line line = new Line(3L, lineName, color);
        given(lineRepository.findById(3L)).willReturn(Optional.of(line));

        // when
        lineService.updateLine(line.getId(), LineRequest.builder().color(expectColor).name(expectLineName).build());

        // then
        assertThat(line.getName()).isEqualTo(expectLineName);
        assertThat(line.getColor()).isEqualTo(expectColor);
    }

    @Test
    void deleteSection() {
        // given
        final Station 기흥역 = new Station(1L, "기흥역");
        final Station 신갈역 = new Station(2L, "신갈역");
        final Station 정자역 = new Station(3L, "정자역");

        final Line line = new Line(4L, "분당선", "yellow");

        line.addSection(기흥역, 신갈역, 10);
        line.addSection(신갈역, 정자역, 10);

        given(lineRepository.findById(line.getId())).willReturn(Optional.of(line));

        // when
        lineService.deleteSection(line.getId(), 정자역.getId());

        // then
        assertAll(
            () -> assertThat(line.getSections().size()).isEqualTo(1),
            () -> assertThat(line.getSections().getStationNames()).containsExactlyInAnyOrder("기흥역", "신갈역")
                 );
    }
}
