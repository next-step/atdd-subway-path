package nextstep.subway.linesection.remove;

import java.util.List;

public class LineSectionRemoverHolder {
    private static LineSectionRemoverHolder instance = new LineSectionRemoverHolder();
    private List<LineSectionRemover> removers;

    private LineSectionRemoverHolder() {
        removers = List.of(new FirstStationRemover(), new EndStationRemover(), new MiddleStationRemover());
    }

    public static LineSectionRemoverHolder getInstance() {
        return instance;
    }

    public List<LineSectionRemover> getContext() {
        return removers;
    }

}
