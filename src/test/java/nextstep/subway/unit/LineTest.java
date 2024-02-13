package nextstep.subway.unit;

import nextstep.config.fixtures.LineFixture;
import nextstep.config.fixtures.SectionFixture;
import nextstep.config.fixtures.StationFixture;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.config.fixtures.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("지하철 구간 엔티티")
class LineTest {

    Line 이호선;

    @BeforeEach
    void 초기_지하철_노선() {
        이호선 = LineFixture.이호선_생성();
    }

    @Nested
    class 지하철_구간_추가 {

        @Nested
        class 성공 {

            @Nested
            class 노선에_구간_없음 {
                /**
                 * Given 지하철 노선이 생성되고
                 * When  지하철 구간을 추가하면
                 * Then  지하철 노선에 구간이 추가된다.
                 */
                @Test
                void 지하철_구간_추가_성공() {
                    // given
                    Section 강남_양재_구간 = SectionFixture.강남_양재_구간(10);

                    // when
                    이호선.addSection(강남_양재_구간);

                    // then
                    assertThat(이호선.getAllSections()).containsAnyOf(강남_양재_구간);
                }
            }

            @Nested
            class 노선에_구간이_있음 {
                Section 삼성_선릉_구간;
                Section 선릉_역삼_구간;
                Section 역삼_강남_구간;
                Section 강남_서초_구간;

                @BeforeEach
                void 초기_노선에_구간_추가() {
                    삼성_선릉_구간 = SectionFixture.삼성_선릉_구간(10);
                    선릉_역삼_구간 = SectionFixture.선릉_역삼_구간(10);
                    역삼_강남_구간 = SectionFixture.역삼_강남_구간(10);
                    강남_서초_구간 = SectionFixture.강남_서초_구간(10);

                    이호선.addSection(삼성_선릉_구간);
                    이호선.addSection(선릉_역삼_구간);
                    이호선.addSection(역삼_강남_구간);
                    이호선.addSection(강남_서초_구간);
                }

                @Nested
                class 노선_처음에_추가 {

                    /**
                     * Given 지하철 노선이 생성되고, 구간을 추가한다.
                     * When  지하철 구간을 노선 처음에 등록할 때
                     * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있으면서
                     * When  요청한 구간의 하행역이 기존 노선의 역으로 등록되어 있지 않을 경우
                     * Then  지하철 구간 등록에 성공한다.
                     */
                    @Test
                    void 추가_요청한_구간의_상행역이_기존_노선의_역으로_등록되어_있고_하행역은_등록되지_않은_역일_경우() {
                        // given
                        Section 삼성_사성_구간 = new Section(삼성, 사성, 5);
                        Section 사성_선릉_구간 = new Section(사성, 선릉, 5);

                        // when
                        이호선.addSection(삼성_사성_구간);

                        // then
                        assertThat(이호선.getStations())
                                .containsOnly(삼성, 사성, 선릉, 역삼, 강남, 서초);

                        assertThat(이호선.getAllSections())
                                .containsOnly(삼성_사성_구간, 사성_선릉_구간, 선릉_역삼_구간, 역삼_강남_구간, 강남_서초_구간);
                    }

                }

                @Nested
                class 노선_중간에_추가 {

                    /**
                     * Given 지하철 노선이 생성되고, 구간을 추가한다.
                     * When  지하철 구간을 노선 중간에 등록할 때
                     * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있으면서
                     * When  요청한 구간의 하행역이 기존 노선의 역으로 등록되어 있지 않을 경우
                     * Then  지하철 구간 등록에 성공한다.
                     */
                    @Test
                    void 추가_요청한_구간의_상행역이_기존_노선의_역으로_등록되어_있고_하행역은_등록되지_않은_역일_경우() {
                        // given
                        Section 역삼_사성_구간 = new Section(역삼, 사성, 5);
                        Section 사성_강남_구간 = new Section(사성, 강남, 5);

                        // when
                        이호선.addSection(역삼_사성_구간);

                        // then
                        assertThat(이호선.getStations())
                                .containsOnly(삼성, 사성, 선릉, 역삼, 강남, 서초);

                        assertThat(이호선.getAllSections())
                                .containsOnly(삼성_선릉_구간, 선릉_역삼_구간, 역삼_사성_구간, 사성_강남_구간, 강남_서초_구간);
                    }

                }

                @Nested
                class 노선_끝에_추가 {

                    /**
                     * Given 지하철 노선이 생성되고, 구간을 추가한다.
                     * When  지하철 구간을 노선 끝에 등록할 때
                     * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있으면서
                     * When  요청한 구간의 하행역이 기존 노선의 역으로 등록되어 있지 않을 경우
                     * Then  지하철 구간 등록에 성공한다.
                     */
                    @Test
                    void 추가_요청한_구간의_상행역이_기존_노선의_역으로_등록되어_있고_하행역은_등록되지_않은_역일_경우() {
                        // given
                        Section 서초_사성_구간 = new Section(서초, 사성, 5);

                        // when
                        이호선.addSection(서초_사성_구간);

                        // then
                        assertThat(이호선.getStations())
                                .containsOnly(삼성, 사성, 선릉, 역삼, 강남, 서초);

                        assertThat(이호선.getAllSections())
                                .containsOnly(삼성_선릉_구간, 선릉_역삼_구간, 역삼_강남_구간, 강남_서초_구간, 서초_사성_구간);
                    }

                }
            }
        }

