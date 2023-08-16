package subway.domain;

import org.jgrapht.graph.DefaultWeightedEdge;

public class PathSectionJGraph extends DefaultWeightedEdge {
    private final PathSection pathSection;

    public static PathSectionJGraph from(PathSection pathSection) {
        return new PathSectionJGraph(pathSection);
    }

    private PathSectionJGraph(PathSection pathSection) {
        this.pathSection = pathSection;
    }

    @Override
    protected double getWeight() {
        return pathSection.getDistanceToDouble();
    }

    @Override
    protected PathStation getSource() {
        return pathSection.getUpStation();
    }

    @Override
    protected PathStation getTarget() {
        return pathSection.getDownStation();
    }
}
