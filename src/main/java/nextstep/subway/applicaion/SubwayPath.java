package nextstep.subway.applicaion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nextstep.subway.domain.Station;

public class SubwayPath {

	private final List<Station> stations;

	private final double minimumDistance;

	public SubwayPath(List<Station> stations, double minimumDistance) {
		this.stations = new ArrayList<>(stations);
		this.minimumDistance = minimumDistance;
	}

	public List<Station> getStations() {
		return Collections.unmodifiableList(this.stations);
	}

	public double getMinimumDistance() {
		return this.minimumDistance;
	}
}
