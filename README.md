# 지하철 노선도 미션

[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

---

# 1단계 - 지하철 구간 추가 기능 개선

## 단위 기능 개발

### 공통사항

* [x] 기존 구간이 없을 경우 : 구간 등록
* [x] 상행 == 상행 && 하행 == 하행 : 예외 처리

### 역 사이에 새로운 역을 등록할 경우

* [x] 상행 == 상행 인 경우를 찾는다.
* [x] 기존 구간 <= 신규 구간 : 예외 처리
* [ ] 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정

### 새로운 역을 상행 종점으로 등록할 경우

### 새로운 역을 하행 종점으로 등록할 경우
