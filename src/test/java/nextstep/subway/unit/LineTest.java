package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PairedStations;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Collectors;

import static nextstep.subway.unit.LineFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {


    @DisplayName("동일한 역으로 구간 역을 설정하여 노선을 생성하면 예외가 발생한다")
    @Test
    void 동일한_역으로_구간생성시_예외_발생() {
        //when then
        assertThrows(IllegalArgumentException.class, () -> {
            상행종점역_하행종점역만_가진_구간이_포함된_노선_생성(1L, 1L);
        });
    }

    @DisplayName("동일한 역으로 구간 역을 설정하여 노선의 구간을 추가하면 예외가 발생한다")
    @Test
    void 동일한_역으로_구간_추가시_예외_발생() {
        //given
        Line line = 상행종점역_하행종점역만_가진_구간이_포함된_노선_생성(1L, 2L);
        Station upStation = 역_생성(2L);
        Station downStation = 역_생성(2L);

        //when then
        assertThrows(IllegalArgumentException.class, () -> {
            line.addSection(new PairedStations(upStation, downStation), 100);
        });
    }

    @DisplayName("노선 하행종점역에 구간을 추가하면 성공한다")
    @Test
    void 노선에_등록된_구간이_있고_하행_종점역_뒤에_구간을_추가할_때_정상() {
        //given
        Line line = 상행종점역_하행종점역만_가진_구간이_포함된_노선_생성(1L, 2L);
        Station upStation = 역_생성(2L);
        Station downStation = 역_생성(3L);

        //when then
        assertDoesNotThrow(() -> line.addSection(new PairedStations(upStation, downStation), 1000));
        assertThat(line.getStations().stream()
                .map(Station::getId)
                .collect(Collectors.toList())).containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @DisplayName("노선 기존 구간에 신규 구간을 추가하면 성공한다")
    @Test
    void 노선에_등록된_구간이_있고_기존_구간사이에_구간을_추가할_때_정상() {
        //given
        Line line = 상행종점역_하행종점역만_가진_구간이_포함된_노선_생성(1L, 2L);
        Station upStation = 역_생성(1L);
        Station downStation = 역_생성(3L);

        //when then
        assertDoesNotThrow(() -> line.addSection(new PairedStations(upStation, downStation), 1000));
        assertThat(line.getStations().stream()
                .map(Station::getId)
                .collect(Collectors.toList())).containsExactlyInAnyOrder(1L, 3L, 2L);
    }

    @DisplayName("노선 기존 구간 사이에 신규 구간을 추가할 때, 기존 구간의 역 사이 길이가 신규 구간의 역 사이 길이보다 같거나 크면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100, 1000, 9999})
    void 구간추가시_기존구간의_역사이길이가_신규구간의_역사이길이보다_같거나_크면_예외발생(int newSectionAddingDistance) {
        //given
        Line line = 상행종점역_하행종점역만_가진_구간이_포함된_노선_생성(1L, 2L);
        Station upStation = 역_생성(1L);
        Station downStation = 역_생성(3L);

        //when then
        assertThrows(IllegalArgumentException.class, () ->
                line.addSection(new PairedStations(upStation, downStation), END_STATIONS_DISTANCE + newSectionAddingDistance));
    }

    @DisplayName("노선 기존 구간 사이에 신규 구간을 추가할 때, 신규 구간의 상행, 하행역 모두 기존 구간에 존재 할 때 예외가 발생한다.")
    @Test
    void 구간추가시_추가할구간의_상행역_하행역_모두_기존구간에_존재할_때_예외발생() {
        //given
        Line line = 상행종점역_하행종점역만_가진_구간이_포함된_노선_생성(1L, 2L);
        Station upStation = 역_생성(1L);
        Station downStation = 역_생성(2L);

        //when then
        assertThrows(IllegalArgumentException.class, () ->
                line.addSection(new PairedStations(upStation, downStation), 1));
    }

    @DisplayName("노선 기존 구간 사이에 신규 구간을 추가할 때, 신규 구간의 상행, 하행역 모두 기존 구간에 존재하지 않으면 예외가 발생한다.")
    @Test
    void 구간추가시_추가할구간의_상행역_하행역_모두_기존구간에_존재하지_않을_때_예외발생() {
        //given
        Line line = 상행종점역_하행종점역만_가진_구간이_포함된_노선_생성(1L, 2L);
        Station upStation = 역_생성(3L);
        Station downStation = 역_생성(4L);

        //when then
        assertThrows(IllegalArgumentException.class, () ->
                line.addSection(new PairedStations(upStation, downStation), 1));
    }

    @DisplayName("노선에 등록된 하행종점역, 상행종점역이 있고, 구간 삭제시 예외가 발생한다")
    @Test
    void 노선에_등록된_구간_삭제_종점역만_등록되어있어_예외() {
        //given
        Line line = 상행종점역_하행종점역만_가진_구간이_포함된_노선_생성(1L, 2L);

        //when then
        assertThrows(IllegalArgumentException.class, () -> line.deleteSection(역_생성(3L)));
    }

    @DisplayName("노선에 등록된 구간이 있고, 중간 구간 삭제시 구간 삭제가 성공하고 구간은 재배치 된다")
    @Test
    void 노선에_등록된_구간_삭제후_재배치() {
        //given
        Line line = 상행종점역_하행종점역만_가진_구간이_포함된_노선_생성(1L, 2L);
        Station upStation = 역_생성(2L);
        Station downStation = 역_생성(3L);
        line.addSection(new PairedStations(upStation, downStation), 100);

        //when then
        assertThat(line.getStations().size()).isEqualTo(3);
        assertDoesNotThrow(() -> line.deleteSection(역_생성(2L)));
        assertThat(line.getStations().size()).isEqualTo(2);
        assertThat(line.getStations().stream().map(Station::getId)).containsExactly(1L, 3L);
    }

    @DisplayName("노선에 등록된 구간이 있고, 구간 삭제시 성공한다")
    @Test
    void 노선에_등록된_구간_삭제() {
        //given
        Line line = 상행종점역_하행종점역만_가진_구간이_포함된_노선_생성(1L, 2L);
        Station upStation = 역_생성(2L);
        Station downStation = 역_생성(3L);
        line.addSection(new PairedStations(upStation, downStation), 100);

        //when then
        assertThat(line.getStations().size()).isEqualTo(3);
        assertDoesNotThrow(() -> line.deleteSection(역_생성(3L)));
        assertThat(line.getStations().size()).isEqualTo(2);
        assertThat(line.getStations().stream().map(Station::getId)).containsExactly(1L, 2L);
    }
}
