# 지하철 노선도 미션

[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

## TODO List

- [x] 구간 단위 테스트 작성 (LineTest)
- [x] Stub을 사용한 구간 서비스 단위 테스트 작성 (LineServiceMockTest)
- [x] Mock 없이 구간 서비스 단위 테스트 (LineServiceTest)
- [x] 단위 테스트를 기반으로 비즈니스 로직을 리팩터링 하기

## Step1 PR 수정 TODO list

- [ ] Line의 getter를 비지니스 메서드보다 아래에 두기
- [x] IndexOutOfBoundsException 던지는 부분에 대한 변경
- [ ] new ArrayList 로 wrapping한 부분 수정
- [ ] Line에서 상행역을 가져오는 메서드 만들기
- [ ] Line에서 마지막 구간을 가져오는 메서드 만들기
- [ ] Line.deleteLastSection 에서 부정조건 피하기
- [ ] Line.getFirstSection 매직넘버 0피하기
- [ ] LineServiceMockTest에서 구현 완료된 부분의 주석 제거
- [x] 불필요한 Line.removeLastSection 제거
- [x] Sections를 1급 컬렉션으로 변경
- [x] Line<Section> 외부 노출 막기
