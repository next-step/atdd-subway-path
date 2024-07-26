package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Section;

public class LineSectionEdge implements WeightedEdge<Long> {

    private final Long source;

    private final Long target;
    private final double weight;

    private LineSectionEdge(final Long source, final Long target, final double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public static LineSectionEdge from(final Section section) {
        return new LineSectionEdge(section.getUpStationId(), section.getDownStationId(),
                section.getDistance().doubleValue());
    }

    @Override
    public Long getSource() {
        return source;
    }

    @Override
    public Long getTarget() {
        return target;
    }

    @Override
    public double getWeight() {
        return weight;
    }
}
