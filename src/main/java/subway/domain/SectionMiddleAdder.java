package subway.domain;


public class SectionMiddleAdder implements SectionAdder {
    @Override
    public SubwaySections addSection(SubwaySections subwaySections, SubwaySection subwaySection) {
        subwaySections.reduceSection(subwaySection);
        subwaySections.register(subwaySection);
        return subwaySections;
    }
}
