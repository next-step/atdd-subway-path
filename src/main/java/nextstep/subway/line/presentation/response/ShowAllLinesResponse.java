package nextstep.subway.line.presentation.response;

import nextstep.subway.line.service.LineDto;

import java.util.List;

public class ShowAllLinesResponse {

    private List<LineDto> lines;

    private ShowAllLinesResponse() {
    }

    private ShowAllLinesResponse(List<LineDto> lineDtos) {
        this.lines = lineDtos;
    }

    public static ShowAllLinesResponse of(List<LineDto> lineDtos) {
        return new ShowAllLinesResponse(lineDtos);
    }

    public List<LineDto> getLines() {
        return lines;
    }

}
