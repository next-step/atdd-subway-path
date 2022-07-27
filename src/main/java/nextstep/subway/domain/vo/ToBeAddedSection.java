package nextstep.subway.domain.vo;

import nextstep.subway.domain.Section;

public class ToBeAddedSection {

    private Section section;

    private SectionLocation location;

    private int index;

    public ToBeAddedSection(SectionLocation location) {
        this(null, location, -1);
    }

    public ToBeAddedSection(SectionLocation location, int index) {
        this(null, location, index);
    }

    public ToBeAddedSection(Section section, SectionLocation location, int index) {
        this.section = section;
        this.location = location;
        this.index = index;
    }

    public Section getSection() {
        return section;
    }

    public SectionLocation getLocation() {
        return location;
    }

    public int getIndex() {
        return index;
    }
}
