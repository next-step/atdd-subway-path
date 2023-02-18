package nextstep.subway.domain.VO;

import lombok.Getter;
import nextstep.subway.domain.Section;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SectionsVO {

    List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        sections.add(section);
    }
}
