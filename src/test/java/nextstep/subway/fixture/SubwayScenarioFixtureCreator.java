package nextstep.subway.fixture;

import static nextstep.subway.fixture.LineFixtureCreator.*;
import static nextstep.subway.fixture.SectionFixtureCreator.*;
import static nextstep.subway.utils.resthelper.ExtractableResponseParser.*;
import static nextstep.subway.utils.resthelper.LineRequestExecutor.*;
import static nextstep.subway.utils.resthelper.SectionRequestExecutor.*;
import static nextstep.subway.utils.resthelper.StationRequestExecutor.*;

/**
 * @author : Rene Choi
 * @since : 2024/02/11
 */
public class SubwayScenarioFixtureCreator {


	public static long createStation(String name) {
		return parseId(executeCreateStationRequest(name));
	}

	public static long createLine(String name, long upStationId, long downStationId, long distance) {
		return parseId(executeCreateLineRequest(createLineCreateRequest(name, upStationId, downStationId, distance)));
	}

	public static void createSection(long lineId, long upStationId, long downStationId, long distance) {
		executeCreateSectionRequest(lineId, createSectionCreateRequestWithUpAndDownAndDistance(upStationId, downStationId, distance));
	}
}
