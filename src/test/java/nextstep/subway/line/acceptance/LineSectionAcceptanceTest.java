package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.controller.dto.LineCreateRequest;
import nextstep.subway.line.controller.dto.SectionAddRequest;
import nextstep.subway.utils.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.helper.JsonPathUtils.getListPath;
import static nextstep.helper.JsonPathUtils.getLongPath;
import static nextstep.subway.station.acceptance.StationApiRequester.createStation;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 구간 관련 기능")
@AcceptanceTest
public class LineSectionAcceptanceTest {

    @DisplayName("지하철 노선 구간 등록")
    @Nested
    class 지하철_노선_구간_등록_테스트 {

        @DisplayName("유효한 구간정보가 주어지면")
        @Nested
        class Context_with_valid_section_data {

            long lineId;
            long upStationId;
            long downStationId;
            long newDownStationId;

            @BeforeEach
            void setup() {
                upStationId = getLongPath(createStation("수원역").body(), "id");
                downStationId = getLongPath(createStation("고색역").body(), "id");
                newDownStationId = getLongPath(createStation("오목천역").body(), "id");

                ExtractableResponse<Response> response = LineApiRequester.createLine(
                    new LineCreateRequest(
                        "수인분당선", "bg-yellow-600", upStationId, downStationId, 10
                    )
                );

                lineId = getLongPath(response.body(), "id");
            }

            /**
             * When 지하철 노선의 구간을 등록하면
             * Then 지하철 노선 조회 시 등록한 구간을 조회할 수 있다
             */
            @DisplayName("구간이 등록되고, 노선 조회 시 등록된 구간을 조회할 수 있다")
            @Test
            void addSection() {
                // when
                ExtractableResponse<Response> response = SectionApiRequester.addSectionToLineSuccess(
                    lineId,
                    new SectionAddRequest(downStationId, newDownStationId, 3)
                );

                assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

                // then
                ExtractableResponse<Response> lineResponse = LineApiRequester.getLineById(lineId);

                assertThat(
                    getListPath(lineResponse.body(), "stations.id", Long.class)
                ).contains(upStationId, downStationId, newDownStationId);
            }
        }

        @DisplayName("주어진 상행역이 기존 노선의 하행종점역이 아니면")
        @Nested
        class Context_up_station_id_is_not_before_down_station_id {

            long lineId;
            long notDownStationId;
            long downStationId;
            long newDownStationId;

            @BeforeEach
            void setup() {
                long upStationId = getLongPath(createStation("수원역").body(), "id");
                downStationId = getLongPath(createStation("고색역").body(), "id");
                newDownStationId = getLongPath(createStation("오목천역").body(), "id");

                ExtractableResponse<Response> response = LineApiRequester.createLine(
                    new LineCreateRequest(
                        "수인분당선", "bg-yellow-600", upStationId, downStationId, 10
                    )
                );

                notDownStationId = upStationId;
                lineId = getLongPath(response.body(), "id");
            }

            @DisplayName("400 상태코드와 함께 에러를 응답한다")
            @Test
            void will_return_400_status_code() {
                // when
                ExtractableResponse<Response> response = SectionApiRequester.addSectionToLine(
                    lineId,
                    new SectionAddRequest(notDownStationId, newDownStationId, 3)
                );

                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }
        }

        @DisplayName("주어진 하행역이 기존 노선에 이미 등록된 역이면")
        @Nested
        class Context_down_station_id_is_already_exist {

            long lineId;
            long alreadyExistStationId;
            long downStationId;

            @BeforeEach
            void setup() {
                long upStationId = getLongPath(createStation("수원역").body(), "id");
                downStationId = getLongPath(createStation("고색역").body(), "id");

                ExtractableResponse<Response> response = LineApiRequester.createLine(
                    new LineCreateRequest(
                        "수인분당선", "bg-yellow-600", upStationId, downStationId, 10
                    )
                );

                alreadyExistStationId = upStationId;
                lineId = getLongPath(response.body(), "id");
            }

