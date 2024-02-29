package nextstep.subway.unit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.LineService;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    @Test
    void addSection() {
        // given
        LineService lineService = new LineService(lineRepository, stationRepository);
        Line line = new Line(1L, "이호선", "초록색");
        Station 강남역 = new Station(1L, "강남역");
        Station 역삼역 = new Station(2L, "역삼역");
        Section section = new Section(강남역, 역삼역, 10);
        given(lineRepository.findById(1L)).willReturn(Optional.of(line));
        given(stationRepository.findById(1L)).willReturn(Optional.of(강남역));
        given(stationRepository.findById(2L)).willReturn(Optional.of(역삼역));

        // when
        lineService.addSection(1L, SectionRequest.from(section));

        // then
        Line lineAfterAddSection = lineRepository.findById(1L).orElseThrow();
        assertThat(lineAfterAddSection.getSections().getLastSection()).isEqualTo(section);
    }
}
