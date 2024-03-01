package nextstep.subway.support.fixture;

import java.util.List;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Sections;

public class SectionsFixture {


    public static Sections giveOne(List<Section> sectionList) {
        return new Sections(sectionList);
    }


}


