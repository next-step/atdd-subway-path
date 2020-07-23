package nextstep.subway.path.application;

import java.util.Arrays;

public enum ShortestPathSearchType {
    DISTANCE("DISTANCE"),
    DURATION("DURATION");

    private final String typeName;

    ShortestPathSearchType(String typeName) {
        this.typeName = typeName;
    }

    public static ShortestPathSearchType findTypeByTypeName(String typeString) {
        return Arrays.stream(ShortestPathSearchType.values())
            .filter(type -> type.getTypeName().equals(typeString))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("nothing type found or registered"));
    }

    public String getTypeName() {
        return typeName;
    }
}
