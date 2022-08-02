package nextstep.subway.domain.vo;

import nextstep.subway.domain.Section;

public class ToBeAddedSection {

    private Section section;

    private AddSectionLocation location;

    private int index;

    public ToBeAddedSection(AddSectionLocation location) {
        this(null, location, -1);
    }

    public ToBeAddedSection(AddSectionLocation location, int index) {
        this(null, location, index);
    }

    public ToBeAddedSection(Section section, AddSectionLocation location, int index) {
        this.section = section;
        this.location = location;
        this.index = index;
    }

    public Section getSection() {
        return section;
    }

    public AddSectionLocation getLocation() {
        return location;
    }

    public int getIndex() {
        return index;
    }
}
