package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 노선 단위 테스트")
class LineTest {
    @Test
    @DisplayName("지하철 노선에 구간을 추가한다.")
    void addSection() {
        //given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");

        //when
        line.addSection(upStation, downStation, 10);

        //then
        assertThat(line.getAllStations()).containsExactly(upStation, downStation);
    }

    @Test
    @DisplayName("지하철 노선의 역들을 조회할 수 있다.")
    void getStations() {
        //given
        Line line = new Line("2호선", "green");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 삼성역 = new Station("삼성역");
        line.addSection(강남역, 역삼역, 10);
        line.addSection(역삼역, 삼성역, 5);

        //when
        List<Station> result = line.getAllStations();

        //then
        assertThat(result).containsExactly(강남역, 역삼역, 삼성역);
    }

    @Test
    @DisplayName("지하철 노선에서 지하철 역을 삭제하면 구간 삭제된다.")
    void removeSection() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 삼성역 = new Station("삼성역");
        Line line = new Line("2호선", "green");
        line.addSection(강남역, 역삼역, 10);
        line.addSection(역삼역, 삼성역, 5);

        //when
        line.removeSection(삼성역);

        //then
        assertThat(line.getAllStations()).containsExactly(강남역, 역삼역);
    }

    @Test
    @DisplayName("역 사이에 새로운 역(상행역이 같을 경우)을 추가한다.")
    void addSectionInMiddleAtSameUpStation() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 삼성역 = new Station("삼성역");
        Station 삼성역2 = new Station("삼성역2");
        Line line = new Line("2호선", "green");

        //when
        line.addSection(강남역, 삼성역, 10);
        line.addSection(강남역, 역삼역, 4);
        line.addSection(삼성역2, 강남역, 3);

        //then
        assertThat(line.getSections()).hasSize(3);
        Section section = line.getSections().stream()
                .filter(it -> it.getUpStation() == 강남역)
                .findFirst().orElseThrow(RuntimeException::new);
        assertThat(section.getDownStation()).isEqualTo(역삼역);
        assertThat(section.getDistance()).isEqualTo(4);
    }

    @Test
    @DisplayName("역 사이에 새로운 역(하행역이 같을 경우)을 추가한다.")
    void addSectionInMiddleAtSameDownStation() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 삼성역 = new Station("삼성역");
        Station 선릉역 = new Station("선릉역");
        Line line = new Line("2호선", "green");

        //when
        line.addSection(강남역, 삼성역, 10);
        line.addSection(삼성역, 선릉역, 3);
        line.addSection(역삼역, 삼성역, 5);

        //then
        assertThat(line.getSections()).hasSize(3);
        Section section = line.getSections().stream()
                .filter(it -> it.getDownStation() == 삼성역)
                .findFirst().orElseThrow(RuntimeException::new);
        assertThat(section.getUpStation()).isEqualTo(역삼역);
        assertThat(section.getDistance()).isEqualTo(5);
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없어 에러가 발생한다.")
    void addSectionBothNotContain() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 삼성역 = new Station("삼성역");
        Station 선릉역 = new Station("선릉역");
        Station 하하역 = new Station("하하역");
        Station 호호역 = new Station("호호역");
        Line line = new Line("2호선", "green");

        line.addSection(강남역, 삼성역, 10);
        line.addSection(삼성역, 선릉역, 3);
        line.addSection(역삼역, 삼성역, 5);

        //when
        //then
        assertThatThrownBy(() -> line.addSection(하하역, 호호역, 9))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음");
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없어 에러가 발생한다.")
    void addSectionBothContain() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 삼성역 = new Station("삼성역");
        Station 선릉역 = new Station("선릉역");
        Line line = new Line("2호선", "green");

        line.addSection(강남역, 삼성역, 10);
        line.addSection(삼성역, 선릉역, 3);
        line.addSection(역삼역, 삼성역, 5);

        //when
        //then
        assertThatThrownBy(() -> line.addSection(역삼역, 선릉역, 9))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없어 에러가 발생한다.")
    void checkDistanceForAddSection() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 삼성역 = new Station("삼성역");
        Station 선릉역 = new Station("선릉역");
        Line line = new Line("2호선", "green");

        line.addSection(강남역, 삼성역, 5);
        line.addSection(삼성역, 선릉역, 3);

        //when
        //then
        assertThatThrownBy(() -> line.addSection(역삼역, 삼성역, 6))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
    }
}
