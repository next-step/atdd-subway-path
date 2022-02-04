package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.util.ReflectionTestUtils.*;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService = new LineService(lineRepository, stationService);

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Line line = new Line("2호선", "green");
        Station firstUpStation = new Station("신림역");
        Station firstDownStation = new Station("봉천역");
        Station newStation = new Station("서울대입구역");

        setField(line, "id", 1L);
        setField(firstUpStation, "id", 1L);
        setField(firstDownStation, "id", 2L);
        setField(newStation, "id", 3L);

        Section firstSection = new Section(line, firstUpStation, firstDownStation, 10);
        line.getSections().add(firstSection);

        given(stationService.findById(firstUpStation.getId())).willReturn(firstUpStation);
        given(stationService.findById(firstDownStation.getId())).willReturn(firstDownStation);
        given(lineRepository.findById(line.getId())).willReturn(Optional.of(line));

        SectionRequest sectionRequest =
                new SectionRequest(firstUpStation.getId(), firstDownStation.getId(), 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), sectionRequest);


        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse response = lineService.findById(line.getId());
        assertThat(response.getStations())
                .containsExactly(
                        StationResponse.of(firstUpStation),
                        StationResponse.of(firstDownStation),
                        StationResponse.of(newStation));
    }
}
