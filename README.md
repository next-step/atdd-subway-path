<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <img alt="npm" src="https://img.shields.io/badge/npm-6.14.15-blue">
  <img alt="node" src="https://img.shields.io/badge/node-14.18.2-blue">
  <a href="https://edu.nextstep.camp/c/R89PYi5H" alt="nextstep atdd">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
</p>

<br>

## 🚀 화이팅

|주차|실습|날짜|
|:---:|:---:|:---:|
|2주차|구간 추가기능 변경|22/02/01|
|2주차|구간 삭제기능 변경|22/02/03|
|2주차|리뷰 리팩토링|22/02/04|

# 2주차
### 구간 추가 제약사항 변경
**역 사이에 새로운 역을 등록하는 경우**
- [x] 한 구간에 대해 상행역이 같고 하행역이 다른 경우 구간 분할
- [x] 한 구간에 대해 하행역이 같고 상행역이 다른 경우 구간 분할
- [x] 한 구간에 대해 상행역에 구간이 추가되는 경우 구간 추가

**노선 조회시 응답되는 역 목록 수정**
- [x] 최 상행역에서 최 하행역 순으로 구간이 조회되어야 한다.

### 예외 케이스
- [x] 역 사이에 새로운 역 등록시 기존 구간의 길이보다 크거나 같으면 등록할 수 없다.
- [x] 추가될 구간의 두 역이 노선에 모두 등록되어있다면 구간이 등록될 수 없다.
- [x] 추가될 구간의 두 역이 모두 노선에 존재하지 않는다면 구간이 등록될 수 없다.

### 리뷰 사항
``` 기능 별로 테스트코드, 성공시키는 프로덕션 코드 작성 등 커밋을 쪼개서 작성!```
- [x] Validator domain 으로 옮겨 검증을 빼먹지 않도록
- [x] 객체에 메시지를 보내는 방향으로 리팩토링하기
- [x] 축약된 변수명 축약 해제하기
- [x] 메서드 분리 리팩토링 진행하기
- [x] 들여쓰기 1로 줄이는 리팩토링하기
- [ ] Service Layer 에 대해 테스트 적용하기
- [x] Line#SectionList 에 대해 일급컬랙션 적용하기
- [ ] 하행 종점에 새로운 역 등록시에 대한 시나리오 구체화
- [ ] LineTest 의 구간생성 테스트들에 대해 distance 에 대한 검증 추가