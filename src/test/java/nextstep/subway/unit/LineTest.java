package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {

        // given
        String 일호선 = "1호선";
        String 파란색 = "파란색";
        String 동암역 = "동암역";
        String 부평역 = "부평역";

        Line 일호선_노선 = new Line(
                일호선,
                파란색
        );

        // when
        일호선_노선.addSection(
                new Station(동암역),
                new Station(부평역),
                10
        );

        // then
        int 일호선_구간_개수 = 일호선_노선.getSections()
                              .size();

        String 일호선_첫번째_구간의_상행역 = 일호선_노선.getSections()
                                       .get(0)
                                       .getUpStation()
                                       .getName();
        String 일호선_첫번째_구간의_하행역 = 일호선_노선.getSections()
                                       .get(0)
                                       .getDownStation()
                                       .getName();

        int 일호선_첫번째_구간의_거리 = 일호선_노선.getSections()
                                   .get(0)
                                   .getDistance();

        assertAll(
                () -> assertThat(일호선_구간_개수).isEqualTo(1),
                () -> assertThat(일호선_첫번째_구간의_상행역).isEqualTo(동암역),
                () -> assertThat(일호선_첫번째_구간의_하행역).isEqualTo(부평역),
                () -> assertThat(일호선_첫번째_구간의_거리).isEqualTo(10)
        );
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        Station 동암역 = new Station("동암역");
        Station 부평역 = new Station("부평역");
        String 일호선 = "1호선";
        String 파란섹 = "파란색";


        Line 일호선_라인 = new Line(
                일호선,
                파란섹,
                동암역,
                부평역,
                10
        );

        //when
        List<Station> 실제_일호선_역_목록 = 일호선_라인.getStations();

        // then
        int 실제_일호선_역_개수 = 실제_일호선_역_목록.size();

        assertAll(
                () -> assertThat(실제_일호선_역_개수).isEqualTo(2),
                () -> assertThat(실제_일호선_역_목록).usingRecursiveComparison()
                                             .isEqualTo(Arrays.asList(
                                                     동암역,
                                                     부평역
                                             ))
        );
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {

        Station 동암역 = new Station("동암역");
        Station 부평역 = new Station("부평역");
        Station 주안역 = new Station("주안역");
        Station 백운역 = new Station("백운역");
        String 일호선 = "1호선";
        String 파란섹 = "파란색";


        Line 일호선_라인 = new Line(
                일호선,
                파란섹,
                동암역,
                부평역,
                10
        );

        일호선_라인.addSection(
                부평역,
                주안역,
                5
        );

        일호선_라인.addSection(
                주안역,
                백운역,
                7
        );

        // when
        일호선_라인.removeSection();

        // then
        int 실제_일호선_역_개수 = 일호선_라인.getStations()
                                .size();
        assertThat(실제_일호선_역_개수).isEqualTo(3);
    }

    @DisplayName("구간 추가 - 상행역이 같은 경우 / 새로운 구간의 거리가 작은 경우")
    @Test
    void addSection2() {

        // given
        String 일호선 = "일호선";
        String 노란색 = "노란색";
        Station A역 = new Station("A역");
        Station B역 = new Station("B역");
        Station C역 = new Station("C역");

        Line 일호선_라인 = new Line(
                일호선,
                노란색,
                A역,
                C역,
                7
        );

        // when
        일호선_라인.addSection(
                new Section(
                        일호선_라인,
                        A역,
                        B역,
                        4
                )
        );

        // then
        int AB구간_거리 = 일호선_라인.getSections()
                            .get(0)
                            .getDistance();
        int BC구간_거리 = 일호선_라인.getSections()
                            .get(1)
                            .getDistance();


        assertAll(
                () -> assertThat(일호선_라인.getStations()).usingRecursiveComparison()
                                                      .isEqualTo(Arrays.asList(
                                                              A역,
                                                              B역,
                                                              C역
                                                      )),
                () -> assertThat(AB구간_거리).isEqualTo(4),
                () -> assertThat(BC구간_거리).isEqualTo(3)
        );

    }

    @DisplayName("구간 추가 - 상행역이 같은 경우 / 기존 구간의 거리가 작은 경우")
    @Test
    void addSection3() {

        // given
        String 일호선 = "일호선";
        String 노란색 = "노란색";
        Station A역 = new Station("A역");
        Station B역 = new Station("B역");
        Station C역 = new Station("C역");

        Line 일호선_라인 = new Line(
                일호선,
                노란색,
                A역,
                B역,
                4
        );

        // when
        일호선_라인.addSection(
                new Section(
                        일호선_라인,
                        A역,
                        C역,
                        7
                )
        );

        // then
        int AB구간_거리 = 일호선_라인.getSections()
                            .get(0)
                            .getDistance();
        int BC구간_거리 = 일호선_라인.getSections()
                            .get(1)
                            .getDistance();


        assertAll(
                () -> assertThat(일호선_라인.getStations()).usingRecursiveComparison()
                                                      .isEqualTo(Arrays.asList(
                                                              A역,
                                                              B역,
                                                              C역
                                                      )),
                () -> assertThat(AB구간_거리).isEqualTo(4),
                () -> assertThat(BC구간_거리).isEqualTo(3)
        );
    }

    @DisplayName("구간 추가 - 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection4() {

        // given
        String 일호선 = "일호선";
        String 노란색 = "노란색";
        Station A역 = new Station("A역");
        Station B역 = new Station("B역");
        Station C역 = new Station("C역");

        Line 일호선_라인 = new Line(
                일호선,
                노란색,
                A역,
                C역,
                7
        );

        // when
        일호선_라인.addSection(
                new Section(
                        일호선_라인,
                        B역,
                        A역,
                        4
                )
        );

        // then
        int BA구간_거리 = 일호선_라인.getSections()
                            .get(0)
                            .getDistance();
        int AC구간_거리 = 일호선_라인.getSections()
                            .get(1)
                            .getDistance();


        assertAll(
                () -> assertThat(일호선_라인.getStations()).usingRecursiveComparison()
                                                      .isEqualTo(Arrays.asList(
                                                              B역,
                                                              A역,
                                                              C역
                                                      )),
                () -> assertThat(BA구간_거리).isEqualTo(4),
                () -> assertThat(AC구간_거리).isEqualTo(7)
        );
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSection5() {
        // given
        String 일호선 = "일호선";
        String 노란색 = "노란색";
        Station A역 = new Station("A역");
        Station B역 = new Station("B역");
        Station C역 = new Station("C역");

        Line 일호선_라인 = new Line(
                일호선,
                노란색,
                A역,
                C역,
                7
        );

        // when
        일호선_라인.addSection(
                new Section(
                        일호선_라인,
                        C역,
                        B역,
                        3
                )
        );

        // then
        int AC구간_거리 = 일호선_라인.getSections()
                            .get(0)
                            .getDistance();
        int CB구간_거리 = 일호선_라인.getSections()
                            .get(1)
                            .getDistance();


        assertAll(
                () -> assertThat(일호선_라인.getStations()).usingRecursiveComparison()
                                                      .isEqualTo(Arrays.asList(
                                                              A역,
                                                              C역,
                                                              B역
                                                      )),
                () -> assertThat(AC구간_거리).isEqualTo(7),
                () -> assertThat(CB구간_거리).isEqualTo(3)
        );

    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addSection6() {
        // given
        String 일호선 = "일호선";
        String 노란색 = "노란색";
        Station A역 = new Station("A역");
        Station B역 = new Station("B역");
        Station C역 = new Station("C역");

        Line 일호선_라인 = new Line(
                일호선,
                노란색,
                A역,
                C역,
                7
        );

        // when, then
        assertThatThrownBy(() -> 일호선_라인.addSection(
                new Section(
                        일호선_라인,
                        B역,
                        C역,
                        7
                )
        )).isInstanceOf(RuntimeException.class);

    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSection7() {
        // given
        String 일호선 = "일호선";
        String 노란색 = "노란색";
        Station A역 = new Station("A역");
        Station B역 = new Station("B역");
        Station C역 = new Station("C역");

        Line 일호선_라인 = new Line(
                일호선,
                노란색,
                A역,
                B역,
                4
        );

        일호선_라인.addSection(
                new Section(
                        일호선_라인,
                        B역,
                        C역,
                        3
                ));

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> 일호선_라인.addSection(
                        new Section(
                                일호선_라인,
                                B역,
                                C역,
                                5
                        )
                )).isInstanceOf(RuntimeException.class),

                () -> assertThatThrownBy(() -> 일호선_라인.addSection(
                        new Section(
                                일호선_라인,
                                B역,
                                C역,
                                3
                        )
                )).isInstanceOf(RuntimeException.class),

                () -> assertThatThrownBy(() -> 일호선_라인.addSection(
                        new Section(
                                일호선_라인,
                                A역,
                                C역,
                                3
                        )
                )).isInstanceOf(RuntimeException.class)
        );

    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSection8() {
        // given
        String 일호선 = "일호선";
        String 노란색 = "노란색";
        Station A역 = new Station("A역");
        Station B역 = new Station("B역");
        Station C역 = new Station("C역");
        Station X역 = new Station("X역");
        Station Y역 = new Station("Y역");

        Line 일호선_라인 = new Line(
                일호선,
                노란색,
                A역,
                B역,
                7
        );

        일호선_라인.addSection(
                new Section(
                        일호선_라인,
                        B역,
                        C역,
                        3
                ));

        // when
        assertThatThrownBy(() -> 일호선_라인.addSection(
                new Section(
                        일호선_라인,
                        X역,
                        Y역,
                        7
                )
        )).isInstanceOf(RuntimeException.class);
    }
}
