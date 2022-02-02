package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("LineServiceMockTest - 가짜 협력 객체 사용")
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private LineService lineService;

    @BeforeEach
    void init() {
        lineService = new LineService(
                lineRepository,
                new StationService(stationRepository)
        );
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void saveLine() {

        //given
        String 이호선 = "2호선";
        String 파란색 = "파란색";

        Line 이호선_노선 = new Line(
                이호선,
                파란색
        );

        //when
        when(lineRepository.save(any())).thenReturn(이호선_노선);

        LineRequest 이호선_생성_정보 = new LineRequest(
                이호선,
                파란색
        );
        LineResponse 저장된_노선_정보 = lineService.saveLine(이호선_생성_정보);

        //then
        assertAll(
                () -> assertThat(저장된_노선_정보.getName()).isEqualTo(이호선),
                () -> assertThat(저장된_노선_정보.getColor()).isEqualTo(파란색)
        );
    }

    @DisplayName("특정 노선을 검색한다.")
    @Test
    void findById() {

        //given
        String 이호선 = "2호선";
        String 파란색 = "파란색";

        Line 이호선_노선 = new Line(
                이호선,
                파란색
        );

        //when
        when(lineRepository.save(any())).thenReturn(이호선_노선);
        LineRequest 이호선_생성_정보 = new LineRequest(
                이호선,
                파란색
        );
        LineResponse 저장된_노선_정보 = lineService.saveLine(이호선_생성_정보);

        Optional<Line> 찾을_노선 = Optional.of(new Line(
                이호선,
                파란색
        ));
        when(lineRepository.findById(any())).thenReturn(찾을_노선);
        LineResponse 찾은_노선_정보 = lineService.findById(저장된_노선_정보.getId());

        assertAll(
                () -> assertThat(찾은_노선_정보.getName()).isEqualTo(이호선_생성_정보.getName()),
                () -> assertThat(찾은_노선_정보.getColor()).isEqualTo(이호선_생성_정보.getColor())
        );
    }

}
