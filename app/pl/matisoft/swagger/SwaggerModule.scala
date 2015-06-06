/**
 * Copyright 2014 Reverb Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.matisoft.swagger

import javax.inject.{Inject, Provider}

import com.wordnik.swagger.config.{ConfigFactory, ScannerFactory, FilterFactory}
import com.wordnik.swagger.core.SwaggerContext
import com.wordnik.swagger.core.filter.SwaggerSpecFilter
import com.wordnik.swagger.reader.ClassReaders
import play.api.routing.Router
import play.api.{Logger, Configuration, Environment}
import play.api.inject.{ApplicationLifecycle, Binding, Module}
import play.modules.swagger.ApiListingCache
import play.api.Application

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SwaggerModule extends Module {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    Seq(bind(classOf[Swagger]).toSelf)

  class Swagger extends Provider[Unit] {

    @Inject
    private var router: Router = _

    @Inject
    private var app: Application = _

    @Inject
    private var config: Configuration = _

    @Inject
    private var lifecycle: ApplicationLifecycle = _

    override def get(): Unit = {
      onStart()
      lifecycle.addStopHook(() => Future {
        onStop()
      })
    }

    def onStart() {
      Logger("swagger").info("Plugin - starting initialisation")

      val apiVersion = config.getString("api.version") match {
        case None => "beta"
        case Some(value) => value
      }

      val basePath = config.getString("swagger.api.basepath") match {
        case Some(e) if (e != "") => {
          //ensure basepath is a valid URL, else throw an exception
          try {
            val basepathUrl = new java.net.URL(e)
            Logger("swagger").info("Basepath configured as: %s".format(e))
            e
          } catch {
            case ex: Exception =>
              Logger("swagger").error("Misconfiguration - basepath not a valid URL: %s. Swagger plugin abandoning initialisation".format(e))
              throw ex
          }
        }
        case _ => "http://localhost"
      }

      SwaggerContext.registerClassLoader(app.classloader)
      ConfigFactory.config.setApiVersion(apiVersion)
      ConfigFactory.config.setBasePath(basePath)
      ScannerFactory.setScanner(new PlayApiScanner(Option(router)))
      ClassReaders.reader = Some(new PlayApiReader(Option(router)))

      config.getString("swagger.filter") match {
        case Some(e) if (e != "") => {
          try {
            FilterFactory.filter = SwaggerContext.loadClass(e).newInstance.asInstanceOf[SwaggerSpecFilter]
            Logger("swagger").info("Setting swagger.filter to %s".format(e))
          }
          catch {
            case ex: Exception => Logger("swagger").error("Failed to load filter " + e, ex)
          }
        }
        case _ =>
      }

      val docRoot = ""
      ApiListingCache.listing(docRoot)

      Logger("swagger").info("Swagger - initialization done")
    }

    def onStop() {
      ApiListingCache.cache = None
      Logger("swagger").info("Swagger - stopped")
    }
  }

}
