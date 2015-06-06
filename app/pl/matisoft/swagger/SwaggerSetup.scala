package pl.matisoft.swagger

import java.net.URL
import javax.inject.{Inject, Provider}

import com.wordnik.swagger.config.{FilterFactory, ScannerFactory, ConfigFactory}
import com.wordnik.swagger.core.SwaggerContext
import com.wordnik.swagger.core.filter.SwaggerSpecFilter
import com.wordnik.swagger.reader.ClassReaders
import play.api.inject.ApplicationLifecycle
import play.api.{Logger, Application}
import play.api.routing.Router
import play.modules.swagger.ApiListingCache
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class SwaggerSetup extends Provider[Unit] {

  val logger = Logger("swagger")

  @Inject
  private var router: Router = _

  @Inject
  private var app: Application = _

  @Inject
  private var lifecycle: ApplicationLifecycle = _

  override def get(): Unit = {
    onStart()
    lifecycle.addStopHook(() => Future {
      onStop()
    })
  }

  def onStart() {
    val config = app.configuration
    logger.info("Swagger - starting initialisation...")

    val apiVersion = config.getString("api.version") match {
      case None => "beta"
      case Some(value) => value
    }

    val basePath = config.getString("swagger.api.basepath") match {
      case Some(path) if !path.isEmpty => {
        //ensure basepath is a valid URL, else throw an exception
        try {
          val basePathUrl = new URL(path)
          logger.info(s"Basepath configured as:$path")
          path
        } catch {
          case ex: Exception =>
            logger.error(s"Misconfiguration - basepath not a valid URL:$path. Swagger abandoning initialisation")
            throw ex
        }
      }

      case _ => "http://localhost:9000"
    }

    SwaggerContext.registerClassLoader(app.classloader)
    ConfigFactory.config.setApiVersion(apiVersion)
    ConfigFactory.config.setBasePath(basePath)
    ScannerFactory.setScanner(new PlayApiScanner(Option(router)))
    ClassReaders.reader = Some(new PlayApiReader(Option(router)))

    app.configuration.getString("swagger.filter") match {
      case Some(filter) if !filter.isEmpty => {
        try {
          FilterFactory.filter = SwaggerContext.loadClass(filter).newInstance.asInstanceOf[SwaggerSpecFilter]
          logger.info(s"Setting swagger.filter to $filter")
        }
        catch {
          case ex: Exception =>logger.error(s"Failed to load filter:$filter", ex)
        }
      }
      case _ =>
    }

    val docRoot = ""
    ApiListingCache.listing(docRoot)

    logger.info("Swagger - initialization done.")
  }

  def onStop() {
    ApiListingCache.cache = None
    logger.info("Swagger - stopped.")
  }

}
