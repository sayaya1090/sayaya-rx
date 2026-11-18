# 시스템 시퀀스 다이어그램 (SSD)

시스템(`:sayaya-rx`)을 블랙박스로 보고 액터가 보내는 **시스템 연산**만 그립니다. 이 라이브러리에서 시스템 연산은 곧 공개 API이므로, 이름을 실제 메서드 이름과 같게 둡니다. 설계에서 각 연산을 실현하는 방식은 [설계 모델](design-model.md)에 있습니다.

RxJS 런타임, 브라우저, HTTP 서버는 시스템 밖의 외부 시스템입니다.

## UC-04 구독하고 해제하기 (UC-01, UC-03 포함 시나리오)

```mermaid
sequenceDiagram
  actor Dev as 애플리케이션 개발자
  participant Sys as :sayaya-rx
  Dev->>Sys: interval(period)
  Sys-->>Dev: Observable~Integer~
  Dev->>Sys: map(func) / filter(predicate) / take(count)
  Sys-->>Dev: Observable~V~
  Dev->>Sys: subscribe(onNext, onError, onComplete)
  Sys-->>Dev: Subscription
  loop [값이 방출될 때마다]
    Sys-->>Dev: onNext(value)
  end
  alt [스트림이 정상 종료]
    Sys-->>Dev: onComplete()
  else [스트림 에러]
    Sys-->>Dev: onError(JsError)
  end
  opt [종료 전에 그만 받고 싶음]
    Dev->>Sys: unsubscribe()
  end
```

**대안: 람다 하나로 구독** (`subscribe(Consumer)`): 에러가 나면 시스템이 개발자에게 직접 알리지 않고 RxJS의 처리되지 않은 에러 경로(`rxjs.config.onUnhandledError` 또는 비동기 throw)로 보고합니다(UC-04 확장 3a-2).

## UC-02 HTTP 응답을 스트림으로 받기

```mermaid
sequenceDiagram
  actor Dev as 애플리케이션 개발자
  participant Sys as :sayaya-rx
  participant Http as HTTP 서버
  Dev->>Sys: fromRequest(request, Class)
  Sys-->>Dev: Observable~AjaxResponse~T~~
  Dev->>Sys: subscribe(onNext, onError, onComplete)
  Sys->>Http: HTTP 요청(method, url, headers, body)
  break [네트워크 실패·타임아웃·에러 상태]
    Http-->>Sys: 실패
    Sys-->>Dev: onError(JsError)
  end
  Http-->>Sys: 응답(status, headers, body)
  Sys-->>Dev: onNext(AjaxResponse: status, headers, response)
  Sys-->>Dev: onComplete()
```

## UC-05 Subject로 상태 공유

```mermaid
sequenceDiagram
  actor Dev as 애플리케이션 개발자
  participant Sys as :sayaya-rx
  Dev->>Sys: behavior(initialValue)
  Sys-->>Dev: BehaviorSubject~T~
  Dev->>Sys: subscribe(consumer)
  Sys-->>Dev: consumer(현재 값)
  loop [상태가 바뀔 때마다]
    Dev->>Sys: next(value)
    Sys-->>Dev: 모든 구독자의 consumer(value)
  end
  Dev->>Sys: getValue()
  Sys-->>Dev: 최신 값
```

## UC-06 작업 예약

```mermaid
sequenceDiagram
  actor Dev as 애플리케이션 개발자
  participant Sys as :sayaya-rx
  Dev->>Sys: asyncScheduler()
  Sys-->>Dev: AsyncScheduler
  Dev->>Sys: schedule(task, delay, state)
  Sys-->>Dev: Subscription
  loop [task가 reschedule(next)를 호출하는 동안]
    Sys-->>Dev: task.exec(state, reschedule) (delay 후)
  end
```

## 시스템 연산 목록

전체 목록은 [추적성 표](../traceability.md)에 있습니다. 대표 연산은 다음과 같습니다.

| 유스케이스 | 시스템 연산 |
|---|---|
| UC-01 | `of`, `range`, `from`, `merge`, `concat`, `forkJoin`, `defer`, `empty`, `throwError`, `interval`, `timer`, `fromEvent`, `fromMouseEvent`, `fromKeyboardEvent` |
| UC-02 | `fromRequest`, `fetchJson` |
| UC-03 | `pipe`와 체인 메서드(`map`, `filter`, ...) |
| UC-04 | `subscribe(...)`, `unsubscribe()` |
| UC-05 | `subject`, `behavior`, `replay*`, `async`, `await`, `json`, `next`, `error`, `complete`, `getValue`, `asObservable` |
| UC-06 | `asyncScheduler`, `asapScheduler`, `queueScheduler`, `animationFrameScheduler`, `schedule` |
