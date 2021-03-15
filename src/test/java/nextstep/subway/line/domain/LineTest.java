package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineTest {

    private Station 역_A;
    private Station 역_B;
    private Station 역_C;
    private Station 역_D;
    private Station 역_E;

    private Line 노선;

    @BeforeEach
    void setUp() {
        역_A = new Station("역_A");
        역_B = new Station("역_B");
        역_C = new Station("역_C");
        역_D = new Station("역_D");
        역_E = new Station("역_E");

        ReflectionTestUtils.setField(역_A, "id", 1L);
        ReflectionTestUtils.setField(역_B, "id", 2L);
        ReflectionTestUtils.setField(역_C, "id", 3L);
        ReflectionTestUtils.setField(역_D, "id", 4L);
        ReflectionTestUtils.setField(역_E, "id", 5L);
    }

    @Test
    void getStations() {
        // given
        노선 = new Line("노선", "YELLOW", 역_A, 역_B, 10);

        // when (역 정보를 가져온다)
        List<Station> stations = 노선.getStations();

        // then (역 개수를 확인한다)
        assertThat(stations.size()).isEqualTo(2);
    }

    @Test
    void addSection() {
        // given
        노선 = new Line("노선", "YELLOW", 역_A, 역_B, 10);

        // when (구간 정보를 추가한다)
        노선.addSection(new Section(노선, 역_B, 역_C, 10));

        // then (구간 개수를 확인한다)
        assertThat(노선.getSections()).hasSize(2);
    }

    @Test
    void removeSection() {
        // given
        노선 = new Line("노선", "YELLOW", 역_A, 역_B, 10);
        Section section = new Section(노선, 역_B, 역_C, 10);
        노선.addSection(section);

        // when (역을 삭제한다)
        노선.removeStationById(역_C.getId());

        // then (구간 개수를 확인한다)
        assertThat(노선.getSections()).hasSize(1);
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        // given
        노선 = new Line("노선", "YELLOW", 역_A, 역_B, 10);

        // when (구간을 1개만 추가 후 삭제 요청한다) & then (익셉션 발생)
        assertThatThrownBy(() -> 노선.removeStationById(역_B.getId())).isInstanceOf(RuntimeException.class);
    }

    /**
     * TDD_Step1 - 유닛테스트 추가
     */
    @DisplayName("기존 1개 노선 중간에 구간을 추가한다. (상행기준)")
    @Test
    void addSectionInMiddleToUpStation() {
        // given (역_A - 역_C)
        노선 = new Line("노선", "YELLOW", 역_A, 역_C, 10);

        // when (중간에 구간 정보를 추가한다)
        노선.addSection(new Section(노선, 역_A, 역_B, 5)); // (역_A - 역_B - 역_C)

        // then
        assertThat(노선.getSections()).hasSize(2);
        assertThat(노선.getStations()).containsExactlyElementsOf(Arrays.asList(역_A, 역_B, 역_C));
    }

    @DisplayName("기존 1개 노선 중간에 구간을 추가한다. (하행기준)")
    @Test
    void addSectionInMiddleToDownStation() {
        // given (역_A - 역_C)
        노선 = new Line("노선", "YELLOW", 역_A, 역_C, 10);

        // when (중간에 구간 정보를 추가한다)
        노선.addSection(new Section(노선, 역_B, 역_C, 5)); // (역_A - 역_B - 역_C)

        // then
        assertThat(노선.getSections()).hasSize(2);
        assertThat(노선.getStations()).containsExactlyElementsOf(Arrays.asList(역_A, 역_B, 역_C));
    }

    @DisplayName("기존 2개 이상 노선 중간에 구간을 추가한다. (상행기준)")
    @Test
    void addSectionInMiddleMultipleUpStation() {
        // given (역_A - 역_B - 역_D)
        노선 = new Line("노선", "YELLOW", 역_A, 역_B, 10);
        노선.addSection(new Section(노선, 역_B, 역_D, 5));

        // when (중간에 구간 정보를 추가한다)
        노선.addSection(new Section(노선, 역_B, 역_C, 3)); // (역_A - 역_B - 역_C - 역_D)

        // then
        assertThat(노선.getSections()).hasSize(3);
        assertThat(노선.getStations()).containsExactlyElementsOf(Arrays.asList(역_A, 역_B, 역_C, 역_D));
    }

    @DisplayName("기존 2개 이상 노선 중간에 구간을 추가한다. (하행기준)")
    @Test
    void addSectionInMiddleMultipleDownStation() {
        // given (역_A - 역_B - 역_D)
        노선 = new Line("노선", "YELLOW", 역_A, 역_B, 10);
        노선.addSection(new Section(노선, 역_B, 역_D, 4));

        // when (중간에 구간 정보를 추가한다)
        노선.addSection(new Section(노선, 역_C, 역_D, 2)); // (역_A - 역_B - 역_C - 역_D)

        // then
        assertThat(노선.getSections()).hasSize(3);
        assertThat(노선.getStations()).containsExactlyElementsOf(Arrays.asList(역_A, 역_B, 역_C, 역_D));
    }

    @DisplayName("기존 노선 상행에 구간을 추가한다.")
    @Test
    void addSectionToUpStation() {
        // given (역_B - 역_C)
        노선 = new Line("노선", "YELLOW", 역_B, 역_C, 10);

        // when (상행에 구간 정보를 추가한다)
        노선.addSection(new Section(노선, 역_A, 역_B, 5)); // (역_A - 역_B - 역_C)

        // then
        assertThat(노선.getSections()).hasSize(2);
        assertThat(노선.getStations()).containsExactlyElementsOf(Arrays.asList(역_A, 역_B, 역_C));
    }

    @DisplayName("기존 노선 하행에 구간을 추가한다.")
    @Test
    void addSectionToDownStation() {
        // given (역_A - 역_B)
        노선 = new Line("노선", "YELLOW", 역_A, 역_B, 10);

        // when (하행에 구간 정보를 추가한다)
        노선.addSection(new Section(노선, 역_B, 역_C, 5)); // (역_A - 역_B - 역_C)

        // then
        assertThat(노선.getSections()).hasSize(2);
        assertThat(노선.getStations()).containsExactlyElementsOf(Arrays.asList(역_A, 역_B, 역_C));
    }

    @DisplayName("기존 1개 노선 중간에 구간을 추가후 거리가 잘 변경되었는지 확인")
    @Test
    void addSectionInMiddleDistance() {
        // given (역_A - 10 - 역_C)
        노선 = new Line("노선", "YELLOW", 역_A, 역_C, 10);

        // when (중간에 구간 정보를 추가한다, )
        노선.addSection(new Section(노선, 역_A, 역_B, 4)); // (역_A - 4 - 역_B - 6 - 역_C)

        // then
        assertThat(노선.getSections()).hasSize(2);
        assertThat(노선.getSections().get(0).getDistance()).isEqualTo(4);
        assertThat(노선.getSections().get(1).getDistance()).isEqualTo(6);
    }

    @DisplayName("기존 2개 이상 노선 중간에 추가후 거리가 잘 변경되었는지 확인")
    @Test
    void addSectionInMiddleMultipleDistance() {
        // given (역_A - 10 - 역_D)
        노선 = new Line("노선", "YELLOW", 역_A, 역_D, 10);
        노선.addSection(new Section(노선, 역_A, 역_C, 4)); // (역_A - 4 - 역_C - 6 - 역_D)

        // when (중간에 구간 정보를 추가한다, 역_A - 역_B)
        노선.addSection(new Section(노선, 역_A, 역_B, 2)); // (역_A - 2 - 역_B - 2 - 역_C - 6 - 역_D)

        // then
        assertThat(노선.getSections()).hasSize(3);
        assertThat(노선.getSections().get(0).getDistance()).isEqualTo(2);
        assertThat(노선.getSections().get(1).getDistance()).isEqualTo(2);
        assertThat(노선.getSections().get(2).getDistance()).isEqualTo(6);
    }

    @DisplayName("기존 노선 구간길이보다 중간에 추가할 구간의 길이가 크거나 같으면 에러 발생")
    @Test
    void addSectionInMiddleDistanceException() {
        // given
        노선 = new Line("노선", "YELLOW", 역_A, 역_C, 10);

        // when & then (익셉션 발생)
        assertThatThrownBy(() -> 노선.addSection(new Section(노선, 역_A, 역_B, 10))).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> 노선.addSection(new Section(노선, 역_A, 역_B, 11))).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("구간 추가시 구간의 역이 모두 이미 포함되어 있으면 에러 발생")
    @Test
    void addSectionInMiddleStationsAlreadyAddedException() {
        // given
        노선 = new Line("노선", "YELLOW", 역_A, 역_D, 10);
        노선.addSection(new Section(노선, 역_A, 역_C, 4));
        노선.addSection(new Section(노선, 역_A, 역_B, 2));

        // when & then (익셉션 발생)
        assertThatThrownBy(() -> 노선.addSection(new Section(노선, 역_A, 역_D, 1))).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("구간 추가시 최소한 구간의 하나의 역이라도 포함되어 있지 않으면 에러 발생")
    @Test
    void addSectionInMiddleStationsNoneException() {
        // given
        노선 = new Line("노선", "YELLOW", 역_A, 역_B, 10);
        노선.addSection(new Section(노선, 역_B, 역_C, 4));

        // when & then (익셉션 발생)
        assertThatThrownBy(() -> 노선.addSection(new Section(노선, 역_D, 역_E, 2))).isInstanceOf(RuntimeException.class);
    }

    /**
     * TDD_Step2 - 유닛테스트 추가
     */
    @DisplayName("기존 노선 중간역 삭제")
    @Test
    void removeStationInTheMiddle() {
        // given (역_A - 역_B - 역_C)
        노선 = new Line("노선", "YELLOW", 역_A, 역_B, 10);
        노선.addSection(new Section(노선, 역_B, 역_C, 10));

        // when (중간역을 삭제한다)
        노선.removeStationById(역_B.getId());

        // then (구간 개수를 확인한다)
        assertThat(노선.getStations()).hasSize(2);
    }
}
