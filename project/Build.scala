import sbt._
import Keys._
import com.earldouglas.xsbtwebplugin.WebPlugin

object BuildSettings {
  import Dependencies._
  import Resolvers._

  val buildOrganization = "com.monarchapis"
  val buildVersion = "0.8.2"
  val buildScalaVersion = "2.11.2"

  val globalSettings = Seq(
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    scalacOptions += "-deprecation",
    fork in test := true,
    libraryDependencies ++= Seq(
      //slf4jSimpleTest,
      junit,
      scalatest,
      mockito,
      jettyServerTest,
      log4j,
      slf4jlog4j12,
      grizzled,
      jodaTime, jodaConvert),
    resolvers := Seq(
      scalaToolsRepo,
      sonatypeRepo))

  val projectSettings = Defaults.defaultSettings ++ globalSettings
}

object Resolvers {
  val sonatypeRepo = "Sonatype Release" at "http://oss.sonatype.org/content/repositories/releases"
  val scalaToolsRepo = "Scala Tools" at "http://scala-tools.org/repo-snapshots/"
}

object Dependencies {
  val junit = "junit" % "junit" % "4.11" % "test"
  val scalatest = "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
  val mockito = "org.mockito" % "mockito-all" % "1.9.5" % "test"

  val slf4jVersion = "1.7.6"
  val slf4jSimple = "org.slf4j" % "slf4j-simple" % slf4jVersion
  val slf4jlog4j12 = "org.slf4j" % "slf4j-log4j12" % slf4jVersion
  val slf4jSimpleTest = slf4jSimple % "test"
  val log4j = "log4j" % "log4j" % "1.2.17"
  val grizzled = "org.clapper" % "grizzled-slf4j_2.10" % "1.0.2"

  val springVersion = "3.2.9.RELEASE"
  val springGroupId = "org.springframework"
  val springCore = springGroupId % "spring-core" % springVersion
  val springBeans = springGroupId % "spring-beans" % springVersion
  val springContext = springGroupId % "spring-context" % springVersion
  val springContextSupport = springGroupId % "spring-context-support" % springVersion
  val springWeb = springGroupId % "spring-web" % springVersion
  val springTx = springGroupId % "spring-tx" % springVersion
  val javaxInject = "javax.inject" % "javax.inject" % "1"

  val validationApi = "javax.validation" % "validation-api" % "1.1.0.Final"
  val hibernateValidator = "org.hibernate" % "hibernate-validator" % "5.1.0.Final"

  val jodaTime = "joda-time" % "joda-time" % "2.3"
  val jodaConvert = "org.joda" % "joda-convert" % "1.6"

  val commonsLang3 = "org.apache.commons" % "commons-lang3" % "3.3.1"
  val commonsIo = "commons-io" % "commons-io" % "2.4"
  val commonsCodec = "commons-codec" % "commons-codec" % "1.9"

  val jacksonVersion = "2.4.1"
  val jacksonGroupId = "com.fasterxml.jackson.core"
  val jacksonCore = jacksonGroupId % "jackson-core" % jacksonVersion
  val jacksonDatabind = jacksonGroupId % "jackson-databind" % jacksonVersion
  val jacksonAnnotations = jacksonGroupId % "jackson-annotations" % jacksonVersion
  val jacksonJaxRs = "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % jacksonVersion
  val jacksonScala = "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % jacksonVersion
  val jacksonJoda = "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % jacksonVersion
  val jacksonYaml = "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.4.0"

  val jerseyVersion = "2.11"
  val jerseyServer = "org.glassfish.jersey.core" % "jersey-server" % jerseyVersion
  val jerseySpring = "org.glassfish.jersey.ext" % "jersey-spring3" % jerseyVersion

  val jettyVersion = "8.1.15.v20140411" //"9.2.1.v20140609"
  val jettyServer = "org.eclipse.jetty" % "jetty-server" % jettyVersion
  val jettyServlet = "org.eclipse.jetty" % "jetty-servlet" % jettyVersion
  val jettyAjp = "org.eclipse.jetty" % "jetty-ajp" % "8.1.15.v20140411"
  val jettyServerTest = jettyServer % "test"
  val jettyWebApp = "org.eclipse.jetty" % "jetty-webapp" % jettyVersion % "container;compile"

  val servletSpec = "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided"

  val casbahCore = "org.mongodb" % "casbah-core_2.11" % "2.7.3"

  val jasypt = "org.jasypt" % "jasypt-spring31" % "1.9.2"
  
  val antlr = "org.antlr" % "antlr4-runtime" % "4.3"
  val maxmind = "com.maxmind.geoip2" % "geoip2" % "0.8.0"
  
  val ehcache = "net.sf.ehcache" % "ehcache" % "2.9.0"

  val quartz = "org.quartz-scheduler" % "quartz" % "2.2.1"
}

object ApiPlatformBuild extends Build {
  import BuildSettings._
  import Dependencies._
  import Resolvers._

  override lazy val settings = super.settings ++ globalSettings

  var jettyApp = Seq(jettyWebApp)
  var jettyDeps = Seq(jettyServer, jettyServlet, jettyAjp)

  val deps = Seq(
    servletSpec,
    validationApi, hibernateValidator,
    commonsLang3, commonsIo, commonsCodec,
    springCore, springBeans, springContext, springContextSupport, springWeb, springTx, javaxInject,
    jacksonCore, jacksonDatabind, jacksonAnnotations, jacksonJaxRs, jacksonScala, jacksonJoda, jacksonYaml,
    jerseyServer, jerseySpring,
    casbahCore, jasypt, antlr, maxmind, ehcache, quartz)

  lazy val apiManager = Project(
    id = "api-manager-standalone",
    base = file("."),
    settings = projectSettings ++
      WebPlugin.webSettings ++
      Seq(
        artifactName := { (config: sbt.ScalaVersion, module: ModuleID, artifact: Artifact) =>
          {
            "api-manager." + artifact.extension
          }
        }) ++
        Seq(libraryDependencies ++= deps ++ jettyApp)) dependsOn (server) aggregate(common, server)

  lazy val common = Project(
    id = "api-manager-common",
    base = file("modules/common"),
    settings = projectSettings ++
      Seq(libraryDependencies ++= deps))

  lazy val server = Project(
    id = "api-manager-server",
    base = file("modules/server"),
    settings = projectSettings ++
      Seq(libraryDependencies ++= deps ++ jettyDeps)) dependsOn (common)
}
