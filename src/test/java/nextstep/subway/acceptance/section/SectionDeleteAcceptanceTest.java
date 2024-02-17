package nextstep.subway.acceptance.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.annotation.AcceptanceTest;
import nextstep.subway.acceptance.line.LineApiRequester;
import nextstep.subway.acceptance.station.StationApiRequester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.SectionCreateRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.JsonPathUtil;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철노선 구간 삭제 관련 기능")
@AcceptanceTest
public class SectionDeleteAcceptanceTest {

    Long 잠실역id;
    Long 용산역id;
    Long 건대입구역id;
    Long 성수역id;
    Long 이호선id;

    @BeforeEach
    void setUp() {
        잠실역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall("잠실역"));
        용산역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall("용산역"));
        건대입구역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall("건대입구역"));
        성수역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall("성수역"));

        LineCreateRequest 이호선 = new LineCreateRequest("2호선", "green", 잠실역id, 용산역id, 10);
        이호선id = JsonPathUtil.getId(LineApiRequester.createLineApiCall(이호선));
    }

    /**
     * Given 지하철 구간을 등록하고
     * When 구간이 2개인 노선의 구간중 1개를 삭제하면
     * Then 삭제한 1개의 구간이 삭제된다
     */
    @DisplayName("지하철 노선 구간 삭제")
    @Test
    void deleteSection() {
        //given
        SectionCreateRequest request = new SectionCreateRequest(용산역id, 건대입구역id, 5);
        SectionApiRequester.generateSection(request, 이호선id);

        //when
        ExtractableResponse<Response> response = SectionApiRequester.deleteSection(이호선id, 건대입구역id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> 이호선 = LineApiRequester.findLineApiCall(이호선id);
        assertThat(getStationIds(이호선)).containsExactly(잠실역id, 용산역id);
    }

    /**
     * Given 지하철 구간을 등록하고
     * When 구간의 위치에 상관없이 구간을 삭제하면
     * Then 해당 구간이 삭제되고, 구간이 재배치 된다
     */
    @DisplayName("구간의 위치에 상관없이 삭제가 가능하다")
    @Test
    void deleteMiddleSection() {
        //given
        SectionCreateRequest 용산건대입구역 = new SectionCreateRequest(용산역id, 건대입구역id, 5);
        SectionApiRequester.generateSection(용산건대입구역, 이호선id);
        SectionCreateRequest 건대입구성수역 = new SectionCreateRequest(건대입구역id, 성수역id, 5);
        SectionApiRequester.generateSection(건대입구성수역, 이호선id);

        //when
        ExtractableResponse<Response> response = SectionApiRequester.deleteSection(이호선id, 건대입구역id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> 이호선 = LineApiRequester.findLineApiCall(이호선id);
        assertThat(getStationIds(이호선)).containsExactly(잠실역id, 용산역id, 성수역id);
    }

    /**
     * Given 지하철 구간을 등록하고
     * When 상행종점역이 제거될 경우
     * Then 상행종점역의 다음 역이 상행종점역이 된다
     */
    @DisplayName("상행종점역이 제거될 경우 상행종점역의 다음 역이 상행종점역이 된다")
    @Test
    void deleteUpFinalStation() {
        //given
        SectionCreateRequest request = new SectionCreateRequest(용산역id, 건대입구역id, 5);
        SectionApiRequester.generateSection(request, 이호선id);

        //when
        ExtractableResponse<Response> response = SectionApiRequester.deleteSection(이호선id, 잠실역id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> 이호선 = LineApiRequester.findLineApiCall(이호선id);
        Long downFinalStationId = getStationIds(이호선).get(0);
        assertThat(downFinalStationId).isEqualTo(용산역id);
    }

    /**
     * Given 지하철 구간을 등록하고
     * When 하행종점역이 제거될 경우
     * Then 하행종점역의 전 역이 하행종점역이 된다
     */
    @DisplayName("하행행종점역이 제거될 경우 하행종점역의 전 역이 하행종점역이 된다")
    @Test
    void deleteDownFinalStation() {
        //given
        SectionCreateRequest request = new SectionCreateRequest(용산역id, 건대입구역id, 5);
        SectionApiRequester.generateSection(request, 이호선id);

        //when
        ExtractableResponse<Response> response = SectionApiRequester.deleteSection(이호선id, 건대입구역id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> 이호선 = LineApiRequester.findLineApiCall(이호선id);
        Long downFinalStationId = getStationIds(이호선).get(getStationIds(이호선).size() - 1);
        assertThat(downFinalStationId).isEqualTo(용산역id);
    }

    /**
     * When 구간이 1개인 노선의 구간을 삭제하면
     * Then 예외가 발생한다
     */
    @DisplayName("구간이 1개인 노선의 구간은 삭제할 수 없다")
    @Test
    void deleteSectionException() {
        //when
        ExtractableResponse<Response> response = SectionApiRequester.deleteSection(이호선id, 잠실역id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asPrettyString()).isEqualTo("구간이 1개인 노선의 구간은 삭제할 수 없습니다.");
    }

    /**
     * When 존재하지 않는 역을 삭제하면
     * Then 예외가 발생한다
     */
    @DisplayName("노선에 존재하지 않는 역은 삭제할 수 없다")
    @Test
    void deleteNotExistsStation() {
        //given
        SectionCreateRequest request = new SectionCreateRequest(용산역id, 건대입구역id, 5);
        SectionApiRequester.generateSection(request, 이호선id);
        Long 역삼역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall("잠실역"));

        //when
        ExtractableResponse<Response> response = SectionApiRequester.deleteSection(이호선id, 역삼역id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asPrettyString()).isEqualTo("노선에 존재하지 않는 역은 삭제할 수 없습니다.");
    }

    private List<Long> getStationIds(ExtractableResponse<Response> findLine) {
        return JsonPathUtil.getList(findLine, "stations", StationResponse.class)
                .stream().map(StationResponse::getId).collect(Collectors.toList());
    }
}
