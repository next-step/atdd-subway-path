package nextstep.subway.unit;

import nextstep.config.annotations.ApplicationTest;
import nextstep.config.fixtures.StationFixture;
import nextstep.subway.application.PathFinder;
import nextstep.subway.application.dto.PathResult;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ApplicationTest
public class PathFinderComponentTest {

    @Autowired
    private PathFinder pathFinder;

    @Nested
    class findShortestPath {

        @Nested
        class 사전_노선_설정됨 {

            Line 이호선;
            Line 신분당선;
            Line 삼호선;
            Line 사호선;

            List<Line> 모든_노선_목록;

            Long 이호선_번호;
            Long 신분당선_번호;
            Long 삼호선_번호;
            Long 사호선_번호;

            Station 교대;
            Station 강남;
            Station 양재;
            Station 남부터미널;
            Station 정왕;
            Station 오이도;
            Station 가산디지털단지;


            Long 교대역_번호;
            Long 강남역_번호;
            Long 양재역_번호;
            Long 남부터미널역_번호;
            Long 정왕역_번호;
            Long 오이도역_번호;
            Long 가산디지털단지역_번호;

            /**
             * 교대역    --- *2호선* ---   강남역
             * |                        |
             * *3호선*                   *신분당선*
             * |                        |
             * 남부터미널역  --- *3호선* ---   양재역
             * <p>
             * <p>
             * 오이도역 --- *4호선* --- 정왕역
             */
            @BeforeEach
            void 사전_노선_설정() {
                이호선_번호 = 1L;
                신분당선_번호 = 2L;
                삼호선_번호 = 3L;
                사호선_번호 = 3L;

                이호선 = new Line("이호선", "green");
                ReflectionTestUtils.setField(이호선, "id", 이호선_번호);

                신분당선 = new Line("신분당선", "red");
                ReflectionTestUtils.setField(신분당선, "id", 신분당선_번호);

                삼호선 = new Line("삼호선", "orange");
                ReflectionTestUtils.setField(삼호선, "id", 삼호선_번호);

                사호선 = new Line("사호선", "blue");
                ReflectionTestUtils.setField(사호선, "id", 사호선_번호);

                모든_노선_목록 = List.of(이호선, 신분당선, 삼호선, 사호선);

                교대역_번호 = 1L;
                강남역_번호 = 2L;
                양재역_번호 = 3L;
                남부터미널역_번호 = 4L;
                정왕역_번호 = 5L;
                오이도역_번호 = 6L;
                가산디지털단지역_번호 = 7L;

                교대 = StationFixture.교대;
                ReflectionTestUtils.setField(교대, "id", 교대역_번호);

                강남 = StationFixture.강남;
                ReflectionTestUtils.setField(강남, "id", 강남역_번호);

                양재 = StationFixture.양재;
                ReflectionTestUtils.setField(양재, "id", 양재역_번호);

                남부터미널 = StationFixture.남부터미널;
                ReflectionTestUtils.setField(남부터미널, "id", 남부터미널역_번호);

                정왕 = StationFixture.정왕;
                ReflectionTestUtils.setField(정왕, "id", 정왕역_번호);

                오이도 = StationFixture.오이도;
                ReflectionTestUtils.setField(오이도, "id", 오이도역_번호);

                가산디지털단지 = StationFixture.가산디지털단지;
                ReflectionTestUtils.setField(가산디지털단지, "id", 가산디지털단지역_번호);

                이호선.addSection(new Section(교대, 강남, 10, 이호선));
                신분당선.addSection(new Section(강남, 양재, 10, 신분당선));
                삼호선.addSection(new Section(교대, 남부터미널, 2, 삼호선));
                삼호선.addSection(new Section(남부터미널, 양재, 3, 삼호선));
                사호선.addSection(new Section(정왕, 오이도, 10, 사호선));
            }

            @Nested
            class 성공 {

                /**
                 * Given 지하철 노선 목록이 생성된다.
                 * When  출발역과 도착역을 통해 경로를 조회할 경우
                 * Then  최단거리의 존재하는 역 목록과 최단거리 값을 확인할 수 있다.
                 */
                @Test
                void 강남역에서_남부터미널역까지_경로_조회() {
                    // when
                    PathResult 경로_조회_결과 = pathFinder.calculateShortestPath(모든_노선_목록, 강남, 남부터미널);

                    // then
                    assertThat(경로_조회_결과).usingRecursiveComparison()
                            .isEqualTo(new PathResult(List.of(강남, 교대, 남부터미널), 12));
                }

                /**
                 * Given 지하철 노선 목록이 생성된다.
                 * When  출발역과 도착역을 통해 경로를 조회할 경우
                 * Then  최단거리의 존재하는 역 목록과 최단거리 값을 확인할 수 있다.
                 */
                @Test
                void 교대역에서_양재역까지_경로_조회() {
                    // when
                    PathResult 경로_조회_결과 = pathFinder.calculateShortestPath(모든_노선_목록, 교대, 양재);

                    //
                    assertThat(경로_조회_결과).usingRecursiveComparison()
                            .isEqualTo(new PathResult(List.of(교대, 남부터미널, 양재), 5));
                }

            }

            @Nested
            class 실패 {

