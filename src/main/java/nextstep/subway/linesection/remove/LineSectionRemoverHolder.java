package nextstep.subway.linesection.remove;

import java.util.List;

public class LineSectionRemoverHolder {
    private static List<LineSectionRemover> removers = List.of(new FirstStationRemover(), new EndStationRemover(), new MiddleStationRemover());

    public static List<LineSectionRemover> getContext() {
        return removers;
    }

}
