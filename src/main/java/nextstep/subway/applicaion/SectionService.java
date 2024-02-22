package nextstep.subway.applicaion;

import java.util.List;
import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SectionService {

  private final SectionRepository sectionRepository;

  public List<Section> findAll() {
    return sectionRepository.findAll();
  }
}
