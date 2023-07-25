package nextstep.subway.entity.group.factory;

import java.util.List;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.group.factory.add.AddDownBoundSection;
import nextstep.subway.entity.group.factory.add.AddEndPoint;
import nextstep.subway.entity.group.factory.add.AddUpBoundSection;
import nextstep.subway.entity.group.factory.add.SectionAddAction;
import nextstep.subway.entity.group.factory.remove.SectionDeleteAction;
import nextstep.subway.entity.group.factory.remove.DeleteMiddleSection;
import nextstep.subway.entity.group.factory.remove.DeleteTerminalSection;

public interface SectionActionFactory {

     static SectionAddAction makeAdd(Section newSection, Section originSection) {

         //중간구간의 상행역에 추가하는가
         if (originSection.isEqualsUpStation(newSection.getUpStationId())) {
             return AddUpBoundSection.of(newSection, originSection);
         }

         //중간구간의 하행역에 추가하는가
         if (originSection.isEqualsDownStation(newSection.getDownStationId())) {
             return AddDownBoundSection.of(newSection, originSection);
         }

         //상행 혹은 하행 종점에 추가하는가
         return new AddEndPoint();
    }

    static SectionDeleteAction makeDelete(List<Section> deleteSections, Line line, long deleteStationId) {

        if (isTerminalSection(deleteSections)) {
            return new DeleteTerminalSection(deleteSections);
        }

        return new DeleteMiddleSection(deleteSections, line, deleteStationId);
    }

    static private boolean isTerminalSection(List<Section> deleteSections) {
        return deleteSections.size() < 2;
    }
}
