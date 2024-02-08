package nextstep.subway.acceptance;


import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.common.Line;
import nextstep.subway.acceptance.common.Section;
import nextstep.subway.acceptance.common.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import nextstep.subway.common.exception.ErrorMessage;
import nextstep.subway.acceptance.common.util.AcceptanceTest;
import nextstep.subway.interfaces.line.dto.LineResponse;
import nextstep.subway.interfaces.station.dto.StationResponse;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 구간 관련 기능")
@AcceptanceTest
public class LineSectionAcceptanceTest {

    StationResponse A역;
    StationResponse B역;
    StationResponse C역;
    StationResponse D역;
    StationResponse E역;
    LineResponse A역_B역_노선;
    Section.RequestBody B역_C역_구간;
    Section.RequestBody C역_D역_구간;
    Section.RequestBody D역_B역_구간;
    Section.RequestBody B역_A역_구간;

    @BeforeEach
    void setUpFixture() {
        A역 = Station.랜덤역생성();
        B역 = Station.랜덤역생성();
        C역 = Station.랜덤역생성();
        D역 = Station.랜덤역생성();
        E역 = Station.랜덤역생성();

        A역_B역_노선 = Line.노선생성(A역.getId(), B역.getId());

        B역_C역_구간 = Section.REQUEST_BODY(B역.getId(), C역.getId());
        C역_D역_구간 = Section.REQUEST_BODY(C역.getId(), D역.getId());
        D역_B역_구간 = Section.REQUEST_BODY(D역.getId(), B역.getId());
        B역_A역_구간 = Section.REQUEST_BODY(B역.getId(), A역.getId());
    }

    /**
     * When  A역-B역 지하철 노선에 B역-C역 구간을 등록하면
     * Then B역-C역 구간이 등록 된다 (201)
     * Then A역-B역 노선의 하행역이 C역으로 변경되어야 한다
     */
    @DisplayName("지하철 노선에 구간을 등록한다.")
    @Test
    void enrollSectionToLine() {
        // When
        ExtractableResponse<Response> response = Section.Api.createBy(A역_B역_노선.getId(), B역_C역_구간);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        //Then
        A역_B역_노선 = Line.Api.retrieveLineBy(A역_B역_노선.getId()).as(LineResponse.class);
        StationResponse A역_B역_마지막역 = A역_B역_노선.getStations().get(A역_B역_노선.getStations().size()-1);
        assertThat(A역_B역_마지막역.getId()).isEqualTo(C역.getId());
        assertThat(A역_B역_마지막역.getName()).isEqualTo(C역.getName());
    }

    /**
     * When A역-B역 노선에 B역-A역 구간을 등록하면
     * Then 구간등록에 실패한다 (409)
     * Then 실패 메시지는 "이미 포함된 하행역" 이어야 한다.
     */
    @DisplayName("구간의 하행역이 이미 노선에 포함되어있는 경우 등록을 실패한다.")
    @Test
    void failEnrollSectionToLineWithAlreadyExist() {
        // When
        ExtractableResponse<Response> response = Section.Api.createBy(A역_B역_노선.getId(), B역_A역_구간);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());

        // Then
        assertFail(response, ErrorMessage.이미_포함된_하행역);
    }

    /**
     * When A역-B역 노선에 C역-D역 구간을 등록하면
     * Then 구간 등록에 실패한다 (400)
     * Then 실패 메시지는 "잘못된 상행역" 이어야 한다.
     */
    @DisplayName("새로운 구간의 상행역과 기존 노선의 하행역이 같지 않으면 등록을 실패한다.")
    @Test
    void failEnrollSectionToLineWithInvalidUpStation() {
        // When
        ExtractableResponse<Response> response = Section.Api.createBy(A역_B역_노선.getId(), C역_D역_구간);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        // Then
        assertFail(response, ErrorMessage.잘못된_상행역);
    }

    /**
     * Given A역-B역 노선에 B역-C역 구간을 등록하고
     * When 하행역이 C역인 구간을 삭제 요청하면
     * Then B역-C역 구간이 삭제된다 (204)
     * Then A역-B역 노선의 새로운 하행역은 B역이 되어야 한다
     */
    @DisplayName("지하철 노선 구간을 삭제한다.")
    @Test
    void deleteLineSection() {
        // Given
        Section.Api.createBy(A역_B역_노선.getId(), B역_C역_구간);

        // When
        ExtractableResponse<Response> response = Section.Api.deleteBy(A역_B역_노선.getId(), C역.getId());

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        //Then
        A역_B역_노선 = Line.Api.retrieveLineBy(A역_B역_노선.getId()).as(LineResponse.class);
        StationResponse A역_B역_마지막역 = A역_B역_노선.getStations().get(A역_B역_노선.getStations().size()-1);
        assertThat(A역_B역_마지막역.getId()).isEqualTo(B역.getId());
        assertThat(A역_B역_마지막역.getName()).isEqualTo(B역.getName());
    }

    /**
     * Given A역-B역 노선에 B역-C역 구간을 등록하고
     * When 하행역이 B역인 구간을 삭제 요청하면
     * Then 구간 삭제를 실패한다 (400)
     * Then 실패 메시지는 "잘못된 하행역" 이어야 한다.
     */
    @DisplayName("삭제할 구간이 노선의 마지막 구간이 아닌 경우 삭제를 실패한다.")
    @Test
    void failDeleteLineSectionWithInvalidDownStation() {
        // Given
        Section.Api.createBy(A역_B역_노선.getId(), B역_C역_구간);

        // When
        ExtractableResponse<Response> response = Section.Api.deleteBy(A역_B역_노선.getId(), B역.getId());

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        //Then
        assertFail(response, ErrorMessage.잘못된_하행역);
    }

    /**
     * When A역-B역 노선에서 하행역이 B역인 구간을 삭제 요청하면
     * Then 구간 삭제를 실패한다 (400)
     * Then 실패 메시지는 "유일한 구간" 이어야 한다.
     */
    @DisplayName("삭제할 구간이 노선의 유일한 구간인 경우 삭제를 실패한다.")
    @Test
    void failDeleteLineSectionWithLastSection() {
        // When
        ExtractableResponse<Response> response = Section.Api.deleteBy(A역_B역_노선.getId(), B역.getId());

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        //Then
        assertFail(response, ErrorMessage.유일한구간);
    }

    private void assertFail(ExtractableResponse<Response> response, String message) {
        String failMessage = response.jsonPath().getString("message");
        assertThat(failMessage).isEqualTo(message);
    }
}
