# sayaya-rx

GWT(Google Web Toolkit)를 위한 RxJS 바인딩 라이브러리입니다. Java/Kotlin 코드에서 RxJS의 반응형 프로그래밍 기능을 활용할 수 있도록 합니다.

## 특징

- RxJS Observable을 Java/Kotlin에서 사용
- Subject (AsyncSubject, BehaviorSubject, ReplaySubject) 지원
- 다양한 Operator (map, filter, debounce, throttle 등) 제공
- Scheduler 지원 (AsyncScheduler)
- GWT JsInterop을 통한 네이티브 RxJS 연동

## 설치

```gradle
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/sayaya1090/maven")
        credentials {
            username = project.findProperty("github_username") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("github_password") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("dev.sayaya:rx:2.1.4")
}
```

GWT 모듈에 다음 내용을 추가하세요:
```xml
<inherits name="dev.sayaya.Rx"/>
```

## 주요 구성 요소

### Observable
- `of()`: 값을 Observable로 변환
- `range()`: 숫자 시퀀스 생성
- `from()`: 배열, Promise 등에서 Observable 생성
- `merge()`: 여러 Observable 병합
- `fromEvent()`: DOM 이벤트를 Observable로 변환
- `interval()`: 주기적으로 값 발행
- `ajax()`: HTTP 요청을 Observable로 변환

### Operators
- **변환**: map, mergeMap, concatMap, switchMap
- **필터링**: filter, take, skip, distinct, distinctUntilChanged
- **시간**: debounceTime, throttleTime, bufferTime, windowTime
- **조합**: zip, combineLatest, concatWith, startWith
- **에러 처리**: catchError, retry
- **기타**: tap, scan, finalize

### Subject
- **Subject**: 기본 멀티캐스트 Observable
- **BehaviorSubject**: 현재 값을 유지하는 Subject
- **ReplaySubject**: 이전 값을 재생하는 Subject
- **AsyncSubject**: 완료 시 마지막 값만 발행

### Scheduler
- **AsyncScheduler**: 비동기 작업 스케줄링
- **TaskScheduler**: 작업 지연/반복 실행

## 사용 예제

```java
// Observable 생성
Observable<Integer> numbers = Observable.range(1, 5);

// Operator 체이닝
numbers
    .map(x -> x * 2)
    .filter(x -> x > 5)
    .subscribe(System.out::println);

// Subject 사용
Subject<String> subject = Subject.subject(String.class);
subject.subscribe(value -> System.out.println("Observer 1: " + value));
subject.subscribe(value -> System.out.println("Observer 2: " + value));
subject.next("Hello");

// 이벤트 처리
Observable.fromMouseEvent(element, click)
    .debounceTime(300)
    .subscribe(event -> handleClick(event));

// HTTP 요청
Observable.fetchJson("https://api.example.com/data", MyClass.class)
    .map(response -> response.getData())
    .catchError(error -> Observable.of(defaultValue))
    .subscribe(data -> updateUI(data));
```

## 개발

### 빌드
```bash
./gradlew build
```

### 테스트
```bash
./gradlew test
```

### GWT 개발 모드
```bash
./gradlew gwtDev
```

## 기술 스택

- GWT 2.12.2
- Kotlin 2.2.21
- RxJS (JsInterop 바인딩)
- Elemento 2.3.2

## 버전

현재 버전: 2.1.4

## 라이선스

이 프로젝트는 GitHub Packages를 통해 배포됩니다.
