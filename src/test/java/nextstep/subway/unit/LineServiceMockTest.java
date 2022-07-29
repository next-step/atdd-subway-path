package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.request.SectionRequest;
import nextstep.subway.applicaion.exceptions.DataNotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.enums.exceptions.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    private final int defaultDistance = 10;

    @Test
    @DisplayName("구간 생성")
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Station 구의역 = new Station("구의역");
        Station 건대입구역 = new Station("건대입구역");
        Line 초록색_라인 = new Line("초록색_라인", "green-bg-30");
        SectionRequest sectionRequest = new SectionRequest(구의역.getId(), 건대입구역.getId(), defaultDistance);

        given(stationService.findById(구의역.getId()))
                .willReturn(구의역);

        given(stationService.findById(건대입구역.getId()))
                .willReturn(건대입구역);

        given(lineRepository.findById(초록색_라인.getId()))
                .willReturn(Optional.of(초록색_라인));

        // when
        // lineService.addSection 호출
        lineService.addSection(초록색_라인.getId(), sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        assertAll(() -> {
            assertThat(초록색_라인.getSections()).hasSize(1);
            assertThat(초록색_라인.getSections()).filteredOn(section -> section.equals(new Section(초록색_라인, 구의역, 건대입구역, defaultDistance)));
        });
    }
}
