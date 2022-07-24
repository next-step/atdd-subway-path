package nextstep.subway.domain;

import java.util.List;

public class LineSectionMaker {


    //구간 추가 유형 확인하고 진행하기
    public Section makeSection(SectionUpdatePosition position, List<Section> sections, Section section) {

        if (position.equals(SectionUpdatePosition.FIRST)
                || position.equals(SectionUpdatePosition.END)) {
            return null;
        }
        if (position.equals(SectionUpdatePosition.MIDDLE)) {
            return addSectionInMid(sections, section);
        }
        return null;
    }

    //역 사이에 새로운 역을 등록하는 경우
    public Section addSectionInMid(List<Section> sections, Section section) {

        Section updateTarget = sections.stream()
                .filter(sec -> sec.getUpStation().equals(section.getUpStation()))
                .findAny().orElseThrow(() -> new IllegalArgumentException("HAS_NO_TARGET_SECTION"));
        updateTarget.setUpStation(section.getDownStation());
        updateTarget.setDistance(updateTarget.getDistance() - section.getDistance());
        return updateTarget;
    }

}
