package nextstep.subway.applicaion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;

@Service
@Transactional(readOnly = true)
public class PathService {

	public PathResponse getPath(PathRequest pathRequest) {
		return null;
	}
}
