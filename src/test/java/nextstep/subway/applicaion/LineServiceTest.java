package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    private LineService lineService;

    @BeforeEach
    void init() {
        StationService stationService = new StationService(stationRepository);
        lineService = new LineService(
                lineRepository,
                stationService
        );
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void saveLine() {
        // given
        Long 동암역_ID = 역_생성_요청("동암역");
        Long 부평역_ID = 역_생성_요청("부평역");

        String 일호선 = "1호선";
        String 파란색 = "파란색";

        // when
        LineResponse 저장된_노선 = 노선_생성_요청(
                동암역_ID,
                부평역_ID,
                일호선,
                파란색,
                10
        );

        // then
        assertAll(
                () -> assertThat(저장된_노선.getName()).isEqualTo(일호선),
                () -> assertThat(저장된_노선.getColor()).isEqualTo(파란색)
        );
    }

    @Test
    @DisplayName("모든 노선을 출력한다.")
    void showLines() {
        // given
        Long 동암역_ID = 역_생성_요청("동암역");
        Long 부평역_ID = 역_생성_요청("부평역");

        String 일호선 = "1호선";
        String 파란색 = "파란색";

        노선_생성_요청(
                동암역_ID,
                부평역_ID,
                일호선,
                파란색,
                10
        );

        Long 주안역_ID = 역_생성_요청("주안역");
        Long 백운역_ID = 역_생성_요청("백운역");

        String 이호선 = "2호선";
        String 노란색 = "노란색";

        노선_생성_요청(
                주안역_ID,
                백운역_ID,
                이호선,
                노란색,
                10
        );

        List<LineResponse> 출력된_노선목록 = lineService.showLines();

        assertAll(
                () -> assertThat(출력된_노선목록.size()).isEqualTo(2),
                () -> assertThat(출력된_노선목록.get(0)
                                         .getName()).isEqualTo(일호선),
                () -> assertThat(출력된_노선목록.get(1)
                                         .getName()).isEqualTo(이호선)
        );

    }

    @Test
    @DisplayName("특정 노선을 검색한다.")
    void findById() {
        // given
        Long 동암역_ID = 역_생성_요청("동암역");
        Long 부평역_ID = 역_생성_요청("부평역");

        String 일호선 = "1호선";
        String 파란색 = "파란색";

        LineResponse 저장된_노선 = 노선_생성_요청(
                동암역_ID,
                부평역_ID,
                일호선,
                파란색,
                10
        );

        LineResponse 찾은_노선 = lineService.findById(저장된_노선.getId());

        assertThat(찾은_노선).usingRecursiveComparison()
                         .isEqualTo(저장된_노선);
    }

    @Test
    @DisplayName("존재하지 않는 노선을 검색한다.")
    void findById2() {
        Long 존재하지_않는_노선_ID = 9999999L;
        assertThatThrownBy(() -> lineService.findById(존재하지_않는_노선_ID)).isInstanceOf(IllegalArgumentException.class);
    }

    private LineResponse 노선_생성_요청(Long 상행역_ID, Long 하행역_ID, String 노선_이름, String 노선_색상, int 노선_거리) {
        LineRequest 노선_생성_정보 = new LineRequest(
                노선_이름,
                노선_색상,
                상행역_ID,
                하행역_ID,
                노선_거리
        );

        LineResponse 저장된_노선 = lineService.saveLine(노선_생성_정보);
        return 저장된_노선;
    }

    @DisplayName("노선 정보를 수정한다.")
    @Test
    void updateLine() {
        // given
        Long 동암역_ID = 역_생성_요청("동암역");
        Long 부평역_ID = 역_생성_요청("부평역");

        String 일호선 = "1호선";
        String 파란색 = "파란색";

        LineResponse 저장된_노선 = 노선_생성_요청(
                동암역_ID,
                부평역_ID,
                일호선,
                파란색,
                10
        );

        //when
        String 이호선 = "이호선";
        String 노란색 = "노란색";
        LineRequest 노선_수정_정보 = new LineRequest(
                이호선,
                노란색
        );
        lineService.updateLine(
                저장된_노선.getId(),
                노선_수정_정보
        );

        // then
        LineResponse 수정된_노선 = lineService.findById(저장된_노선.getId());
        assertAll(
                () -> assertThat(수정된_노선.getName()).isEqualTo(이호선),
                () -> assertThat(수정된_노선.getColor()).isEqualTo(노란색)
        );
    }

    @DisplayName("존재하지 않는 노선 정보를 수정한다.")
    @Test
    void updateLine2() {
        // when
        Long 존재하지_않는_노선_ID = 9999999L;
        String 이호선 = "이호선";
        String 노란색 = "노란색";
        LineRequest 노선_수정_정보 = new LineRequest(
                이호선,
                노란색
        );

        //then
        assertThatThrownBy(() -> lineService.updateLine(
                존재하지_않는_노선_ID,
                노선_수정_정보
        )).isInstanceOf(IllegalArgumentException.class);
    }

    private Long 역_생성_요청(String 동암역) {
        return stationRepository
                .save(new Station(동암역))
                .getId();
    }

}
