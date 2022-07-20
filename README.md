# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

---

### 요구사항

- [X] 지하철 구간 관련 단위 테스트를 완성하세요.
  - [X] 구간 단위 테스트 (LineTest)
  - [X] 구간 서비스 단위 테스트 with Mock (LineServiceMockTest)
  - [X] 구간 서비스 단위 테스트 without Mock (LineServiceTest)
  - [ ] 구간 등록 
    - [ ] 구간 사이에 등록 시 구간 사이에 신규 구간이 추가된다.
    - [ ] 구간 사이에 등록 시 신규 구간의 길이가 구간 사이의 길이보다 작아야한다.
    - [ ] 신규 구간의 상행역과 하행역이 둘중 하나만 노선에 등록되어 있어야한다.
- [X] 단위 테스트를 기반으로 비즈니스 로직을 리팩터링 하세요.
- [ ] 인수조건을 검증하는 인수 테스트를 작성하세요.
  - [X] 구간 사이에 새로운 구간 등록 인수 테스트
  - [ ] 상행 종점에 새로운 구간 등록 인수 테스트
  - [ ] 노선 조회 시 지하철 목록 순서 인수테스트
  
### 인수 조건

- 구간 사이에 새로운 구간을 등록하면 구간이 추가된다.
  > `when` 노선의 구간 사이에 새로운 구간을 추가하면   
  > `then` 구간 사이에 새로운 구간이 추가된다. 
- 상행 종점에 새로운 구간을 등록하면 구간이 추가된다.
  > `when` 노선의 상행 종점역에 새로운 구간을 추가하면   
  > `then` 구간 상행에 새로운 구간이 추가된다. 
- 노선을 조회 시 지하철 역 목록은 구간 순서에 맞게 제공한다.
  > `given` 지하철 노선에 새로운 구간 추가를 요청하고   
  > `when` 지하철 노선을 조회하면   
  > `then` 구간 순서에 맞게 지하철 역 목록을 제공한다.