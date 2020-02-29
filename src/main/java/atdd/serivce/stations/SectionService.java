package atdd.serivce.stations;

import atdd.domain.stations.Section;
import atdd.domain.stations.SectionRepository;
import atdd.web.dto.section.SectionCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SectionService {
    private final SectionRepository sectionRepository;

    public Section create(SectionCreateRequestDto dto){
        return sectionRepository.save(dto.toEntity());
    }


}
