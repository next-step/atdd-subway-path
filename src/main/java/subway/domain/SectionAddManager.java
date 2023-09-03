package subway.domain;


public class SectionAddManager {

    SectionAdder getUpdater(SubwaySections sections, SubwaySection newSection) {
        if (sections.hasDuplicateSection(newSection)) {
            return new SectionMiddleAdder();
        }
        return new SectionDefaultAdder();
    }
}
