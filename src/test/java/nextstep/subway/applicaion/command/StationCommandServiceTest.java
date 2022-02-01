package nextstep.subway.applicaion.command;

import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.station.DuplicateStationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 역 관련 서비스 로직 검증")
@SpringBootTest
@Transactional
class StationCommandServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private StationCommandService stationCommandService;

    private Station 강남역;
    private Station 정자역;

    @BeforeEach
    void setUp() {
        강남역 = Station.of("강남역");
        정자역 = Station.of("정자역");
        stationRepository.save(강남역);
    }

    @DisplayName("역을 추가할 수 있다")
    @Test
    void stationQueryService() {
        // given
        StationRequest request = StationRequest.of(정자역.getName());

        // when
        StationResponse response = stationCommandService.saveStation(request);

        // then
        Station station = stationRepository.getById(response.getId());
        assertThat(station).isNotNull();
    }

    @DisplayName("같은 이름의 역을 추가할 수 없다")
    @Test
    void stationQueryService_fail() {
        // given
        StationRequest request = StationRequest.of(강남역.getName());

        // then
        assertThatThrownBy(() -> stationCommandService.saveStation(request))
                .isInstanceOf(DuplicateStationException.class);
    }

    @DisplayName("역을 삭제할 수 있다")
    @Test
    void deleteStationById() {
        // when
        stationCommandService.deleteStationById(강남역.getId());

        // then
        Optional<Station> station = stationRepository.findById(강남역.getId());
        assertThat(station.isPresent()).isFalse();
    }

}