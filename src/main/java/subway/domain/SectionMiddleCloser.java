package subway.domain;


public class SectionMiddleCloser implements SectionCloser{
    @Override
    public SubwaySections closeSection(SubwaySections subwaySections, Station station) {
        subwaySections.extendSection(station);
        subwaySections.close(station);
        return subwaySections;
    }
}
