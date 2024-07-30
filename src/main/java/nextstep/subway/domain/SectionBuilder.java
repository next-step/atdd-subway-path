package nextstep.subway.domain;

public class SectionBuilder {
    private Long distance;
    private Station upStation;
    private Station downStation;

    public SectionBuilder() {
    }

    public SectionBuilder distance(Long distance) {
        this.distance = distance;
        return this;
    }

    public SectionBuilder upStation(Station upStation) {
        this.upStation = upStation;
        return this;
    }

    public SectionBuilder downStation(Station downStation) {
        this.downStation = downStation;
        return this;
    }

    public Section build() {
        if (this.distance == null || this.upStation == null || this.downStation == null) {
            throw new IllegalArgumentException("필수값 입니다");
        }
        if (!this.upStation.isPersisted() || !this.downStation.isPersisted()) {
            throw new IllegalArgumentException("저장되지 않은 역입니다.");
        }
        return new Section(this.distance, this.upStation, this.downStation);
    }
}
