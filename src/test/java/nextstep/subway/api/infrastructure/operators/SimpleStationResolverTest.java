package nextstep.subway.api.infrastructure.operators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.api.domain.model.entity.Station;
import nextstep.subway.api.infrastructure.persistence.StationRepository;

/**
 * @author : Rene Choi
 * @since : 2024/02/03
 */
@DisplayName(value = "런던파 스타일의 SimpleStationResolver 단위 테스트")
@ExtendWith(MockitoExtension.class)
class SimpleStationResolverTest {

	@InjectMocks
	private SimpleStationResolver simpleStationResolver;

	@Mock
	private StationRepository stationRepository;

	@Test
	@DisplayName("모든 역을 조회 - 성공 케이스")
	void fetchAll_Success() {
		// given
		Station station1 = mock(Station.class);
		Station station2 = mock(Station.class);
		List<Station> expectedStations = Arrays.asList(station1, station2);
		given(stationRepository.findAll()).willReturn(expectedStations);

		// when
		List<Station> actualStations = simpleStationResolver.fetchAll();

		// then
		assertEquals(expectedStations, actualStations, "조회된 역들이 예상과 일치해야 합니다.");
	}

	@Test
	@DisplayName("ID로 역 조회 - 성공 케이스")
	void fetchOptional_Success() {
		// given
		Long stationId = 1L;
		Station expectedStation = mock(Station.class);
		given(stationRepository.findById(stationId)).willReturn(Optional.of(expectedStation));

		// when
		Optional<Station> actualStation = simpleStationResolver.fetchOptional(stationId);

		// then
		assertTrue(actualStation.isPresent(), "역이 존재해야 합니다.");
		assertEquals(expectedStation, actualStation.get(), "조회된 역이 예상과 일치해야 합니다.");
	}
}
