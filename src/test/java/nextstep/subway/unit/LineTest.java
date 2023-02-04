package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.unit.SubwayFixture.구간_생성;
import static nextstep.subway.unit.SubwayFixture.노선_생성;
import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {
        //given
        Line 분당선 = 노선_생성("분당선", "yellow");
        Section 구간 = 구간_생성(분당선, "수원역", "매탄권선역", 5);

        //when
        분당선.addSection(구간);

        //then
        assertThat(분당선.getSections()).hasSize(1);
    }

    @Test
    void getStations() {
        //given
        Line 분당선 = 노선_생성("분당선", "yellow");
        분당선.addSection(구간_생성(분당선, "수원역", "매탄권선역", 5));
        분당선.addSection(구간_생성(분당선, "매탄권선역", "망포역", 5));

        //when
        List<Station> stations = 분당선.getStations();

        //then
        assertThat(stations).hasSize(3)
                .extracting(Station::getName)
                .containsExactlyInAnyOrder("수원역", "매탄권선역", "망포역");
    }

    @Test
    void removeSection() {
        //given
        Line 분당선 = 노선_생성("분당선", "yellow");
        분당선.addSection(구간_생성(분당선, "수원역", "매탄권선역", 5));
        분당선.addSection(구간_생성(분당선, "매탄권선역", "망포역", 5));

        //then
        List<Section> sections = 분당선.getSections();
        assertThat(sections).hasSize(2);

        //when
        분당선.removeSection("망포역");

        //then
        sections = 분당선.getSections();
        assertThat(sections).hasSize(1);

        List<Station> stations = 분당선.getStations();
        assertThat(stations).hasSize(2)
                .extracting(Station::getName)
                .containsExactlyInAnyOrder("수원역", "매탄권선역");
    }
}
