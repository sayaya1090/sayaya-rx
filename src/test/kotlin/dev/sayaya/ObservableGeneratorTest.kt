package dev.sayaya

import com.microsoft.playwright.Page
import dev.sayaya.gwt.test.GwtHtml
import dev.sayaya.gwt.test.GwtTestSpec

@GwtHtml("observable_generator.html")
internal class ObservableGeneratorTest: GwtTestSpec({
    Given("Observable 생성 함수") {
        // 비동기 작업 대기
        page.waitForFunction(
            "window.__TEST_DONE__ === true",
            null,
            Page.WaitForFunctionOptions().setTimeout(30_000.0)
        )
        val logs = page.getConsoleLogs()
        When("of()로 Observable을 생성할 때") {
            Then("값들이 순서대로 방출되고 완료되어야 한다") { logs.shouldPassAll("of") }
        }
        When("range()로 Observable을 생성할 때") {
            Then("값들이 순서대로 방출되고 완료되어야 한다") { logs.shouldPassAll("range") }
        }
        When("fromEvent()로 Observable을 생성할 때") {
            Then("DOM 이벤트를 캡처해야 한다") { logs.shouldPassAll("fromEvent") }
        }
        When("fromKeyboardEvent()로 Observable을 생성할 때") {
            Then("키보드 이벤트를 구독 해제 전까지 캡처해야 한다") { logs.shouldPassAll("fromKeyboardEvent()") }
        }
        When("fromMouseEvent()에 EventTarget 타입의 이벤트(mousedown)를 줄 때") {
            Then("마우스 이벤트를 캡처해야 한다") { logs.shouldPassAll("fromMouseEvent(mousedown)") }
        }
        When("ajax()로 Observable을 생성할 때") {
            Then("HTTP 요청을 통해 데이터를 가져와야 한다") { logs.shouldPassAll("ajax") }
        }
        When("timer()로 Observable을 생성할 때") {
            Then("일정 간격으로 값을 방출해야 한다") { logs.shouldPassAll("timer") }
        }
        When("merge()로 Observable을 생성할 때") {
            Then("여러 Observable의 값들을 병합해야 한다") { logs.shouldPassAll("merge") }
        }
        When("forkJoin()으로 Observable을 생성할 때") {
            Then("모든 Observable이 완료된 후 마지막 값들을 조합해야 한다") { logs.shouldPassAll("forkJoin") }
        }
        When("interval()로 Observable을 생성할 때") {
            Then("지정한 주기로 정수를 순서대로 방출해야 한다") { logs.shouldPassAll("interval") }
        }
        When("ajax 요청/응답 headers를 다룰 때") {
            Then("요청 헤더가 전달되고 응답 헤더를 읽을 수 있어야 한다") { logs.shouldPassAll("headers(ajax)") }
        }
        When("RxJS 7 생성 함수를 사용할 때") {
            listOf(
                "never()", "iif()", "generate()", "connectable()", "using()", "scheduled()",
                "fromEventPattern()", "partition()", "firstValueFrom()", "lastValueFrom()"
            ).forEach { prefix ->
                Then("$prefix 검증이 실행되고 통과해야 한다") { logs.shouldPassAll(prefix) }
            }
        }
    }
})
