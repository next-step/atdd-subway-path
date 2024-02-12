package nextstep.subway.section.service;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public Section save(Section section) {
        return sectionRepository.save(section);
    }

}
