# 구현 가이드

UC-07(RxJS 바인딩 추가)의 상세 절차입니다. 설계 원칙은 [설계 모델](../02-elaboration/design-model.md#설계-원칙-모든-바인딩에-공통)을 따릅니다.

## 소스 배치

| 경로 | 내용 |
|---|---|
| `src/main/java/dev/sayaya/Rx.gwt.xml` | 라이브러리 GWT 모듈. `User`, `org.jboss.elemento.Core` 상속, `source path="rx"` |
| `src/main/java/dev/sayaya/rx/Observable.java` | 생성 함수(static), 구독, `pipe`, 체인 메서드 |
| `src/main/java/dev/sayaya/rx/Operator.java` | RxJS 연산자 네이티브 선언과 람다 어댑터 오버로드 |
| `src/main/java/dev/sayaya/rx/subject/` | Subject 4종 |
| `src/main/java/dev/sayaya/rx/scheduler/` | Scheduler 접근자와 스케줄러 인터페이스 |
| `src/main/java/dev/sayaya/rx/function/` | `@JsFunction` 인터페이스 |

## 연산자 추가 절차 (UC-07 주 성공 시나리오)

1. **RxJS 시그니처 확인**: [rxjs.dev](https://rxjs.dev) API 문서에서 인자·반환 타입을 확인합니다. 숫자, 배열, 가변인자, 콜백 인자가 있는지 표시해 둡니다.
2. **`Operator`에 네이티브 선언 추가**
   ```java
   /**
    * 한 줄 설명.
    * @see <a href="https://rxjs.dev/api/index/function/take">Guide</a>
    */
   @JsMethod(namespace="rxjs", name="take")
   public static native <T> OperatorFunction<T, T> take(int count);
   ```
   콜백을 받으면 RxJS 모양 그대로의 `@JsFunction`(`ProjectWithIndex`, `PredicateWithIndex` 등)으로 선언하고, `java.util.function` 람다를 받는 어댑터 오버로드를 추가합니다.
   ```java
   public static <T, R> OperatorFunction<T, R> map(Function<T, R> func) {
       return map((a, i) -> func.apply(a));
   }
   ```
3. **`Observable`에 체인 메서드 추가**: `@JsOverlay public final` 메서드로 `pipe(Operator.x(...))`에 위임합니다.
   ```java
   @JsOverlay public final Observable<T> take(int count) { return pipe(Operator.take(count)); }
   ```
4. **JS 경계 값 변환**: 아래 함정 표를 보고 숫자·배열을 Java 타입으로 바꿔 노출합니다.
5. **검증 EntryPoint 추가**: 해당 테스트 모듈의 Java EntryPoint(`src/test/java/dev/sayaya/rx/*Test.java`)에 `test<이름>()` 메서드를 만들고 `onModuleLoad()`에서 호출합니다. 검증 이름은 `"<연산자>()"` 형식으로 시작합니다.
6. **Kotlin 검사 추가**: 같은 이름의 Kotlin spec(`src/test/kotlin/dev/sayaya/*Test.kt`)에 `logs.shouldPassAll("<연산자>()")`를 추가합니다.
7. **검증**: `./gradlew clean build`로 확인하고 `build/test-results/test/*.xml`의 `failures="0" errors="0"`을 확인합니다. 자세한 내용은 [테스트 설계](test.md)에 있습니다.
8. **문서 갱신**: [추적성 표](../traceability.md)에 행을 추가하고, 공개 API라면 README의 "주요 구성 요소"에도 추가합니다.

**확장**
- 2a. 생성 함수(연산자가 아닌 것)는 `Observable`에 `public static native`로 선언합니다. 예: `of`, `merge`, `concat`.
- 2b. RxJS 객체를 반환하는 상수(`rxjs.EMPTY`, `rxjs.asyncScheduler`)는 `@JsProperty(namespace="rxjs", name=...)`로 선언합니다. 예: `Observable.empty()`, `Scheduler.async()`.
- 2c. RxJS 시그니처가 Java 사용자에게 불편하면 네이티브 메서드를 `private`으로 두고 공개 `@JsOverlay`만 노출합니다. 예: `range(JsNumber, JsNumber)`, `throwErrorWith(Factory)`.

## 알려진 JsInterop 함정

이 저장소에서 실제로 결함이 났던 지점입니다.

| 증상 | 원인 | 해결 | 예 |
|---|---|---|---|
| `Integer`로 받은 값이 산술·비교에서 이상하게 동작 | RxJS가 내보내는 JS number는 Java `Integer`(박싱 객체)가 아님 | 네이티브는 `Observable<JsNumber>`로 받고 `toInt()`로 변환 | `interval`, `range`, `timer` |
| `TypeError: headers.get is not a function` | 네이티브 JS 객체를 `java.util.Map`으로 선언 | `JsPropertyMap<String>`으로 선언 | `AjaxRequest.headers`, `AjaxResponse.headers` |
| `TypeError: You provided an invalid object where a stream was expected` | Java 가변인자(`Object...`)가 JS 인자 여러 개로 펼쳐짐 | 배열 하나를 넘겨야 하면 `(Object) array`로 넘김 | `from(List)` |
| 람다 구독에서 스트림 에러가 사라짐 | 빈 `error()`를 가진 익명 `Observer`로 감쌈 | JS 함수를 그대로 넘기는 private native 오버로드(`subscribeNext`) | `subscribe(Consumer)` |
| Elemento의 `keydown`·`mousedown` 등을 넘기면 컴파일 오류(`EventType<KeyboardEvent,EventTarget> cannot be converted to EventType<KeyboardEvent,Element>`) | Elemento는 이벤트마다 두 번째 타입 인자를 `Element` 또는 `EventTarget`으로 다르게 선언함 | 바인딩 파라미터를 `EventType<E, ?>`로 받음 | `fromKeyboardEvent`, `fromMouseEvent` |
| javac `unchecked` 경고 | 제네릭 캐스트 `(Observable<O>) this` | `Js.uncheckedCast`를 private helper 한 곳에 모음 | `flatten` |

## 코딩 규칙

- 공개 메서드에는 한국어 한 줄 설명과 `@see` RxJS 문서 링크를 답니다.
- 오버레이는 `@JsOverlay public final`(인스턴스) 또는 `@JsOverlay public static`(정적)로 둡니다.
- 공개 API 변경(시그니처 변경·삭제)은 메이저 버전 반복에서만 합니다. 가능한 한 오버로드를 추가하고 기존 것은 `@Deprecated`로 표시합니다.
