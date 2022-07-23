package nextstep.subway.unit;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StationServiceMockTest {

    @InjectMocks
    private StationService stationService;

    @Mock
    private StationRepository stationRepository;

    @Test
    @DisplayName("지하철 역을 추가한다.")
    void createStation() {
        given(stationRepository.save(any())).willReturn(new Station("강남역"));

        StationResponse 강남역 = stationService.saveStation(new StationRequest("강남역"));

        assertThat(강남역.getName()).isEqualTo("강남역");
    }

    @Test
    @DisplayName("지하철 목록을 조회힌다.")
    void findAllStation() {
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        List<StationResponse> 비교값 = List.of(강남역, 역삼역).stream()
            .map(StationResponse::of)
            .collect(Collectors.toList());

        given(stationRepository.findAll()).willReturn(List.of(강남역, 역삼역));

        List<StationResponse> 노선목록 = stationService.findAllStations();

        assertThat(노선목록).isEqualTo(비교값);
    }

    @Test
    @DisplayName("지하철 역을 조회한다.")
    void findByStation() {
        given(stationRepository.findById(any())).willReturn(Optional.of(new Station("강남역")));

        Station 강남역 = stationService.findById(1L);

        assertThat(강남역).isEqualTo(new Station("강남역"));
    }

    @Test
    @DisplayName("지하철역 삭제한다.")
    void deleteStationById() {
        stationService.deleteStationById(1L);

        assertThatIllegalArgumentException().isThrownBy(() -> {
            stationService.findById(1L);
        });
    }

}
