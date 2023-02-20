package nextstep.subway.domain.section;

public class BasicAddSection implements AddSectionStrategy {

    @Override
    public void addSection(SectionCollection sectionCollection, Section section) {
        sectionCollection.addSectionElement(section);
    }
}
