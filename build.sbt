// Set application details
name := "Predictor"
version := "1.0-SNAPSHOT"
organization := "Singular Logic SA"

// Set Scala Version for app
scalaVersion := "2.11.8"

// Set main class for app
val predictorMainClass = "com.fivegmedia.predictor.DLClassifier"

// Set versions of dependencies
val sparkVersion = "2.3.0"
val kafkaVersion = "1.1.0"
val jacksonVersion = "2.9.4"
val nd4jVersion = "0.8.0"
val dlsVersion = "latest.release"

// Merge strategy for assembly
mainClass in assembly := Some(predictorMainClass)
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
assemblyOption in assembly := (assemblyOption in assembly).value.copy(cacheUnzip = false)
assemblyOption in assembly := (assemblyOption in assembly).value.copy(cacheOutput = false)
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

// Override dependencies for Jackson
dependencyOverrides ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % jacksonVersion
)

// All Apache Dependencies
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-streaming" % sparkVersion  % "provided",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion,
  "org.apache.kafka" %% "kafka" % kafkaVersion,
  "org.apache.kafka" % "kafka-clients" % kafkaVersion
)

// All DeepLearning.scala dependencies.
libraryDependencies ++= Seq(
  "com.thoughtworks.deeplearning" %% "plugins-builtins" % dlsVersion,
  "com.thoughtworks.each" %% "each" % dlsVersion
)

// The native backend for nd4j.
libraryDependencies += "org.nd4j" % "nd4j-native-platform" % nd4jVersion

// Deeplearning.scala Compiler Plugins
addCompilerPlugin("com.thoughtworks.import" %% "import" % dlsVersion)
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

fork := true