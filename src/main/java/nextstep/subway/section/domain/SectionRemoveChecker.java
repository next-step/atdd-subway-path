package nextstep.subway.section.domain;

import java.util.List;
import nextstep.subway.station.domain.Station;

public class SectionRemoveChecker {


    private Section upwardIncludeSection = null;
    private Section downwardIncludeSection = null;

    public SectionRemoveChecker(List<Section> sectionList, Station targetStation) {
        for (Section section : sectionList) {
            if (section.hasSameUpwardStation(targetStation)) {
                upwardIncludeSection = section;
            }

            if (section.hasSameDownwardStation(targetStation)) {
                downwardIncludeSection = section;
            }
        }
    }


    public boolean isNotFound() {
        return upwardIncludeSection == null && downwardIncludeSection == null;
    }

    public boolean isUpLastDelete() {
        return upwardIncludeSection != null && downwardIncludeSection == null;
    }

    public boolean isDownLastDelete() {
        return upwardIncludeSection == null && downwardIncludeSection != null;
    }

    public Section getUpwardIncludeSection() {
        return upwardIncludeSection;
    }

    public Section getDownwardIncludeSection() {
        return downwardIncludeSection;
    }


}
