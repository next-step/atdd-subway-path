package nextstep.subway.unit.fixture;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

public class LineFixture {
    private static Long id = 1L;

    public static Line 지하철_노선_생성(String name, String color, Station upStation, Station downStation, int distance) {
        Line line = new Line(name,color,upStation,downStation,distance);

        ReflectionTestUtils.setField(line,"id",id++);

        for (Section section : line.getSections()) {
            ReflectionTestUtils.setField(section,"id",SectionFixture.useId());
        }
        return line;
    }

    public static void setSectionsId(Line line) {
        for(Section section : line.getSections()){
            if(section.getId() == null){
                ReflectionTestUtils.setField(section,"id",SectionFixture.useId());
            }
        }
    }
}
