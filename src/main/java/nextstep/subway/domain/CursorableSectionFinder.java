package nextstep.subway.domain;

public class CursorableSectionFinder {
    public static Section find(Section cursor, SectionFindStrategy sectionFindStrategy) {
        return sectionFindStrategy.find(cursor);
    }
}
