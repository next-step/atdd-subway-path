package nextstep.subway.line.domain;

import nextstep.subway.line.exception.*;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class LineTest {
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
        이호선 = new Line("2호선", "green", 강남역, 역삼역, 10);
    }

    @DisplayName("노선의 역들을 조회 가능")
    @Test
    void getStations() {
        // given
        이호선.addSection(역삼역, 삼성역, 1);

        // when, then
        assertThat(이호선.getStations()).isEqualTo(Arrays.asList(강남역, 역삼역, 삼성역));
    }

    @DisplayName("노선에 구간을 추가하면, 노선의 크기가 증가")
    @Test
    void addSection() {
        // given
        int expectedSize = 이호선.size() + 1;

        // when
        이호선.addSection(역삼역, 삼성역, 1);

        // then
        assertThat(이호선.size()).isEqualTo(expectedSize);
    }

    @DisplayName("노선의 마지막 하행에 구간을 추가")
    @Test
    void addSectionLastDownStation() {
        // given
        int expectedSize = 이호선.size() + 1;

        // when
        이호선.addSection(역삼역, 선릉역, 1);

        // then
        assertThat(이호선.size()).isEqualTo(expectedSize);
        assertThat(이호선.getStations()).isEqualTo(Arrays.asList(강남역, 역삼역, 선릉역));
    }

    @DisplayName("노선의 첫번째 상행에 구간을 추가")
    @Test
    void addSectionFirstUpStation() {
        // given
        int expectedSize = 이호선.size() + 1;

        // when
        이호선.addSection(교대역, 강남역, 1);

        // then
        assertThat(이호선.size()).isEqualTo(expectedSize);
        assertThat(이호선.getStations()).isEqualTo(Arrays.asList(교대역, 강남역, 역삼역));
    }

    @DisplayName("노선의 중간에 구간을 추가")
    @Test
    void addSectionMiddle() {
        // given
        int expectedSize = 이호선.size() + 2;

        // when
        이호선.addSection(강남역, 선릉역, 2);
        이호선.addSection(삼성역, 역삼역, 3);

        // then
        assertThat(이호선.size()).isEqualTo(expectedSize);
        assertThat(이호선.getStations()).isEqualTo(Arrays.asList(강남역, 선릉역, 삼성역, 역삼역));
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
        assertThatExceptionOfType(SectionAlreadyRegisteredException.class)
                .isThrownBy(() -> 이호선.addSection(강남역, 역삼역, 1));
        assertThatExceptionOfType(SectionAlreadyRegisteredException.class)
                .isThrownBy(() -> 이호선.addSection(역삼역, 강남역, 1));
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가 시 에러 발생")
    @Test
    void addSectionStationNotFound() {
        assertThatExceptionOfType(SectionNotSearchedException.class)
                .isThrownBy(() -> 이호선.addSection(교대역, 삼성역, 1));
    }

    @DisplayName("노선에서 구간을 삭제하면, 노선의 크기가 감소")
    @Test
    void removeSection() {
        // given
        이호선.addSection(역삼역, 삼성역, 1);
        int expectedSize = 이호선.size() - 1;

        // when
        이호선.removeSection(삼성역);

        // then
        assertThat(이호선.size()).isEqualTo(expectedSize);
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        assertThatExceptionOfType(EmptyLineException.class)
                .isThrownBy(() -> 이호선.removeSection(역삼역));
    }

    @DisplayName("마지막이 아닌 역을 삭제시 에러 발생")
    @Test
    void removeSectionInvalidUpStation() {
        // given
        이호선.addSection(역삼역, 삼성역, 1);

        // then
        assertThatExceptionOfType(NotLastStationException.class)
                .isThrownBy(() -> 이호선.removeSection(역삼역));
    }
}
