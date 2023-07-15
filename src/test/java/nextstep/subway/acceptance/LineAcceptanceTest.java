package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static nextstep.subway.utils.LineFixture.*;
import static nextstep.subway.utils.StationFixture.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.exception.LineDuplicationNameException;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.SectionDuplicationStationException;
import nextstep.subway.exception.SectionNotConnectingStationException;
import nextstep.subway.exception.SectionRemoveLastStationException;
import nextstep.subway.exception.SectionRemoveSizeException;
import nextstep.subway.exception.StationNotFoundException;
import nextstep.subway.utils.LineFixture;
import nextstep.subway.utils.StationFixture;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        var 신사역 = 지하철역_생성(StationFixture.신사역);
        var 논현역 = 지하철역_생성(StationFixture.신사역);

        // when
        var response = 지하철_노선_생성_요청(신분당선_이름, 신분당선_색상, 신사역.getId(), 논현역.getId(), 10);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("지하철 노선을 생성할 때 중복된 이름이 있으면 실패한다.")
    @Test
    void crateLineFail() {
        // given
        지하철_신분당선_노선_생성();

        // when
        var response = 지하철_노선_중복된_이름으로_생성_요청();

        // then
        중복된_이름으로_노선_생성이_실패됨(response);
    }

    @DisplayName("지하철 노선을 생성할 때 해당 지하철역이 없으면 실패한다.")
    @Test
    void crateLineFail2() {
        // given

        // when
        var response = 존재하지_않는_지하철역으로_지하철_노선_생성_요청();

        // then
        지하철역이_존재_하지_않아_노선_생성이_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void showLines() {
        // given
        var 신분당선 = 지하철_신분당선_노선_생성();
        var 경강선 = 지하철_경강선_노선_생성();

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_조회됨(response, 신분당선, 경강선);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void showLine() {
        // given
        var 신분당선 = 지하철_신분당선_노선_생성();

        // when
        var response = 지하철_노선_조회_요청(신분당선.getId());

        // then
        지하철_노선_조회됨(response, 신분당선);
    }

    @DisplayName("지하철 노선을 조회할 때 해당 지하철 노선이 없으면 실패한다.")
    @Test
    void showLineFail() {
        // given

        // when
        var response = 지하철_노선_조회_요청(1L);

        // then
        지하철_노선이_존재_하지_않아_실패됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        var 신분당선 = 지하철_신분당선_노선_생성();

        // when
        var response = 지하철_노선_수정_요청(신분당선.getId(), 경강선_이름, 경강선_색상);

        // then
        지하철_노선_수정됨(response, 신분당선);
    }

    @DisplayName("지하철 노선을 수정할 때 해당 지하철 노선이 없으면 실패한다.")
    @Test
    void updateLineFail() {
        // given

        // when
        var response = 지하철_노선_수정_요청(1L, 경강선_이름, 경강선_색상);

        // then
        지하철_노선이_존재_하지_않아_실패됨(response);
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        var 신분당선 = 지하철_신분당선_노선_생성();

        // when
        var response = 지하철_노선_삭제_요청(신분당선.getId());

        // then
        지하철_노선_삭제됨(response, 신분당선);
    }

    @DisplayName("지하철 노선에 구간을 등록한다.")
    @Test
    void createSection() {
        // given
        var 신분당선 = 지하철_신분당선_노선_생성();


        // when
        var response = 지하철_노선에_구간_생성_요청(신분당선);

        // then
        지하철_노선에_구간_생성됨(response);
    }

    @DisplayName("지하철 노선에 구간을 생성할 때 하행 종점역이 새로운 구간의 상행역이 아니면 실패한다.")
    @Test
    void createSectionFail() {
        // given
        var 신분당선 = 지하철_신분당선_노선_생성();

        // when
        var response = 지하철_노선_하행_종점역과_상행역이_다른_구간_생성_요청(신분당선);

        // then
        하행_종점역이_새로운_구간의_상행역이_아니면_실패됨(response);
    }

    @DisplayName("지하철 노선에 구간을 생성할 때 새로운 구간의 하행역이 중복된 역이라면 실패한다.")
    @Test
    void createSectionFail2() {
        // given
        var 신분당선 = 지하철_신분당선_노선_생성();

        // when
        var response = 지하철_노선에_하행역이_중복된_구간_생성_요청(신분당선);

        // then
        새로운_구간의_하행역이_중복된_역이면_실패됨(response);
    }

    @DisplayName("지하철 노선에 구간을 삭제한다.")
    @Test
    void deleteSection() {
        // given
        var 신분당선 = 지하철_구간을_포함한_노선_생성();

        // when
        var response = 지하철_노선의_마지막_구간_삭제_요청(신분당선);

        // then
        지하철_노선의_구간이_삭제됨(response, 신분당선);
    }

    @DisplayName("지하철 노선에 구간을 삭제할 떄 마지막 구간이 아닌 경우 실패한다.")
    @Test
    void deleteSectionFail() {
        // given
        var 신분당선 = 지하철_구간을_포함한_노선_생성();

        // when
        var response = 지하철_노선의_중간_구간_삭제_요청(신분당선);

        // then
        마지막_구간이_아닌_경우_실패됨(response);
    }

    @DisplayName("지하철 노선에 구간을 삭제할 때 구간이 1개인 경우 실패한다.")
    @Test
    void deleteSectionFail2() {
        // given
        var 신분당선 = 지하철_신분당선_노선_생성();

        // when
        var response = 지하철_노선의_마지막_구간_삭제_요청(신분당선);

        // then
        구간이_1개인_경우_실패됨(response);
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        지하철_노선_목록_조회됨(지하철_노선_목록_조회_요청(), 지하철_노선_리스폰_변환(response));
    }

    private ExtractableResponse<Response> 지하철_노선_중복된_이름으로_생성_요청() {
        return 지하철_노선_생성_요청(신분당선_이름, 신분당선_색상, 1L, 2L, 10);
    }

    private void 중복된_이름으로_노선_생성이_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getInt("status"))
            .isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message"))
            .isEqualTo(LineDuplicationNameException.message);
    }

    private ExtractableResponse<Response> 존재하지_않는_지하철역으로_지하철_노선_생성_요청() {
        return 지하철_노선_생성_요청(신분당선_이름, 신분당선_색상, 1L, 2L, 10);
    }

    private void 지하철역이_존재_하지_않아_노선_생성이_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.jsonPath().getInt("status"))
            .isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.jsonPath().getString("message"))
            .isEqualTo(StationNotFoundException.message);
    }

    private void 지하철_노선_목록_조회됨(ExtractableResponse<Response> response, LineResponse... lineResponses) {
        List<LineResponse> responses = response.jsonPath().getList(".", LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        IntStream.range(0, responses.size())
            .forEach(i -> 지하철_노선_조회됨(지하철_노선_조회_요청(responses.get(i).getId()), lineResponses[i]));
    }

    private void 지하철_노선_조회됨(ExtractableResponse<Response> response, LineResponse expected) {
        LineResponse actual = 지하철_노선_리스폰_변환(response);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private void 지하철_노선이_존재_하지_않아_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.jsonPath().getInt("status"))
            .isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.jsonPath().getString("message"))
            .isEqualTo(LineNotFoundException.message);
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response, LineResponse expected) {
        LineResponse findResponse = 지하철_노선_리스폰_변환(지하철_노선_조회_요청(expected.getId()));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findResponse).usingRecursiveComparison()
            .isNotEqualTo(expected);
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response, LineResponse expected) {
        List<String> lineNames = 지하철_노선_목록_조회_요청().jsonPath().getList("name", String.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(lineNames).doesNotContain(expected.getName());
    }

    private void 지하철_노선에_구간_생성됨(ExtractableResponse<Response> response) {
        LineResponse expected = 지하철_노선_리스폰_변환(response);
        ExtractableResponse<Response> findResponse = 지하철_노선_조회_요청(expected.getId());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        지하철_노선_조회됨(findResponse, expected);
    }

    private ExtractableResponse<Response> 지하철_노선_하행_종점역과_상행역이_다른_구간_생성_요청(LineResponse lineResponse) {
        var upStation = 지하철역_생성(StationFixture.신논현역);
        var downStation = 지하철역_생성(StationFixture.강남역);
        return LineFixture.지하철_노선의_구간_생성_요청(lineResponse.getId(), upStation.getId(), downStation.getId(), 10);
    }

    private ExtractableResponse<Response> 지하철_노선에_구간_생성_요청(LineResponse lineResponse) {
        var upStation = lineResponse.getStations().get(1);
        var downStation = 지하철역_생성(신논현역);
        return LineFixture.지하철_노선의_구간_생성_요청(lineResponse.getId(), upStation.getId(), downStation.getId(), 10);
    }

    private void 하행_종점역이_새로운_구간의_상행역이_아니면_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getInt("status"))
            .isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message"))
            .isEqualTo(SectionNotConnectingStationException.MESSAGE);
    }

    private void 새로운_구간의_하행역이_중복된_역이면_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getInt("status"))
            .isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message"))
            .isEqualTo(SectionDuplicationStationException.MESSAGE);
    }

    private ExtractableResponse<Response> 지하철_노선에_하행역이_중복된_구간_생성_요청(LineResponse lineResponse) {
        var upStation = lineResponse.getStations().get(1);
        var downStation = lineResponse.getStations().get(0);
        return LineFixture.지하철_노선의_구간_생성_요청(lineResponse.getId(), upStation.getId(), downStation.getId(), 10);
    }

    private void 지하철_노선의_구간이_삭제됨(ExtractableResponse<Response> response, LineResponse expected) {
        LineResponse actual = 지하철_노선_리스폰_변환(지하철_노선_조회_요청(expected.getId()));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(actual).usingRecursiveComparison()
            .isNotEqualTo(expected);
    }

    private ExtractableResponse<Response> 지하철_노선의_마지막_구간_삭제_요청(LineResponse lineResponse) {
        var stations = lineResponse.getStations();
        StationResponse removeStation = stations.get(stations.size() - 1);
        return 지하철_노선의_구간_삭제_요청(lineResponse.getId(), removeStation.getId());
    }

    private ExtractableResponse<Response> 지하철_노선의_중간_구간_삭제_요청(LineResponse response) {
        List<StationResponse> stations = response.getStations();
        StationResponse removeStation = stations.get(stations.size() - 2);
        return 지하철_노선의_구간_삭제_요청(response.getId(), removeStation.getId());
    }

    private void 구간이_1개인_경우_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getInt("status"))
            .isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message"))
            .isEqualTo(SectionRemoveSizeException.MESSAGE);
    }

    private void 마지막_구간이_아닌_경우_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getInt("status"))
            .isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message"))
            .isEqualTo(SectionRemoveLastStationException.MESSAGE);
    }
}
