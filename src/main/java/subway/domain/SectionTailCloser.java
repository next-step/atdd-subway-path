package subway.domain;

class SectionTailCloser implements SectionCloser{
    @Override
    public void apply(SubwayLine subwayLine, Station station) {
        subwayLine.closeSection(station);
    }
}
