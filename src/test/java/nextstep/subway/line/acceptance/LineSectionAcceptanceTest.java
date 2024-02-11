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
import static nextstep.subway.line.acceptance.SectionApiRequester.addSectionToLine;
import static nextstep.subway.line.acceptance.SectionApiRequester.addSectionToLineSuccess;
import static nextstep.subway.station.acceptance.StationApiRequester.createStation;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 구간 관련 기능")
@AcceptanceTest
public class LineSectionAcceptanceTest {

    @DisplayName("지하철 노선 구간 등록")
    @Nested
    class 지하철_노선_구간_등록_테스트 {

        @DisplayName("유효한 구간 등록 정보가 주어지면")
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
            @DisplayName("구간 등록에 성공한다")
            @Test
            void addSection() {
                // when
                ExtractableResponse<Response> response = addSectionToLineSuccess(
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

        @DisplayName("주어진 상행역이 기존 노선에 존재하고")
        @Nested
        class Context_with_existing_up_station_id {

            @DisplayName("주어진 하행역이 노선에 등록되어 있지 않다면")
            @Nested
            class Context_with_not_existing_down_station_id {

                long lineId;
                long upStationId;
                long downStationId;
                long newDownStationId;

                @BeforeEach
                void setup() {
                    upStationId = getLongPath(createStation("수원역").body(), "id");
                    downStationId = getLongPath(createStation("오목천역").body(), "id");

                    newDownStationId = getLongPath(createStation("고색역").body(), "id");

                    ExtractableResponse<Response> response = LineApiRequester.createLine(
                        new LineCreateRequest(
                            "수인분당선", "bg-yellow-600", upStationId, downStationId, 10
                        )
                    );

                    lineId = getLongPath(response.body(), "id");
                }

                @DisplayName("구간 등록에 성공한다")
                @Test
                void add_section_in_middle() {
                    ExtractableResponse<Response> response = addSectionToLineSuccess(
                        lineId,
                        new SectionAddRequest(upStationId, newDownStationId, 3)
                    );

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

                    ExtractableResponse<Response> lineResponse = LineApiRequester.getLineById(lineId);

                    assertThat(
                        getListPath(lineResponse.body(), "stations.id", Long.class)
                    ).contains(upStationId, downStationId, newDownStationId);
                }
            }

            @DisplayName("주어진 하행역이 노선에 이미 등록되어 있다면")
            @Nested
            class Context_with_existing_down_station_id {

                long lineId;
                long upStationId;
                long existStationId;

                @BeforeEach
                void setup() {
                    upStationId = getLongPath(createStation("수원역").body(), "id");
                    long downStationId = getLongPath(createStation("고색역").body(), "id");
                    existStationId = downStationId;

                    // 수원역 - 고색역 구간생성
                    ExtractableResponse<Response> response = LineApiRequester.createLine(
                        new LineCreateRequest(
                            "수인분당선", "bg-yellow-600", upStationId, downStationId, 10
                        )
                    );
                    lineId = getLongPath(response.body(), "id");

                    long newDownStationId = getLongPath(createStation("오목천역").body(), "id");

                    // 고색역 - 오목천역 구간생성
                    addSectionToLineSuccess(
                        lineId,
                        new SectionAddRequest(downStationId, newDownStationId, 3)
                    );
                }

                @DisplayName("구간 등록에 실패한다")
                @Test
                void will_return_400_status_code() {
                    // 수원역 - 오목천역 구간을 생성하려고 하면
                    ExtractableResponse<Response> response = addSectionToLine(
                        lineId,
                        new SectionAddRequest(upStationId, existStationId, 3)
                    );

                    // 오목천역은 이미 있어서 예외 던져야 함
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }
            }
        }

        @DisplayName("노선 처음에 역을 추가하려고 할 때")
        @Nested
        class Context_up_station_id_is_not_before_down_station_id {

            @DisplayName("주어진 하행역이 기존 노선의 첫 역이면")
            @Nested
            class Context_with_down_station_id_is_first_up_station_id {

                long lineId;

                long newUpStationId;
                long newDownStationId;

                @BeforeEach
                void setup() {
                    newDownStationId = getLongPath(createStation("고색역").body(), "id");
                    long downStationId = getLongPath(createStation("오목천역").body(), "id");

                    newUpStationId = getLongPath(createStation("수원역").body(), "id");

                    ExtractableResponse<Response> response = LineApiRequester.createLine(
                        new LineCreateRequest(
                            "수인분당선", "bg-yellow-600", newDownStationId, downStationId, 10
                        )
                    );

                    lineId = getLongPath(response.body(), "id");
                }

                @DisplayName("구간 등록에 성공한다")
                @Test
                void will_return_201_created_status() {
                    ExtractableResponse<Response> response = addSectionToLineSuccess(
                        lineId,
                        new SectionAddRequest(newUpStationId, newDownStationId, 3)
                    );

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                }
            }

            @DisplayName("주어진 하행역이 기존 노선에 존재하지 않으면")
            @Nested
            class Context_with_down_station_id_is_not_exist_in_sections {

                long lineId;

                long newUpStationId;
                long notExistStationId;

                @BeforeEach
                void setup() {
                    long upStationId = getLongPath(createStation("고색역").body(), "id");
                    long downStationId = getLongPath(createStation("오목천역").body(), "id");

                    newUpStationId = getLongPath(createStation("수원역").body(), "id");
                    notExistStationId = getLongPath(createStation("망포역").body(), "id");

                    ExtractableResponse<Response> response = LineApiRequester.createLine(
                        new LineCreateRequest(
                            "수인분당선", "bg-yellow-600", upStationId, downStationId, 10
                        )
                    );

                    lineId = getLongPath(response.body(), "id");
                }

                @DisplayName("구간 등록에 실패한다")
                @Test
                void will_return_400_status_code() {
                    // when
                    ExtractableResponse<Response> response = SectionApiRequester.addSectionToLine(
                        lineId,
                        new SectionAddRequest(newUpStationId, notExistStationId, 3)
                    );

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }
            }
        }

        @DisplayName("노선 마지막에 역을 추가하려고 할 때")
        @Nested
        class Context_up_station_id_equals_last_sections_down_station_id {

            @DisplayName("주어진 하행역이 기존 노선에 이미 등록되어 있다면")
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

                @DisplayName("구간 등록에 실패한다")
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

        @DisplayName("이미 존재하는 구간이 주어지면")
        @Nested
        class Context_with_already_exist_section {

            long lineId;
            long upStationId;
            long downStationId;

            @BeforeEach
            void setup() {
                upStationId = getLongPath(createStation("수원역").body(), "id");
                downStationId = getLongPath(createStation("고색역").body(), "id");

                ExtractableResponse<Response> response = LineApiRequester.createLine(
                    new LineCreateRequest(
                        "수인분당선", "bg-yellow-600", upStationId, downStationId, 10
                    )
                );

                lineId = getLongPath(response.body(), "id");
            }

            @DisplayName("구간 등록에 실패한다")
            @Test
            void will_return_400_status_code() {
                // when
                ExtractableResponse<Response> response = SectionApiRequester.addSectionToLine(
                    lineId,
                    new SectionAddRequest(upStationId, downStationId, 3)
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

                addSectionToLineSuccess(
                    lineId,
                    new SectionAddRequest(downStationId, deleteStationId, 3)
                );
            }

            @DisplayName("노선의 맨 마지막 구간이 제거된다")
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

        @DisplayName("해당 노선의 상행 종점역 ID가 주어지면")
        @Nested
        class Context_with_up_station_id {

            long lineId;
            long 맨_앞_역_ID;

            @BeforeEach
            void setup() {
                long 수원역 = getLongPath(createStation("수원역").body(), "id");
                long 고색역 = getLongPath(createStation("고색역").body(), "id");
                long 오목천역 = getLongPath(createStation("오목천역").body(), "id");

                ExtractableResponse<Response> response = LineApiRequester.createLine(
                    new LineCreateRequest(
                        "수인분당선", "bg-yellow-600", 수원역, 고색역, 10
                    )
                );

                lineId = getLongPath(response.body(), "id");

                addSectionToLineSuccess(
                    lineId,
                    new SectionAddRequest(고색역, 오목천역, 3)
                );

                맨_앞_역_ID = 수원역;
            }

            @DisplayName("노선의 맨 앞 구간이 제거된다")
            @Test
            void delete_up_station() {
                ExtractableResponse<Response> response = SectionApiRequester.deleteSection(lineId, 맨_앞_역_ID);

                assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

                ExtractableResponse<Response> lineResponse = LineApiRequester.getLineById(lineId);

                assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
                assertThat(
                    getListPath(lineResponse.body(), "stations.id", Long.class)
                ).noneMatch(id -> id.equals(맨_앞_역_ID));
            }
        }

        @DisplayName("주어진 역 ID가 해당 노선 가운데 있으면")
        @Nested
        class Context_with_not_down_station_id {

            long lineId;
            long 중간_역_ID;

            @BeforeEach
            void setup() {
                long 수원역 = getLongPath(createStation("수원역").body(), "id");
                long 고색역 = getLongPath(createStation("고색역").body(), "id");
                long 오목천역 = getLongPath(createStation("오목천역").body(), "id");

                // 수원 - 고색
                ExtractableResponse<Response> response = LineApiRequester.createLine(
                    new LineCreateRequest(
                        "수인분당선", "bg-yellow-600", 수원역, 고색역, 10
                    )
                );

                lineId = getLongPath(response.body(), "id");

                // 고색 - 오목천
                addSectionToLineSuccess(
                    lineId,
                    new SectionAddRequest(고색역, 오목천역, 3)
                );

                중간_역_ID = 고색역;
            }

            @DisplayName("중간 구간이 제거된다")
            @Test
            void will_return_204_no_content() {
                ExtractableResponse<Response> response = SectionApiRequester.deleteSection(lineId, 중간_역_ID);

                assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

                ExtractableResponse<Response> lineResponse = LineApiRequester.getLineById(lineId);

                assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
                assertThat(
                    getListPath(lineResponse.body(), "stations.id", Long.class)
                ).noneMatch(id -> id.equals(중간_역_ID));
            }
        }

        @DisplayName("해당 노선에 하나의 구간만 있으면")
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
