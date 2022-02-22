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

# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

<br>

## 🚀 Getting Started

### Install
#### npm 설치
```
cd frontend
npm install
```
> `frontend` 디렉토리에서 수행해야 합니다.

### Usage
#### webpack server 구동
```
npm run dev
```
#### application 구동
```
./gradlew bootRun
```

---
### step0 생각해보기
LineServiceMockTest와 LineServiceTest의 addSection테스트를 완성시켜야한다.
같은 테스트지만 mock테스트는 stub이용, 그냥 테스트는 실제 객체를 이용하는 차이가 있다.
결과적으로 협력객체만 차이가 생길 뿐, 전체 테스트 로직의 흐름은 동일하다.

### step1 피드백
- Request객체에 대한 유효성 검증 - Bean Validation 이용하거나, 객체 자체적으로 확인해본다.
- 정적 팩토리 메서드 네이밍 - 인자 하나는 from, 여러 개는 of.
- 여러 로직을 한 줄에 적는 방식 - 디버깅하기 어렵기 때문에 주의하기.
- 로직의 들여쓰기 1레벨로 줄이기
- 도메인 객체의 public 메서드. 부족한 테스트 추가.
- 불필요한 setter 지우기
- Section을 불변객체로 설계
- 삭제할 때 멱등성? by index? by object?

### step1 피드백 질문
- LineRequest객체를 자체적을 확인하는 방식으로 바꿔보았는데 피드백 주신 내용에 적절한지 궁금합니다.
- 엔티티 equals and hashCode - id는 빼는게 맞다고 생각함.(준영속상태나 비영속상태와의 비교를 위해선 id가 제외되어야 한다.) 그런 의미에서 distance는 포함되어야 함. 다른 방안으로는 별도의 메서드를 만드는 것이 있다.(현재 로직에서는 이 방법이 더 맞는 방법인 것 같음. 왜냐하면 distance는 존재하는 구간인가에 대한 정보에 비교될 필요가 없음.), 엔티티에는 equals and hashcode를 안 쓰는게 부담이 덜한 방식인지? 현업에서는 엔티티에 대해서 equalsAndHashCode를 구현해서 사용하는가에 대한 궁금증이 있습니다.
- 불변객체로 구현해봤는데 확인 부탁드립니다.
- Line의 update메서드를 BDD? 방식으로 구현해보았는데, 확인 부탁드립니다. - 주신 링크 글은 BDD에 관한 글이라기 보다는 Describe - Context - It 패턴과 계층구조로 테스트코드를 작성해서 깔끔한 결과를 얻는 글이라고 생각했는데 맞을까요?

### step2 생각해보기
- 지하철 노선에 구간이 AB BC 가 있다고 가정할 때,
- A로 지우기 가능
- B로 지우기 가능
  - AC가 되고 AB와 BC 구간 길이의 합이 AC의 구간 길이가 된다.
- C로 지우기 가능
- D로 지우기 불가능
- 구간이 1개인 경우엔 지우기 불가능

### step2 피드백
- requestDto의 경우 null 체크는 @Bean Validation을 이용해서 하자.(도메인의 경우면 생성자로 생성 당시에 체크) - null 체크를 별도로 해줄 필요가 없게 된다.
- dto에는 의미 있는 로직을 두지 않는다. - 오로지 데이터 전달용으로만 이용하자.
- JPA의 엔티티는 불변객체를 접근제어자와 로직을 통해 확보한다. - JPA의 필드값은 1.필드값, 2.프로퍼티(getter,setter) 주입해주는 방식 2가지가 존재한다.
- Section의 Distance를 일급객체로 변경 - 확인 로직 등을 해당 일급객체에게 넘겨줄 수 있다.
- 줄바꿈 120자를 넘지 않도록 하기. - 인텔리제이에 수직선 하나가 표시되어 있음. 그걸 기준으로 하면 됨.
- 가변인수 사용 주의하기(int ... values, 인수가 0개도 가능) - 가변인수를 쓰면, 매번 배열을 생성하여 메서드에 넘겨준다.(인수가 1개 이상이어야하는 경우와, 성능 문제가 발생한다.)

### step2 피드백 질문
- Sections.addMiddleSection()의 경우, return을 없애도 괜찮다는 피드백을 받았는데, upStation 로직 처리 후, downStaion 로직 처리시, upStation에서 add가 되어 downStation로직에 영향을 미침. 따라서 return이 필요함. 하지만 반대로 down먼저하고 up을 할 경우엔, if에 걸리지 않음. 이렇게 로직의 순서에 따라서 결과가 다를 때, 그걸 방지하고자 return을 썼었던 것임. 어떻게 생각하시는지?
- 물론 그렇게 해주면 다른 피드백 준 것처럼 ifPresent를 사용할 수 있게는 됨.