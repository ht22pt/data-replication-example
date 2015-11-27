organization := "com.banno"

name := "api"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "confluent" at "http://packages.confluent.io/maven/"
)

libraryDependencies ++= {
  val akkaHttpVersion = "2.0-M1"
  Seq(
    "com.typesafe" % "config" % "1.3.0",
    "com.typesafe.akka" %% "akka-http-experimental" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaHttpVersion,
    "org.postgresql" % "postgresql" % "9.4-1206-jdbc42",
    "joda-time" % "joda-time" % "2.9.1",
    "org.joda" % "joda-convert" % "1.8.1",
    "org.rocksdb" % "rocksdbjni" % "4.0",
    "io.confluent" % "kafka-avro-serializer" % "1.0.1" exclude("log4j", "log4j") exclude("org.slf4j", "slf4j-log4j12"),
    "org.apache.kafka" %% "kafka" % "0.8.2.0-cp" exclude("log4j", "log4j") exclude("org.slf4j", "slf4j-log4j12"), //why is there no 0.8.2.1-cp?
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "org.slf4j" % "log4j-over-slf4j" % "1.7.13"
  )
}

Revolver.settings
