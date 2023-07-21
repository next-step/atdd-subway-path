package nextstep.subway.fixture.unit.group;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.group.SectionGroup;
import nextstep.subway.fixture.unit.entity.StationFixture;
import org.mockito.BDDMockito;

public class SectionGroupFixture {

    private static final Line line = BDDMockito.mock(Line.class);
    private static final int distance = 10;

    public static SectionGroup make() {
        return SectionGroup.of(
            List.of(
                new Section(line, StationFixture.of(1), StationFixture.of(3), distance),
                new Section(line, StationFixture.of(3), StationFixture.of(5), distance),
                new Section(line, StationFixture.of(5), StationFixture.of(7), distance)
            )
        );
    }

    public static SectionGroup make(List<Section> sections, Section section) {

        List<Section> list = new ArrayList<>(sections);
        list.add(section);

        return SectionGroup.of(list);
    }
}
