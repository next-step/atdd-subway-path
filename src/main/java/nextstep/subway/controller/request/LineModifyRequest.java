package nextstep.subway.controller.request;

import nextstep.subway.service.command.LineModifyCommand;

public class LineModifyRequest implements LineModifyCommand {

    private String name;
    private String color;

    public LineModifyRequest() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
