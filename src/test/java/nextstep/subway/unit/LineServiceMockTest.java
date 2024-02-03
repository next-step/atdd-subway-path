package nextstep.subway.unit;

import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineService;
import nextstep.subway.section.SectionAddRequest;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static nextstep.subway.utils.LineFixture.LINE5_ID;
import static nextstep.subway.utils.LineFixture.Line5;
import static nextstep.subway.utils.StationFixture.*;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private StationRepository stationRepository;
    @Mock
    private LineRepository lineRepository;

    private LineService lineService;

    @BeforeEach
    void setup() {
        lineService = new LineService(lineRepository, stationRepository);
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        SectionAddRequest request = new SectionAddRequest(
                GANGDONG_ID,
                MACHEON_ID,
                5
        );
        given(lineRepository.findById(LINE5_ID)).willReturn(Optional.of(Line5()));
        given(stationRepository.findById(GANGDONG_ID)).willReturn(Optional.of(GangdongStation()));
        given(stationRepository.findById(MACHEON_ID)).willReturn(Optional.of(MacheonStation()));
        /*
        * 이 테스트가 실패하는데, 그 이유는 lineService.addSection()의 내부 로직에서
        * station.getId()를 사용하는데 여기서 Null 에러가 납니다...
        * 그럼 station.getId()가 올바른 Id를 리턴하도록 위처럼 given절로 세팅을 해줘야하는 건가요?
        * 아니면 내부 로직에서 station.getId()가 아니라 getName() 을 사용하도록 리팩토링해야 하나요?
        * */

        // when
        // lineService.addSection 호출
        lineService.addSection(LINE5_ID, request);

        // then
        // lineService.findLineById 메서드를 통해 검증
        assertThat(lineService.findById(LINE5_ID).getStations().size()).isEqualTo(3);
    }
}
