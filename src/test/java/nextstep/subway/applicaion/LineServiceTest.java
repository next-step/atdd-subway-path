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

import static org.assertj.core.api.Assertions.assertThat;
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
        lineService = new LineService(lineRepository, stationService);
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
        LineResponse 저장된_노선 = 노선_생성_요청(동암역_ID, 부평역_ID, 일호선, 파란색, 10);

        // then
        assertAll(
                () -> assertThat(저장된_노선.getName()).isEqualTo(일호선),
                () -> assertThat(저장된_노선.getColor()).isEqualTo(파란색)
        );
    }

    private LineResponse 노선_생성_요청(Long 상행역_ID, Long 하행역_ID, String 노선_이름, String 노선_색상, int 노선_거리) {
        LineRequest 노선_생성_정보 = new LineRequest(노선_이름, 노선_색상, 상행역_ID, 하행역_ID, 노선_거리);

        LineResponse 저장된_노선 = lineService.saveLine(노선_생성_정보);
        return 저장된_노선;
    }

    private Long 역_생성_요청(String 동암역) {
        return stationRepository.save(new Station(동암역))
                                .getId();
    }

}
