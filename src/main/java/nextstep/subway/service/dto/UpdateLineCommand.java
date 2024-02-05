package nextstep.subway.service.dto;

public class UpdateLineCommand {
    private final Long targetId;
    private final String name;
    private final String color;

    public UpdateLineCommand(Long id, String name, String color) {
        this.targetId = id;
        this.name = name;
        this.color = color;
    }

    public Long getTargetId() {
        return targetId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
