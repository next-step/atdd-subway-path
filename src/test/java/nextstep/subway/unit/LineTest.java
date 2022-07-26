package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class LineTest {
    private Station 당곡역;
    private Station 보라매역;
    private Station 신림역;
    private Line 신림선;
    @BeforeEach
    void setUp() {
        당곡역 = new Station("당곡역");
        보라매역 = new Station("보라매역");
        신림역 = new Station("신림역");

        신림선 = new Line("신림선", "blue-bg-300");
    }
    @Test
    void addSection() {
        //when
        신림선.addSection(당곡역, 보라매역, 10);

        //then
        assertThat(신림선.getSections())
                .filteredOn(section -> section.equals(new Section(신림선, 당곡역, 보라매역, 10)));
    }

    @Test
    void getStations() {
        신림선.addSection(당곡역, 보라매역, 10);
        신림선.addSection(보라매역, 신림역, 4);

        assertAll(() -> {
            assertThat(신림선.getStation()).contains(당곡역, 보라매역, 신림역);
            assertThat(신림선.getStation().size()).isEqualTo(3);
        });
    }

    @Test
    void removeSection() {
        신림선.addSection(당곡역, 보라매역, 10);
        신림선.addSection(보라매역, 신림역, 4);

        신림선.removeSection(신림역);

        assertAll(() -> {
            assertThat(신림선.getSections()).hasSize(2);
            assertThat(신림선.getSections()).doesNotContain(new Section(신림선, 보라매역, 신림역, 4));
        });
    }
}
