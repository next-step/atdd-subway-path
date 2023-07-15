package nextstep.subway.acceptance;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import nextstep.subway.acceptance.commonStep.AcceptanceTest;
import nextstep.subway.acceptance.commonStep.LineStep;
import nextstep.subway.acceptance.commonStep.SectionStep;
import nextstep.subway.acceptance.commonStep.StationStep;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private Long 이호선;

    private Long 강남역;
    private Long 역삼역;
    private Long 선릉역;
    private Long 삼성역;
    private Long 종합운동장역;
    private Long 잠실역;

    @BeforeEach
    public void setGivenData() {

        이호선 =  LineStep.지하철_노선_생성( "2호선", "green").jsonPath().getLong("id");

        강남역 =  StationStep.지하철역_생성("강남역").jsonPath().getLong("id");
        역삼역 =  StationStep.지하철역_생성("역삼역").jsonPath().getLong("id");
        선릉역 =  StationStep.지하철역_생성("선릉역").jsonPath().getLong("id");
        삼성역 =  StationStep.지하철역_생성("삼성역").jsonPath().getLong("id");
        종합운동장역 =  StationStep.지하철역_생성("종합운동장역").jsonPath().getLong("id");
        잠실역 =  StationStep.지하철역_생성("잠실역").jsonPath().getLong("id");
    }


    /**
     * Given 지하철 역과 노선을 생성하고
     * When 해당 노선에 구간을 추가하면
     * Then 해당 노선 조회 시 추가된 구간을 확인할 수 있다
     */
    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {

        // when
        ExtractableResponse<Response> response = SectionStep.지하철구간_생성(이호선,강남역,역삼역,10L);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        JsonPath lineJsonPath = LineStep.지하철_노선_조회(이호선);
        assertThat(lineJsonPath.getList("stations.id", Long.class)).containsExactly(강남역,역삼역);

    }

    /**
     * Given 지하철 역과 노선을 생성하고
     * When 역 사이에 새로운 역을 등록할 경우
     * Then 해당 노선 조회 시 추가된 구간을 확인할 수 있다
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void createSectionBetweenExistingSection() {
        //given
        Long 선릉잠실구간거리 = 20L;
        SectionStep.지하철구간_생성(이호선,선릉역,잠실역,선릉잠실구간거리);

        // when
        ExtractableResponse<Response> response1 = SectionStep.지하철구간_생성(이호선,선릉역,삼성역,선릉잠실구간거리/2-1);
        ExtractableResponse<Response> response2 = SectionStep.지하철구간_생성(이호선,종합운동장역,잠실역,선릉잠실구간거리/2-1);

        //then
        assertThat(response1.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.OK.value());
        JsonPath lineJsonPath = LineStep.지하철_노선_조회(이호선);
        assertThat(lineJsonPath.getList("stations.id", Long.class)).containsExactly(선릉역,삼성역,종합운동장역,잠실역);
    }



    /**
     * Given 지하철 역과 노선을 생성하고
     * When  존재하지 않는 역을 가진 구간을 추가하면
     * Then Bad Request 400 error가 발생한다
     */
    @DisplayName("존재하지 않는 역을 가진 구간을 생성한다.")
    @Test
    void createInvalidSectionNotExistsStation() {
        //given
        SectionStep.지하철구간_생성(이호선,강남역,역삼역,10L);
        List<Long> stationIds = StationStep.지하철역_목록_전체조회().getList("id", Long.class);
        Long 존재하지_않는_역의_PK = stationIds.get(stationIds.size() - 1) + 1L;

        // when
        ExtractableResponse<Response> response = SectionStep.지하철구간_생성(이호선,역삼역,존재하지_않는_역의_PK,20L);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }



    /**
     * Given 지하철 역과 노선을 생성하고
     * When  기존 역 사이 길이보다 크거나 같은 거리를 가진 구간을 추가하면
     * Then Bad Request 400 error가 발생한다
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void createInvalidSectionDueToDistance() {
        //given
        Long 선릉잠실구간거리 = 20L;
        SectionStep.지하철구간_생성(이호선,선릉역,잠실역,선릉잠실구간거리);

        // when
        ExtractableResponse<Response> response1 = SectionStep.지하철구간_생성(이호선,선릉역,삼성역,선릉잠실구간거리+10L);
        ExtractableResponse<Response> response2 = SectionStep.지하철구간_생성(이호선,종합운동장역,잠실역,선릉잠실구간거리);

        //then
        assertThat(response1.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 역과 노선을 생성하고
     * When  해당 노선에 상행역과 하행역이 이미 노선에 모두 등록되어 있는 구간을 추가하면
     * Then Bad Request 400 error가 발생한다
     */

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void createInvalidSectionDueToDownStation() {
        //given
        SectionStep.지하철구간_생성(이호선,강남역,역삼역,10L);

        // when
        ExtractableResponse<Response> response = SectionStep.지하철구간_생성(이호선,역삼역,강남역,20L);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 역과 노선을 생성하고
     * When 해당 노선에 상행역과 하행역 둘 중 하나도 포함되어있지 않은 구간을 추가하면
     * Then Bad Request 400 error가 발생한다
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다")
    @Test
    void createInvalidSectionDueToUpStation() {
        //given
        SectionStep.지하철구간_생성(이호선,강남역,역삼역,10L);

        // when
        ExtractableResponse<Response> response = SectionStep.지하철구간_생성(이호선,선릉역,삼성역,20L);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }






    /**
     * Given 지하철 역,노선, 구간을 생성하고
     * When 생성한 구간을 삭제하면
     * Then 해당 노선 조회 시 삭제된 노선을 조회할 수 없다
     */
    @DisplayName("지하철 구간을 삭제한다")
    @Test
    void deleteSection() {
        //given
        SectionStep.지하철구간_생성(이호선,강남역,역삼역,10L);
        SectionStep.지하철구간_생성(이호선,역삼역,선릉역,20L);

        // when
        ExtractableResponse<Response> response = SectionStep.지하철구간_삭제(이호선, 역삼역);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 역,노선, 구간을 생성하고
     * When 지하철 노선에 등록된 마지막 구간이 아닌 구간을 삭제시도할 경우
     * Then  Bad Request 400 error가 발생한다
     */
    @DisplayName("지하철 노선에 등록된 마지막 구간이 아닌 구간을 삭제한다")
    @Test
    void invalidDeleteSectionNotLastSection() {
        //given
        SectionStep.지하철구간_생성(이호선,강남역,역삼역,10L);
        SectionStep.지하철구간_생성(이호선,역삼역,선릉역,20L);

        // when
        ExtractableResponse<Response> response = SectionStep.지하철구간_삭제(이호선, 강남역);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 역,노선, 구간을 생성하고
     * When 지하철 노선에 등록된 구간이 1개인 경우에 구간을 삭제시도할 경우
     * Then  Bad Request 400 error가 발생한다
     */
    @DisplayName("지하철 노선에 등록된 구간이 1개인 경우에 구간을 삭제시도한다")
    @Test
    void invalidDeleteSectionOnlyOneSection() {
        //given
        SectionStep.지하철구간_생성(이호선,강남역,역삼역,10L);

        // when
        ExtractableResponse<Response> response = SectionStep.지하철구간_삭제(이호선, 강남역);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않는 역을 가진 구간을 삭제한다.")
    @Test
    void InvalidDeleteSectionNotExistsStation() {

        //given
        SectionStep.지하철구간_생성(이호선,강남역,역삼역,10L);
        List<Long> stationIds = StationStep.지하철역_목록_전체조회().getList("id", Long.class);
        Long 존재하지_않는_역의_PK = stationIds.get(stationIds.size() - 1) + 1L;

        // when
        ExtractableResponse<Response> response = SectionStep.지하철구간_삭제(이호선, 존재하지_않는_역의_PK);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }
}
