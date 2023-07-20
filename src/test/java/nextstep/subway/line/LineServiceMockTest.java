package nextstep.subway.line;

import static common.Constants.*;
import static nextstep.subway.section.SectionBuilder.aSection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private LineService lineService;

    @BeforeEach
    void init() {
        lineService = new LineService(lineRepository, stationRepository);
    }

    @Test
    void saveLine() {
        LineRequest lineRequest = new LineRequest(신분당선, 빨강색600, 1L, 2L, 10);

        Station gangnamStation = new Station(1L, 강남역);
        Station sinnonhyeonStation = new Station(2L, 신논현역);
        Sections sections = new Sections(new ArrayList<>(List.of(new Section(gangnamStation, sinnonhyeonStation, 10))));

        when(lineRepository.save(any())).thenReturn(new Line(1L, 신분당선, 빨강색600, sections));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(gangnamStation));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(sinnonhyeonStation));

        LineResponse lineResponse = lineService.saveLine(lineRequest);

        assertAll(
            () -> assertThat(lineResponse.getName()).isEqualTo(신분당선),
            () -> assertThat(lineResponse.getColor()).isEqualTo(빨강색600),
            () -> assertThat(lineResponse.getStations()).containsExactly(
                new StationResponse(1L, 강남역),
                new StationResponse(2L, 신논현역))
        );
    }

    @Test
    void findAllLines() {
        when(lineRepository.findAll()).thenReturn(new ArrayList<>(List.of(
            new Line(1L, 신분당선, 빨강색600, new Sections(List.of(aSection().build()))),
            new Line(2L, 분당선, 빨강색600, new Sections(List.of(aSection().build())))
        )));

        List<LineResponse> lineResponses = lineService.findAllLines();

        LineResponse firstLineResponse = lineResponses.get(0);
        LineResponse secondLineResponse = lineResponses.get(1);
        assertAll(
            () -> assertThat(firstLineResponse).isEqualTo(new LineResponse(1L, 신분당선, 빨강색600, List.of(new StationResponse(1L, 강남역), new StationResponse(2L, 신논현역)))),
            () -> assertThat(secondLineResponse).isEqualTo(new LineResponse(2L, 분당선, 빨강색600, List.of(new StationResponse(1L, 강남역), new StationResponse(2L, 신논현역))))
        );
    }

    @Test
    void findLine() {
        when(lineRepository.findById(any())).thenReturn(Optional.of(new Line(1L, 신분당선, 빨강색600, new Sections(List.of(aSection().build())))));

        LineResponse lineResponse = lineService.findLine(1L);

        assertThat(lineResponse).isEqualTo(new LineResponse(1L, 신분당선, 빨강색600, List.of(new StationResponse(1L, 강남역), new StationResponse(2L, 신논현역))));
    }
}