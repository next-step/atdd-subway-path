package nextstep.subway.line.presentation.request;

import java.util.HashMap;

public class DeleteSectionRequest {

    private Long sectionId;

    private DeleteSectionRequest() {
    }

    private DeleteSectionRequest(Long sectionId) {
        this.sectionId = sectionId;
    }

    public static DeleteSectionRequest from(Long sectionId) {
        return new DeleteSectionRequest(sectionId);
    }

    public Long getSectionId() {
        return sectionId;
    }

    public HashMap<String, Object> transferMap() {
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("sectionId", sectionId);
        return requestMap;
    }

}
