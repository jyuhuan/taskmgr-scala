import me.yuhuan.taskmgr.client._

/**
  * Created by Yuhuan Jiang on 4/19/17.
  */
object Demo extends App {
  
  implicit val taskMgr = TaskMgr.of("https://taskmgr-service.herokuapp.com")
  
  val t = Task.of("A Java Task", "A task for demo purpose", 0, 10)
  
  for (i <- 0 until 10) {
    Thread.sleep(500)
    taskMgr.updateProgress(t, i)
  }

  taskMgr.finish(t)
  
  val bp = 0

}