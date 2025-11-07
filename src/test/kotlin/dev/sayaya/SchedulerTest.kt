package dev.sayaya

import dev.sayaya.gwt.test.GwtHtml
import dev.sayaya.gwt.test.GwtTestSpec
import io.kotest.matchers.string.shouldNotContain
import java.lang.Thread.sleep

@GwtHtml("src/test/webapp/scheduler.html")
internal class SchedulerTest: GwtTestSpec({
    Given("Scheduler 타입") {
        sleep(3000) // Scheduler 테스트는 타이밍이 중요하므로 충분히 대기
        val logs = document.getConsoleLogs()

        When("asyncScheduler를 사용할 때") {
            val log = logs.find { line -> line.toString().startsWith("asyncScheduler()") } as String
            Then("지정된 지연 시간 후에 실행되어야 한다") {
                println(log)
                log shouldNotContain "Assertion failed!"
            }
        }

        When("asapScheduler를 사용할 때") {
            val log = logs.find { line -> line.toString().startsWith("asapScheduler()") } as String
            Then("가능한 빨리 비동기적으로 실행되어야 한다") {
                println(log)
                log shouldNotContain "Assertion failed!"
            }
        }

        When("queueScheduler를 사용할 때") {
            val log = logs.find { line -> line.toString().startsWith("queueScheduler()") } as String
            Then("대기열 순서대로 동기적으로 실행되어야 한다") {
                println(log)
                log shouldNotContain "Assertion failed!"
            }
        }

        When("여러 Scheduler를 함께 사용할 때") {
            val log = logs.find { line -> line.toString().startsWith("All schedulers") } as String
            Then("각 Scheduler의 특성에 맞게 실행 순서가 결정되어야 한다") {
                println(log)
                log shouldNotContain "Assertion failed!"
            }
        }
    }
})