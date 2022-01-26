package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.stream.Collectors;
import nextstep.subway.line.exception.EmptyLineException;
import nextstep.subway.line.exception.InvalidDistanceException;
import nextstep.subway.line.exception.SectionAlreadyRegisteredException;
import nextstep.subway.line.exception.SectionNotSearchedException;
import nextstep.subway.line.exception.StationNotFoundException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineTest {
    private static final int FIRST_INDEX = 0;

    private Station 교대역;
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Station 삼성역;
    private Line 이호선;

    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");
        삼성역 = new Station("삼성역");

        int lineDistance = 10;
        이호선 = new Line("2호선", "green", 강남역, 역삼역, lineDistance);
    }

    @DisplayName("노선의 역들을 조회 가능")
    @Test
    void getStations() {
        // given
        int distance = 1;
        이호선.addSection(역삼역, 삼성역, distance);

        // when, then
        assertThat(이호선.getStations()).isEqualTo(Arrays.asList(강남역, 역삼역, 삼성역));
    }

    @DisplayName("노선에 구간을 추가하면, 노선의 크기가 증가")
    @Test
    void addSection() {
        // given
        int expectedSize = 이호선.size() + 1;

        // when
        int distance = 1;
        이호선.addSection(역삼역, 삼성역, distance);

        // then
        assertThat(이호선.size()).isEqualTo(expectedSize);
    }

    @DisplayName("노선의 마지막 하행에 구간을 추가")
    @Test
    void addSectionLastDownStation() {
        // given
        int expectedSize = 이호선.size() + 1;

        // when
        int distance = 1;
        이호선.addSection(역삼역, 선릉역, distance);

        // then
        assertAll(
                () -> assertThat(이호선.size()).isEqualTo(expectedSize),
                () -> assertThat(이호선.getStations()).isEqualTo(Arrays.asList(강남역, 역삼역, 선릉역))
        );
    }

    @DisplayName("노선의 첫번째 상행에 구간을 추가")
    @Test
    void addSectionFirstUpStation() {
        // given
        int expectedSize = 이호선.size() + 1;

        // when
        int distance = 1;
        이호선.addSection(교대역, 강남역, distance);

        // then
        assertAll(
                () -> assertThat(이호선.size()).isEqualTo(expectedSize),
                () -> assertThat(이호선.getStations().stream().map(Station::getName).collect(Collectors.toList()))
                        .isEqualTo(Arrays.asList(교대역, 강남역, 역삼역).stream().map(Station::getName).collect(Collectors.toList()))
        );
    }

    @DisplayName("노선의 중간에 구간을 추가")
    @Test
    void addSectionMiddle() {
        // given
        int expectedSize = 이호선.size() + 2;

        // when
        int distance = 1;
        이호선.addSection(강남역, 선릉역, distance);
        이호선.addSection(삼성역, 역삼역, distance);

        // then
        assertAll(
                () -> assertThat(이호선.size()).isEqualTo(expectedSize),
                () -> assertThat(이호선.getStations()).isEqualTo(Arrays.asList(강남역, 선릉역, 삼성역, 역삼역))
        );
    }

    @DisplayName("노선의 중간에 구간을 추가 시, 구간의 길이가 노선의 길이 이상이면 에러 발생")
    @ValueSource(ints = {10, 11, 100})
    @ParameterizedTest
    void addSectionMiddleInvalidDistance(int distance) {
        assertThatExceptionOfType(InvalidDistanceException.class)
                .isThrownBy(() -> 이호선.addSection(강남역, 선릉역, distance));
    }

    @DisplayName("상행역과 하행역이 이미 모선에 모두 등록되어 있다면 추가 시 에러 발생")
    @Test
    void addSectionAlreadyRegistered() {
        int distance = 1;
        assertAll(
                () -> assertThatExceptionOfType(SectionAlreadyRegisteredException.class)
                        .isThrownBy(() -> 이호선.addSection(강남역, 역삼역, distance)),
                () -> assertThatExceptionOfType(SectionAlreadyRegisteredException.class)
                        .isThrownBy(() -> 이호선.addSection(역삼역, 강남역, distance))
        );
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가 시 에러 발생")
    @Test
    void addSectionNotSearched() {
        int distance = 1;
        assertThatExceptionOfType(SectionNotSearchedException.class)
                .isThrownBy(() -> 이호선.addSection(교대역, 삼성역, distance));
    }

    @DisplayName("노선에서 구간을 삭제하면, 노선의 크기가 감소")
    @Test
    void removeSection() {
        // given
        int distance = 1;
        이호선.addSection(역삼역, 삼성역, distance);
        int expectedSize = 이호선.size() - 1;
        int expectedDistance = distance + distance;

        // when
        이호선.removeSection(삼성역);

        // then
        assertAll(
                () -> assertThat(이호선.size()).isEqualTo(expectedSize),
                () -> assertThat(이호선.getStations()).isEqualTo(Arrays.asList(강남역, 역삼역)),
                () -> assertThat(이호선.getSections().get(FIRST_INDEX).getDistance()).isEqualTo(expectedDistance)
        );
    }

    @DisplayName("노선의 마지막 하행을 삭제")
    @Test
    void removeLastDownStation() {
        // given
        int distance = 1;
        이호선.addSection(역삼역, 선릉역, distance);
        int expectedSize = 이호선.size() - 1;
        int expectedDistance = distance + distance;

        // when
        이호선.removeSection(선릉역);

        // then
        assertAll(
                () -> assertThat(이호선.size()).isEqualTo(expectedSize),
                () -> assertThat(이호선.getStations()).isEqualTo(Arrays.asList(강남역, 역삼역)),
                () -> assertThat(이호선.getSections().get(FIRST_INDEX).getDistance()).isEqualTo(expectedDistance)
        );
    }

    @DisplayName("노선의 첫번째 상행을 삭제")
    @Test
    void removeFirstUpStation() {
        // given
        int distance = 1;
        이호선.addSection(역삼역, 선릉역, distance);
        int expectedSize = 이호선.size() - 1;
        int expectedDistance = distance + distance;

        // when
        이호선.removeSection(강남역);

        // then
        assertAll(
                () -> assertThat(이호선.size()).isEqualTo(expectedSize),
                () -> assertThat(이호선.getStations()).isEqualTo(Arrays.asList(역삼역, 선릉역)),
                () -> assertThat(이호선.getSections().get(FIRST_INDEX).getDistance()).isEqualTo(expectedDistance)
        );
    }

    @DisplayName("노선의 중간을 삭제")
    @Test
    void removeMiddleStation() {
        // given
        int distance = 1;
        이호선.addSection(역삼역, 선릉역, distance);
        int expectedSize = 이호선.size() - 1;
        int expectedDistance = distance + distance;

        // when
        이호선.removeSection(역삼역);

        // then
        assertAll(
                () -> assertThat(이호선.size()).isEqualTo(expectedSize),
                () -> assertThat(이호선.getStations()).isEqualTo(Arrays.asList(강남역, 선릉역)),
                () -> assertThat(이호선.getSections().get(FIRST_INDEX).getDistance()).isEqualTo(expectedDistance)
        );
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionEmptyLine() {
        assertThatExceptionOfType(EmptyLineException.class)
                .isThrownBy(() -> 이호선.removeSection(역삼역));
    }

    @DisplayName("노선에 존재하지 않는 역을 삭제시 에러 발생")
    @Test
    void removeSectionStationNotFound() {
        // given
        int distance = 1;
        이호선.addSection(역삼역, 선릉역, distance);

        // then
        assertThatExceptionOfType(StationNotFoundException.class)
                .isThrownBy(() -> 이호선.removeSection(교대역));
    }
}
