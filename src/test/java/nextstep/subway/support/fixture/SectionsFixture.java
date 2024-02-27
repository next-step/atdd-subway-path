package nextstep.subway.support.fixture;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.domians.domain.Section;
import nextstep.subway.domians.domain.Sections;
import org.springframework.test.util.ReflectionTestUtils;

public class SectionsFixture {


    public static Sections giveOne(List<Section> sectionList) {
        return new Sections(sectionList);
    }


}


