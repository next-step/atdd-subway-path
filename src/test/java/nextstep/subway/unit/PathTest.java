package nextstep.subway.unit;

import nextstep.subway.domain.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.domain.Path.addPath;
import static org.assertj.core.api.Assertions.assertThat;

public class PathTest {
	private Path path;

	@Test
	@DisplayName("경로 조회")
	void getPath() {
		path.addPath(1L, 2L, 12);
		path.addPath(1L, 3L, 10);
		path.addPath(3L, 2L, 3);

		assertThat(path.getPath(1L, 2L)).containsExactly(1L, 2L);
	}
}
