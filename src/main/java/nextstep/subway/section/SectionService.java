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
        validSection(section, line.getDownStationId(), line);
        line.addSection(section);
    }

    @Transactional
    public void removeSection(Long lineId, Long stationId) {
        Line line = lineService.findLineById(lineId);
        line.removeStation(stationId);
    }

    private void validSection(Section section, Long lastDownStationId, Line line) {
        notNull(section, REQUIRED_SECTION);
        if(!Objects.equals(section.getUpStationId(), lastDownStationId)) {
            throw new IllegalArgumentException("새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 합니다");
        }
        //이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없다.
        if(line.isDupleCate(section)) {
            throw new IllegalArgumentException("이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없습니다");
        }
    }
}
