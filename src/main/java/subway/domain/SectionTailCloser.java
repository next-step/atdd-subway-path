package subway.domain;


public class SectionTailCloser implements SectionCloser{
    @Override
    public SubwaySections closeSection(SubwaySections subwaySections, Station station) {
        subwaySections.closeTail(station);
        return subwaySections;
    }
}
