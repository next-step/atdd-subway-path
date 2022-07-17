package nextstep.subway.unit;

import static nextstep.subway.unit.LineTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class SectionTest {

	Line line;

	@BeforeEach
	void setUp() {
		line = new Line(1L, "신분당선", "color");
		line.addSection(강남역, 역삼역, 8);
	}

	@DisplayName("section 이 한개인경우 역 조회")
	@Test
	void getFirstStation() {

		//when
		List<Station> stations = line.getStations();
		//then
		assertThat(stations).hasSize(2)
			.containsExactly(강남역, 역삼역);
	}

	@DisplayName("section 이 2개 이상경우 역 조회")
	@Test
	void getStations() {
		//given
		line.addSection(역삼역, 선릉역, 8);
		//when
		List<Station> stations = line.getStations();
		//then
		assertThat(stations).hasSize(3)
			.containsExactly(강남역, 역삼역, 선릉역);
	}
	
}
