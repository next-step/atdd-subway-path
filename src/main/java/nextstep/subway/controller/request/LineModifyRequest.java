package nextstep.subway.controller.request;

import nextstep.subway.domain.command.LineModifyCommand;

public class LineModifyRequest implements LineModifyCommand {

    private String name;
    private String color;

    public LineModifyRequest() {
    }

    public LineModifyRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
