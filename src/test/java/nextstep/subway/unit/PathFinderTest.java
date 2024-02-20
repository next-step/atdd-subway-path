package nextstep.subway.unit;

import nextstep.subway.domain.PathFinder;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PathFinderTest {
	private static final Long 첫번째_역 = 1L;
	private static final Long 두번째_역 = 2L;
	private static final Long 세번째_역 = 3L;
	private PathFinder pathFinder;

	@BeforeEach
	void setup() {
		Line 노선1 = new Line("노선1", "파란색", 첫번째_역, 세번째_역, 10);
		Line 노선2 = new Line("노선2", "빨간색", 첫번째_역, 두번째_역, 5);

		pathFinder = new PathFinder(List.of(
				new Section(노선1, 첫번째_역, 세번째_역, 10)
				,new Section(노선2, 첫번째_역, 두번째_역, 5)
				,new Section(노선2, 두번째_역, 세번째_역, 4)
		));
	}
	@Test
	@DisplayName("경로 조회")
	void getPath() {
		assertThat(pathFinder.getPath(첫번째_역, 세번째_역)).containsExactly(첫번째_역, 두번째_역, 세번째_역);
	}

	@Test
	@DisplayName("최단 거리 조회")
	void getDistance() {
		assertThat(pathFinder.getDistance(첫번째_역, 세번째_역)).isEqualTo(9);
	}
}
