package nextstep.subway.unit;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Distance;
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

@DisplayName("구간 서비스 단위 테스트")
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
    private long deleteStationId;
    private int distance;
    private Line line;
    private Station upStation;
    private Station downStation;
    private Station deleteStation;

    @BeforeEach
    void setUp() {
        this.lineId = 1L;
        this.upStationId = 1L;
        this.downStationId = 2L;
        this.deleteStationId = 3L;
        this.distance = 10;
        this.line = new Line("2호선", "bg-red-500");
        this.upStation = new Station("강남역");
        this.downStation = new Station("역삼역");
        this.deleteStation = new Station("선릉역");
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
                () -> assertThat(section.getDistance()).isEqualTo(new Distance(distance))
        );
    }

    @DisplayName("노선에 구간을 제거한다.")
    @Test
    void deleteSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        when(stationService.findById(upStationId)).thenReturn(upStation);
        when(stationService.findById(downStationId)).thenReturn(downStation);
        when(stationService.findById(deleteStationId)).thenReturn(deleteStation);
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));
        lineService.addSection(lineId, new SectionRequest(upStationId, downStationId, distance));
        lineService.addSection(lineId, new SectionRequest(downStationId, deleteStationId, distance));

        // when
        // lineService.deleteSection 호출
        lineService.deleteSection(lineId, deleteStationId);

        // then
        // lineService.findLineById 메서드를 통해 검증
        List<Section> sections = lineService.findLineById(lineId).getSections();
        Section section = sections.get(0);
        assertAll(
                () -> assertThat(sections).hasSize(1),
                () -> assertThat(section.getUpStation()).isEqualTo(upStation),
                () -> assertThat(section.getDownStation()).isEqualTo(downStation),
                () -> assertThat(section.getDistance()).isEqualTo(new Distance(distance))
        );
    }
}
