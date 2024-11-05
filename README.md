# **프리코스 4주차 미션 - 편의점**
### 많은 요구사항과 비즈니스 로직을 꼼꼼히 적용하기 🗄
## 📖 주요 피드백 키워드
>**01. 메서드 라인에 대한 기준도 적용한다**  
>**02. 예외 상황에 대해 항상 고민한다**  
>**03. 비즈니스 로직과 UI 로직를 분리한다**  
>**04. 연관성이 있는 상수는 static final 대신 enum을 활용한다**  
>**05. final 키워드를 사용해 값의 변경을 막는다**  
>**06. 객체의 상태 접근을 제한한다** 
>**07. 객체는 객체답게 사용한다**   
>**08. 필드(인스턴스 변수)의 수를 줄이기 위해 노력한다**  
>**09. 성공하는 케이스 뿐만 아니라 예외 케이스도 테스트한다**   
>**10. 테스트 코드도 코드다**   
>**11. 테스트를 위한 코드는 구현 코드에서 분리되어야 한다**   
>**12. private 함수를 테스트 하고 싶다면 클래스(객체) 분리를 고려한다**   
>**13. 위 사항들을 철저히 준수하도록 노력한다**   
---------------------
# ☑️ 요구사항 정리
## 1) 입력 요구사항
> ## 1-1) **products, promotions 파일의 목록을 불러와야 함**
> ### **➡️ (형식을 유지하고 재사용 할 것)**
> ## 1-2) **구매할 상품과 수량을 입력 받음**
> ### **➡️ (상품명, 수량은 하이픈( - ), 개별 상품은 대괄호( [] )로 묶어 쉼표로 구분해야 함 ( 예: [사이다-2],[감자칩-1] )**
> ## 1-3) **프로모션 적용이 가능한 상품에 대해 고객이 해당 수량보다 적게 가져온 경우, 그 수량만큼 추가할지 여부를 입력받아야 함**
> ### **➡️ Y: 증정 받을 수 있는 상품을 추가 ( 🙋2+1 제품인데, 한 개 더 가져오시면 1개 증정입니다 )**
> ### **➡️ N: 증정 받을 수 있는 상품을 추가하지 않음 ( 🙋‍♀️그냥 두 개만 살래요. )**  
> ## 1-4) **프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우, 일부 수량에 대해 정가로 결제할지 여부를 입력받아야 함**
> ### **➡️ Y: 일부 수량에 대해 정가로 결제 ( 🙋가져오신 5개는 2+1 행사 중이네요. 근데 재고는 5개 밖에 없네요.. 나머지는 행사 없이 구매하실래요? )**  
> ### **➡️ N: 정가로 결제해야 하는 수량만큼 제와한 후 결제를 진행함 ( 🙋‍ 아뇨, 2+1 적용되는 3개만 살게요. 나머지 2개는 빼주세요. )**  
> ## 1-5) **멤버십 할인 적용 여부를 입력받아야 함** 
> ### **➡️️ Y: 멤버실 할인 적용 ( 프로모션 미적용 금액의 30%를 할인 / 프로모션 적용 후 남은 금액에 대해 멤버십 할인을 적용 / 최대 한도는 8,000원 )**  
> ### **➡️ N: 멤버십 할인 적용X**  
> ## 1-6) **모든 사항을 적용 후 사용자에게 영수증을 출력해야 함**
> 영수증은 고객의 구매 내역과 할인을 요약하여 출력  
영수증 항목은 아래와 같음  
**구매 상품 내역: 구매한 상품명, 수량, 가격**  
**증정 상품 내역: 프로모션에 따라 무료로 제공된 증정 상품의 목록**  
**금액 정보**  
**총구매액: 구매한 상품의 총 수량과 총 금액**    
**행사할인: 프로모션에 의해 할인된 금액**  
**멤버십할인: 멤버십에 의해 추가로 할인된 금액**  
**내실돈: 최종 결제 금액**  
영수증의 구성 요소를 보기 좋게 정렬하여 고객이 쉽게 금액과 수량을 확인할 수 있게 한다.
> ## 1-7) **추가 구매 여부를 입력받아야 함**
> ### **➡️ Y: 재고가 업데이트된 상품 목록을 확인 후 추가로 구매를 진행 ( 재고 갱신 후 고객에게 출력 )**
> ### **➡️ N: 구매를 종료한다 ( 프로그램 종료 )**

