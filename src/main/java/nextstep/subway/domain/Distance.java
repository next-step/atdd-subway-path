package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {

	@Column
	private int distance;

	protected Distance() {
	}

	public Distance(int distance) {
		this.distance = distance;
	}

	public static Distance valueOf(int distance) {
		return new Distance(distance);
	}

	public int getDistance() {
		return distance;
	}

	public Distance minus(Distance other) {
		return Distance.valueOf(distance - other.distance);
	}

	public boolean isLessThan(Distance oldSectionDistance) {
		return distance < oldSectionDistance.distance;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Distance distance1 = (Distance) o;
		return distance == distance1.distance;
	}

	@Override
	public int hashCode() {
		return Objects.hash(distance);
	}
}
