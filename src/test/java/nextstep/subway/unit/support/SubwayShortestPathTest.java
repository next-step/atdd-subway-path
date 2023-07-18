package nextstep.subway.unit.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static nextstep.subway.unit.StationFixture.강남역;
import static nextstep.subway.unit.StationFixture.교대역;
import static nextstep.subway.unit.StationFixture.삼성역;
import static nextstep.subway.unit.StationFixture.선릉역;
import static nextstep.subway.unit.StationFixture.역삼역;

import java.util.List;

import org.junit.jupiter.api.Test;

import nextstep.subway.global.SubwayException;
import nextstep.subway.support.SubwayShortestPath;
import nextstep.subway.unit.LineFixture;

class SubwayShortestPathTest {

    @Test
    void 출발역부터_도착역까지의_역_목록을_반환한다() {
        // 교대역 - 강남역 - 선릉역 - 역삼역 - 삼성역
        final var line = LineFixture.makeLine(교대역, 삼성역, 40);
        LineFixture.appendSection(line, 교대역, 강남역, 10);
        LineFixture.appendSection(line, 선릉역, 삼성역, 20);
        LineFixture.appendSection(line, 선릉역, 역삼역, 10);

        final var path = SubwayShortestPath.builder(line.getStations(), line.getSections())
                .source(line.getFirstStation())
                .target(line.getLastStation())
                .build();

        final var actual = path.getStation();
        assertThat(actual).containsExactly(교대역, 강남역, 선릉역, 역삼역, 삼성역);
    }

    @Test
    void 출발역부터_도착역까지의_경로를_찾지_못하면_예외가_발생한다() {
        final var line = LineFixture.makeLine(교대역, 삼성역, 40);

        assertThatThrownBy(() ->
                SubwayShortestPath.builder(List.of(교대역, 삼성역, 선릉역), line.getSections())
                        .source(교대역)
                        .target(선릉역)
                        .build()
        ).isInstanceOf(SubwayException.class);
    }
}