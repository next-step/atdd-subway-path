package nextstep.subway.applicaion.dto;

import static nextstep.subway.common.exception.errorcode.BusinessErrorCode.*;

import org.springframework.util.ObjectUtils;

import nextstep.subway.common.exception.BusinessException;

public class PathRequest {

	private Long source;
	private Long target;

	public PathRequest(Long source, Long target) {
		this.source = source;
		this.target = target;
	}

	public long getSource() {
		return source;
	}

	public long getTarget() {
		return target;
	}

	public void validationOfStation() {
		nullValidation();
	}

	private void nullValidation() {
		if (ObjectUtils.isEmpty(source)) {
			throw new BusinessException(INVALID_STATUS);
		}
		if (ObjectUtils.isEmpty(target)) {
			throw new BusinessException(INVALID_STATUS);
		}
	}

}
