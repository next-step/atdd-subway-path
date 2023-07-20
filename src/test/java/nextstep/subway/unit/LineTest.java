package nextstep.subway.unit;

import nextstep.subway.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 단위 테스트")
class LineTest {

    private Station 강남역;
    private Station 양재역;
    private Line line;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        line = new Line("2호선", "green", 강남역, 양재역, 7);
    }

    @DisplayName("지하철 노선에 구간 추가")
    @Test
    void addSection() {
        // given
        Station 역삼역 = new Station("역삼역");
        Station 구디역 = new Station("구디역");
        Section section = new Section(line, 역삼역, 구디역, 3);

        // when
        line.addSection(section);

        // then
        Assertions.assertThat(line.getSections()).hasSize(2);
    }

    @DisplayName("지하철 노선의 역 조회")
    @Test
    void getStations() {
        // given
        Section section = line.getSections().get(0);

        // when
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();

        // then
        assertThat(upStation).isEqualTo(강남역);
        assertThat(downStation).isEqualTo(양재역);
    }

    @DisplayName("지하철 구간 삭제")
    @Test
    void removeSection() {

        // when
        line.deleteSectionByUpStation(강남역);

        // then
        Assertions.assertThat(line.getSections()).hasSize(0);
    }
}
