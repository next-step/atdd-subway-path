package nextstep.subway.fixture.acceptance.then;

import static nextstep.subway.fixture.acceptance.when.LineApiFixture.지하철역_노선_단건_조회;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;

public abstract class SectionThenFixture {

    public static AbstractStringAssert<?> 노선구간추가시_모든역이_노선에_이미_존재할때_오류_검사(ExtractableResponse<Response> response) {
        return assertThat(response.jsonPath().getString("message")).isEqualTo(
            "상행역과 하행역이 이미 노선에 모두 등록되어있습니다.");
    }

    public static AbstractStringAssert<?> 노선구간추가시_모든역이_노선에_존재하지않을때_오류_검사(ExtractableResponse<Response> response) {
        return assertThat(response.jsonPath().getString("message")).isEqualTo(
            "상행역과 하행역 둘 중 하나도 전체구간에 포함되지 않습니다.");
    }

    public static AbstractStringAssert<?> 노선구간추가시_구간거리가_기존거리보다_같거나_길다면_에러(ExtractableResponse<Response> response) {
        return assertThat(response.jsonPath().getString("message")).isEqualTo(
            "추가할려는 구간이 기존 구간 길이와 같거나 더 깁니다.");
    }

    public static ListAssert<Object> 지하철_노선_조회시_구간포함_확인(long lineId, Long... sectionIds) {

        ExtractableResponse<Response> 지하철역_노선_단건_조회 = 지하철역_노선_단건_조회(lineId);

        return Assertions.assertThat(지하철역_노선_단건_조회.jsonPath().getList("stations.id"))
            .containsOnly(sectionIds[0].intValue(), sectionIds[1].intValue(), sectionIds[2].intValue());
    }

    public static ListAssert<Object> 지하철_노선_조회시_해당구간_불포함_확인(long lineId, long deleteSectionId) {

        ExtractableResponse<Response> 지하철역_노선_단건_조회 = 지하철역_노선_단건_조회(lineId);

        return Assertions.assertThat(지하철역_노선_단건_조회.jsonPath().getList("stations.id"))
            .isNotEmpty()
            .doesNotContain(Long.valueOf(deleteSectionId).intValue());
    }

    public static AbstractStringAssert<?> 삭제할_노선_구간이_하행종점역이_아닐경우_에러(ExtractableResponse<Response> response) {
        return assertThat(response.jsonPath().getString("message"))
            .isEqualTo("하행 종점역이 아니면 삭제할 수 없습니다.");
    }

    public static AbstractStringAssert<?> 삭제할_노선_구간_1개인경우_에러(ExtractableResponse<Response> response) {
        return assertThat(response.jsonPath().getString("message"))
            .isEqualTo("구간이 1개인 경우 삭제할 수 없습니다.");
    }

    public static ListAssert<Object> 지하철_노선_조회시_구간_id_순서_검사(long lineId, Long... sectionIds) {

        ExtractableResponse<Response> 지하철역_노선_단건_조회 = 지하철역_노선_단건_조회(lineId);

        return Assertions.assertThat(지하철역_노선_단건_조회.jsonPath().getList("stations.id"))
            .containsExactly(sectionIds[0].intValue(), sectionIds[1].intValue(),
                sectionIds[2].intValue());
    }
}
