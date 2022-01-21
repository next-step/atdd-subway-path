package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.exception.*;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Station 교대역;
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Station 삼성역;
    private Line 이호선;

    @BeforeEach
    void setUp() {
        교대역 = stationRepository.save(new Station("교대역"));
        강남역 = stationRepository.save(new Station("강남역"));
        역삼역 = stationRepository.save(new Station("역삼역"));
        선릉역 = stationRepository.save(new Station("선릉역"));
        삼성역 = stationRepository.save(new Station("삼성역"));
        이호선 = lineRepository.save(new Line("2호선", "green", 강남역, 역삼역, 10));
    }

    @DisplayName("노선에 구간을 추가하면, 노선의 크기가 증가")
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        int expectedSize = 이호선.size() + 1;

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선, 역삼역, 삼성역, 1);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(이호선.size()).isEqualTo(expectedSize);
        assertThat(이호선.getStations()).isEqualTo(Arrays.asList(강남역, 역삼역, 삼성역));
    }

    @DisplayName("노선의 마지막 하행에 구간을 추가")
    @Test
    void addSectionLastDownStation() {
        // given
        int expectedSize = 이호선.size() + 1;

        // when
        lineService.addSection(이호선, 역삼역, 선릉역, 1);

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
        lineService.addSection(이호선, 교대역, 강남역, 1);

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
        lineService.addSection(이호선, 강남역, 선릉역, 2);
        lineService.addSection(이호선, 삼성역, 역삼역, 3);

        // then
        assertThat(이호선.size()).isEqualTo(expectedSize);
        assertThat(이호선.getStations()).isEqualTo(Arrays.asList(강남역, 선릉역, 삼성역, 역삼역));
    }

    @DisplayName("노선의 중간에 구간을 추가 시, 구간의 길이가 노선의 길이 이상이면 에러 발생")
    @Test
    void addSectionMiddleInvalidDistance() {
        assertThatExceptionOfType(InvalidDistanceException.class)
                .isThrownBy(() -> lineService.addSection(이호선, 역삼역, 선릉역, 10));
        assertThatExceptionOfType(InvalidDistanceException.class)
                .isThrownBy(() -> lineService.addSection(이호선, 역삼역, 선릉역, 11));
    }

    @DisplayName("상행역과 하행역이 이미 모선에 모드 등록되어 있다면 추가 시 에러 발생")
    @Test
    void addSectionAlreadyRegistered() {
        assertThatExceptionOfType(SectionAlreadyRegisteredException.class)
                .isThrownBy(() -> lineService.addSection(이호선, 강남역, 역삼역, 1));
        assertThatExceptionOfType(SectionAlreadyRegisteredException.class)
                .isThrownBy(() -> lineService.addSection(이호선, 역삼역, 강남역, 1));
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가 시 에러 발생")
    @Test
    void addSectionStationNotFound() {
        assertThatExceptionOfType(StationNotFoundException.class)
                .isThrownBy(() -> lineService.addSection(이호선, 교대역, 삼성역, 1));
    }

    @DisplayName("노선에서 구간을 삭제하면, 노선의 크기가 감소")
    @Test
    void removeSection() {
        // given
        lineService.addSection(이호선, 역삼역, 삼성역, 1);
        int expected = 이호선.size() - 1;

        // when
        lineService.removeSection(이호선, 삼성역);

        // then
        assertThat(이호선.size()).isEqualTo(expected);
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        assertThatExceptionOfType(EmptyLineException.class)
                .isThrownBy(() -> lineService.removeSection(이호선, 삼성역));
    }

    @DisplayName("마지막이 아닌 역을 삭제시 에러 발생")
    @Test
    void removeSectionInvalidUpStation() {
        // given
        lineService.addSection(이호선, 역삼역, 삼성역, 1);

        // then
        assertThatExceptionOfType(NotLastStationException.class)
                .isThrownBy(() -> lineService.removeSection(이호선, 역삼역));
    }
}
