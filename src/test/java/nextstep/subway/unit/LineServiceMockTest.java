package nextstep.subway.unit;

import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    private LineService lineService;

    @Test
    void addSection() {
        // given
        /**
         * 요구 사항 : lineRepository, stationService stub 설정을 통해 초기값 셋팅
         * 먼저 lineRepository를 stub 하려고 함.
         * 현재 작성하려는 테스트 코드인 addSecion() 내부에서 lineRepository를 사용하는 코드는 findById가 있음
         * 따라서 lineRepository의 findById를 stub 해줬음
         *
         * 다음으로 stationService를 stub 해줘야함. 얘를 stub 해야 하는 이유는 addSection 내부에서 stationService의 findById가 사용되고 있음
         * 따라서 stationService의 findById를 stub 해줬음.
         * 이때 findById의 인자로 사용될 station이 필요하기 때문에 강남역, 양재역을 만들었음
         *
         * 여기까지 해주면 요구 받은 stub은 다 해준듯
         * */
        Station 강남역 = new Station("강남역");
        ReflectionTestUtils.setField(강남역, "id", 1L);
        Station 양재역 = new Station("양재역");
        ReflectionTestUtils.setField(양재역, "id", 2L);

        Line 신분당선 = new Line("신분당선", "red");
        신분당선.setId(1L);

        when(lineRepository.findById(신분당선.getId())).thenReturn(Optional.of(신분당선));
        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        when(stationService.findById(양재역.getId())).thenReturn(양재역);

        // when
        /**
         * 요구 사항 : lineService.addSection 호출
         * lineService.addSection 만들어 주라고 했는데 lineService가 없음. 그래서 만들고 @Autowired로 받음.
         * Mock으로 하면 안될듯. 여기서 테스트 하고자 하는건 lineService기 때문에 lineService는 진짜 인스턴스를 써야할 듯
         * lineService @Autowired 쓰면 안됨. 테스트 코드에서 context를 사용하고 있지 않기 때문인듯?
         * 따라서 lineService를 전역으로 만들어서 사용하려고 했는데 이것도 안됨. Mock으로 만들어지는 인스턴스는 테스트 환경이 구성된 이후인듯
         * 그래서 여기서 lineService 인스턴스를 만들어서 사용했음
         * */
        SectionRequest 신분당선_구간_리퀘스트 = new SectionRequest();
        ReflectionTestUtils.setField(신분당선_구간_리퀘스트, "upStationId", 1L);
        ReflectionTestUtils.setField(신분당선_구간_리퀘스트, "downStationId", 2L);
        ReflectionTestUtils.setField(신분당선_구간_리퀘스트, "distance", 10);

        lineService = new LineService(lineRepository, stationService);
        lineService.addSection(신분당선.getId(), 신분당선_구간_리퀘스트);

        // then
        /**
         * 요구사항 : lineService.findLineById 메서드를 통해 검증
         * */
        LineResponse lineResponse = lineService.findById(신분당선.getId());
        assertThat(lineResponse.getStations().size()).isEqualTo(2);
    }
}
