package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.InvalidDistanceBetweenStationsException;
import nextstep.subway.exception.SectionRegistrationException;
import nextstep.subway.exception.SectionRemovalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static nextstep.subway.utils.GivenUtils.FIVE;
import static nextstep.subway.utils.GivenUtils.RED;
import static nextstep.subway.utils.GivenUtils.TEN;
import static nextstep.subway.utils.GivenUtils.강남역;
import static nextstep.subway.utils.GivenUtils.강남역_이름;
import static nextstep.subway.utils.GivenUtils.분당선;
import static nextstep.subway.utils.GivenUtils.선릉역;
import static nextstep.subway.utils.GivenUtils.선릉역_이름;
import static nextstep.subway.utils.GivenUtils.신분당선_이름;
import static nextstep.subway.utils.GivenUtils.양재역;
import static nextstep.subway.utils.GivenUtils.양재역_이름;
import static nextstep.subway.utils.GivenUtils.역삼역;
import static nextstep.subway.utils.GivenUtils.역삼역_이름;
import static nextstep.subway.utils.GivenUtils.이호선;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {

    @Test
    @DisplayName("line 이름 및 컬러 수정")
    void updateNameAndColor() {
        // given
        Line line = 분당선();

        // when
        Line newLine = line.updateNameAndColor(신분당선_이름, RED);

        // then
        assertThat(newLine.getName()).isEqualTo(신분당선_이름);
        assertThat(newLine.getColor()).isEqualTo(RED);
    }

    @Test
    @DisplayName("section 추가 - 하행 종점 뒤에 추가")
    void addSectionNextToTail() {
        // given
        int expectedSize = 3;
        Line line = 이호선();
        line.addSection(강남역(), 역삼역(), TEN);

        // when
        line.addSection(역삼역(), 선릉역(), TEN);

        // then
        assertThat(getStationNames(line)).hasSize(expectedSize)
                .containsExactly(강남역_이름, 역삼역_이름, 선릉역_이름);
    }

    @Test
    @DisplayName("section 추가 - 하행 종점 앞에 추가")
    void addSectionInFrontOfTail() {
        // given
        int expectedSize = 3;
        Line line = 이호선();
        line.addSection(강남역(), 역삼역(), TEN);

        // when
        line.addSection(선릉역(), 역삼역(), FIVE);

        // then
        assertThat(getStationNames(line)).hasSize(expectedSize)
                .containsExactly(강남역_이름, 선릉역_이름, 역삼역_이름);
    }

    @Test
    @DisplayName("section 추가 - 상행 종점 뒤에 추가")
    void addSectionNextToHead() {
        // given
        int expectedSize = 3;
        Line line = 이호선();
        line.addSection(강남역(), 역삼역(), TEN);

        // when
        line.addSection(강남역(), 선릉역(), FIVE);

        // then
        assertThat(getStationNames(line)).hasSize(expectedSize)
                .containsExactly(강남역_이름, 선릉역_이름, 역삼역_이름);
    }

    @Test
    @DisplayName("section 추가 - 상행 종점 앞에 추가")
    void addSectionInFrontOfHead() {
        // given
        int expectedSize = 3;
        Line line = 이호선();
        line.addSection(강남역(), 역삼역(), TEN);

        // when
        line.addSection(선릉역(), 강남역(), TEN);

        // then
        assertThat(getStationNames(line)).hasSize(expectedSize)
                .containsExactly(선릉역_이름, 강남역_이름, 역삼역_이름);
    }

    @Test
    @DisplayName("section 추가 실패 - 하행 종점 앞에 추가 (역 사이 거리보다 같거나 큰 구간 추가)")
    void addSectionInFrontOfTailWithInvalidDistance() {
        // given
        Line line = 이호선();
        line.addSection(강남역(), 역삼역(), TEN);

        // when
        Executable executable = () -> line.addSection(선릉역(), 역삼역(), TEN);

        // then
        assertThrows(InvalidDistanceBetweenStationsException.class, executable);
    }

    @Test
    @DisplayName("section 추가 실패 - 상행 종점 뒤에 추가 (역 사이 거리보다 같거나 큰 구간 추가)")
    void addSectionNextToHeadWithInvalidDistance() {
        // given
        Line line = 이호선();
        line.addSection(강남역(), 역삼역(), TEN);

        // when
        Executable executable = () -> line.addSection(강남역(), 선릉역(), TEN);

        // then
        assertThrows(InvalidDistanceBetweenStationsException.class, executable);
    }

    @Test
    @DisplayName("section 추가 실패 - 등록하려는 상행, 하행역이 노선에 이미 등록된 경우")
    void addSectionWithDuplicatedStations() {
        // given
        Line line = 이호선();
        line.addSection(강남역(), 역삼역(), TEN);

        // when
        Executable executable = () -> line.addSection(역삼역(), 강남역(), TEN);

        // then
        assertThrows(SectionRegistrationException.class, executable);
    }

    @Test
    @DisplayName("section 추가 실패 - 등록하려는 역이 노선의 상행, 하행역에 포함되지 않는 경우")
    void addSectionWithInvalidStations() {
        // given
        Line line = 이호선();
        line.addSection(강남역(), 역삼역(), TEN);

        // when
        Executable executable = () -> line.addSection(선릉역(), 양재역(), TEN);

        // then
        assertThrows(SectionRegistrationException.class, executable);
    }

    @Test
    @DisplayName("section 제거 - 마지막 역")
    void removeLastSection() {
        // given
        int expectedSize = 2;
        Line line = 이호선();
        line.addSection(강남역(), 역삼역(), TEN);
        line.addSection(역삼역(), 선릉역(), FIVE);

        // when
        line.removeSection(선릉역());

        // then
        assertThat(getStationNames(line)).hasSize(expectedSize)
                .containsExactly(강남역_이름, 역삼역_이름);
    }

    @Test
    @DisplayName("section 제거 - 첫번째 역")
    void removeFirstSection() {
        // given
        int expectedSize = 2;
        Line line = 이호선();
        line.addSection(강남역(), 역삼역(), TEN);
        line.addSection(역삼역(), 선릉역(), FIVE);

        // when
        line.removeSection(강남역());

        // then
        assertThat(getStationNames(line)).hasSize(expectedSize)
                .containsExactly(역삼역_이름, 선릉역_이름);
    }

    @Test
    @DisplayName("section 제거 - 중간역")
    void removeMiddleSection() {
        // given
        int expectedSize = 3;
        Line line = 이호선();
        line.addSection(양재역(), 강남역(), TEN);
        line.addSection(강남역(), 역삼역(), TEN);
        line.addSection(역삼역(), 선릉역(), FIVE);

        // when
        line.removeSection(역삼역());

        // then
        assertThat(getStationNames(line)).hasSize(expectedSize)
                .containsExactly(양재역_이름, 강남역_이름, 선릉역_이름);
    }

    @Test
    @DisplayName("section 제거 실패 - 존재하지 않는 역 제거")
    void removeSectionWithNonExistStation() {
        // given
        Line line = 이호선();
        line.addSection(강남역(), 역삼역(), TEN);

        // when
        Executable executable = () -> line.removeSection(선릉역());

        // then
        assertThrows(NoSuchElementException.class, executable);
    }

    @Test
    @DisplayName("section 제거 실패 - 구간이 1개인 경우")
    void removeSingleSection() {
        // given
        Line line = 이호선();
        line.addSection(강남역(), 역삼역(), TEN);

        // when
        Executable executable = () -> line.removeSection(역삼역());

        // then
        assertThrows(SectionRemovalException.class, executable);
    }

    private List<String> getStationNames(Line line) {
        return line.getStations()
                .stream()
                .map(Station::getName)
                .collect(Collectors.toList());
    }

}