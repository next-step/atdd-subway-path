package nextstep.subway.api.domain.model.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author : Rene Choi
 * @since : 2024/02/03
 */
@ExtendWith(MockitoExtension.class)
class LineTest {

	@InjectMocks
	private Line line;

	@Mock
	private Sections sections;

	@Test
	@DisplayName("라인 이름 업데이트 - 성공 케이스")
	void updateName_Success() {
		// given
		String newName = "새로운 라인";
		line.setName("기존 라인");

		// when
		Line updatedLine = line.updateName(newName);

		// then
		assertEquals(newName, updatedLine.getName(), "라인 이름이 올바르게 업데이트 되어야 한다.");
	}

	@Test
	@DisplayName("특정 역 포함 여부 확인 - 성공 케이스")
	void isContainsAnyStation_Success() {
		// given
		Long stationId = 1L;
		given(sections.isContainsAnyStation(stationId)).willReturn(true);

		// when
		boolean result = line.isContainsAnyStation(stationId);

		// then
		assertTrue(result, "라인이 특정 역을 포함해야 한다.");
	}

	@Test
	@DisplayName("라인 색상 업데이트 - 성공 케이스")
	void updateColor_Success() {
		// given
		String newColor = "bg-blue-500";
		line.setColor("bg-red-600");

		// when
		Line updatedLine = line.updateColor(newColor);

		// then
		assertEquals(newColor, updatedLine.getColor(), "라인 색상이 올바르게 업데이트 되어야 한다.");
	}

	@Test
	@DisplayName("마지막 구간 제거 - 성공 케이스")
	void removeLastSection_Success() {
		// when
		line.removeLastSection();

		// then
		verify(sections).removeLastSection();
	}

	@Test
	@DisplayName("구간 수가 임계값 이하인지 확인 - 성공 케이스")
	void isSectionCountBelowThreshold_Success() {
		// given
		long threshold = 3L;
		given(sections.isSizeBelow(threshold)).willReturn(true);

		// when
		boolean result = line.isSectionCountBelowThreshold(threshold);

		// then
		assertTrue(result, "구간 수가 임계값 이하이면 true를 반환해야 한다.");
	}

	@Test
	@DisplayName("하행 종점 역 확인 - 성공 케이스")
	void isDownEndStation_Success() {
		// given
		Long stationId = 2L;
		given(sections.isDownEndStation(stationId)).willReturn(true);

		// when
		boolean result = line.isDownEndStation(stationId);

		// then
		assertTrue(result, "해당 역이 하행 종점 역이면 true를 반환해야 한다.");
	}

	@Test
	@DisplayName("하행 종점 역이 아님 확인 - 성공 케이스")
	void isNotDownEndStation_Success() {
		// given
		Long stationId = 3L;
		given(sections.isDownEndStation(stationId)).willReturn(false);

		// when
		boolean result = line.isNotDownEndStation(stationId);

		// then
		assertTrue(result, "해당 역이 하행 종점 역이 아니면 true를 반환해야 한다.");
	}

}