## 2) 출력 요구사항
> ### 2-1) **환영 인사와 함께 상품명, 가격, 프로모션 이름, 재고를 안내한다. 만약 재고가 0개라면 (재고 없음)을 출력**
```
안녕하세요. W편의점입니다.
현재 보유하고 있는 상품입니다.

- 콜라 1,000원 10개 탄산2+1
- 콜라 1,000원 10개
- 사이다 1,000원 8개 탄산2+1
- 사이다 1,000원 7개
- 오렌지주스 1,800원 9개 MD추천상품
- 오렌지주스 1,800원 재고 없음
- 탄산수 1,200원 5개 탄산2+1
- 탄산수 1,200원 재고 없음
- 물 500원 10개
- 비타민워터 1,500원 6개
- 감자칩 1,500원 5개 반짝할인
- 감자칩 1,500원 5개
- 초코바 1,200원 5개 MD추천상품
- 초코바 1,200원 5개
- 에너지바 2,000원 5개
- 정식도시락 6,400원 8개
- 컵라면 1,700원 1개 MD추천상품
- 컵라면 1,700원 10개

구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])
```
> ### 2-2) **프로모션 적용이 가능한 상품에 대해 고객이 해당 수량만큼 가져오지 않았을 경우, 혜택에 대한 안내 메시지를 출력**
```
현재 {상품명}은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)
```
> ### 2-3) **프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우, 일부 수량에 대해 정가로 결제할지 여부에 대한 안내 메시지를 출력**
```
현재 {상품명} {수량}개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)
```
> ### 2-4) **멤버십 할인 적용 여부를 확인하기 위해 안내 문구를 출력**
```
멤버십 할인을 받으시겠습니까? (Y/N)
```
> ### 2-5) **구매 상품 내역, 증정 상품 내역, 금액 정보를 출력**
```
===========W 편의점=============
상품명		수량	금액
콜라		3 	3,000
에너지바 		5 	10,000
===========증	정=============
콜라		1
==============================
총구매액		8	13,000
행사할인			-1,000
멤버십할인			-3,000
내실돈			 9,000
```
> ### 2-6) **추가 구매 여부를 확인하기 위해 안내 문구를 출력**
```
감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)
Y -> [2-1) 환영 인사]로 돌아가기
N -> 프로그램 종료
```
> ### 2-7) **사용자가 잘못된 값을 입력했을 때, [ERROR]로 시작하는 오류 메시지와 함께 상황에 맞는 안내를 출력**
```
구매할 상품과 수량 형식이 올바르지 않은 경우: [ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.
존재하지 않는 상품을 입력한 경우: [ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.
구매 수량이 재고 수량을 초과한 경우: [ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.
기타 잘못된 입력의 경우: [ERROR] 잘못된 입력입니다. 다시 입력해 주세요.
```
---------------------
> ### 실행 결과 예시
```
안녕하세요. W편의점입니다.
현재 보유하고 있는 상품입니다.

- 콜라 1,000원 10개 탄산2+1
- 콜라 1,000원 10개
- 사이다 1,000원 8개 탄산2+1
- 사이다 1,000원 7개
- 오렌지주스 1,800원 9개 MD추천상품
- 오렌지주스 1,800원 재고 없음
- 탄산수 1,200원 5개 탄산2+1
- 탄산수 1,200원 재고 없음
- 물 500원 10개
- 비타민워터 1,500원 6개
- 감자칩 1,500원 5개 반짝할인
- 감자칩 1,500원 5개
- 초코바 1,200원 5개 MD추천상품
- 초코바 1,200원 5개
- 에너지바 2,000원 5개
- 정식도시락 6,400원 8개
- 컵라면 1,700원 1개 MD추천상품
- 컵라면 1,700원 10개

구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])
[콜라-3],[에너지바-5]

멤버십 할인을 받으시겠습니까? (Y/N)
Y 

===========W 편의점=============
상품명		수량	금액
콜라		3 	3,000
에너지바 		5 	10,000
===========증	정=============
콜라		1
==============================
총구매액		8	13,000
행사할인			-1,000
멤버십할인			-3,000
내실돈			 9,000

감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)
Y

안녕하세요. W편의점입니다.
현재 보유하고 있는 상품입니다.

- 콜라 1,000원 7개 탄산2+1
- 콜라 1,000원 10개
- 사이다 1,000원 8개 탄산2+1
- 사이다 1,000원 7개
- 오렌지주스 1,800원 9개 MD추천상품
- 오렌지주스 1,800원 재고 없음
- 탄산수 1,200원 5개 탄산2+1
- 탄산수 1,200원 재고 없음
- 물 500원 10개
- 비타민워터 1,500원 6개
- 감자칩 1,500원 5개 반짝할인
- 감자칩 1,500원 5개
- 초코바 1,200원 5개 MD추천상품
- 초코바 1,200원 5개
- 에너지바 2,000원 재고 없음
- 정식도시락 6,400원 8개
- 컵라면 1,700원 1개 MD추천상품
- 컵라면 1,700원 10개

구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])
[콜라-10]

현재 콜라 4개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)
Y

멤버십 할인을 받으시겠습니까? (Y/N)
N

===========W 편의점=============
상품명		수량	금액
콜라		10 	10,000
===========증	정=============
콜라		2
==============================
총구매액		10	10,000
행사할인			-2,000
멤버십할인			-0
내실돈			 8,000

감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)
Y

안녕하세요. W편의점입니다.
현재 보유하고 있는 상품입니다.

- 콜라 1,000원 재고 없음 탄산2+1
- 콜라 1,000원 7개
- 사이다 1,000원 8개 탄산2+1
- 사이다 1,000원 7개
- 오렌지주스 1,800원 9개 MD추천상품
- 오렌지주스 1,800원 재고 없음
- 탄산수 1,200원 5개 탄산2+1
- 탄산수 1,200원 재고 없음
- 물 500원 10개
- 비타민워터 1,500원 6개
- 감자칩 1,500원 5개 반짝할인
- 감자칩 1,500원 5개
- 초코바 1,200원 5개 MD추천상품
- 초코바 1,200원 5개
- 에너지바 2,000원 재고 없음
- 정식도시락 6,400원 8개
- 컵라면 1,700원 1개 MD추천상품
- 컵라면 1,700원 10개

구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])
[오렌지주스-1]

현재 오렌지주스은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)
Y

멤버십 할인을 받으시겠습니까? (Y/N)
Y

===========W 편의점=============
상품명		수량	금액
오렌지주스		2 	3,600
===========증	정=============
오렌지주스		1
==============================
총구매액		2	3,600
행사할인			-1,800
멤버십할인			-0
내실돈			 1,800

감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)
N
```
---------------------
> ## ⚠️개발 과정에서 지켜야 할 사항들⚠️
> ### **함수(또는 메서드)가 한 가지 일만 하도록 최대한 작게 만들어야 함**
> ### **인덴트는 3이 넘지 않아야 햠**
> ### **3항 연산자를 쓰지 않아야 함**
> ### **정리한 기능이 정상적으로 작동하는지 테스트 코드로 확인해야 함**
> ### **위에 따른 테스트 코드를 작성해야 함**
> ### **메서드의 길이가 10라인을 넘어가지 않도록 구현해야 함**
> ### **else 예약어를 쓰지 않아야 함**
> ### **Enum을 사용하기**
> ### **UI로직을 제외한 모든 메서드 단위 테스트를 작성해야 함**
> ### **사용자가 입력하는 값은 camp.nextstep.edu.missionutils.Console의 readLine()을 활용해야 함**
> ### **현재 날짜와 시간을 가져오려면 camp.nextstep.edu.missionutils.DateTimes의 now()를 활용해야 함**
### **⬇️입출력을 담당하는 클래스는 꼭 아래와 같이 별도로 구현하기⬇️**
```
public class InputView {
    public String readItem() {
        System.out.println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        String input = Console.readLine();    
        // ...
    }
    // ...
}
public class OutputView {
        public void printProducts() {
            System.out.println("- 콜라 1,000원 10개 탄산2+1");
            // ...
        }
    // ...
}
```  

---------------------
# ☑️ 구현할 기능 목록
> ### **1) products, promotions 파일에서 값을 읽고 객체로 변환하기 -> 테스트**
> ### **2) 1번에서 생성한 값을 사용자에게 출력하는 OutputView 생성하기 -> 테스트**
> ### **3) 2번에서 사용할 [ERROR] Enum, Const 생성 후 리팩터링하기**
> ### **4) 사용자에게 물품과 수량 입력받고 검증하는 InputView 생성하기 -> 테스트**
> ### **5) 비즈니스 로직(프로모션) 구현 후 대상 여부 검증하기 -> 테스트**
> ### **6) 비즈니스 로직(멤버십 할인 여부) 구현 후 적용하기 -> 테스트**
> ### **7) 구매 상품 내역, 증정 상품 내역, 금액 정보 영수증 생성, 출력하기 -> 테스트**
> ### **8) 추가 구매 여부를 확인하고 초기 로직으로 돌아가기 or 종료하는 로직 구현하기 -> 테스트**
---------------------
[//]: # (# ✅ 개발 완료 - 주요 관심사)
[//]: # (> ### **1&#41; ??**)
[//]: # (---------------------)
[//]: # (# ✅️ 테스트와 결과)
