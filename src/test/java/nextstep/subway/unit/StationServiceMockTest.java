package nextstep.subway.unit;

import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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
    void 역을_추가한다() {
        // given
        given(stationRepository.save(any())).willReturn(new Station("암사역"));

        // when
        StationResponse response = stationService.saveStation(new StationRequest("암사역"));

        // then
        assertThat(response.getName()).isEqualTo("암사역");
    }

    @Test
    void 역_목록을_조회한다() {
        // given
        given(stationRepository.findAll()).willReturn(
                List.of(
                        new Station("암사역"), new Station("모란역")
                )
        );

        // when
        List<StationResponse> allStations = stationService.findAllStations();

        // then
        assertThat(allStations).hasSize(2);
    }

    @Test
    void 역을_삭제한다() {
        // when
        stationService.deleteStationById(1L);

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> stationService.findById(1L));
    }
}
