package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static nextstep.subway.utils.LineFixture.라인_생성_2호선;
import static nextstep.subway.utils.SectionFixture.구간생성;
import static nextstep.subway.utils.StationFixture.역생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationService);
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        final Line line = 라인_생성_2호선();
        when(stationService.findById(anyLong())).thenReturn(new Station());
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));

        // when
        // lineService.addSection 호출
        final SectionRequest sectionRequest = new SectionRequest();
        sectionRequest.setUpStationId(1L);
        sectionRequest.setDownStationId(2L);
        sectionRequest.setDistance(3);
        lineService.addSection(1L, sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        assertThat(line.getSections()).hasSize(1);
    }

    @Test
    void deleteSection() {
        final Line line = 라인_생성_2호선();
        final Station 강남역 = 역생성("강남역");
        final Station 역삼역 = 역생성("역삼역");
        final Station 선릉역 = 역생성("선릉역");

        line.addSection(구간생성(line, 강남역, 역삼역, 3));
        line.addSection(구간생성(line, 역삼역, 선릉역, 2));

        when(stationService.findById(anyLong())).thenReturn(선릉역);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));

        lineService.deleteSection(line.getId(), 선릉역.getId());

        assertThat(line.getLastSection().getUpStation()).isEqualTo(강남역);
        assertThat(line.getLastSection().getDownStation()).isEqualTo(역삼역);
    }
}
