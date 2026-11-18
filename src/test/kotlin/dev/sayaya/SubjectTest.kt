package dev.sayaya

import com.microsoft.playwright.Page
import dev.sayaya.gwt.test.GwtHtml
import dev.sayaya.gwt.test.GwtTestSpec

@GwtHtml("subject.html")
internal class SubjectTest: GwtTestSpec({
    Given("Subject 타입") {
        // 비동기 작업 대기
        page.waitForFunction(
            "window.__TEST_DONE__ === true",
            null,
            Page.WaitForFunctionOptions().setTimeout(30_000.0)
        )
        val logs = page.getConsoleLogs()
        When("Subject를 사용할 때") {
            Then("모든 구독자에게 값을 멀티캐스트해야 한다") { logs.shouldPassAll("Subject") }
        }
        When("BehaviorSubject를 사용할 때") {
            Then("초기값과 이후 값들을 방출해야 한다") { logs.shouldPassAll("BehaviorSubject") }
        }
        When("ReplaySubject를 사용할 때") {
            Then("버퍼 크기만큼 이전 값들을 재생해야 한다") { logs.shouldPassAll("ReplaySubject") }
        }
        When("AsyncSubject를 사용할 때") {
            Then("완료 시 마지막 값만 방출해야 한다") { logs.shouldPassAll("AsyncSubject") }
        }
        When("Subject.asObservable()을 사용할 때") {
            Then("값을 전달하되 Subject 자체는 노출하지 않아야 한다") { logs.shouldPassAll("asObservable()", expectedCount = 2) }
        }
        When("BehaviorSubject.getValue()를 사용할 때") {
            Then("최신 값을 돌려줘야 한다") { logs.shouldPassAll("getValue()") }
        }
        When("ReplaySubject.replayWithBufferAndWindowTime()을 사용할 때") {
            Then("버퍼 크기와 시간 창 안의 값만 재생해야 한다") { logs.shouldPassAll("replayWithBufferAndWindowTime()", expectedCount = 2) }
        }
        When("AsyncSubject.await()를 사용할 때") {
            Then("Promise가 resolve한 값을 방출해야 한다") { logs.shouldPassAll("await()") }
        }
        When("AsyncSubject.json()을 사용할 때") {
            Then("응답 본문 JSON을 방출해야 한다") { logs.shouldPassAll("json()") }
        }
    }
})
