package dev.sayaya

import dev.sayaya.gwt.test.GwtHtml
import dev.sayaya.gwt.test.GwtTestSpec
import java.lang.Thread.sleep

@GwtHtml("subject.html")
internal class SubjectTest: GwtTestSpec({
    Given("Subject 타입") {
        sleep(1000)
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
    }
})
