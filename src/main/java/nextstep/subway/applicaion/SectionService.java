package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.SectionDto;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;

    public List<SectionDto> findAll() {
        return sectionRepository.findAll()
                .stream()
                .map(SectionDto::from)
                .collect(Collectors.toList());
    }

}
