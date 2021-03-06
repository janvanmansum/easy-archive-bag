/**
 * Copyright (C) 2015 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.easy.archivebag

import org.apache.commons.configuration.PropertiesConfiguration
import org.rogach.scallop.ScallopConf
import java.io.File
import java.net.URL

class Conf(args: Seq[String], props: PropertiesConfiguration) extends ScallopConf(args) {
  printedName = "easy-archive-bag"
  version(s"$printedName ${Version()}")
  banner("""
                |Send a bag to archival storage.
                |
                |Usage: easy-archive-bag <bag-directory> [<storage-service-url>]
                |Options:
                |""".stripMargin)
  val username = opt[String]("username",
    descr = "Username to use for authentication/authorisation to the storage service",
    default = props.getString("default.storage-service-username") match {
      case s: String => Some(s)
      case _ => throw new RuntimeException("No username provided")
    })
  val password = opt[String]("password",
    descr = "Password to use for authentication/authorisation to the storage service",
    default = props.getString("default.storage-service-password") match {
      case s: String => Some(s)
      case _ => throw new RuntimeException("No password provided")
    })
  val checkInterval = opt[Int]("check-interval",
    descr = "Interval (in milliseconds) that the service must check the storage service (the store service is checked until the given state is other than FINALIZING)",
    default = Option(1000)
  )

  val maxCheckCount = opt[Int]("max-check-count",
    descr = "The maximum number that the service must check the storage service.",
    default = Option(10)
  )
  val slug = opt[String]("slug",
    descr = "The value to send in the Slug-header",
    default = None)
  val bagDirectory = trailArg[File](name = "bag-directory", required = true,
    descr = "Directory in BagIt format that will be sent to archival storage")
  validateOpt(bagDirectory) {
    case Some(dir) =>
      Right(Unit)
    case _ => Left("Could not parse parameter <bag-directory>")
  }
  val storageServiceUrl = trailArg[URL](
    name = "storage-service-url",
    required = false,
    default = props.getString("default.storage-service-url") match {
      case s: String => Some(new URL(s))
      case _ => throw new RuntimeException("No storage service URL provided")
    })
}
