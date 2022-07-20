package nextstep.subway.domain;

public class SectionIndex {

	private int index;

	protected SectionIndex() {
	}

	public int getIndex() {
		return index;
	}

	public void calculateIndex(int index) {
		this.index = index;
	}

}
