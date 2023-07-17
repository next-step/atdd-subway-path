package nextstep.subway.fixture.then;

import static nextstep.subway.fixture.when.LineApiFixture.지하철역_노선_단건_조회;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;

public abstract class SectionThenFixture {

    public static AbstractStringAssert<?> 노선구간추가_상행역설정_오류_검사(ExtractableResponse<Response> response) {
        return assertThat(response.jsonPath().getString("message")).isEqualTo(
            "추가하고자 하는 구간의 상행역이, 노선의 하행종점역이 아닙니다.");
    }

    public static AbstractStringAssert<?> 노선구간추가_하행역이_이미존재할떄_오류_검사(ExtractableResponse<Response> response) {
        return assertThat(response.jsonPath().getString("message")).isEqualTo(
            "추가하고자 하는 구간의 하행역이 이미 구간에 존재합니다.");
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
}
