package nextstep.subway.unit;

import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Line 도메인 객체 테스트")
@SpringBootTest
@Transactional
class LineTest {
    private Station 영등포역;
    private Station 신도림역;
    private Station 구로역;

    @BeforeEach
    public void setUp() {
        영등포역 = new Station("영등포역");
        신도림역 = new Station("신도림역");
        구로역 = new Station("구로역");
    }

    @Nested
    @DisplayName("구간 추가")
    class addSection {
        @DisplayName("하행 종점역 구간추가")
        @Test
        void addSectionDownStationTerminal() {
            // given
            Line 이호선 = new Line("2호선", "green");

            // when
            이호선.addSection(영등포역, 신도림역, 10);
            이호선.addSection(신도림역, 구로역, 5);

            // then
            assertAll(
                    () -> assertThat(이호선.getStations()).hasSize(3),
                    () -> assertThat(이호선.getStations()).containsExactly(영등포역, 신도림역, 구로역)
            );
        }

        @DisplayName("기존 구간이 존재할 경우 구간추가 실패")
        @Test
        void addSectionDuplication() {
            // given
            Line 이호선 = new Line("2호선", "green");

            // when
            이호선.addSection(신도림역, 구로역, 5);

            // then
            assertThatThrownBy(() -> 이호선.addSection(신도림역, 구로역, 10))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("노선의 모든 지하철역 가져오기")
    @Test
    void getStations() {
        // given
        Line 이호선 = new Line("2호선", "green");
        이호선.addSection(영등포역, 신도림역, 10);
        이호선.addSection(신도림역, 구로역, 5);

        // when
        List<Station> stations = 이호선.getStations();

        // then
        assertAll(
                () -> assertThat(stations).hasSize(3),
                () -> assertThat(stations).containsExactly(영등포역, 신도림역, 구로역)
        );
    }

    @DisplayName("구간 삭제")
    @Test
    void removeSection() {
        // given
        Line 이호선 = new Line("2호선", "green");
        이호선.addSection(영등포역, 신도림역, 10);
        이호선.addSection(신도림역, 구로역, 5);

        // when
        이호선.deleteSection(구로역);

        // then
        assertAll(
                () -> assertThat(이호선.getStations()).hasSize(2),
                () -> assertThat(이호선.getStations()).containsExactly(영등포역, 신도림역)
        );
    }

    @DisplayName("하나의 구간을 가진 노선 삭제시 예외 발생")
    @Test
    void removeSectionHasOneSection() {
        // given
        Line 이호선 = new Line("2호선", "green");
        이호선.addSection(영등포역, 신도림역, 10);

        // when
        // then
        assertThatThrownBy(() -> 이호선.deleteSection(신도림역))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
