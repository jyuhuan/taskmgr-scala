# TaskMgr Scala Client

## How To

Add the following to your `build.sbt`:

```scala
resolvers += Resolver.sonatypeRepo("snapshots")
libraryDependencies += "me.yuhuan" %% "taskmgr-scala" % "0.0.0-SNAPSHOT"
```

Use in your code as follows:

```scala
import me.yuhuan.taskmgr.client._
implicit val taskMgr = TaskMgr.of("https://taskmgr-service.herokuapp.com")
val t = Task.of(
  name = "A Scala Task", 
  description = "A task for demo purpose", 
  steps = 0, 
  max = 10
)
for (i <- 0 until 10) {
  Thread.sleep(500)
  taskMgr.updateProgress(t, i)
}
taskMgr.finish(t)
```