# 용어집

| 용어 | 뜻 | 코드 |
|---|---|---|
| Observable | 시간에 따라 0개 이상의 값을 밀어 보내고(next) 완료(complete) 또는 에러(error)로 끝나는 지연 실행 스트림 | `dev.sayaya.rx.Observable<T>` → `rxjs.Observable` |
| Observer | 값·에러·완료 알림을 받는 쪽 | `Observer<T>` |
| Subscription | 구독 핸들. 해제하면 스트림의 자원이 정리된다 | `Subscription.unsubscribe()` |
| 연산자(Operator) | Observable을 받아 새 Observable을 돌려주는 함수. `pipe`로 적용 | `Operator.*`, `OperatorFunction<T, R>` |
| 체인 메서드 | 연산자를 `observable.map(...)`처럼 쓰게 해 주는 `Observable`의 메서드 | `@JsOverlay` 메서드 |
| Subject | Observable이면서 Observer. 받은 값을 모든 구독자에게 멀티캐스트 | `Subject`, `BehaviorSubject`, `ReplaySubject`, `AsyncSubject` |
| Scheduler | 작업을 언제 실행할지 정하는 실행 문맥 | `Scheduler`, `AsyncScheduler<T>`, `TaskScheduler` |
| 처리되지 않은 에러 | 에러 콜백 없이 구독한 스트림의 에러. RxJS는 `config.onUnhandledError` 또는 비동기 throw로 보고 | `subscribe(Consumer)` |
| 바인딩 | RxJS의 JS 함수·클래스를 Java 시그니처로 선언한 것 | `@JsType(isNative = true)`, `@JsMethod` |
| JsInterop | GWT의 Java↔JavaScript 상호운용 어노테이션 체계 | `jsinterop.annotations.*` |
| 네이티브 타입 | JS에 이미 있는 객체를 가리키는 Java 선언. Java 쪽에서 구현하지 않음 | `@JsType(isNative = true)` |
| 오버레이 | 네이티브 타입에 붙이는 Java 구현 메서드. 호출 지점에 인라인되어 JS 객체에 메서드를 추가하지 않음 | `@JsOverlay` |
| JS 함수 인터페이스 | Java 람다를 JS 함수로 넘기기 위한 함수형 인터페이스 | `@JsFunction` (`Callback`, `UnaryFunction` 등) |
| 타입 토큰 | 제네릭 타입 추론을 돕기 위한 `Class<T>` 인자. 런타임에는 쓰이지 않음. 3.0.0부터 대체 수단이 없는 곳에만 남음 | `fetchJson(url, Class<T>)`, `concatAll(Class<V>)` |
| devMode 테스트 모듈 | 테스트용 GWT EntryPoint 모듈. 브라우저 콘솔에 검증 결과를 남김 | `dev.sayaya.OperatorTest` 등 4개 |
| 검증 로그 | `<테스트 이름>: ✓ Assertion passed: ...` 또는 `... ✗ Assertion failed! ...` 형식의 콘솔 줄 | `Helper.assertEquals` |
| 완료 신호 | 비동기 검증이 모두 끝났음을 알리는 `window.__TEST_DONE__ = true` | `Helper.signalDoneWhenSettled` |
