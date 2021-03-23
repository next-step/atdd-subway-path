package nextstep.subway.line.domain.support;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;

import java.util.List;

public class SectionsDomainBuilder {

    public static Sections sectionsBuild(List<Integer> distances, Station ...stations){
        Sections sections = new Sections();
        for(int i = 0; i < stations.length - 1 ; i++){
            sections.addSection(new Section(new Line("1호선", "블루"), stations[i], stations[i+1], distances.get(i)));
        }
        return sections;
    }

}
