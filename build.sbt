lazy val lesson00 = project in (file("./lesson00"))
lazy val lesson01 = (project in (file("./lesson01"))).dependsOn(lesson00)

dependsOn(lesson01)

addCommandAlias("test01", ";lesson01/clean;lesson01/run")