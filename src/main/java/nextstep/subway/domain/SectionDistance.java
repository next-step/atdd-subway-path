package nextstep.subway.domain;

import static nextstep.subway.common.exception.errorcode.BusinessErrorCode.*;

import nextstep.subway.common.exception.BusinessException;

public class SectionDistance {
	private int distance;

	protected SectionDistance() {
	}

	public SectionDistance(int distance) {
		if (distance <= 0) {
			throw new BusinessException(INVALID_STATUS);
		}
		this.distance = distance;
	}

	public int getDistance() {
		return distance;
	}

	public void validationOfDistance(long distance) {
		if (this.distance <= distance) {
			throw new BusinessException(INVALID_STATUS);
		}
	}

	public void plusDistance(int addDistance) {
		this.distance = this.distance + addDistance;
	}
}
