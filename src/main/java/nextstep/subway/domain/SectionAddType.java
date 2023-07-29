package nextstep.subway.domain;

import java.util.function.BiConsumer;

public enum SectionAddType {
    FIRST(Sections::addFirst),
    LAST(Sections::addLast),
    MIDDLE_UP_STATION(Sections::addMiddleUpStation),
    MIDDLE_DOWN_STATION(Sections::addMiddleDownStation);

    private final BiConsumer<Sections, Section> add;

    SectionAddType(BiConsumer<Sections, Section> add) {
        this.add = add;
    }

    public void apply(Sections sections, Section section) {
        add.accept(sections, section);
    }
}
