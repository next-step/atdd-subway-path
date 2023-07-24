package nextstep.subway.entity.group.factory;

import nextstep.subway.entity.Section;

public interface SectionAddActionFactory {

     static SectionAddAction make(Section newSection, Section originSection) {

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

}
