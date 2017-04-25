package me.yuhuan.taskmgr.client

import java.io._
import java.net._

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * Created by Yuhuan Jiang on 4/19/17.
  */
class TaskMgr(val server: String) {
  
  def sendRequest(method: String, urlString: String)(json: String): Future[JSONObject] = {
    Future[JSONObject] {
      val url = new URL(urlString)
      val connection = url.openConnection().asInstanceOf[HttpURLConnection]
      connection.setRequestMethod(method)
      connection.setDoOutput(true)
      connection.setRequestProperty("Content-Type", "application/json")
      connection.setRequestProperty("Accept", "application/json")
      
      if (json.nonEmpty) {
        val out = new OutputStreamWriter(connection.getOutputStream)
        out.write(json)
        out.close()
      }
      val res = connection.getInputStream
      val resReader = new BufferedReader(new InputStreamReader(res, "UTF-8"))
      val sb = new StringBuilder()
      var str = resReader.readLine()
      while (str != null) {
        sb.append(str)
        str = resReader.readLine()
      }
      val s = sb.toString()
      val p = new JSONParser()
      val j = p.parse(s)
      j.asInstanceOf[JSONObject]
    }
  }

  /**
    * 
    * @param name
    * @param description
    * @param steps
    * @param max
    * @return
    *         
    * @note This method is blocking.
    */
  def createTask(name: String, description: String, steps: Long, max: Long): Task = {
    val taskIdFut = sendRequest("POST", s"$server/tasks"){
      s"""
         |{
         |  "name": "$name",
         |  "description": "$description",
         |  "steps": $steps,
         |  "max": $max
         |}
      """.stripMargin
    }.map(_.get("data").asInstanceOf[Long])
    val taskId = Await.result(taskIdFut, Duration.Inf)
    new Task(id = taskId, name = name, description = description, steps = steps, max = max)
  }
  
  def getTaskById(id: Long): Future[Task] = {
    val jsonFut = sendRequest("GET", s"$server/tasks/$id")("")
    jsonFut.map(j => new Task(
      id = j.get("id").asInstanceOf[Long],
      name = j.get("name").asInstanceOf[String],
      description = j.get("description").asInstanceOf[String],
      steps = j.get("steps").asInstanceOf[Long],
      max = j.get("max").asInstanceOf[Long]
    ))
  }
  
  //region General update methods
  //endregion
  
  def updateProgress(task: Task, newSteps: Int): Future[JSONObject] = {
    sendRequest("PUT", s"$server/tasks/${task.id}/steps")(s""" { "val": $newSteps } """)
  }
  
  def updateProgress(task: Task, newSteps: Int, newMax: Int): Unit = {
    updateProgress(task, newSteps)
    sendRequest("PUT", s"$server/tasks/${task.id}/max")(s""" { "val": $newMax } """)
  }
  
  def finish(task: Task): Unit = {
    val req = sendRequest("PUT", s"$server/tasks/${task.id}/finish")("{}")
    Await.result(req, Duration.Inf)
  }
  
}


object TaskMgr {
  def of(serverUrl: String) = new TaskMgr(serverUrl)
}