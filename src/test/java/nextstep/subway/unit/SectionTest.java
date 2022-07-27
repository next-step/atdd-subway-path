package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionTest {
    private Line 일호선;
    private Station 잠실역;
    private Station 교대역;

    @BeforeEach
    void init() {
        일호선 = new Line("일호선", "Green");
        잠실역 = new Station("잠실역");
        교대역 = new Station("교대역");
    }

    @DisplayName("구간이 해당 역들을 하나라도 포함하는지 여부를 반환한다.")
    @CsvSource(value = {"잠실역:교대역:true", "잠실역:강남역:true", "선릉역:교대역:true", "양재역:고속터미널역:false"}, delimiter = ':')
    @ParameterizedTest
    void contains(String upStation, String downStation, boolean result) {
        Section 구간 = new Section(일호선, 잠실역, 교대역, 10);

        Station 상행역 = new Station(upStation);
        Station 하행역 = new Station(downStation);

        assertThat(구간.contains(상행역, 하행역)).isEqualTo(result);
    }

    @DisplayName("구간의 역이 해당 역들과 모두 일치하는지 여부를 반환한다.")
    @CsvSource(value = {"잠실역:교대역:true", "교대역:잠실역:true", "잠실역:강남역:false", "선릉역:교대역:false", "양재역:고속터미널역:false"}, delimiter = ':')
    @ParameterizedTest
    void hasSameStations(String upStation, String downStation, boolean result) {
        Section 구간 = new Section(일호선, 잠실역, 교대역, 10);

        Station 상행역 = new Station(upStation);
        Station 하행역 = new Station(downStation);

        assertThat(구간.hasSameStations(상행역, 하행역)).isEqualTo(result);
    }

    @DisplayName("해당 역이 구간의 상행역인지 여부를 반환한다.")
    @CsvSource(value = {"잠실역:true", "선릉역:false"}, delimiter = ':')
    @ParameterizedTest
    void isUpStation(String name, boolean result) {
        Section 구간 = new Section(일호선, 잠실역, 교대역, 10);

        Station 지하철역 = new Station(name);

        assertThat(구간.isUpStation(지하철역)).isEqualTo(result);
    }

    @DisplayName("해당 역이 구간의 하행역인지 여부를 반환한다.")
    @CsvSource(value = {"교대역:true", "선릉역:false"}, delimiter = ':')
    @ParameterizedTest
    void isDownStation(String name, boolean result) {
        Section 구간 = new Section(일호선, 잠실역, 교대역, 10);

        Station 지하철역 = new Station(name);

        assertThat(구간.isDownStation(지하철역)).isEqualTo(result);
    }
}
