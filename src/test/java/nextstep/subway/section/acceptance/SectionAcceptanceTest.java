package nextstep.subway.section.acceptance;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static nextstep.subway.common.constant.ErrorCode.*;
import static nextstep.subway.util.LineStep.지하철_노선_생성;
import static nextstep.subway.util.LineStep.지하철_노선_조회;
import static nextstep.subway.util.SectionStep.*;
import static nextstep.subway.util.StationStep.지하철_역_등록;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class SectionAcceptanceTest {

    @Autowired
    DatabaseCleanup databaseCleanup;

    private StationResponse 강남역;
    private StationResponse 선릉역;
    private StationResponse 삼성역;
    private StationResponse 언주역;
    private StationResponse 논현역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setup() {

        databaseCleanup.execute();

        강남역 = 지하철_역_등록("강남역");
        선릉역 = 지하철_역_등록("선릉역");
        삼성역 = 지하철_역_등록("삼성역");
        언주역 = 지하철_역_등록("언주역");
        논현역 = 지하철_역_등록("논현역");

        신분당선 = 지하철_노선_생성("신분당선", "Red", 강남역.getId(), 선릉역.getId(), 10L);
    }

    /**
     * User Story : 관리자로서, 지하철 노선도 관리를 하기 위해 지하철 역을 노선에 등록한다.
     **/

    /* Given: 지하철 역과 지하철 노선이 등록되어 있고,
       When: 관리자가 지하철 노선에 새로운 구간을 등록 요청하면,
       Then: 지하철 노선에 관리자가 등록 요청한 새로운 구간이 추가된다. */
    @DisplayName("지하철 구간을 등록한다.")
    @Test
    public void addSection_success() {
        // given ...

        // when
        지하철_구간_등록(신분당선.getId(), SectionRequest.of(선릉역.getId(), 삼성역.getId(), 10L));
        var 구간이_등록된_신분당선 = 지하철_노선_조회(신분당선.getId());

        // then
        assertThat(구간이_등록된_신분당선.getStations()).containsExactlyInAnyOrder(강남역, 선릉역, 삼성역);

    }

    /* Given: 지하철 역과 지하철 노선이 등록되어 있고,
       When: 관리자가 지하철 노선에 기존 구간 사이로 새로운 구간을 등록 요청하면,
       Then: 지하철 노선에 관리자가 등록 요청한 새로운 구간이 기존 구간 사이로 추가된다. */
    @DisplayName("지하철 기존 구간 사이에 새로운 구간을 등록한다.")
    @Test
    public void addSection_success_2() {
        // given ...
        지하철_구간_등록(신분당선.getId(), SectionRequest.of(선릉역.getId(), 삼성역.getId(), 10L));

        // when
        지하철_구간_등록(신분당선.getId(), SectionRequest.of(선릉역.getId(), 언주역.getId(), 4L));
        var 구간이_등록된_신분당선 = 지하철_노선_조회(신분당선.getId());

        // then
        assertThat(구간이_등록된_신분당선.getStations()).containsExactlyInAnyOrder(강남역, 선릉역, 삼성역, 언주역);

    }

    /* Given: 지하철 역과 지하철 노선이 등록되어 있고,
       When: 관리자가 지하철 노선의 첫번째 구간으로 새로운 구간을 등록 요청하면,
       Then: 지하철 노선에 관리자가 등록 요청한 새로운 구간이 노선의 첫번째 구간으로 추가된다. */
    @DisplayName("지하철 기존 구간의 첫 번째 구간으로 새로운 구간을 등록한다.")
    @Test
    public void addSection_success_3() {
        // given ...
        지하철_구간_등록(신분당선.getId(), SectionRequest.of(선릉역.getId(), 삼성역.getId(), 10L));

        // when
        지하철_구간_등록(신분당선.getId(), SectionRequest.of(언주역.getId(), 강남역.getId(), 4L));
        var 구간이_등록된_신분당선 = 지하철_노선_조회(신분당선.getId());

        // then
        assertThat(구간이_등록된_신분당선.getStations()).containsExactlyInAnyOrder(강남역, 선릉역, 삼성역, 언주역);

    }

    /* Given: 지하철 역과 지하철 노선이 등록되어 있고,
   When: 관리자가 지하철 노선의 마지막 구간으로 새로운 구간을 등록 요청하면,
   Then: 지하철 노선에 관리자가 등록 요청한 새로운 구간이 노선의 마지막 구간으로 추가된다. */
    @DisplayName("지하철 기존 구간의 마지막 구간으로 새로운 구간을 등록한다.")
    @Test
    public void addSection_success_4() {
        // given ...
        지하철_구간_등록(신분당선.getId(), SectionRequest.of(선릉역.getId(), 삼성역.getId(), 10L));

        // when
        지하철_구간_등록(신분당선.getId(), SectionRequest.of(삼성역.getId(), 언주역.getId(), 4L));
        var 구간이_등록된_신분당선 = 지하철_노선_조회(신분당선.getId());

        // then
        assertThat(구간이_등록된_신분당선.getStations()).containsExactlyInAnyOrder(강남역, 선릉역, 삼성역, 언주역);

    }

    /* Given: 지하철 역과 지하철 노선이 등록되어 있고,
       When: 관리자가 지하철 노선에 기존 구간 사이로 기존 구간의 Distance보다 길거나 같은 Distance를 가진 새로운 구간을 등록 요청하면,
       Then: 관리자의 구간 추가 요청은 실패한다. */
    @DisplayName("지하철 구간 등록에 실패한다. 기존 구간의 Distance보다 길거나 같은 Distance를 가진 새로운 구간은 생성될 수 없다.")
    @Test
    public void addSection_fail_1() {
        // given ...
        지하철_구간_등록(신분당선.getId(), SectionRequest.of(선릉역.getId(), 삼성역.getId(), 10L));

        // when
        var errorResponse = 지하철_구간_등록_실패(신분당선.getId(), SectionRequest.of(선릉역.getId(), 언주역.getId(), 10L));

        // then
        Assertions.assertEquals(errorResponse.getCode(), SECTION_DISTANCE_LESS_THAN_EXISTING.getCode());

    }

    /* Given: 지하철 역과 지하철 노선이 등록되어 있고,
        When: 관리자가 지하철 노선에 이미 등록되어 있는 상행역과 하행역을 가진 새로운 구간을 생성 요청하면,
        Then: 관리자의 구간 추가 요청은 실패한다. */
    @DisplayName("지하철 구간 등록에 실패한다. 이미 노선에 등록되어 있는 상행역과 하행역을 가진 새로운 구간은 생성될 수 없다.")
    @Test
    public void addSection_fail_2() {
        // given ...

        // when & then
        var errorResponse = 지하철_구간_등록_실패(신분당선.getId(), SectionRequest.of(강남역.getId(), 선릉역.getId(), 10L));
        Assertions.assertEquals(errorResponse.getCode(), SECTION_ALREADY_EXIST.getCode());

    }

    /* Given: 지하철 역과 지하철 노선이 등록되어 있고,
        When: 관리자가 지하철 노선에 등록되어 있지 않은 상행역과 하행역을 가진 새로운 구간을 생성 요청하면,
        Then: 관리자의 구간 추가 요청은 실패한다. */
    @DisplayName("지하철 구간 등록에 실패한다. 새로운 구간의 상행역과 하행역 둘 중 하나가 노선에 등록되어 있지않으면 새로운 구간은 생성될 수 없다.")
    @Test
    public void addSection_fail_3() {
        // given ...

        // when & then
        var errorResponse = 지하철_구간_등록_실패(신분당선.getId(), SectionRequest.of(언주역.getId(), 논현역.getId(), 2L));
        Assertions.assertEquals(errorResponse.getCode(), SECTION_NOT_FOUND.getCode());

    }

    /* Given: 지하철 역과 지하철 노선이 등록되어 있고,
       When: 관리자가 지하철 노선의 상행 종점역 구간을 삭제 요청하면,
       Then: 관리자가 삭제 요청한 구간이 삭제된다. */
    @DisplayName("지하철 노선의 상행 종점역을 삭제한다.")
    @Test
    public void deleteSection_success1() {
        // given ...
        지하철_구간_등록(신분당선.getId(), SectionRequest.of(선릉역.getId(), 삼성역.getId(), 10L));
        var 구간이_등록된_신분당선 = 지하철_노선_조회(신분당선.getId());
        assertThat(구간이_등록된_신분당선.getStations()).containsExactlyInAnyOrder(강남역, 선릉역, 삼성역);

        // when
        지하철_구간_삭제(구간이_등록된_신분당선.getId(), 강남역.getId());

        // then
        var 구간이_삭제된_신분당선 = 지하철_노선_조회(신분당선.getId());
        Assertions.assertFalse(구간이_삭제된_신분당선.getStations().contains(강남역));

    }

    /* Given: 지하철 역과 지하철 노선이 등록되어 있고,
       When: 관리자가 지하철 노선의 상행 종점역 구간을 삭제 요청하면,
       Then: 관리자가 삭제 요청한 구간이 삭제된다. */
    @DisplayName("지하철 노선의 중간 구간을 삭제한다.")
    @Test
    public void deleteSection_success2() {
        // given ...
        지하철_구간_등록(신분당선.getId(), SectionRequest.of(선릉역.getId(), 삼성역.getId(), 10L));
        지하철_구간_등록(신분당선.getId(), SectionRequest.of(삼성역.getId(), 언주역.getId(), 10L));
        var 구간이_등록된_신분당선 = 지하철_노선_조회(신분당선.getId());
        assertThat(구간이_등록된_신분당선.getStations()).containsExactlyInAnyOrder(강남역, 선릉역, 삼성역, 언주역);

        // when
        지하철_구간_삭제(구간이_등록된_신분당선.getId(), 선릉역.getId());

        // then
        var 구간이_삭제된_신분당선 = 지하철_노선_조회(신분당선.getId());
        Assertions.assertFalse(구간이_삭제된_신분당선.getStations().contains(선릉역));

    }

    /* Given: 지하철 역과 지하철 노선이 등록되어 있고,
       When: 관리자가 지하철 노선의 상행 종점역 구간을 삭제 요청하면,
       Then: 관리자가 삭제 요청한 구간이 삭제된다. */
    @DisplayName("지하철 노선의  하행 종점역을 삭제한다.")
    @Test
    public void deleteSection_success3() {
        // given ...
        지하철_구간_등록(신분당선.getId(), SectionRequest.of(선릉역.getId(), 삼성역.getId(), 10L));
        var 구간이_등록된_신분당선 = 지하철_노선_조회(신분당선.getId());
        assertThat(구간이_등록된_신분당선.getStations()).containsExactlyInAnyOrder(강남역, 선릉역, 삼성역);

        // when
        지하철_구간_삭제(구간이_등록된_신분당선.getId(), 삼성역.getId());

        // then
        var 구간이_삭제된_신분당선 = 지하철_노선_조회(신분당선.getId());
        Assertions.assertFalse(구간이_삭제된_신분당선.getStations().contains(삼성역));

    }

    /* Given: 지하철 역과 지하철 노선이 등록되어 있고,
       When: 관리자가 지하철 노선에 존재하지 않는 역을 삭제 요청하면,
       Then: 관리자가 구간 삭제 요청은 실패한다. */
    @DisplayName("지하철 구간 삭제를 실패한다. 노선에 존재하는 구간만 삭제 가능하다.")
    @Test
    public void deleteSection_fail() {
        // given ...
        지하철_구간_등록(신분당선.getId(), SectionRequest.of(선릉역.getId(), 삼성역.getId(), 10L));
        var 구간이_등록된_신분당선 = 지하철_노선_조회(신분당선.getId());
        assertThat(구간이_등록된_신분당선.getStations()).containsExactlyInAnyOrder(강남역, 선릉역, 삼성역);

        // when & then
        var errorResponse = 지하철_구간_삭제_실패(구간이_등록된_신분당선.getId(), 언주역.getId());
        Assertions.assertEquals(errorResponse.getCode(), SECTION_NOT_FOUND.getCode());

    }

    /* Given: 지하철 역과 지하철 노선이 등록되어 있고,
        When: 관리자가 지하철 노선의 구간이 1개인 경우,
        Then: 관리자가 삭제 요청은 실패한다. */
    @DisplayName("지하철 구간 삭제 실패한다. 구간이 2개 이상이어야 삭제 가능하다.")
    @Test
    public void deleteSection_fail_2() {
        // given ...
        var 구간이_등록된_신분당선 = 지하철_노선_조회(신분당선.getId());
        assertThat(구간이_등록된_신분당선.getStations()).containsExactlyInAnyOrder(강남역, 선릉역);

        // when & then
        var errorResponse = 지하철_구간_삭제_실패(구간이_등록된_신분당선.getId(), 선릉역.getId());
        Assertions.assertEquals(errorResponse.getCode(), SECTION_NOT_PERMISSION_COUNT_TOO_LOW.getCode());

    }

}