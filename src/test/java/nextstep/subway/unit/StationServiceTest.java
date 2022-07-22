package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.List;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("지하철역 관련")
@SpringBootTest
@Transactional
public class StationServiceTest {

    @Autowired
    private StationService stationService;

    @Autowired
    private StationRepository stationRepository;

    @Test
    @DisplayName("지하철 역을 추가한다.")
    void createStation() {
        StationResponse 강남역 = stationService.saveStation(new StationRequest("강남역"));

        assertThat(강남역.getName()).isEqualTo("강남역");
    }

    @Test
    @DisplayName("지하철 목록을 조회한다.")
    void findAllStation() {
        stationRepository.save(new Station("강남역"));
        stationRepository.save(new Station("역삼역"));

        List<StationResponse> 노선목록 = stationService.findAllStations();

        assertThat(노선목록).hasSize(2);
    }

    @Test
    @DisplayName("지하철 역을 조회한다.")
    void findByStation() {
        Long id = stationRepository.save(new Station("강남역")).getId();

        Station 강남역 = stationService.findById(id);

        assertThat(강남역.getName()).isEqualTo("강남역");
    }

    @Test
    @DisplayName("지하철역 삭제한다.")
    void deleteStationById() {
        Long id = stationRepository.save(new Station("강남역")).getId();

        stationService.deleteStationById(id);

        assertThatIllegalArgumentException().isThrownBy(() -> {
            stationService.findById(id);
        });
    }

}
