package atdd.line.service;

import atdd.line.domain.Line;
import atdd.line.dto.CreateLineAndStationsRequest;
import atdd.line.dto.CreateLineRequest;
import atdd.line.dto.FindLineResponse;

import java.util.List;

public interface LineService {

	Line save(CreateLineRequest request);

	Line saveLineAndStations(CreateLineAndStationsRequest request);

	List<Line> findAll();

	FindLineResponse findByName(String name);
}
