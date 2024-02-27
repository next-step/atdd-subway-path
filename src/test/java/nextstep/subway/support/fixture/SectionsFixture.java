package nextstep.subway.support.fixture;

import java.util.List;
import nextstep.subway.domians.domain.Section;
import nextstep.subway.domians.domain.Sections;

public class SectionsFixture {


    public static Sections giveOne(List<Section> sectionList) {
        return new Sections(sectionList);
    }


}


