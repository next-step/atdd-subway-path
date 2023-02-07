package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.unit.SubwayFixture.구간_생성;
import static nextstep.subway.unit.SubwayFixture.노선_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineSectionTest {
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        신분당선 = 노선_생성("신분당선", "red");
        신분당선.addSection(구간_생성(신분당선, "광교역", "수지구청역", 10));
    }

    @DisplayName("노선에 상행 종점역을 추가한다")
    @Test
    void addSectionOfFinalUpStation() {
        //when
        신분당선.addSection(구간_생성(신분당선, "호매실역", "광교역", 5));

        //then
        assertThat(신분당선.getStations()).hasSize(3)
                .extracting(Station::getName).containsExactly("호매실역", "광교역", "수지구청역");
    }

    @DisplayName("노선에 하행 종점역을 추가한다")
    @Test
    void addSectionOfFinalDownStation() {
        //when
        신분당선.addSection(구간_생성(신분당선, "수지구청역", "미금역", 5));

        //then
        assertThat(신분당선.getStations()).hasSize(3)
                .extracting(Station::getName).containsExactly("광교역", "수지구청역", "미금역");
    }

    @DisplayName("노선의 상행 구간을 추가한다")
    @Test
    void addSectionOfMiddleUpStation() {
        //when
        신분당선.addSection(구간_생성(신분당선, "광교역", "광교중앙역", 3));

        //then
        assertThat(신분당선.getStations()).hasSize(3)
                .extracting(Station::getName).containsExactly("광교역", "광교중앙역", "수지구청역");
        assertThat(신분당선.getSectionDistances()).hasSize(2).containsExactly(3, 7);
    }

    @DisplayName("노선의 하행 구간을 추가한다")
    @Test
    void addSectionOfMiddleDownStation() {
        //when
        신분당선.addSection(구간_생성(신분당선, "광교중앙역", "수지구청역", 3));

        //then
        assertThat(신분당선.getStations()).hasSize(3)
                .extracting(Station::getName).containsExactly("광교역", "광교중앙역", "수지구청역");
        assertThat(신분당선.getSectionDistances()).hasSize(2).containsExactly(7, 3);
    }

    @DisplayName("노선에 존재하지 않는 구간을 추가한다")
    @Test
    void addSectionOfUnknownStation() {
        //then
        assertThatThrownBy(() -> 신분당선.addSection(구간_생성(신분당선, "마포역", "홍대역", 5)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선에 중복된 구간을 추가한다")
    @Test
    void addSectionOfDuplicateStation() {
        //then
        assertThatThrownBy(() -> 신분당선.addSection(구간_생성(신분당선, "광교역", "수지구청역", 5)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선의 기존 구간 길이보다 긴 구간을 추가한다")
    @Test
    void addSectionOfLongerDistanceStation() {
        //then
        assertThatThrownBy(() -> 신분당선.addSection(구간_생성(신분당선, "광교중앙역", "수지구청역", 10)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
