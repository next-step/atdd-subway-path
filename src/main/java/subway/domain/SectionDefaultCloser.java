package subway.domain;


public class SectionDefaultCloser implements SectionCloser{
    @Override
    public SubwaySections closeSection(SubwaySections subwaySections, Station station) {
        subwaySections.close(station);
        return subwaySections;
    }
}
