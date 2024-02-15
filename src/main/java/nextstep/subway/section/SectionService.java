package nextstep.subway.section;

import static org.springframework.util.Assert.notNull;

import java.util.Objects;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class SectionService {
    private final static String REQUIRED_SECTION = "노선 등록시 구간 등록은 필수입니다.";
    private final LineService lineService;

    public SectionService(LineService lineService) {
        this.lineService = lineService;
    }

    @Transactional
    public void saveSection(Long lineId, SectionRequest request) {
        Section section = request.toEntity();
        Line line = lineService.findLineById(lineId);
        line.addSection(section);
    }

    @Transactional
    public void removeSection(Long lineId, Long stationId) {
        Line line = lineService.findLineById(lineId);
        line.removeStation(stationId);
    }
}
