package subway.db.h2.adapter;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import subway.application.query.out.PathStationsLoadPort;
import subway.common.annotation.UnitTest;
import subway.db.h2.entity.StationJpa;
import subway.db.h2.mapper.PathStationMapper;
import subway.db.h2.repository.StationRepository;
import subway.domain.PathStation;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@UnitTest
@DisplayName("역 조회 기능 단위 테스트")
class PathStationsLoadPersistenceAdapterTest {
    private StationRepository stationRepository = Mockito.mock(StationRepository.class);
    private PathStationMapper pathStationMapper = new PathStationMapper();
    private final PathStationsLoadPort pathStationsLoadPort = new PathStationsLoadPersistenceAdapter(stationRepository, pathStationMapper);

    /**
     * @given 역들이 존재한다고 한다면
     * @when id로 조회할 때
     * @then 조회할 수 있다.
     */
    @Test
    @DisplayName("id로 역들을 조회할 수 있다.")
    void findAllById() {
        //given

        Long 강남역DbId = 1L;
        Long 역삼역DbId = 2L;
        StationJpa 강남역 = StationJpa.of(강남역DbId, "강남역");
        StationJpa 역삼역 = StationJpa.of(역삼역DbId, "역삼역");

        given(stationRepository.findAllByIdIn(List.of(강남역DbId, 역삼역DbId)))
                .willReturn(List.of(강남역, 역삼역));

        //when
        PathStation.Id 강남역Id = PathStation.Id.of(강남역DbId);
        PathStation.Id 역삼역Id = PathStation.Id.of(역삼역DbId);

        PathStationsLoadPort.Response response = pathStationsLoadPort.findAllBy(List.of(강남역Id, 역삼역Id));

        //then
        assertThat(response)
                .extracting("pathStations")
                .asInstanceOf(InstanceOfAssertFactories.MAP)
                .containsKeys(강남역Id, 역삼역Id)
                .satisfies(map -> {
                    assertThat(map)
                            .extractingByKey(강남역Id)
                            .extracting("id", "name")
                            .containsExactly(강남역Id, 강남역.getName());
                    assertThat(map)
                            .extractingByKey(역삼역Id)
                            .extracting("id", "name")
                            .containsExactly(역삼역Id, 역삼역.getName());});

    }

}