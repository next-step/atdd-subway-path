package nextstep.subway.line.service;

import nextstep.subway.line.dto.UpdateLineRequest;
import nextstep.subway.line.repository.Line;
import nextstep.subway.section.dto.CreateSectionRequest;

public interface LineUpdatable {
    void updateLineById(Long id, UpdateLineRequest request);

    Line addSection(Long id, CreateSectionRequest request);

    void deleteSectionByStationId(Long lineId, Long stationId);
}
