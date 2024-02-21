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
                    Section 강남_양재_구간 = SectionFixture.강남_양재_구간(10, 이호선);

                    // when
                    이호선.addSection(강남_양재_구간);

                    // then
                    assertThat(이호선.getSortedAllSections()).containsAnyOf(강남_양재_구간);
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
                    삼성_선릉_구간 = SectionFixture.삼성_선릉_구간(10, 이호선);
                    선릉_역삼_구간 = SectionFixture.선릉_역삼_구간(10, 이호선);
                    역삼_강남_구간 = SectionFixture.역삼_강남_구간(10, 이호선);
                    강남_서초_구간 = SectionFixture.강남_서초_구간(10, 이호선);

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
                     * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있지 않으면서
                     * When  요청한 구간의 하행역이 기존 노선의 가장 앞쪽에 역으로 등록되어 있는 경우
                     * Then  지하철 구간 등록에 성공한다.
                     */
                    @Test
                    void 추가_요청한_구간의_상행역이_기존_노선의_역으로_등록되지_않은_역이면서_하행역은_등록된_역일_경우() {
                        // given
                        Section 사성_삼성_구간 = new Section(사성, 삼성, 5, 이호선);

                        // when
                        이호선.addSection(사성_삼성_구간);

                        // then
                        assertThat(이호선.getAllStations())
                                .containsOnly(사성, 삼성, 선릉, 역삼, 강남, 서초);

                        assertThat(이호선.getSortedAllSections())
                                .containsOnly(사성_삼성_구간, 삼성_선릉_구간, 선릉_역삼_구간, 역삼_강남_구간, 강남_서초_구간);
                    }

                    /**
                     * Given 지하철 노선이 생성되고, 구간을 추가한다.
                     * When  지하철 구간을 노선 처음에 등록할 때
                     * When  요청한 구간의 상행역이 기존 노선의 가장 앞쪽에 역으로 등록되어 있으면서
                     * When  요청한 구간의 하행역이 기존 노선의 역으로 등록되어 있지 않을 경우
                     * Then  지하철 구간 등록에 성공한다.
                     */
                    @Test
                    void 추가_요청한_구간의_상행역이_기존_노선의_역으로_등록되어_있고_하행역은_등록되지_않은_역일_경우() {
                        // given
                        Section 삼성_사성_구간 = new Section(삼성, 사성, 5, 이호선);
                        Section 사성_선릉_구간 = new Section(사성, 선릉, 5, 이호선);

                        // when
                        이호선.addSection(삼성_사성_구간);

                        // then
                        assertThat(이호선.getAllStations())
                                .containsOnly(삼성, 사성, 선릉, 역삼, 강남, 서초);

                        assertThat(이호선.getSortedAllSections())
                                .containsOnly(삼성_사성_구간, 사성_선릉_구간, 선릉_역삼_구간, 역삼_강남_구간, 강남_서초_구간);
                    }

                }

                @Nested
                class 노선_중간에_추가 {

                    /**
                     * Given 지하철 노선이 생성되고, 구간을 추가한다.
                     * When  지하철 구간을 노선 중간에 등록할 때
                     * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있지 않으면서
                     * When  요청한 구간의 하행역이 기존 노선의 역으로 등록되어 있는 경우
                     * Then  지하철 구간 등록에 성공한다.
                     */
                    @Test
                    void 추가_요청한_구간의_상행역이_기존_노선의_역으로_등록되어_있지_않고_하행역은_등록된_역일_경우() {
                        // given
                        Section 사성_역삼_구간 = new Section(사성, 역삼, 5, 이호선);
                        Section 선릉_사성_구간 = new Section(선릉, 사성, 5, 이호선);

                        // when
                        이호선.addSection(사성_역삼_구간);

                        // then
                        assertThat(이호선.getAllStations())
                                .containsOnly(삼성, 선릉, 사성, 역삼, 강남, 서초);

                        assertThat(이호선.getSortedAllSections())
                                .containsOnly(삼성_선릉_구간, 선릉_사성_구간, 사성_역삼_구간, 역삼_강남_구간, 강남_서초_구간);
                    }

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
                        Section 역삼_사성_구간 = new Section(역삼, 사성, 5, 이호선);
                        Section 사성_강남_구간 = new Section(사성, 강남, 5, 이호선);

                        // when
                        이호선.addSection(역삼_사성_구간);

                        // then
                        assertThat(이호선.getAllStations())
                                .containsOnly(삼성, 사성, 선릉, 역삼, 강남, 서초);

                        assertThat(이호선.getSortedAllSections())
                                .containsOnly(삼성_선릉_구간, 선릉_역삼_구간, 역삼_사성_구간, 사성_강남_구간, 강남_서초_구간);
                    }

                }

                @Nested
                class 노선_끝에_추가 {

                    /**
                     * Given 지하철 노선이 생성되고, 구간을 추가한다.
                     * When  지하철 구간을 노선 중간에 등록할 때
                     * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있지 않으면서
                     * When  요청한 구간의 하행역이 기존 노선의 역으로 등록되어 있는 경우
                     * Then  지하철 구간 등록에 성공한다.
                     */
                    @Test
                    void 추가_요청한_구간의_상행역이_기존_노선의_역으로_등록되어_있지_않고_하행역은_등록된_역일_경우() {
                        // given
                        Section 사성_서초_구간 = new Section(사성, 서초, 5, 이호선);
                        Section 강남_사성_구간 = new Section(강남, 사성, 5, 이호선);

                        // when
                        이호선.addSection(사성_서초_구간);

                        // then
                        assertThat(이호선.getAllStations())
                                .containsOnly(삼성, 선릉, 역삼, 강남, 사성, 서초);

                        assertThat(이호선.getSortedAllSections())
                                .containsOnly(삼성_선릉_구간, 선릉_역삼_구간, 역삼_강남_구간, 강남_사성_구간, 사성_서초_구간);
                    }

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
                        Section 서초_사성_구간 = new Section(서초, 사성, 5, 이호선);

                        // when
                        이호선.addSection(서초_사성_구간);

                        // then
                        assertThat(이호선.getAllStations())
                                .containsOnly(삼성, 사성, 선릉, 역삼, 강남, 서초);

                        assertThat(이호선.getSortedAllSections())
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
                삼성_선릉_구간 = SectionFixture.삼성_선릉_구간(10, 이호선);
                선릉_역삼_구간 = SectionFixture.선릉_역삼_구간(10, 이호선);
                역삼_강남_구간 = SectionFixture.역삼_강남_구간(10, 이호선);
                강남_서초_구간 = SectionFixture.강남_서초_구간(10, 이호선);

                이호선.addSection(삼성_선릉_구간);
                이호선.addSection(선릉_역삼_구간);
                이호선.addSection(역삼_강남_구간);
                이호선.addSection(강남_서초_구간);
            }

            @Nested
            class 공통 {

                /**
                 * Given 지하철 노선이 생성되고 구간이 추가된다.
                 * When  추가할 구간의 상행역과 하행역 중 하나라도 기존 노선에 등록되어 있지 않을 경우
                 * Then  지하철 노선에 구간이 추가되지 않는다.
                 */
                @Test
                void 추가할_구간의_상행역과_하행역_중_하나라도_기존_노선에_등록되지_않았을_경우() {
                    // given
                    Section 양재_사성_구간 = new Section(양재, 사성, 5, 이호선);

                    // when, then
                    assertThatExceptionOfType(IllegalArgumentException.class)
                            .isThrownBy(() -> {
                                이호선.addSection(양재_사성_구간);
                            })
                            .withMessageMatching("노선에 연결할 수 있는 상행역 혹은 하행역이 아닙니다.");
                }

                /**
                 * Given 지하철 노선이 생성되고 구간이 추가된다.
                 * When  상행역과 하행역이 동일한 구간을 추가하면
                 * Then  지하철 노선에 구간이 추가되지 않는다.
                 */
                @Test
                void 상행역과_하행역이_동일한_구간일_경우() {
                    // given
                    Section 역삼_역삼_구간 = new Section(역삼, 역삼, 5, 이호선);

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
                 * When  구간의 하행역을 기준으로 직후 역과의 거리가 1보다 작은 경우
                 * Then  지하철 노선에 구간이 추가되지 않는다.
                 */
                @Test
                void 추가할_구간의_하행역을_기준으로_직후_역과의_거리가_1보다_작은_경우() {
                    // given
                    Section 삼성_사성_구간 = new Section(삼성, 사성, 10, 이호선);

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
                 * When  구간의 하행역을 기준으로 직후 역과의 거리가 1보다 작은 경우
                 * Then  지하철 노선에 구간이 추가되지 않는다.
                 */
                @Test
                void 추가할_구간의_하행역을_기준으로_직후_역과의_거리가_1보다_작은_경우() {
                    // given
                    Section 역삼_사성_구간 = new Section(역삼, 사성, 10, 이호선);

                    // when, then
                    assertThatExceptionOfType(IllegalArgumentException.class)
                            .isThrownBy(() -> {
                                이호선.addSection(역삼_사성_구간);
                            })
                            .withMessageMatching("거리는 0보다 커야합니다.");
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
        Section 삼성_선릉_구간 = SectionFixture.삼성_선릉_구간(10, 이호선);

        이호선.addSection(삼성_선릉_구간);

        // when
        List<Station> 이호선_모든_역 = 이호선.getAllStations();

        // then
        assertThat(이호선_모든_역).containsOnly(
                삼성_선릉_구간.getUpStation(),
                삼성_선릉_구간.getDownStation());
    }

    @Nested
    class 지하철_구간_제거 {

        @Nested
        class 성공 {

            Section 삼성_선릉_구간;
            Section 선릉_역삼_구간;
            Section 역삼_강남_구간;
            Section 강남_서초_구간;

            @BeforeEach
            void 초기_노선에_구간_추가() {
                삼성_선릉_구간 = SectionFixture.삼성_선릉_구간(10, 이호선);
                선릉_역삼_구간 = SectionFixture.선릉_역삼_구간(10, 이호선);
                역삼_강남_구간 = SectionFixture.역삼_강남_구간(10, 이호선);
                강남_서초_구간 = SectionFixture.강남_서초_구간(10, 이호선);

                이호선.addSection(역삼_강남_구간); // New (역삼 - 강남)
                이호선.addSection(선릉_역삼_구간); // New (선릉 - 역삼) + 역삼 - 강남
                이호선.addSection(삼성_선릉_구간); // New (삼성 - 선릉) + 선릉 - 역삼 + 역삼 - 강남
                이호선.addSection(강남_서초_구간); // 삼성 - 선릉 + 선릉 - 역삼 + 역삼 - 강남 + New (강남 - 서초)
                
            }

            @Nested
            class 노선_상행역_삭제 {

                /**
                 * Given 지하철 노선이 생성되고, 지하철 구간을 추가한다.
                 * When  지하철 노선의 가장 앞쪽에 위치한 구간의 상행역을 삭제하는 경우
                 * Then  지하철 구간 삭제에 성공한다.
                 */
                @Test
                void 구간이_한개_이상_존재하고_가장_앞쪽의_위치한_구간의_상행역을_삭제하는_경우_삭제_성공() {
                    // when
                    이호선.delete(StationFixture.삼성);

                    // then
                    assertThat(이호선.getSortedAllSections())
                            .containsExactly(선릉_역삼_구간, 역삼_강남_구간, 강남_서초_구간);
                }

            }

            @Nested
            class 노선_중간역_삭제 {

                /**
                 * Given 지하철 노선이 생성되고, 지하철 구간을 추가한다.
                 * When  지하철 노선 중앙에 위치한 구간의 상행역을 삭제하는 경우
                 * Then  직전 역과 직후 역이 연결된 구간이 생성되고 지하철 구간 삭제에 성공한다.
                 */
                @Test
                void 구간이_한개_이상_존재하고_노선_중앙의_위치한_구간의_상행역_삭제하는_경우_삭제_성공() {
                    // given
                    Section 선릉_강남_구간 =  new Section(StationFixture.선릉, StationFixture.강남, 20, 이호선);

                    // when
                    이호선.delete(StationFixture.역삼);

                    // then
                    assertThat(이호선.getSortedAllSections())
                            .containsExactly(삼성_선릉_구간, 선릉_강남_구간, 강남_서초_구간);
                }

                /**
                 * Given 지하철 노선이 생성되고, 지하철 구간을 추가한다.
                 * When  지하철 노선 중앙에 위치한 구간의 하행역을 삭제하는 경우
                 * Then  직전 역과 직후 역이 연결된 구간이 생성되고 지하철 구간 삭제에 성공한다.
                 */
                @Test
                void 구간이_한개_이상_존재하고_노선_중앙의_위치한_구간의_하행역을_삭제하는_경우_삭제_성공() {
                    // given
                    Section 역삼_서초_구간 =  new Section(StationFixture.역삼, StationFixture.서초, 20, 이호선);
                    
                    // when
                    이호선.delete(StationFixture.강남);

                    // then
                    assertThat(이호선.getSortedAllSections())
                            .containsExactly(삼성_선릉_구간, 선릉_역삼_구간, 역삼_서초_구간);
                }
            }

            @Nested
            class 노선_하행역_삭제 {

                /**
                 * Given 지하철 노선이 생성되고, 지하철 구간을 추가한다.
                 * When  지하철 노선의 마지막 구간의 하행역을 삭제하는 경우
                 * Then  지하철 구간 삭제에 성공한다.
                 */
                @Test
                void 구간이_한개_이상_존재하고_하행_종점역을_삭제하는_경우_삭제_성공() {
                    // when
                    이호선.delete(StationFixture.서초);

                    // then
                    assertThat(이호선.getSortedAllSections())
                            .containsExactly(삼성_선릉_구간, 선릉_역삼_구간, 역삼_강남_구간);
                }

            }
        }

        @Nested
        class 실패 {

            /**
             * Given 지하철 노선이 생성되고, 지하철 구간을 추가한다.
             * When  노선의 특정 역을 삭제할 때, 구간이 한개만 존재할 경우
             * Then  지하철 구간 삭제에 실패한다.
             */
            @Test
            void 한개의_구간만_존재할_경우() {
                // given
                이호선.addSection(SectionFixture.삼성_선릉_구간(10, 이호선));

                // when, then
                assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> {
                            이호선.delete(선릉);
                        })
                        .withMessageMatching("구간이 최소 2개 이상일 경우에만 삭제할 수 있습니다.");
            }

            /**
             * Given 지하철 노선이 생성되고, 지하철 구간을 추가한다.
             * When  지하철 특정 역을 삭제할 때, 삭제할 역이 구간에 등록되어 있지 않을 경우
             * Then  지하철 구간 삭제에 실패한다.
             */
            @Test
            void 기존_노선_구간_목록에_구간이_존재하지_않을_경우_삭제_실패() {
                이호선.addSection(SectionFixture.삼성_선릉_구간(10, 이호선));
                이호선.addSection(SectionFixture.선릉_역삼_구간(10, 이호선));
                이호선.addSection(SectionFixture.역삼_강남_구간(10, 이호선));
                이호선.addSection(SectionFixture.강남_서초_구간(10, 이호선));

                assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> {
                            이호선.delete(사성);
                        })
                        .withMessageMatching("해당 역이 구간에 존재하지 않습니다.");
            }
        }
    }
}
