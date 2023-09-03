package subway.domain;

public class SectionCloseManager {

    SectionCloser getOperator(SubwaySections sections, Station station) {
        if (!sections.existsUpStation(station)) {
            return new SectionTailCloser();
        }

        if (sections.existsDownStation(station)) {
            return new SectionMiddleCloser();
        }
        return new SectionDefaultCloser();
    }

}
