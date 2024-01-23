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
import subway.line.dto.LineCreateRequest;
import subway.section.SectionCreateRequest;
import subway.station.dto.StationResponse;
import nextstep.subway.utils.JsonPathUtil;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철노선 구간 관련 기능")
@AcceptanceTest
public class SectionAcceptanceTest {

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
     * When 노선에 구간을 등록하면
     * Then 노선을 조회 했을때 등록한 구간이 조회된다
     */
    @DisplayName("지하철 노선 구간 등록")
    @Test
    void generateSection() {
        //when
        SectionCreateRequest request = new SectionCreateRequest(용산역id, 건대입구역id, 5);

        ExtractableResponse<Response> response = SectionApiRequester.generateSection(request, 이호선id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        ExtractableResponse<Response> 이호선 = LineApiRequester.findLineApiCall(이호선id);
        assertThat(getStationIds(이호선)).containsExactly(잠실역id, 용산역id, 건대입구역id);
    }

    /**
     * When 등록할 구간의 상행역이 노선에 등록되어있는 하행종점역이 아닌 구간을 등록하면
     * Then 예외가 발생한다
     */
    @DisplayName("등록할 구간의 상행역이 노선에 등록되어있는 하행종점역이 아닌 경우 구간을 등록할 수 없다")
    @Test
    void generateSectionException() {
        //when
        SectionCreateRequest request = new SectionCreateRequest(건대입구역id, 성수역id, 5);

        ExtractableResponse<Response> response = SectionApiRequester.generateSection(request, 이호선id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asPrettyString()).isEqualTo("등록할 구간의 상행역이 노선에 등록되어있는 하행종점역이 아닌 경우 구간 등록이 불가능합니다.");
    }

    /**
     * When 등록할 구간의 하행역이 이미 해당 노선에 등록되어있으면
     * Then 예외가 발생한다
     */
    @DisplayName("이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없다.")
    @Test
    void generateAlreadySection() {
        //when
        SectionCreateRequest request = new SectionCreateRequest(용산역id, 잠실역id, 5);

        ExtractableResponse<Response> response = SectionApiRequester.generateSection(request, 이호선id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asPrettyString()).isEqualTo("이미 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없습니다.");
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
     * When 노선의 하행종점역이 아닌 구간을 삭제하면
     * Then 예외가 발생한다
     */
    @DisplayName("노선의 하행종점역이 아닌 구간은 삭제할 수 없다")
    @Test
    void deleteNotDownSection() {
        //given
        SectionCreateRequest request = new SectionCreateRequest(용산역id, 건대입구역id, 5);

        SectionApiRequester.generateSection(request, 이호선id);

        //when
        ExtractableResponse<Response> response = SectionApiRequester.deleteSection(이호선id, 용산역id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asPrettyString()).isEqualTo("노선의 하행종점역만 제거할 수 있습니다.");
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

    private List<Long> getStationIds(ExtractableResponse<Response> findLine) {
        return JsonPathUtil.getList(findLine, "stations", StationResponse.class)
                .stream().map(StationResponse::getId).collect(Collectors.toList());
    }
}
