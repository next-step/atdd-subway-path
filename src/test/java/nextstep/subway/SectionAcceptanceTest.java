package nextstep.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.CommonAcceptanceTest;
import nextstep.subway.common.LineRestAssuredCRUD;
import nextstep.subway.common.SectionRestAssuredCRUD;
import nextstep.subway.common.StationRestAssuredCRUD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends CommonAcceptanceTest {
    private static Long 강남역Id;
    private static Long 선릉역Id;
    private static Long 이호선Id;

    @BeforeEach
    void setUp() {
        ExtractableResponse<Response> lineResponse = LineRestAssuredCRUD.createLine("2호선", "bg-red-600");
        이호선Id = lineResponse.jsonPath().getLong("id");
        강남역Id = extractResponseId(StationRestAssuredCRUD.createStation("강남역"));
        선릉역Id = extractResponseId(StationRestAssuredCRUD.createStation("선릉역"));
    }

    Long extractResponseId(ExtractableResponse<Response> response) {
        return response.body().jsonPath().getLong("id");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 구간을 등록하면
     * Then 지하철 노선 조회 시 추가된 역을 조회할 수 있다.
     */
    @DisplayName("지하철 노선에 새로운 구간을 추가한다.")
    @Test
    void addSection() {
        //given & when
        ExtractableResponse<Response> addResponse = SectionRestAssuredCRUD.addSection(강남역Id, 선릉역Id, 10, 이호선Id);
        assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        //then
        ExtractableResponse<Response> line = LineRestAssuredCRUD.showLine(이호선Id);
        List<Long> ids = line.jsonPath().getList("stations.id", Long.class);

        assertThat(ids).containsExactly(강남역Id, 선릉역Id);
    }


    /**
     * Given 지하철 노선에 2개의 구간을 등록하고
     * When 노선에 등록된 역이 하행역인 구간을 등록하면
     * Then 400에러가 발생한다.
     */
    @Test
    @DisplayName("하행역이 지하철 노선에 등록된 역인 구간을 추가하면 400에러가 발생한다.")
    void addSectionExistDownStationException() {
        //given
        Long 삼성역Id = extractResponseId(StationRestAssuredCRUD.createStation("삼성역"));
        SectionRestAssuredCRUD.addSection(강남역Id, 선릉역Id, 7, 이호선Id);
        SectionRestAssuredCRUD.addSection(선릉역Id, 삼성역Id, 3, 이호선Id);

        //when
        ExtractableResponse<Response> addResponse = SectionRestAssuredCRUD.addSection(삼성역Id, 강남역Id, 4, 이호선Id);

        //then
        assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 하나의 구간을 등록하고
     * When 지하철 노선의 하행역과 새로운 구간의 상행역이 일치하지 않은 구간을 추가하면
     * Then 400에러가 발생한다.
     */
    @Test
    @DisplayName("지하철 노선의 하행역과 새로운 구간의 생행역이 일치하지 않는 구간을 등록하면 400에러가 발생한다.")
    void addSectionNotMatchException() {
        //given
        SectionRestAssuredCRUD.addSection(강남역Id, 선릉역Id, 7, 이호선Id);
        Long 잠실역Id = extractResponseId(StationRestAssuredCRUD.createStation("잠실역"));
        Long 삼성역Id = extractResponseId(StationRestAssuredCRUD.createStation("삼성역"));

        //when
        ExtractableResponse<Response> addResponse = SectionRestAssuredCRUD.addSection(잠실역Id, 삼성역Id, 10, 이호선Id);

        //then
        assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 하나의 구간을 등록하고
     * When 존재하지 않는 지하철역을 포함한 구간을 추가하면
     * Then 400에러가 발생한다.
     */
    @Test
    @DisplayName("존재하지 않는 지하철역을 포함한 구간을 추가하면 400에러가 발생한다.")
    void addSectionNotExistStationException() {
        //given
        SectionRestAssuredCRUD.addSection(강남역Id, 선릉역Id, 7, 이호선Id);
        Long 존재하지_않는_역ID = 10L;

        //when
        ExtractableResponse<Response> addResponse = SectionRestAssuredCRUD.addSection(선릉역Id, 존재하지_않는_역ID, 10, 이호선Id);

        //then
        assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 하나의 구간이 등록된 지하철 노선에 새로운 구간을 등록하고
     * When 마지막 구간을 제거하면
     * Then 지하철 노선에서 마지막 구간이 삭제된다.
     */
    @Test
    @DisplayName("지하철 노선에서 구간을 제거한다.")
    void deleteSection() {
        //given
        SectionRestAssuredCRUD.addSection(강남역Id, 선릉역Id, 7, 이호선Id);

        Long 삼성역Id = extractResponseId(StationRestAssuredCRUD.createStation("삼성역"));
        SectionRestAssuredCRUD.addSection(선릉역Id, 삼성역Id, 10, 이호선Id);

        //when
        ExtractableResponse<Response> deleteResponse = SectionRestAssuredCRUD.deleteSection(이호선Id, 삼성역Id);

        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        //then
        ExtractableResponse<Response> lineResponse = LineRestAssuredCRUD.showLine(이호선Id);
        List<Long> stationIds = lineResponse.jsonPath().getList("stations.id", Long.class);

        assertThat(stationIds).doesNotContain(삼성역Id);
    }

    /**
     * Given 하나의 구간이 등록된 지하철 노선에 새로운 구간을 등록하고
     * When 마지막 구간이 아닌 역을 제거하면
     * Then 500에러가 발생한다.
     */
    @Test
    @DisplayName("지하철 노선에서 마지막 구간이 아닌 역을 제거하면 500에러가 발생한다.")
    void deleteMiddleSectionException() {
        //given
        SectionRestAssuredCRUD.addSection(강남역Id, 선릉역Id, 7, 이호선Id);

        Long 삼성역Id = extractResponseId(StationRestAssuredCRUD.createStation("삼성역"));
        SectionRestAssuredCRUD.addSection(선릉역Id, 삼성역Id, 10, 이호선Id);

        //when
        ExtractableResponse<Response> deleteResponse = SectionRestAssuredCRUD.deleteSection(이호선Id, 선릉역Id);

        //then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 지하철 노선에 하나의 구간을 등록하고
     * When 마지막 구간을 제거하면
     * Then 500에러가 발생한다.
     */
    @Test
    @DisplayName("노선의 마지막 구간을 제거하면 500에러가 발생한다.")
    void deleteLastSectionException() {
        //given
        SectionRestAssuredCRUD.addSection(강남역Id, 선릉역Id, 7, 이호선Id);

        //when
        ExtractableResponse<Response> deleteResponse = SectionRestAssuredCRUD.deleteSection(이호선Id, 선릉역Id);

        //then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 지하철 노선에 하나의 구간을 등록하고
     * When 존재하지 않는 지하철역을 포함한 구간을 삭제하면
     * Then 400에러가 발생한다.
     */
    @Test
    @DisplayName("존재하지 않는 지하철역을 포함한 구간을 삭제하면 400에러가 발생한다.")
    void deleteSectionNotExistStationException() {
        //given
        SectionRestAssuredCRUD.addSection(강남역Id, 선릉역Id, 7, 이호선Id);

        Long 존재하지_않는_역Id = 10L;

        //when
        ExtractableResponse<Response> deleteResponse = SectionRestAssuredCRUD.deleteSection(이호선Id, 존재하지_않는_역Id);

        //then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 구간을 등록하고
     * When 기존 구간의 중간역을 갖는 구간을 추가하면
     * Then 지하철 노선 조회 시 추가된 역을 조회할 수 있다.
     */
    @Test
    @DisplayName("노선의 구간 중간에 신규 구간을 추가한다.")
    void addMiddleSection() {
        //given
        SectionRestAssuredCRUD.addSection(강남역Id, 선릉역Id, 10, 이호선Id);

        //when
        Long 역삼역Id = extractResponseId(StationRestAssuredCRUD.createStation("역삼역"));
        SectionRestAssuredCRUD.addSection(강남역Id, 역삼역Id, 3, 이호선Id);

        //then
        ExtractableResponse<Response> lineResponse = LineRestAssuredCRUD.showLine(이호선Id);
        List<String> stationNames = lineResponse.jsonPath().getList("stations.name", String.class);

        assertThat(stationNames).containsOnly("강남역", "역삼역", "선릉역");
    }


    /**
     * Given 지하철 노선에 2개의 구간을 등록하고
     * When 등록하려는 구간과 같은 구간을 추가하면
     * Then 400에러가 발생한다.
     */
    @Test
    @DisplayName("기존 구간과 같은 구간을 추가하면 400에러가 발생한다.")
    void addSameSectionException() {
        //given
        SectionRestAssuredCRUD.addSection(강남역Id, 선릉역Id, 10, 이호선Id);

        //when
        ExtractableResponse<Response> addResponse = SectionRestAssuredCRUD.addSection(강남역Id, 선릉역Id, 10, 이호선Id);

        //then
        assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 하나의 구간을 등록하고
     * When 등록한 구간보다 긴 구간을 추가하면
     * Then 400에러가 발생한다.
     */
    @Test
    @DisplayName("기존 구간보다 긴 구간을 추가하면 400에러가 발생한다.")
    void addLongerDistanceSectionException() {
        //given
        SectionRestAssuredCRUD.addSection(강남역Id, 선릉역Id, 10, 이호선Id);

        //when
        Long 역삼역Id = extractResponseId(StationRestAssuredCRUD.createStation("역삼역"));
        ExtractableResponse<Response> addResponse = SectionRestAssuredCRUD.addSection(강남역Id, 역삼역Id, 13, 이호선Id);

        //then
        assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 구간을 하나 등록하고
     * When 기존 구간의 앞에 구간을 추가하면
     * Then 지하철 노선 조회 시 추가된 역을 조회할 수 있다.
     */
    @Test
    @DisplayName("노선의 구간 앞에 신규 구간을 추가한다.")
    void addFirstSection() {
        //given
        SectionRestAssuredCRUD.addSection(강남역Id, 선릉역Id, 10, 이호선Id);

        //when
        Long 서초역Id = extractResponseId(StationRestAssuredCRUD.createStation("서초역"));
        SectionRestAssuredCRUD.addSection(서초역Id, 강남역Id, 4, 이호선Id);

        //then
        ExtractableResponse<Response> lineResponse = LineRestAssuredCRUD.showLine(이호선Id);
        List<String> stationNames = lineResponse.jsonPath().getList("stations.name", String.class);

        assertThat(stationNames).containsOnly("서초역", "강남역", "선릉역");
    }

    /**
     * Given 지하철 노선에 두개의 구간을 등록하고
     * When 노선에 하행역으로 등록된 역을 하행역으로 갖는 구간을 추가하면
     * Then 400에러가 발생한다.
     */
    @Test
    @DisplayName("신규 구간의 하행역을 하행역으로 갖는 구간이 노선에 이미 존재할 경우 에러가 발생한다.")
    void wrongSectionRequestException() {
        //given
        SectionRestAssuredCRUD.addSection(강남역Id, 선릉역Id, 10, 이호선Id);
        Long 삼성역Id = extractResponseId(StationRestAssuredCRUD.createStation("삼성역"));
        SectionRestAssuredCRUD.addSection(선릉역Id, 삼성역Id, 3, 이호선Id);

        //when
        Long 역삼역Id = extractResponseId(StationRestAssuredCRUD.createStation("역삼역"));
        ExtractableResponse<Response> addResponse = SectionRestAssuredCRUD.addSection(역삼역Id, 선릉역Id, 3, 이호선Id);

        //then
        assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 하나의 구간을 등록하고
     * When 상행역이 이미 노선에 존재하는 구간을 맨 앞에 추가하면
     * Then 400에러가 발생한다.
     */
    @Test
    @DisplayName("구간 맨 앞에 역 추가 시 신규 구간의 상행역이 노선에 이미 등록되어 있는 역이면 에러가 발생한다.")
    void firstSectionAlreadyExistException() {
        //given
        SectionRestAssuredCRUD.addSection(강남역Id, 선릉역Id, 10, 이호선Id);

        //when
        ExtractableResponse<Response> addResponse = SectionRestAssuredCRUD.addSection(선릉역Id, 강남역Id, 3, 이호선Id);

        //then
        assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
