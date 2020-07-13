package nextstep.subway.map.application;

import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.map.dto.MapResponse;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@DisplayName("노선도 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MapServiceTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    @Test
    @DisplayName("노선도 조회시 등록된 노선도가 없으면 빈 리스트를 담은 객체를 리턴한다.")
    void getEmptyResult() {
        //given
        MapService mapService = new MapService(lineRepository, stationRepository);
        when(lineRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        MapResponse maps = mapService.getMaps();

        //then
        assertThat(maps.getData()).isEmpty();
    }

    @Test
    @DisplayName("노선도 조회시 등록된 노선도를 응답한다")
    void getMaps() {

    }

}