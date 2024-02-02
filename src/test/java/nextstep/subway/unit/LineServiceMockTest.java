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

        // when
        // lineService.addSection 호출
        lineService.addSection(LINE5_ID, request);

        // then
        // lineService.findLineById 메서드를 통해 검증
        assertThat(lineService.findById(LINE5_ID).getStations().size()).isEqualTo(3);
    }
}