            @DisplayName("400 상태코드와 함께 에러를 응답한다")
            @Test
            void will_return_400_status_code() {
                // when
                ExtractableResponse<Response> response = SectionApiRequester.addSectionToLine(
                    lineId,
                    new SectionAddRequest(downStationId, alreadyExistStationId, 3)
                );

                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }
        }
    }

    @DisplayName("지하철 노선 구간 삭제")
    @Nested
    class 지하철_노선_구간_삭제_테스트 {

        @DisplayName("해당 노선의 하행 종점역 ID가 주어지면")
        @Nested
        class Context_with_down_station_id {

            long lineId;
            long downStationId;
            long deleteStationId;

            @BeforeEach
            void setup() {
                long upStationId = getLongPath(createStation("수원역").body(), "id");
                downStationId = getLongPath(createStation("고색역").body(), "id");
                deleteStationId = getLongPath(createStation("오목천역").body(), "id");

                ExtractableResponse<Response> response = LineApiRequester.createLine(
                    new LineCreateRequest(
                        "수인분당선", "bg-yellow-600", upStationId, downStationId, 10
                    )
                );

                lineId = getLongPath(response.body(), "id");

                SectionApiRequester.addSectionToLineSuccess(
                    lineId,
                    new SectionAddRequest(downStationId, deleteStationId, 3)
                );
            }

            @DisplayName("구간이 삭제되어 노선조회 시 해당 역을 조회할 수 없다")
            @Test
            void delete_station() {
                // when
                ExtractableResponse<Response> response = SectionApiRequester.deleteSection(lineId, deleteStationId);

                assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

                // then
                ExtractableResponse<Response> lineResponse = LineApiRequester.getLineById(lineId);

                assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
                assertThat(
                    getListPath(lineResponse.body(), "stations.id", Long.class)
                ).noneMatch(id -> id.equals(deleteStationId));
            }
        }

        @DisplayName("주어진 역 ID가 해당 노선의 하행종점역이 아니면")
        @Nested
        class Context_with_not_down_station_id {

            long lineId;
            long notDownStationId;

            @BeforeEach
            void setup() {
                long upStationId = getLongPath(createStation("수원역").body(), "id");
                long downStationId = getLongPath(createStation("고색역").body(), "id");
                long newDownStationId = getLongPath(createStation("오목천역").body(), "id");

                ExtractableResponse<Response> response = LineApiRequester.createLine(
                    new LineCreateRequest(
                        "수인분당선", "bg-yellow-600", upStationId, downStationId, 10
                    )
                );

                lineId = getLongPath(response.body(), "id");

                SectionApiRequester.addSectionToLineSuccess(
                    lineId,
                    new SectionAddRequest(downStationId, newDownStationId, 3)
                );

                notDownStationId = downStationId;
            }

            @DisplayName("구간 제거에 실패한다")
            @Test
            void will_return_400_status_code() {
                // when
                ExtractableResponse<Response> response = SectionApiRequester.deleteSection(lineId, notDownStationId);

                //then
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }
        }

        @DisplayName("해당 노선에 상행 종점역과 하행 종점역만 있으면")
        @Nested
        class Context_line_has_only_up_station_and_down_station {

            long lineId;
            long downStationId;

            @BeforeEach
            void setup() {
                long upStationId = getLongPath(createStation("수원역").body(), "id");
                downStationId = getLongPath(createStation("고색역").body(), "id");

                ExtractableResponse<Response> response = LineApiRequester.createLine(
                    new LineCreateRequest(
                        "수인분당선", "bg-yellow-600", upStationId, downStationId, 10
                    )
                );

                lineId = getLongPath(response.body(), "id");
            }

            @DisplayName("구간 제거에 실패한다")
            @Test
            void will_return_400_status_code() {
                // when
                ExtractableResponse<Response> response = SectionApiRequester.deleteSection(lineId, downStationId);

                //then
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }
        }
    }
}
