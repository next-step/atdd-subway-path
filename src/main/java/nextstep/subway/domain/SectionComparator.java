package nextstep.subway.domain;

import java.util.Comparator;

public class SectionComparator implements Comparator<Section> {

    public static enum SectionSort {
        ASC("ASC"), DESC("DESC"), NONE("NONE");

        private final String sortType;

        SectionSort(String sortType) {
            this.sortType = sortType;
        }

        public String getSortType() {
            return sortType;
        }
    }

    @Override
    public int compare(Section o1, Section o2) {
        if (o2.getDownStation().equals(o1.getUpStation())) {
            return 1;
        } else if (o1.getDownStation().equals(o2.getUpStation())) {
            return -1;
        } else {
            return 0;
        }

    }
}
