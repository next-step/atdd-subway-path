package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

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
    void setup() {
        lineService = new LineService(lineRepository, stationService);
    }

    @DisplayName("구간 등록하기")
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅

        final String 강남역_이름 = "강남역";
        when(stationService.findById(anyLong())).thenReturn(new Station(강남역_이름));

        final String 시청역_이름 = "시청역";
        when(stationService.findById(anyLong())).thenReturn(new Station(시청역_이름));

        final String 구로디지털단지역_이름 = "구로디지털단지역";
        when(stationService.findById(anyLong())).thenReturn(new Station(구로디지털단지역_이름));

        final String 신분당선_이름 = "신분당선";
        final String red = "red";
        Line 신분당선 = new Line(신분당선_이름, red);
        ReflectionTestUtils.setField(신분당선, "id", 1L);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.ofNullable(신분당선));

        SectionRequest sectionRequest = new SectionRequest();
        ReflectionTestUtils.setField(sectionRequest, "upStationId", 1L);
        ReflectionTestUtils.setField(sectionRequest, "downStationId", 2L);
        ReflectionTestUtils.setField(sectionRequest, "distance", 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        Line 반환된_신분당선 = lineService.findLineById(신분당선.getId());
        assertThat(반환된_신분당선.getSections().size()).isEqualTo(1);
    }
}
