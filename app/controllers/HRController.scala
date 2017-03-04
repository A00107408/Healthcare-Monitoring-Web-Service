package controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import play.api.routing._
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import reactivemongo.api.ReadPreference
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by eoghan on 16/02/2017.
  * Mongo I/O code taken from a Lightbend Activator seed
  * project called play.reactive.mongodb available by running
  * activator ui from the command line.
  */


/** Declaration of a class from non blocking MongoDB i/o methods.*/
class HRController @Inject()(val reactiveMongoApi: ReactiveMongoApi)(implicit exec: ExecutionContext) extends Controller
                                                                    with MongoController with ReactiveMongoComponents {


  /** Declare what values to read from the Database
    * ie: time and heart rate value
    */
  val transformer: Reads[JsObject] =
    Reads.jsPickBranch[JsString](__ \ "time") and
      Reads.jsPickBranch[JsNumber](__ \ "value") and
      Reads.jsPut(__ \ "created", JsNumber(new java.util.Date().getTime())) reduce


  /**
    * ReactiveMongoApi uses future promise
    * for asynchronous functionality
    * named hrFuture for "Heart Rate Future value".
    * Document Collection name = 'activities-heart-intraday'
    */
  def hrFuture: Future[JSONCollection] = database.map(_.collection[JSONCollection]("activities-heart-intraday"))

  /** In Play Framework you need to explicitly
    * create a Javascript router for Ajax POSTs.
    * Only these routes will be exposed to scripts
    * for added security.
    */
  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.HRController.createBulkFromJson,
        routes.javascript.HRController.findAll,
        routes.javascript.Plumber.stuff,
        routes.javascript.Plumber.stuffb
      )
    ).as("text/javascript")
  }

  /**
    * Create a single MongoDB entry from Json POSTed
    */
  def createFromJson = Action.async(parse.json) { request =>
    request.body.transform(transformer) match {
      case JsSuccess(heartRate, _) =>
        for {
          heartRates <- hrFuture
         // lastError <- heartRates.insert(heartRate)
        }
          yield {
            Logger.debug(s"Successfully inserted ")//with LastError: $lastError")
            Created("Created 1 heartRate")
          }
      case _ =>
        Future.successful(BadRequest("invalid json"))
    }
  }

  /**
    * Creat many heart rate Mongo entries
    * from POSTed json
    */
 /* def createBulkFromJson = Action.async(parse.json) { request =>
    //Transformation silent in case of failures.
    val documents = for {
      heartRate       <- request.body.asOpt[JsArray].toStream
      maybeHeartRate   <- heartRate.value
      validHeartRate   <- maybeHeartRate.transform(transformer).asOpt.toList
    } yield validHeartRate

    for {
      heartRate <- hrFuture
      multiResult <- heartRate.bulkInsert(documents = documents, ordered = true)
    } yield {
      Logger.debug(s"Successfully inserted with multiResult: $multiResult")
      Created(s"Created ${multiResult.n} heartRate")
    }
  }*/

  /**
    * A method to write heart rates to MongoDB
    * from json POSTed from the front end using
    * Ajax.
    */
  def createBulkFromJson = Action.async(parse.json) { request =>
    //Transformation silent in case of failures.
    val documents = for {
      heartRate        <- request.body.asOpt[JsArray].toStream
      maybeHeartRate   <- heartRate.value
      validHeartRate   <- maybeHeartRate.transform(transformer).asOpt.toList
    } yield validHeartRate

    for {
      heartRate <- hrFuture
      multiResult <- heartRate.bulkInsert(documents = documents, ordered = true)
    } yield {
      Logger.debug(s"Successfully inserted with multiResult: $multiResult")
      Created(s"Created ${multiResult.n} heartRate")
    }
  }

  /**
    * A Scala method to read certain heart rates
    * from the MongoDB
    */
  def findAll() = Action.async {

    val cursor: Future[List[JsObject]] = hrFuture.flatMap { heartrates =>

     // heartrates.find(Json.obj("value" -> value)).

          /**Find All Heart Rates*/
          heartrates.find(Json.obj()).

          /**Sort them by creation date*/
          sort(Json.obj("created" -> 1)).

          /**perform the query and get a cursor of JsObject*/
          cursor[JsObject](ReadPreference.primary).collect[List]()
    }

    // everything's ok! Let's reply with a JsValue
    cursor.map { heartrates =>
      Ok(Json.toJson(heartrates))
    }
  }
}