package nextstep.subway.acceptance;

import io.restassured.response.ValidatableResponse;
import nextstep.marker.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.utils.AcceptanceTestUtils.getId;
import static nextstep.subway.utils.AcceptanceTestUtils.verifyResponseStatus;

@AcceptanceTest
public class PathAcceptanceTest extends PathAcceptanceTestHelper {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;


    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    void setUp() {

        교대역 = getId(createStation("교대역"));
        강남역 = getId(createStation("강남역"));
        양재역 = getId(createStation("양재역"));
        남부터미널역 = getId(createStation("남부터미널역"));

        이호선 = getId(createLines("2호선", "green", 교대역, 강남역, 10L));
        신분당선 = getId(createLines("신분당선", "red", 강남역, 양재역, 10L));
        삼호선 = getId(createLines("3호선", "orange", 교대역, 남부터미널역, 2L));

        createSection(삼호선, 남부터미널역, 양재역, 3);
    }

    @Nested
    class Success {

        @Test
        void 교대역에서_양재역을_가는_최단_경로는_교대_남부터미널_양재_5미터이다() {
            // when
            ValidatableResponse pathResponse = getPath(교대역, 양재역);

            // then
            verifyFoundPath(pathResponse, 5L, "교대역", "남부터미널역", "양재역");
        }

        @Test
        void 강남역에서_남부터미널역을_가는_최단_경로는_강남_양재_남부터미널_13미터이다() {
            // when
            ValidatableResponse pathResponse = getPath(강남역, 남부터미널역);

            // then
            verifyFoundPath(pathResponse, 13L, "강남역", "양재역", "남부터미널역");
        }

        @Test
        void 강남역에서_양재역을_가는_최단_경로는_강남_양재_2미터이다() {
            // when
            ValidatableResponse pathResponse = getPath(강남역, 양재역);

            // then
            verifyFoundPath(pathResponse, 2L, "강남역", "양재역");
        }

    }

    @Nested
    class Fail {

        @Test
        void 출발역과_도착역이_같은_경우() {
            // when
            ValidatableResponse pathResponse = getPath(강남역, 강남역);

            // then
            verifyResponseStatus(pathResponse, HttpStatus.BAD_REQUEST);
        }

        @Test
        void 출발역과_도착역이_연결되지_않은_경우() {
            // when
            Long 까치산역 = getId(createStation("까치산역"));
            ValidatableResponse pathResponse = getPath(까치산역, 강남역);

            // then
            verifyResponseStatus(pathResponse, HttpStatus.BAD_REQUEST);
        }

        @Test
        void 존재하지_않은_출발역이나_도착역을_조회_할_경우() {
            // when
            ValidatableResponse pathResponse = getPath(10000L, 강남역);

            // then
            verifyResponseStatus(pathResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
