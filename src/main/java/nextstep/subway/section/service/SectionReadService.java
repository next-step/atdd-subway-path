package nextstep.subway.section.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.repository.SectionRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SectionReadService {
    private final SectionRepository sectionRepository;

    public List<Section> getAll() {
        return sectionRepository.findAll();
    }
}
