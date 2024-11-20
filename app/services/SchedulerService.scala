package services

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import java.time.{Duration, LocalDateTime, LocalTime}
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

@Singleton
class SchedulerService @Inject()(allocationservice: AllocationRequestService)(implicit ec: ExecutionContext) {
  private val dailyExecutionTime: LocalTime = LocalTime.of(15, 6)
  private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

  startDailyTaskScheduler()

  // Function to calculate the initial delay until the specified time
  private def calculateInitialDelay(targetTime: LocalTime): Long = {
    val now = LocalDateTime.now()
    val targetDateTime = if (now.toLocalTime.isBefore(targetTime)) {
      now.toLocalDate.atTime(targetTime) //Today at the target time
    } else {
      now.toLocalDate.plusDays(1).atTime(targetTime) // Tomorrow at the target time
    }
    Duration.between(now, targetDateTime).getSeconds
  }

  // Function to start the daily task scheduler
  private def startDailyTaskScheduler(): Unit = {
    println("inside the start daily schdeuler")
    val initialDelay = calculateInitialDelay(dailyExecutionTime)
    scheduler.scheduleAtFixedRate(
      new Runnable {
        override def run(): Unit = {
          runDailyTasks()
        }
      },
      initialDelay,
      TimeUnit.DAYS.toSeconds(1), // Run every 24 hours
      TimeUnit.SECONDS
    )
  }

  // Function to call the alert functions
  private def runDailyTasks(): Unit = {
    allocationservice.checkOverdueAllocations()

    println("Daily tasks executed successfully.")
  }
}
