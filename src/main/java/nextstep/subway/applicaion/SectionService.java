package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.SectionResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public List<SectionResponse> showSections(Line line) {
        return sectionRepository.findAllByLine(line).stream()
            .map(section -> new SectionResponse(section.getId(), StationResponse.from(section.getUpStation()), StationResponse.from(section.getDownStation()), section.getDistance()))
            .collect(Collectors.toList());
    }

    public List<Section> findAllSections() {
        return sectionRepository.findAll();
    }

}
