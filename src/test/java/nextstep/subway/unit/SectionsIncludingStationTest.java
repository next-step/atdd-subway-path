package nextstep.subway.unit;

import nextstep.subway.domain.Section;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static nextstep.subway.unit.LineFixtures.구간_생성;

class SectionsIncludingStationTest {
    private Set<Section> sections = new HashSet<>();

    @BeforeEach
    void setUp() {
        sections.add(구간_생성(1L, 1L, 2L, 100));
        sections.add(구간_생성(2L, 2L, 3L, 200));
    }

    @Test
    void name() {
//        new SectionsIncludingStation(sections, )
    }
}
