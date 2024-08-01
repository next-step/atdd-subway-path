package nextstep.subway.line.acceptance;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import nextstep.subway.line.dto.ModifyLineRequest;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static nextstep.subway.util.LineStep.*;
import static nextstep.subway.util.StationStep.여러_개의_지하철_역_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class LineAcceptanceTest {

    @Autowired
    DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setup() {

        databaseCleanup.execute();

    }

    /* Given: 새로운 지하철 노선 정보를 입력하고,
       When: 관리자가 노선을 생성하면,
       Then: 해당 노선이 생성되고 노선 목록에 포함된다. */
    @DisplayName("지하철 노선을 생성한다")
    @Test
    public void createLine() {
        // given
        var 지하철_역_목록 = 여러_개의_지하철_역_생성(List.of("강남역", "역삼역"));

        // when
        var 원본_지하철_노선 = new LineResponse("신분당선", "Red", List.of(지하철_역_목록.get(0), 지하철_역_목록.get(1)), 10L);
        var 생성된_지하철_노선 = 지하철_노선_생성("신분당선", "Red", 지하철_역_목록.get(0).getId(), 지하철_역_목록.get(1).getId(), 10L);
        원본_지하철_노선.setId(생성된_지하철_노선.getId());

        // then
        assertThat(생성된_지하철_노선.equals(원본_지하철_노선)).isTrue();
    }

    /* Given: 여러 개의 지하철 노선과 지하철 역이 등록되어 있고,
       When: 관리자가 지하철 노선 목록을 조회하면,
       Then: 모든 지하철 노선 목록이 반환된다. */
    @DisplayName("전체 지하철 노선을 조회한다")
    @Test
    public void retrieveAllLine() {
        // given
        var 생성된_지하철_역_목록 = 여러_개의_지하철_역_생성(List.of("강남역", "역삼역", "선릉역", "논현역"));

        var 비교할_지하철_노선_목록 = new LinesResponse(List.of((지하철_노선_생성("신분당선", "Red", 생성된_지하철_역_목록.get(0).getId(), 생성된_지하철_역_목록.get(1).getId(), 10L)),
                지하철_노선_생성("분당선", "Red", 생성된_지하철_역_목록.get(2).getId(), 생성된_지하철_역_목록.get(3).getId(), 20L)));

        // when
        var 지하철_노선_목록 = 지하철_전체_노선_조회();

        // then
        assertThat(지하철_노선_목록.getLineResponseList().size()).isEqualTo(2);
        for (int i = 0; i < 비교할_지하철_노선_목록.getLineResponseList().size(); i++) {
            LineResponse 비교할_지하철_노선 = 비교할_지하철_노선_목록.getLineResponseList().get(i);
            LineResponse 지하철_노선 = 지하철_노선_목록.getLineResponseList().get(i);

            assertThat(비교할_지하철_노선.equals(지하철_노선)).isTrue();
        }

    }

    /* Given: 특정 지하철 노선과 지하철 역이 등록되어 있고,
       When: 관리자가 해당 노선을 조회하면,
       Then: 해당 노선의 정보가 반환된다. */
    @DisplayName("지하철 노선을 조회한다")
    @Test
    public void retrieveLine() {
        // when
        var 생성된_지하철_역_목록 = 여러_개의_지하철_역_생성(List.of("강남역", "역삼역", "선릉역", "논현역"));
        var 비교할_지하철_노선_목록 = new LinesResponse();
        var 지하철_노선_1 = 지하철_노선_생성("신분당선", "Red", 생성된_지하철_역_목록.get(0).getId(), 생성된_지하철_역_목록.get(1).getId(), 10L);
        비교할_지하철_노선_목록.getLineResponseList().add(지하철_노선_1);

        // when
        var 지하철_노선 = 지하철_노선_조회(지하철_노선_1.getId());

        // then
        var 비교할_지하철_노선 = 비교할_지하철_노선_목록.getLineResponseList().get(0);
        assertThat(비교할_지하철_노선.equals(지하철_노선)).isTrue();

    }

    /* Given: 특정 지하철 노선과 지하철 역이 등록되어 있고,
       When: 관리자가 해당 노선을 수정하면,
       Then: 해당 노선의 정보가 수정된다. */
    @DisplayName("지하철 노선을 수정한다")
    @Test
    public void modifyStation() {
        // given
        var 생성된_지하철_역_목록 = 여러_개의_지하철_역_생성(List.of("강남역", "역삼역", "선릉역", "논현역"));
        var 지하철_노선_1 = 지하철_노선_생성("신분당선", "Red", 생성된_지하철_역_목록.get(0).getId(), 생성된_지하철_역_목록.get(1).getId(), 10L);

        ModifyLineRequest 지하철_노선_변경_요청 = new ModifyLineRequest("바꾼_신분당선", "Blue");

        // when
        지하철_노선_수정(지하철_노선_1.getId(), 지하철_노선_변경_요청);

        // then
        var 변경된_지하철_노선 = 지하철_노선_조회(지하철_노선_1.getId());
        assertThat(지하철_노선_변경_요청.getName()).isEqualTo(변경된_지하철_노선.getName());
        assertThat(지하철_노선_변경_요청.getColor()).isEqualTo(변경된_지하철_노선.getColor());

    }

    /* Given: 특정 지하철 역과 특정 지하철 노선이 등록되어 있고,
       When: 관리자가 해당 노선을 삭제하면,
       Then: 해당 노선이 삭제되고 노선 목록에서 제외된다. */
    @DisplayName("지하철 노선을 삭제한다")
    @Test
    public void deleteStation() {
        // given
        var 생성된_지하철_역_목록 = 여러_개의_지하철_역_생성(List.of("강남역", "역삼역", "선릉역", "논현역"));
        var 지하철_노선_1 = 지하철_노선_생성("신분당선", "Red", 생성된_지하철_역_목록.get(0).getId(), 생성된_지하철_역_목록.get(1).getId(), 10L);

        // when
        지하철_노선_삭제(지하철_노선_1.getId());

        // then
        var 지하철_노선_목록 = 지하철_전체_노선_조회();
        var 지하철_노선_정보 = 지하철_노선_목록.getLineResponseList();
        assertThat(지하철_노선_정보).doesNotContain(지하철_노선_1);
    }

}
