package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.SectionService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SectionServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private SectionService sectionService;

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
        sectionService.addSection(line.getId(), new SectionRequest(기흥역.getId(), 신갈역.getId(), 10));

        // then
        assertAll(
            () -> assertThat(line.getSections().size()).isEqualTo(1),
            () -> assertThat(line.getSections().getStationNames()).containsExactlyInAnyOrder("기흥역", "신갈역")
                 );
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
        sectionService.deleteSection(line.getId(), 정자역.getId());

        // then
        assertAll(
            () -> assertThat(line.getSections().size()).isEqualTo(1),
            () -> assertThat(line.getSections().getStationNames()).containsExactlyInAnyOrder("기흥역", "신갈역")
                 );
    }
}
