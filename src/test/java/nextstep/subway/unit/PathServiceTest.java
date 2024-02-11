package nextstep.subway.unit;


import nextstep.subway.application.LineService;
import nextstep.subway.application.PathService;
import nextstep.subway.application.StationService;
import nextstep.subway.application.dto.PathResponse;
import nextstep.subway.application.dto.StationRequest;
import nextstep.subway.application.dto.StationResponse;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class PathServiceTest {
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    @Autowired
    private PathService pathService;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @BeforeEach
    void init() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");
        stationRepository.save(교대역);
        stationRepository.save(강남역);
        stationRepository.save(양재역);
        stationRepository.save(남부터미널역);

        이호선 = new Line(1L, "2호선", "green", 교대역, 강남역, 10);
        신분당선 = new Line(2L, "신분당선", "red", 강남역, 양재역, 10);
        삼호선 = new Line(3L, "3호선", "orange", 교대역, 남부터미널역, 2);
        lineRepository.save(이호선);
        lineRepository.save(신분당선);
        lineRepository.save(삼호선);
    }

    @DisplayName("최단거리 경로를 조회시, 지하철역이 존재 하지 않으면 예외가 발생한다.")
    @Test
    void findPath_invalid_not_found_station() {
        // Given
        Long source = 99L;
        Long target = 양재역.getId();

        // When
        assertThatThrownBy(() -> { pathService.findPath(source, target); })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재 하지 않는 지하철역 입니다.");
    }
}
