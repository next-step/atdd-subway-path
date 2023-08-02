package nextstep.subway.linesection.append;

import java.util.List;

public class LineSectionAppenderHolder {
    private static List<LineSectionAppender> appenders = List.of(
            new AppendInFirstLineSectionAppender(),
            new AppendInEndLineSectionAppender(),
            new UpStationLineSectionAppender(),
            new DownStationLineSectionAppender());

    public static List<LineSectionAppender> getContext() {
        return appenders;
    }
}
