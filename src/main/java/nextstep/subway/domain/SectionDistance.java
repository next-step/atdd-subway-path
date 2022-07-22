package nextstep.subway.domain;

import static nextstep.subway.common.exception.errorcode.StatusErrorCode.*;

import nextstep.subway.common.exception.BusinessException;

public class SectionDistance {
	private int distance;

	protected SectionDistance() {
	}

	public SectionDistance(int distance) {
		this.distance = distance;
	}

	public int getDistance() {
		return distance;
	}

	public void validationOfDistance(SectionDistance distance) {
		if (this.distance <= distance.getDistance()) {
			throw new BusinessException(INVALID_STATUS);
		}
	}
}
