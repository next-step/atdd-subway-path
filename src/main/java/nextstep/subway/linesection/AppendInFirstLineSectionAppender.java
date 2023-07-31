package nextstep.subway.linesection;

public class AppendInFirstLineSectionAppender implements LineSectionAppender {
    @Override
    public boolean append(LineSections sections, LineSection addSection) {
        if (isAddableInFirst(sections, addSection)) {
            sections.getSections().add(0, addSection);
            return true;
        }
        return false;
    }

    private boolean isAddableInFirst(LineSections sections, LineSection addSection) {
        return sections.getFirstStation().equals(addSection.getDownStation());
    }
}
