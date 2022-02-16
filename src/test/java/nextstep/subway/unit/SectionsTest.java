package nextstep.subway.unit;

import nextstep.subway.applicaion.exception.NotExistSectionException;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {
	final Station 강남역 = Station.of("강남역");
	final Station 역삼역 = Station.of("역삼역");
	final Station 합정역 = Station.of("합정역");
	final Station 홍대역 = Station.of("홍대역");

	Sections 구간들;

	final int 거리 = 100;

	/**
	 * Given 노선에 3개의 지하철역을 등록하였다고 가정한 후
	 */
	@BeforeEach
	public void setUp() {
		ReflectionTestUtils.setField(강남역, "id", 1L);
		ReflectionTestUtils.setField(역삼역, "id", 2L);
		ReflectionTestUtils.setField(합정역, "id", 3L);
		ReflectionTestUtils.setField(홍대역, "id", 4L);

		final Line 이호선 = Line.of("2호선", "bg-green-600", 강남역, 역삼역, Distance.from(100));
		ReflectionTestUtils.setField(이호선, "id", 1L);

		이호선.addSection(역삼역, 합정역, Distance.from(100));

		구간들 = (Sections) ReflectionTestUtils.getField(이호선, "sections");
		List<Section> sections = 구간들.getSections();

		IntStream.rangeClosed(1, 2).forEach(index -> {
			ReflectionTestUtils.setField(sections.get(index - 1), "id", Long.valueOf(index));
		});
	}

	/**
	 * When 노선의 중간 구간을 제거하면
	 * Then 노선에 해당 구간이 삭제된다.
	 */
	@DisplayName("구간 목록 중간을 삭제할 경우")
	@Test
	void deleteMiddleSection() {
		//when
		구간들.deleteSection(역삼역);

		//then
		assertThat(구간들.getStations()).containsExactly(강남역, 합정역);
		assertThat(구간들.getSections())
				.extracting(Section::getDistance)
				.containsExactly(200);
	}

	/**
	 * When 노선의 첫 구간을 제거하면
	 * Then 노선에 해당 구간이 삭제된다.
	 */
	@DisplayName("구간 목록 첫 구간을 삭제할 경우")
	@Test
	void deleteFirstSection() {
		//when
		구간들.deleteSection(강남역);

		//then
		assertThat(구간들.getStations()).containsExactly(역삼역, 합정역);
		assertThat(구간들.getSections())
				.extracting(Section::getDistance)
				.containsExactly(거리);
	}

	/**
	 * When 노선의 마지막 구간을 제거하면
	 * Then 노선에 해당 구간이 삭제된다.
	 */
	@DisplayName("구간 목록 마지막 구간을 삭제할 경우")
	@Test
	void deleteLastSection() {
		//when
		구간들.deleteSection(합정역);

		//then
		assertThat(구간들.getStations()).containsExactly(강남역, 역삼역);
		assertThat(구간들.getSections())
				.extracting(Section::getDistance)
				.containsExactly(거리);
	}

	/**
	 * When 노선에 존재하지 않는 구간을 제거요청하면
	 * Then 노선의 구간 삭제가 실패된다.
	 */
	@DisplayName("구간 목록에 존재하지 않는 구간을 삭제할 경우")
	@Test
	void deleteNotExistSection() {
		//when & then
		assertThatThrownBy(() -> 구간들.deleteSection(홍대역))
				.isInstanceOf(NotExistSectionException.class);
	}
}
