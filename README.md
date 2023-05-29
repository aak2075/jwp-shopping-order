# jwp-shopping-order

## 요구사항 정리

### 주문

- [ ] 장바구니에 담은 상품을 주문할 수 있다.
    - [ ] 상품 주문시 쿠폰을 선택할 수 있다.
    - [ ] 주문한 상품은 장바구니에서 제거된다.
- [ ] 사용자 별로 주문 목록을 확인할 수 있다.
- [ ] 특정 주문의 상세 정보를 확인할 수 있다.

### 쿠폰

- [ ] 쿠폰을 사용하면 할인이 된다. 할인에는 3가지 종류가 존재한다.
    - [X] 금액 할인 정책이 존재한다.
    - [X] 비율 할인 정책이 존재한다.
    - [X] 배달비 할인 정책이 존재한다.
- [X] 할인 조건에 따라 쿠폰을 적용할 수 있다.
    - [X] 최소 주문 금액 조건이 존재한다.
    - [X] 조건 없이 쿠폰을 적용할 수 있는 조건이 존재한다.
- [ ] 쿠폰을 적용한 뒤 주문 금액이 0원 미만으로 내려갈 수 없다.
- [ ] 쿠폰을 적용하면 쿠폰이 제거된다.

PRICE, PERCENT, DELIVERY
할인 조건은 MINIMUM_PRICE, NONE
