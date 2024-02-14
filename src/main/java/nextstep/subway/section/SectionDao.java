package nextstep.subway.section;



public interface SectionDao {

    Section save(Section section);
    Section findUpStation(long upStationsId);
    Section findDownStation(long downStationsId);
    LineSections findSectionsByLine(Long lineId);
    void deleteSection(Section sectionId);

}