                /**
                 * Given 지하철 노선 목록이 생성된다.
                 * When  출발역과 도착역을 통해 경로를 조회할 때,
                 * When  출발역과 도착역이 연결되어 있지 않을 경우
                 * Then   최단거리의 존재하는 역 목록과 최단거리 값을 확인할 수 있다.
                 */
                @Test
                void 강남역에서_오이도역까지_경로_조회() {
                    // when, then
                    assertThatExceptionOfType(IllegalArgumentException.class)
                            .isThrownBy(() -> {
                                pathFinder.calculateShortestPath(모든_노선_목록, 강남, 오이도);
                            })
                            .withMessageMatching("출발역과 도착역이 연결되어 있지 않습니다.");
                }

                /**
                 * Given 지하철 노선 목록이 생성된다.
                 * When  출발역과 도착역을 통해 경로를 조회할 때,
                 * When    출발역이 노선에 등록되어 있지 않을 경우
                 * Then  최단거리의 존재하는 역 목록과 최단거리 값을 확인할 수 있다.
                 */
                @Test
                void 강남역에서_가산디지털단지역까지_경로_조회() {
                    // when, then
                    assertThatExceptionOfType(IllegalArgumentException.class)
                            .isThrownBy(() -> {
                                pathFinder.calculateShortestPath(모든_노선_목록, 가산디지털단지, 강남);
                            })
                            .withMessageMatching("노선에 연결된 출발역이 아닙니다.");
                }

                /**
                 * Given 지하철 노선 목록이 생성된다.
                 * When  출발역과 도착역을 통해 경로를 조회할 때,
                 * When    도착역이 노선에 등록되어 있지 않을 경우
                 * Then  최단거리의 존재하는 역 목록과 최단거리 값을 확인할 수 있다.
                 */
                @Test
                void 가산디지털단지역에서_강남역까지_경로_조회() {
                    // when, then
                    assertThatExceptionOfType(IllegalArgumentException.class)
                            .isThrownBy(() -> {
                                pathFinder.calculateShortestPath(모든_노선_목록, 강남, 가산디지털단지);
                            })
                            .withMessageMatching("노선에 연결된 도착역이 아닙니다.");
                }
            }
        }

        @Nested
        class 사전_노선_설정_안됨 {

            Line 이호선;
            Line 신분당선;
            Line 삼호선;
            Line 사호선;

            List<Line> 모든_노선_목록;

            Long 이호선_번호;
            Long 신분당선_번호;
            Long 삼호선_번호;
            Long 사호선_번호;

            Station 교대;
            Station 강남;
            Station 양재;
            Station 남부터미널;
            Station 정왕;
            Station 오이도;

            Long 교대역_번호;
            Long 강남역_번호;
            Long 양재역_번호;
            Long 남부터미널역_번호;
            Long 정왕역_번호;
            Long 오이도역_번호;

            @BeforeEach
            void 사전_노선_설정() {
                이호선_번호 = 1L;
                신분당선_번호 = 2L;
                삼호선_번호 = 3L;
                사호선_번호 = 3L;

                이호선 = new Line("이호선", "green");
                ReflectionTestUtils.setField(이호선, "id", 이호선_번호);

                신분당선 = new Line("신분당선", "red");
                ReflectionTestUtils.setField(신분당선, "id", 신분당선_번호);

                삼호선 = new Line("삼호선", "orange");
                ReflectionTestUtils.setField(삼호선, "id", 삼호선_번호);

                사호선 = new Line("사호선", "blue");
                ReflectionTestUtils.setField(사호선, "id", 사호선_번호);

                모든_노선_목록 = List.of(이호선, 신분당선, 삼호선, 사호선);

                교대역_번호 = 1L;
                강남역_번호 = 2L;
                양재역_번호 = 3L;
                남부터미널역_번호 = 4L;
                정왕역_번호 = 5L;
                오이도역_번호 = 6L;

                교대 = StationFixture.교대;
                ReflectionTestUtils.setField(교대, "id", 교대역_번호);

                강남 = StationFixture.강남;
                ReflectionTestUtils.setField(강남, "id", 강남역_번호);

                양재 = StationFixture.양재;
                ReflectionTestUtils.setField(양재, "id", 양재역_번호);

                남부터미널 = StationFixture.남부터미널;
                ReflectionTestUtils.setField(남부터미널, "id", 남부터미널역_번호);

                정왕 = StationFixture.정왕;
                ReflectionTestUtils.setField(정왕, "id", 정왕역_번호);

                오이도 = StationFixture.오이도;
                ReflectionTestUtils.setField(오이도, "id", 오이도역_번호);
            }

            /**
             * Given 지하철 노선 목록이 생성된다.
             * When  출발역과 도착역을 통해 경로를 조회할 때,
             * When     출발역과 도착역이 연결되어 있지 않을 경우
             * Then  최단거리의 존재하는 역 목록과 최단거리 값을 확인할 수 있다.
             */
            @Test
            void 강남역에서_남부터미널역까지_경로_조회() {
                // when, then
                assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> {
                            pathFinder.calculateShortestPath(모든_노선_목록, 강남, 오이도);
                        })
                        .withMessageMatching("노선에 연결된 구간이 하나라도 존재해야 합니다.");
            }
        }
    }
}
