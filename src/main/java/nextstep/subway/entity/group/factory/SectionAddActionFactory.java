package nextstep.subway.entity.group.factory;

import nextstep.subway.entity.Section;

public interface SectionAddActionFactory {

     static SectionAddAction make(long upStationId, long downStationId, Section currentSection) {

        if (currentSection.isUpEndPointSection()
            && currentSection.getUpStationId().equals(downStationId)) {
            return new AddAscendingEndPoint();
        }

        if (currentSection.getUpStationId().equals(upStationId)) {
            return new AddUpBoundSection();
        }

        if (currentSection.getDownStationId().equals(downStationId)) {
            return new AddDownBoundSection();
        }

        if (currentSection.isDownEndPointSection()
            && currentSection.getDownStationId().equals(upStationId)) {

            return new AddDescendingEndPoint();
        }

        return new AddNone();
    }

}
