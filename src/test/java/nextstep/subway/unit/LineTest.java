package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {

    private Station 강남역;
    private Station 삼성역;
    private Station 잠실역;
    private Station 판교역;

    @BeforeEach
    void SetUp() {
        강남역 = new Station("강남역");
        삼성역 = new Station("삼성역");
        잠실역 = new Station("잠실역");
        판교역 = new Station("판교역");
    }


    /**
     * 2호선 : 강남역 <--- 5 ---> 잠실역
     */
    @Test
    @DisplayName("성공: 구간 추가")
    void add_Section() {
        Line line = new Line("이호선", "green");
        line.addSection(강남역, 잠실역, 5);

        assertAll(
                () -> assertThat(line.getSections()).hasSize(1),
                () -> assertThat(line.getStations())
                        .extracting(Station::getName)
                        .containsExactlyInAnyOrder("강남역", "잠실역")
        );
    }

    /**
     * (기존 구간) 강남역 <--- 5 ---> 잠실역
     * (추가 구간) 삼성역 <--- 3 ---> 판교역
     */
    @Test
    @DisplayName("실패: 일치하는 역이 없는 구간 추가")
    void fail_add_Section() {
        Line line = new Line("이호선", "green");
        line.addSection(강남역, 잠실역, 5);

        assertThrows(IllegalArgumentException.class,
                () -> line.addSection(삼성역, 판교역, 3));
    }

    /**
     * (기존 구간) 강남역 <--- 5 ---> 잠실역
     * (추가 구간) 강남역 <--- 3 ---> 잠실역
     */
    @Test
    @DisplayName("실패: 중복된 구간 추가")
    void fail_add_same_Section() {
        Line line = new Line("이호선", "green");
        line.addSection(강남역, 잠실역, 5);

        assertThrows(IllegalArgumentException.class,
                () -> line.addSection(강남역, 잠실역, 3));
    }

    @Test
    @DisplayName("성공: 지하철역 목록 조회")
    void getStations() {
        // 노선 추가
        Line line = new Line("이호선", "green");
        line.addSection(강남역, 잠실역, 5);

        assertSoftly(softly -> {
            softly.assertThat(line.getSections()).hasSize(1);
            softly.assertThat(line.getStations())
                    .extracting(Station::getName)
                    .containsExactlyInAnyOrder("강남역", "잠실역");
        });
    }

    /**
     * (기존 구간) 강남역 <--- 5 ---> 삼성역 <--- 10 ---> 잠실역
     * (삭제 구간) 삼성역 <--- 10 ---> 잠실역
     */
    @Test
    @DisplayName("성공: 구간 삭제")
    void removeSection() {
        // 노선 추가
        Line line = new Line("이호선", "green");
        // 1번째 구간추가
        line.addSection(강남역, 삼성역, 5);

        // 2번째 구간추가
        line.addSection(삼성역, 잠실역, 10);

        // 마지막구간 삭제
        line.deleteSection(잠실역);

        assertAll(
                () -> assertThat(line.getSections()).hasSize(1),
                () -> assertThat(line.getStations())
                        .extracting(Station::getName)
                        .doesNotContain("잠실역")
        );
    }
}