        @Nested
        class 실패 {

            Section 삼성_선릉_구간;
            Section 선릉_역삼_구간;
            Section 역삼_강남_구간;
            Section 강남_서초_구간;

            @BeforeEach
            void 초기_노선에_구간_추가() {
                삼성_선릉_구간 = SectionFixture.삼성_선릉_구간(10);
                선릉_역삼_구간 = SectionFixture.선릉_역삼_구간(10);
                역삼_강남_구간 = SectionFixture.역삼_강남_구간(10);
                강남_서초_구간 = SectionFixture.강남_서초_구간(10);

                이호선.addSection(삼성_선릉_구간);
                이호선.addSection(선릉_역삼_구간);
                이호선.addSection(역삼_강남_구간);
                이호선.addSection(강남_서초_구간);
            }

            @Nested
            class 공통 {

                /**
                 * Given 지하철 노선이 생성되고 구간이 추가된다.
                 * When  추가할 구간의 상행역이 기존 노선에 등록되어 있지 않을 경우
                 * Then  지하철 노선에 구간이 추가되지 않는다.
                 */
                @Test
                void 추가할_구간의_상행역이_기존_노선에_등록되지_않았을_경우() {
                    // given
                    Section 양재_사성_구간 = new Section(양재, 사성, 5);

                    // when, then
                    assertThatExceptionOfType(IllegalArgumentException.class)
                            .isThrownBy(() -> {
                                이호선.addSection(양재_사성_구간);
                            })
                            .withMessageMatching("추가할 구간의 상행역이 노선에 존재하지 않습니다.");
                }

                /**
                 * Given 지하철 노선이 생성되고 구간이 추가된다.
                 * When  상행역과 하행역이 동일한 구간을 추가하면
                 * Then  지하철 노선에 구간이 추가되지 않는다.
                 */
                @Test
                void 상행역과_하행역이_동일한_구간일_경우() {
                    // given
                    Section 역삼_역삼_구간 = new Section(역삼, 역삼, 5);

                    // when, then
                    assertThatExceptionOfType(IllegalArgumentException.class)
                            .isThrownBy(() -> {
                                이호선.addSection(역삼_역삼_구간);
                            })
                            .withMessageMatching("추가할 구간의 상행역과 하행역은 동일할 수 없습니다.");
                }
            }

            @Nested
            class 노선_처음에_추가 {

                /**
                 * Given 지하철 노선이 생성되고 구간이 추가된다.
                 * When  추가할 구간의 하행역이 기존 모든 구간의 역중 하나라도 같은 역이 존재할 경우
                 * Then  지하철 노선에 구간이 추가되지 않는다.
                 */
                @Test
                void 추가할_구간의_하행역과_기존_모든_구간의_역중_하나라도_같은_역이_존재할_경우() {
                    // given
                    Section 삼성_강남_구간 = new Section(삼성, 강남, 5);

                    // when, then
                    assertThatExceptionOfType(IllegalArgumentException.class)
                            .isThrownBy(() -> {
                                이호선.addSection(삼성_강남_구간);
                            })
                            .withMessageMatching("이미 노선에 추가된 하행역입니다.");
                }

                /**
                 * Given 지하철 노선이 생성되고 구간이 추가된다.
                 * When  구간의 하행역을 기준으로 직후 역과의 거리가 1보다 작은 경우
                 * Then  지하철 노선에 구간이 추가되지 않는다.
                 */
                @Test
                void 추가할_구간의_하행역을_기준으로_직후_역과의_거리가_1보다_작은_경우() {
                    // given
                    Section 삼성_사성_구간 = new Section(삼성, 사성, 10);

                    // when, then
                    assertThatExceptionOfType(IllegalArgumentException.class)
                            .isThrownBy(() -> {
                                이호선.addSection(삼성_사성_구간);
                            })
                            .withMessageMatching("거리는 0보다 커야합니다.");
                }
            }

            @Nested
            class 노선_중간에_추가 {

                /**
                 * Given 지하철 노선이 생성되고 구간이 추가된다.
                 * When  추가할 구간의 하행역이 기존 모든 구간의 역중 하나라도 같은 역이 존재할 경우
                 * Then  지하철 노선에 구간이 추가되지 않는다.
                 */
                @Test
                void 추가할_구간의_하행역과_기존_모든_구간의_역중_하나라도_같은_역이_존재할_경우() {
                    // given
                    Section 역삼_서초_구간 = new Section(역삼, 서초, 5);

                    // when, then
                    assertThatExceptionOfType(IllegalArgumentException.class)
                            .isThrownBy(() -> {
                                이호선.addSection(역삼_서초_구간);
                            })
                            .withMessageMatching("이미 노선에 추가된 하행역입니다.");
                }

