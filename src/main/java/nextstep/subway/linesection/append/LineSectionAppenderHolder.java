package nextstep.subway.linesection.append;

import org.springframework.stereotype.Component;

import java.util.List;

public class LineSectionAppenderHolder {
    private static LineSectionAppenderHolder instance = new LineSectionAppenderHolder();
    private List<LineSectionAppender> appenders;

    private LineSectionAppenderHolder() {
        this.appenders = List.of(new AppendInFirstLineSectionAppender(), new AppendInEndLineSectionAppender(), new UpStationLineSectionAppender(), new DownStationLineSectionAppender());
    }

    public static LineSectionAppenderHolder getInstance() {
        return instance;
    }

    public List<LineSectionAppender> getContext() {
        return appenders;
    }
}
