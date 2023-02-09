package nextstep.subway.unit;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    private long lineId;
    private long upStationId;
    private long downStationId;
    private int distance;
    private Line line;
    private Station upStation;
    private Station downStation;

    @BeforeEach
    void setUp() {
        this.lineId = 1L;
        this.upStationId = 1L;
        this.downStationId = 2L;
        this.distance = 10;
        this.line = new Line("2호선", "bg-red-500");
        this.upStation = new Station("강남역");
        this.downStation = new Station("역삼역");
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(stationService.findById(upStationId)).thenReturn(upStation);
        when(stationService.findById(downStationId)).thenReturn(downStation);
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));

        // when
        // lineService.addSection 호출
        lineService.addSection(lineId, new SectionRequest(upStationId, downStationId, distance));

        // then
        // lineService.findLineById 메서드를 통해 검증
        List<Section> sections = lineService.findLineById(lineId).getSections();
        Section section = sections.get(0);
        assertAll(
                () -> assertThat(sections).hasSize(1),
                () -> assertThat(section.getUpStation()).isEqualTo(upStation),
                () -> assertThat(section.getDownStation()).isEqualTo(downStation),
                () -> assertThat(section.getDistance()).isEqualTo(distance)
        );
    }
}
