package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    @Test
    @DisplayName("노선에 구간 추가.")
    void addSection() {
        // given
        Line line = new Line("7호선", "green darken-2");
        Station upStation = new Station("상도역");
        Station downStation = new Station("장승배기역");
        int distance = 5;

        // when
        line.addSection(upStation, downStation, distance);

        // then
        Assertions.assertAll(
                () -> assertThat(line.getSections()).isNotNull(),
                () -> assertThat(line.getSections().getStations()).containsExactly(upStation, downStation)
        );
    }

    @Test
    @DisplayName("노선의 모든 역 조회.")
    void getStations() {
        // given
        Line line = createLine();

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).extracting("name").containsExactly("내방역", "고속터미널역", "반포역");
    }

    @Test
    @DisplayName("구간 삭제.")
    void removeSection() {
        // given
        Line line = createLine();
        Station lastStation = line.getSections().getLastSection().getDownStation();

        // when
        line.removeSection(lastStation);

        // then
        Assertions.assertAll(
                () -> assertThat(line.getStations()).extracting("name").containsExactly("내방역", "고속터미널역"),
                () -> assertThat(line.getSections().getLastSection().getDownStation()).isNotEqualTo(lastStation),
                () -> assertThat(line.getSections().getLastSection().getDownStation()).extracting("name").isEqualTo("고속터미널역")
        );
    }

    @Test
    @DisplayName("구간 삭제 실패.")
    void removeSection_WithException() {
        // given
        Line line = createLine();
        Station lastStation = line.getSections().getLastSection().getUpStation();
        Station realLastStation = line.getSections().getLastSection().getDownStation();

        // then
        assertThatThrownBy(() -> line.removeSection(lastStation)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> {
            line.removeSection(realLastStation);
            line.removeSection(lastStation);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    private Line createLine() {
        // given
        Line line = new Line("7호선", "green darken-2");
        line.addSection(new Station("내방역"), new Station("고속터미널역"), 10);
        line.addSection(new Station("고속터미널역"), new Station("반포역"), 7);

        return line;
    }
}
