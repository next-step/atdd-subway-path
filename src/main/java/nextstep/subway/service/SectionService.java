package nextstep.subway.service;

import nextstep.subway.entity.Section;
import nextstep.subway.repository.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SectionService {
	private SectionRepository sectionRepository;

	public SectionService(SectionRepository sectionRepository) {
		this.sectionRepository = sectionRepository;
	}

	public List<Section> findAll() {
		return sectionRepository.findAll();
	}
}