                /**
                 * Given 지하철 노선이 생성되고 구간이 추가된다.
                 * When  구간의 하행역을 기준으로 직후 역과의 거리가 1보다 작은 경우
                 * Then  지하철 노선에 구간이 추가되지 않는다.
                 */
                @Test
                void 추가할_구간의_하행역을_기준으로_직후_역과의_거리가_1보다_작은_경우() {
                    // given
                    Section 역삼_사성_구간 = new Section(역삼, 사성, 10);

                    // when, then
                    assertThatExceptionOfType(IllegalArgumentException.class)
                            .isThrownBy(() -> {
                                이호선.addSection(역삼_사성_구간);
                            })
                            .withMessageMatching("거리는 0보다 커야합니다.");
                }
            }

            @Nested
            class 노선_끝에_추가 {

                /**
                 * Given 지하철 노선이 생성되고 구간이 추가된다.
                 * When  추가할 구간의 하행역이 기존 모든 구간의 역중 하나라도 같은 역이 존재할 경우
                 * Then  지하철 노선에 구간이 추가되지 않는다.
                 */
                @Test
                void 추가할_구간의_하행역과_기존_모든_구간의_역중_하나라도_같은_역이_존재할_경우() {
                    // when, then
                    Section 서초_삼성_구간 = new Section(서초, 삼성, 5);

                    assertThatExceptionOfType(IllegalArgumentException.class)
                            .isThrownBy(() -> {
                                이호선.addSection(서초_삼성_구간);
                            })
                            .withMessageMatching("이미 노선에 추가된 하행역입니다.");
                }
            }
        }
    }

    /**
     * Given 지하철 노선이 생성되고, 지하철 구간을 추가한다.
     * When  지하철 노선에 포함된 지하철 역을 조회할 경우
     * Then  모든 지하철 역이 조회된다.
     */
    @Test
    void 지하철_모든_구간의_역_조회() {
        // given
        Section 삼성_선릉_구간 = SectionFixture.삼성_선릉_구간(10);;

        이호선.addSection(삼성_선릉_구간);

        // when
        List<Station> 이호선_모든_역 = 이호선.getStations();

        // then
        assertThat(이호선_모든_역).containsOnly(
                삼성_선릉_구간.getUpStation(),
                삼성_선릉_구간.getDownStation());
    }

    @Nested
    class 지하철_구간_제거 {
        @Nested
        class 성공 {
            /**
             * Given 지하철 노선이 생성되고, 지하철 구간을 추가한다.
             * When  지하철 역을 삭제할 때, 삭제할 역이 기존 노선의 마지막 구간의 하행역과 동일할 경우
             * Then  특정 역이 하행역으로 추가된 구간이 삭제된다.
             */
            @Test
            void 삭제할_역이_기존_노선의_마지막_구간의_하행역과_동일할_경우_삭제_성공() {
                // given
                이호선.addSection(SectionFixture.삼성_선릉_구간(10));
                이호선.addSection(SectionFixture.선릉_역삼_구간(10));

                // when
                이호선.deleteSection(StationFixture.역삼);

                // then
                assertThat(이호선.getStations()).containsAnyOf(삼성, StationFixture.선릉);
            }
        }

        @Nested
        class 실패 {
            /**
             * Given 지하철 노선이 생성되고, 지하철 구간을 추가한다.
             * When  지하철 특정 역을 삭제할 때, 삭제할 역이 기존 노선 마지막 구간의 하행역과 동일하지 않을 경우
             * Then  특정 역이 하행역으로 추가된 구간이 삭제되지 않는다.
             */
            @Test
            void 기존_노선_구간_목록에_구간이_존재하지_않을_경우_삭제_실패() {
                assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> {
                            이호선.deleteSection(삼성);
                        })
                        .withMessageMatching("해당 노선에 구간이 존재하지 않습니다.");
            }

            /**
             * Given 지하철 노선이 생성되고, 지하철 구간을 추가한다.
             * When  지하철 특정 역을 삭제할 때, 삭제할 역이 기존 노선 마지막 구간의 하행역과 동일하지 않을 경우
             * Then  특정 역이 하행역으로 추가된 구간이 삭제되지 않는다.
             */
            @Test
            void 삭제할_역이_기존_노선_마지막_구간의_하행역과_동일하지_않을_경우_삭제_실패() {
                // given
                이호선.addSection(SectionFixture.삼성_선릉_구간(10));
                이호선.addSection(SectionFixture.선릉_역삼_구간(10));

                // when, then
                assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> {
                            이호선.deleteSection(삼성);
                        })
                        .withMessageMatching("마지막 구간의 하행역과 동일하지 않습니다.");
            }
        }
    }
}
