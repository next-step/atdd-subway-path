package nextstep.subway.section.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.repository.SectionRepository;
import nextstep.subway.station.domain.Station;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SectionReadService {
    private final SectionRepository sectionRepository;

    public Section getSection(Station station) {
        return sectionRepository.findByUpStation(station);
    }
}
