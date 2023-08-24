package subway.application.query;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import subway.application.mapper.PathResponseMapper;
import subway.application.query.in.PathQuery;
import subway.application.query.out.PathStationsLoadPort;
import subway.application.query.out.PathSearcherLoadPort;
import subway.application.query.validator.PathQueryCommandValidator;
import subway.application.response.PathResponse;
import subway.common.annotation.UnitTest;
import subway.core.ValidationError;
import subway.core.ValidationErrorException;
import subway.db.h2.adapter.PathSearcherLoadPersistenceAdapter;
import subway.db.h2.adapter.PathStationsLoadPersistenceAdapter;
import subway.db.h2.entity.StationJpa;
import subway.db.h2.entity.SubwaySectionJpa;
import subway.db.h2.mapper.PathSectionMapper;
import subway.db.h2.mapper.PathStationMapper;
import subway.db.h2.repository.StationRepository;
import subway.db.h2.repository.SubwaySectionJpaRepository;
import subway.domain.Kilometer;
import subway.domain.PathStation;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@UnitTest
@DisplayName("경로 조회 기능 테스트")
class PathQueryServiceTest {

    private final StationRepository stationRepository = Mockito.mock(StationRepository.class);
    private final PathStationMapper pathStationMapper = new PathStationMapper();
    private final SubwaySectionJpaRepository subwaySectionJpaRepository = Mockito.mock(SubwaySectionJpaRepository.class);
    private final PathSectionMapper pathSectionMapper = new PathSectionMapper();
    private final PathStationsLoadPort pathStationsLoadPort = new PathStationsLoadPersistenceAdapter(stationRepository, pathStationMapper);
    private final PathSearcherLoadPort pathSearcherLoadPort = new PathSearcherLoadPersistenceAdapter(subwaySectionJpaRepository, pathSectionMapper);
    private final PathResponseMapper pathResponseMapper = new PathResponseMapper();
    private final PathQuery pathQuery = new PathQueryService(pathStationsLoadPort, pathSearcherLoadPort, pathResponseMapper);

    /**
     * @given 경로 조회할 역들이 존재하고
     * @given 경로가 존재한다면
     * @when 경로 조회를 할 때
     * @then 결과를 조회할 수 있다.
     */
    @Test
    @DisplayName("경로 조회할 역들이 존재하고 경로가 존재한다면 결과를 조회할 수 있다.")
    void findPathShortestDistance() {
        //given
        StationJpa 강남역_데이터 = StationJpa.of(1L, "강남역");
        StationJpa 역삼역_데이터 = StationJpa.of(2L, "역삼역");
        given(stationRepository.findAllByIdIn(BDDMockito.anyList()))
                .willReturn(List.of(강남역_데이터, 역삼역_데이터));

        //given
        SubwaySectionJpa 구간 = SubwaySectionJpa.of(1L, 강남역_데이터.getId(), 강남역_데이터.getName(), 역삼역_데이터.getId(), 역삼역_데이터.getName(), BigDecimal.valueOf(10));
        given(subwaySectionJpaRepository.findAll())
                .willReturn(List.of(구간));

        //when
        PathQuery.Command command = PathQuery.Command.builder()
                .startStationId(PathStation.Id.of(1L))
                .endStationId(PathStation.Id.of(2L))
                .validator(new PathQueryCommandValidator())
                .build();
        PathResponse response = pathQuery.findOne(command);

        //then
        assertAll(
                () -> assertThat(response)
                        .extracting("stations").asList()
                        .hasSize(2)
                        .extracting("id", "name")
                        .containsExactly(tuple(1L, "강남역"), tuple(2L, "역삼역")),
                () -> assertThat(response)
                        .isNotNull()
                        .extracting("distance")
                        .isEqualTo(10.0)
        );
    }

    /**
     * @given 경로 조회할 역들이 존재하지 않다면
     * @when 경로 조회를 할 때
     * @then 예외를 반환한다.
     */
    @Test
    @DisplayName("경로 조회할 역들이 존재하지 않다면 경로 조회를 할 수 없다.")
    void cantSearchNotExistStation() {
        //given
        given(stationRepository.findAllByIdIn(BDDMockito.anyList()))
                .willReturn(List.of());
        //when
        PathQuery.Command command = PathQuery.Command.builder()
                .startStationId(PathStation.Id.of(1L))
                .endStationId(PathStation.Id.of(2L))
                .validator(new PathQueryCommandValidator())
                .build();

        Throwable throwable = catchThrowable(() -> pathQuery.findOne(command));

        //then
        assertThat(throwable)
                .isInstanceOf(NoSuchElementException.class);
    }

    /**
     * @given 경로 조회 승하차역을 같은 역으로 요청한다면
     * @when 경로 조회를 할 때
     * @then 예외를 반환한다.
     */
    @Test
    @DisplayName("경로 조회시에 같은 역을 승하차역으로 요청할 수 없다.")
    void cantRequestSameStations() {
        //given

        PathQuery.Command.CommandBuilder commandBuilder = PathQuery.Command.builder()
                .startStationId(PathStation.Id.of(1L))
                .endStationId(PathStation.Id.of(1L))
                .validator(new PathQueryCommandValidator());

        //when
        Throwable throwable = catchThrowable(commandBuilder::build);

        //then
        assertThat(throwable)
                .isInstanceOf(ValidationErrorException.class)
                .extracting("errors").asList()
                .hasSize(1)
                .extracting("field", "message")
                .containsExactly(tuple("startStationId, endStationId", " are same"));
    }

    /**
     * @given 경로 조회 시에 요청 중 하나라도 값이 null이라면
     * @when 경로 조회를 할 때
     * @then 예외를 반환한다.
     */
    @Test
    @DisplayName("경로 조회 시에 요청 값이 하나라도 값이 null이면 요청이 불가능하다.")
    void cantRequestNullProperty() {
        //given
        PathQuery.Command.CommandBuilder commandBuilder = PathQuery.Command.builder()
                .startStationId(null)
                .endStationId(null)
                .validator(new PathQueryCommandValidator());

        //when
        Throwable throwable = catchThrowable(commandBuilder::build);

        //then
        assertThat(throwable)
                .isInstanceOf(ValidationErrorException.class)
                .extracting("errors").asList()
                .hasSize(1)
                .extracting("field", "message")
                .containsExactly(tuple("startStationId", " is null"));
    }
}