package nextstep.subway.line.application;

import java.util.Arrays;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.exception.EmptyLineException;
import nextstep.subway.line.exception.InvalidDistanceException;
import nextstep.subway.line.exception.NotLastStationException;
import nextstep.subway.line.exception.SectionAlreadyRegisteredException;
import nextstep.subway.line.exception.SectionNotSearchedException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

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

        int lineDistance = 10;
        이호선 = lineRepository.save(new Line("2호선", "green", 강남역, 역삼역, lineDistance));
    }

    @DisplayName("노선에 구간을 추가하면, 노선의 크기가 증가")
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        int expectedSize = 이호선.size() + 1;

        // when
        // lineService.addSection 호출
        int distance = 1;
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 삼성역.getId(), distance));

        // then
        // line.getSections 메서드를 통해 검증
        assertAll(
                () -> assertThat(이호선.size()).isEqualTo(expectedSize),
                () -> assertThat(이호선.getStations()).isEqualTo(Arrays.asList(강남역, 역삼역, 삼성역))
        );
    }

    @DisplayName("노선의 마지막 하행에 구간을 추가")
    @Test
    void addSectionLastDownStation() {
        // given
        int expectedSize = 이호선.size() + 1;

        // when
        int distance = 1;
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 선릉역.getId(), distance));

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
        lineService.addSection(이호선.getId(), new SectionRequest(교대역.getId(), 강남역.getId(), distance));

        // then
        assertAll(
                () -> assertThat(이호선.size()).isEqualTo(expectedSize),
                () -> assertThat(이호선.getStations()).isEqualTo(Arrays.asList(교대역, 강남역, 역삼역))
        );
    }

    @DisplayName("노선의 중간에 구간을 추가")
    @Test
    void addSectionMiddle() {
        // given
        int expectedSize = 이호선.size() + 2;

        // when
        int distance = 1;
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 선릉역.getId(), distance));
        lineService.addSection(이호선.getId(), new SectionRequest(삼성역.getId(), 역삼역.getId(), distance));

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
                .isThrownBy(() -> lineService.addSection(이호선.getId(), new SectionRequest(선릉역.getId(), 역삼역.getId(), distance)));
    }

    @DisplayName("상행역과 하행역이 이미 모선에 모두 등록되어 있다면 추가 시 에러 발생")
    @Test
    void addSectionAlreadyRegistered() {
        int distance = 1;
        assertAll(
                () -> assertThatExceptionOfType(SectionAlreadyRegisteredException.class)
                        .isThrownBy(() -> lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), distance))),
                () -> assertThatExceptionOfType(SectionAlreadyRegisteredException.class)
                        .isThrownBy(() -> lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 강남역.getId(), distance)))
        );
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가 시 에러 발생")
    @Test
    void addSectionStationNotFound() {
        int distance = 1;
        assertThatExceptionOfType(SectionNotSearchedException.class)
                .isThrownBy(() -> lineService.addSection(이호선.getId(), new SectionRequest(교대역.getId(), 삼성역.getId(), distance)));
    }

    @DisplayName("노선에서 구간을 삭제하면, 노선의 크기가 감소")
    @Test
    void removeSection() {
        // given
        int distance = 1;
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 삼성역.getId(), distance));
        int expected = 이호선.size() - 1;

        // when
        lineService.removeSection(이호선.getId(), 삼성역.getId());

        // then
        assertThat(이호선.size()).isEqualTo(expected);
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        assertThatExceptionOfType(EmptyLineException.class)
                .isThrownBy(() -> lineService.removeSection(이호선.getId(), 삼성역.getId()));
    }

    @DisplayName("마지막이 아닌 역을 삭제시 에러 발생")
    @Test
    void removeSectionInvalidUpStation() {
        // given
        int distance = 1;
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 삼성역.getId(), distance));

        // then
        assertThatExceptionOfType(NotLastStationException.class)
                .isThrownBy(() -> lineService.removeSection(이호선.getId(), 역삼역.getId()));
    }
}
