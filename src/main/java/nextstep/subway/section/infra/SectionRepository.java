package nextstep.subway.section.infra;

import java.util.List;
import nextstep.subway.section.domain.Section;

public interface SectionRepository {
  List<Section> findAll();
}
