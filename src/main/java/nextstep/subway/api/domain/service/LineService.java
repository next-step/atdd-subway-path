package nextstep.subway.api.domain.service;

import java.util.List;

import nextstep.subway.api.domain.dto.inport.LineCreateCommand;
import nextstep.subway.api.domain.dto.inport.LineUpdateCommand;
import nextstep.subway.api.interfaces.dto.response.LineResponse;

/**
 * @author : Rene Choi
 * @since : 2024/01/27
 */
public interface LineService {
	LineResponse saveLine(LineCreateCommand createRequest);

	List<LineResponse> findAllLines();

	LineResponse findLineById(Long id);


	LineResponse updateLineById(Long id, LineUpdateCommand updateRequest);

	void deleteLineById(Long id);
}
