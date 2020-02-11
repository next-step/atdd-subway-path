package atdd.station.fixture;

import atdd.station.domain.Subway;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static atdd.station.fixture.StationFixture.KANNAM_STATON;
import static atdd.station.fixture.StationFixture.PANGYO;
import static atdd.station.fixture.SubwayLineFixture.NEW_BUNDANG_SUBWAY_LINE;
import static atdd.station.fixture.SubwayLineFixture.SECOND_SUBWAY_LINE;

public class SubwaysFixture {
    public static final Subway SECOND_SUBWAY = new Subway(KANNAM_STATON, SECOND_SUBWAY_LINE);
    public static final Subway NEW_BUNDANG_SUBWAY = new Subway(PANGYO, NEW_BUNDANG_SUBWAY_LINE);

    public static final List<Subway> SUBWAYS = new ArrayList<>(Arrays.asList(SECOND_SUBWAY, NEW_BUNDANG_SUBWAY));
}
