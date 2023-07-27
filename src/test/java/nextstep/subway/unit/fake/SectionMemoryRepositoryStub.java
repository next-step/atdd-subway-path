package nextstep.subway.unit.fake;

import java.util.List;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.infra.SectionRepository;

public class SectionMemoryRepositoryStub implements SectionRepository {

  private List<Section> sections;

  @Override
  public List<Section> findAll() {
    return sections;
  }

  public void setSections(List<Section> sections) {
    this.sections = sections;
  }
}
