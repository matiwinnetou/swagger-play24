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
package play.modules.swagger

import com.wordnik.swagger.config._
import com.wordnik.swagger.model.ApiListing
import com.wordnik.swagger.reader._
import com.wordnik.swagger.core.util.ReaderUtil
import play.api.Logger

object ApiListingCache extends ReaderUtil {

  var cache: Option[Map[String, ApiListing]] = None

  def listing(docRoot: String): Option[Map[String, com.wordnik.swagger.model.ApiListing]] = {
    cache.orElse {
      Logger("swagger").info("Loading API metadata")
      ClassReaders.reader.map{reader =>
        ScannerFactory.scanner.map(scanner => {
          val classes = scanner match {
            case scanner: Scanner => scanner.classes()
            case _ => List()
          }
          Logger("swagger").debug("Classes count: %s".format(classes.length))
          classes.foreach{ clazz =>
            Logger("swagger").debug("Controller: %s".format(clazz.getName))
          }
          val listings = (for(cls <- classes) yield reader.read(docRoot, cls, ConfigFactory.config)).flatten
          val mergedListings = groupByResourcePath(listings)
          cache = Some(mergedListings.map(m => (m.resourcePath, m)).toMap)
        })
      }
      cache
    }
  }

}