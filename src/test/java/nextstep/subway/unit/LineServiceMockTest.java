package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.utils.TestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
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

    private final TestObjectFactory testObjectFactory = new TestObjectFactory();

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Station 양재역 = testObjectFactory.역생성("양재역");
        Station 양재시민의숲역 = testObjectFactory.역생성("양재시민의숲역");
        Station 청계산입구역 = testObjectFactory.역생성("청계산입구역");
        Line 분당선 = testObjectFactory.노선생성("분당선");

        분당선.addSection(new Section(분당선, 양재역, 양재시민의숲역, 10));

        when(stationService.findById(양재시민의숲역.getId())).thenReturn(양재시민의숲역);
        when(stationService.findById(청계산입구역.getId())).thenReturn(청계산입구역);
        when(lineRepository.findById(분당선.getId())).thenReturn(Optional.of(분당선));

        // when
        // lineService.addSection 호출
        lineService.addSection(분당선.getId(), new SectionRequest(양재시민의숲역.getId(), 청계산입구역.getId(), 10));

        // then
        // line.findLineById 메서드를 통해 검증
        Line line = lineRepository.findById(분당선.getId()).get();
        assertThat(line.getSections().size()).isEqualTo(2);
        assertThat(line.getStations()).contains(양재역, 양재시민의숲역, 청계산입구역);
    }
}
