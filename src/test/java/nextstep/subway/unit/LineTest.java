package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
}
