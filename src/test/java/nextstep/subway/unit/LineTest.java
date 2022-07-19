package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    @Test
    void addSection() {
        final Line line = new Line();
        final Section section = new Section(line, new Station("강남역"), new Station("역삼역"), 2);

        line.addSection(section);

        assertThat(line.getSections()).contains(section);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 수 있다")
    @Test
    void addSectionByUpStation() {
        final Line line = new Line("2호선", "bg-green");
        final Station 강남역 = new Station(1L, "강남역");
        final Station 역삼역 = new Station(2L, "역삼역");
        final Station 선릉역 = new Station(3L, "선릉역");

        final Section 역삼역_선릉역_구간 = new Section(line, 역삼역, 선릉역, 3);
        line.addSection(역삼역_선릉역_구간);

        final Section 강남역_역삼역_구간 = new Section(line, 강남역, 역삼역, 2);
        line.addSection(강남역_역삼역_구간);

        assertThat(line.getSections()).containsSequence(강남역_역삼역_구간, 역삼역_선릉역_구간);
        assertThat(line.getDistance()).isEqualTo(역삼역_선릉역_구간.getDistance() + 강남역_역삼역_구간.getDistance());
    }

    @DisplayName("새로운 역을 하행 좀정으로 등록 할 수 있다")
    @Test
    void addSectionByDownStation() {
        final Line line = new Line("2호선", "bg-green");
        final Station 강남역 = new Station(1L, "강남역");
        final Station 역삼역 = new Station(2L, "역삼역");
        final Station 선릉역 = new Station(3L, "선릉역");

        final Section 강남역_역삼역_구간 = new Section(line, 강남역, 역삼역, 2);
        line.addSection(강남역_역삼역_구간);

        final Section 역삼역_선릉역_구간 = new Section(line, 역삼역, 선릉역, 3);
        line.addSection(역삼역_선릉역_구간);

        assertThat(line.getSections()).containsSequence(강남역_역삼역_구간, 역삼역_선릉역_구간);
        assertThat(line.getDistance()).isEqualTo(역삼역_선릉역_구간.getDistance() + 강남역_역삼역_구간.getDistance());
    }

    @DisplayName("역 사이에 새로운 역을 등록 할 수 있다")
    @Test
    void addSectionBetweenStations() {
        final Line line = new Line("2호선", "bg-green");
        final Station 강남역 = new Station(1L, "강남역");
        final Station 역삼역 = new Station(2L, "역삼역");
        final Station 선릉역 = new Station(3L, "선릉역");

        final Section 강남역_선릉역_구간 = new Section(line, 강남역, 선릉역, 5);
        line.addSection(강남역_선릉역_구간);

        final Section 강남역_역삼역_구간 = new Section(line, 강남역, 역삼역, 3);
        line.addSection(강남역_역삼역_구간);

        final int 역삼역_선릉역_구간_거리 = 강남역_선릉역_구간.getDistance() - 강남역_역삼역_구간.getDistance();
        final Section 역삼역_선릉역_구간 = new Section(line, 역삼역, 선릉역, 역삼역_선릉역_구간_거리);

        assertThat(line.getSections()).containsSequence(강남역_역삼역_구간, 역삼역_선릉역_구간);
        assertThat(line.getDistance()).isEqualTo(강남역_선릉역_구간.getDistance());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다")
    @Test
    void addSectionBetweenStationsFail() {
        final Line line = new Line("2호선", "bg-green");
        final Station 강남역 = new Station(1L, "강남역");
        final Station 역삼역 = new Station(2L, "역삼역");
        final Station 선릉역 = new Station(3L, "선릉역");

        final Section 역삼역_선릉역_구간 = new Section(line, 역삼역, 선릉역, 2);
        line.addSection(역삼역_선릉역_구간);

        final Section 강남역_선릉역_구간 = new Section(line, 강남역, 선릉역, 5);

        assertThatThrownBy(() -> line.addSection(강남역_선릉역_구간))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다")
    @Test
    void addSectionBetweenStationsFail2() {
        final Line line = new Line("2호선", "bg-green");
        final Station 강남역 = new Station(1L, "강남역");
        final Station 역삼역 = new Station(2L, "역삼역");

        final Section 강남역_역삼역_구간 = new Section(line, 강남역, 역삼역, 3);
        line.addSection(강남역_역삼역_구간);

        assertThatThrownBy(() -> line.addSection(강남역_역삼역_구간))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다")
    @Test
    void addSectionBetweenStationsFail3() {
        final Line line = new Line("2호선", "bg-green");
        final Station 강남역 = new Station(1L, "강남역");
        final Station 역삼역 = new Station(2L, "역삼역");
        final Station 선릉역 = new Station(3L, "선릉역");
        final Station 삼성역 = new Station(4L, "삼성역");

        final Section 강남역_역삼역_구간 = new Section(line, 강남역, 역삼역, 3);
        line.addSection(강남역_역삼역_구간);

        final Section 선릉역_삼성역_구간 = new Section(line, 선릉역, 삼성역, 2);

        assertThatThrownBy(() -> line.addSection(선릉역_삼성역_구간))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
    }

    @Test
    void getStations() {
        final Line line = new Line("2호선", "bg-green");
        final Station 강남역 = new Station(1L, "강남역");
        final Station 역삼역 = new Station(2L, "역삼역");
        final Station 선릉역 = new Station(3L, "선릉역");

        final Section 강남_역삼_구간 = new Section(line, 강남역, 역삼역, 2);
        final Section 역삼_선릉_구간 = new Section(line, 역삼역, 선릉역, 3);

        line.addSection(강남_역삼_구간);
        line.addSection(역삼_선릉_구간);

        assertThat(line.getSections()).contains(강남_역삼_구간, 역삼_선릉_구간);
    }

    @Test
    void removeSection() {
        final Line line = new Line("2호선", "bg-green");
        final Station 강남역 = new Station(1L, "강남역");
        final Station 역삼역 = new Station(2L, "역삼역");
        final Station 선릉역 = new Station(3L, "선릉역");

        final Section 강남_역삼_구간 = new Section(line, 강남역, 역삼역, 2);
        final Section 역삼_선릉_구간 = new Section(line, 역삼역, 선릉역, 3);
        line.addSection(강남_역삼_구간);
        line.addSection(역삼_선릉_구간);

        line.deleteSection(역삼_선릉_구간);

        assertThat(line.getSections()).doesNotContain(역삼_선릉_구간);
    }

}
