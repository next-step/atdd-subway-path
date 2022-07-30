package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.InvalidDistanceBetweenStationsException;
import nextstep.subway.exception.InvalidDistanceValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static nextstep.subway.utils.GivenUtils.강남역;
import static nextstep.subway.utils.GivenUtils.선릉역;
import static nextstep.subway.utils.GivenUtils.역삼역;
import static nextstep.subway.utils.GivenUtils.이호선;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SectionTest {

    @Test
    @DisplayName("유효하지 않은 거리 값 - 0보다 작은 수")
    void invalidDistance() {
        // given
        int distance = -1;
        Line line = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();

        // when
        Executable executable = () -> new Section(line, 강남역, 역삼역, distance);

        // then
        assertThrows(InvalidDistanceValueException.class, executable);
    }

    @Test
    @DisplayName("유효한 거리 값 - 0보다 같거나 큰 수")
    void validDistance() {
        // given
        int distance = 10;
        Line line = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();

        // when
        Section section = new Section(line, 강남역, 역삼역, distance);

        // then
        assertThat(section.getDistance()).isEqualTo(distance);
    }

    @Test
    @DisplayName("구간에 역 포함하는지 확인")
    void containsStation() {
        // given
        int distance = 10;
        Line line = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();
        Section section = new Section(line, 강남역, 역삼역, distance);

        // when
        boolean trueResult = section.containsStation(강남역());
        boolean falseResult = section.containsStation(선릉역());

        // then
        assertThat(trueResult).isTrue();
        assertThat(falseResult).isFalse();
    }

    @Test
    @DisplayName("구간의 역 업데이트")
    void updateStation() {
        // given
        int distance = 10;
        int newDistance = 3;
        Line line = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();
        Station 선릉역 = 선릉역();
        Section section = new Section(line, 강남역, 역삼역, distance);

        // when
        section.updateUpStation(new Section(line, 강남역, 선릉역, newDistance));

        // then
        assertThat(section.getDistance()).isEqualTo(distance - newDistance);
        assertThat(section.getUpStation()).isEqualTo(선릉역);
        assertThat(section.getDownStation()).isEqualTo(역삼역);
    }

    @Test
    @DisplayName("구간의 역 업데이트 - 사이 간격보다 큰 경우")
    void updateStationFail() {
        // given
        int distance = 10;
        int newDistance = 10;
        Line line = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();
        Station 선릉역 = 선릉역();
        Section section = new Section(line, 강남역, 역삼역, distance);

        // when
        Executable executable = () -> section.updateUpStation(new Section(line, 강남역, 선릉역, newDistance));

        // then
        assertThrows(InvalidDistanceBetweenStationsException.class, executable);
    }

    @Test
    @DisplayName("구간의 상행역 제거")
    void removeUpStation() {
        // given
        int distance = 10;
        int newDistance = 3;
        Line line = 이호선();
        Station 강남역 = 강남역();
        Station 역삼역 = 역삼역();
        Station 선릉역 = 선릉역();
        Section section = new Section(line, 역삼역, 선릉역, distance);

        // when
        section.removeUpStation(new Section(line, 강남역, 역삼역, newDistance));

        // then
        assertThat(section.getDistance()).isEqualTo(distance + newDistance);
        assertThat(section.getUpStation()).isEqualTo(강남역);
        assertThat(section.getDownStation()).isEqualTo(선릉역);
    }

}