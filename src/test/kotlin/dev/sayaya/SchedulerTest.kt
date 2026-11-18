package dev.sayaya

import com.microsoft.playwright.Page
import dev.sayaya.gwt.test.GwtHtml
import dev.sayaya.gwt.test.GwtTestSpec

@GwtHtml("scheduler.html")
internal class SchedulerTest: GwtTestSpec({
    Given("Scheduler 타입") {
        // 비동기 작업 대기
        page.waitForFunction(
            "window.__TEST_DONE__ === true",
            null,
            Page.WaitForFunctionOptions().setTimeout(30_000.0)
        )
        val logs = page.getConsoleLogs()

        When("asyncScheduler를 사용할 때") {
            Then("지정된 지연 시간 후에 실행되어야 한다") { logs.shouldPassAll("asyncScheduler()") }
        }

        When("asapScheduler를 사용할 때") {
            Then("가능한 빨리 비동기적으로 실행되어야 한다") { logs.shouldPassAll("asapScheduler()") }
        }

        When("queueScheduler를 사용할 때") {
            Then("대기열 순서대로 동기적으로 실행되어야 한다") { logs.shouldPassAll("queueScheduler()") }
        }

        When("여러 Scheduler를 함께 사용할 때") {
            Then("각 Scheduler의 특성에 맞게 실행 순서가 결정되어야 한다") { logs.shouldPassAll("All schedulers") }
        }

        When("asyncScheduler.schedule(state)로 작업을 다시 예약할 때") {
            Then("새 state로 작업이 다시 실행되어야 한다") { logs.shouldPassAll("asyncScheduler.schedule(state)") }
        }
    }
})
