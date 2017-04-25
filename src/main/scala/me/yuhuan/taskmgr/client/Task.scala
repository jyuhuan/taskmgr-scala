package me.yuhuan.taskmgr.client

/**
  * Created by Yuhuan Jiang on 4/19/17.
  */
class Task private[yuhuan](val id: Long, var name: String, var description: String, var steps: Long, var max: Long)

object Task {
  def of(name: String, description: String, steps: Long, max: Long)(implicit taskMgr: TaskMgr): Task = {
    taskMgr.createTask(name, description, steps, max)
  }
}
