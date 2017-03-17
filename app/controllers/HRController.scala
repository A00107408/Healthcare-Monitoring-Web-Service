/* Student: A00107408
 * Date: 2016-2017
 * Project: Msc Software Engineering Project.
 * College: Athlone Institute of Technology.
 *
 * Credits:
 * MongoDB I/O Based On: Lightbeand Activator seed project :-
 * https://github.com/jonasanso/play-reactive-mongo-db.git (15-02-2017)
 */

package controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import reactivemongo.api.ReadPreference
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}
import System.out.{println => puts}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by eoghan on 16/02/2017.
  */


/** Declaration of a class from non blocking MongoDB i/o methods.*/
class HRController @Inject()(val reactiveMongoApi: ReactiveMongoApi)(implicit exec: ExecutionContext) extends Controller
                                                                    with MongoController with ReactiveMongoComponents {


  /** Declare what values to read from the Database
    * ie: time and heart rate value
    */
  val transformer: Reads[JsObject] =
      Reads.jsPickBranch[JsString](__ \ "user") and
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

  def masterFuture: Future[JSONCollection] = database.map(_.collection[JSONCollection]("historical"))

  /**
    * Create a single MongoDB entry from Json POSTed
    * ie; from a form or from postman during development.
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
    * A method to write heart rates to MongoDB
    * from json POSTed from the front end using
    * Ajax for the users historical heart rates
    * for graphing in the Master Heart Rate Graph.
    */
  def writeHistoricalHR = Action.async(parse.json) { request => //customise max body size.

      //Transformation silent in case of failures.
      val documents = for {
        heartRate        <- request.body.asOpt[JsArray].toStream
        maybeHeartRate   <- heartRate.value
        validHeartRate   <- maybeHeartRate.transform(transformer).asOpt.toList
      } yield validHeartRate

      for {
        heartRate <- masterFuture
        multiResult <- heartRate.bulkInsert(documents = documents, ordered = true)
      } yield {
        Logger.debug(s"Successfully inserted with multiResult: $multiResult")
        Created(s"Created ${multiResult.n} heartRate")
      }
  }

  /**
    * A Scala method to read all heart rates for a given user from the MongoDB
    * in the same order as they were entered.
    */
  def findAll(user: String) = Action.async {

      puts("username: " +user)

      val cursor: Future[List[JsObject]] = hrFuture.flatMap { heartrates =>

          /**Find All Heart Rates for given user*/
          heartrates.find(Json.obj("user" -> user)).

          /**Find All Heart Rates*/
          //  heartrates.find(Json.obj()).

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

  /**
    * A Scala method to read all heart rates from the MongoDB in the same
    * order as they were entered.
    */
 /* def SMSMaker(user: String) = Action.async {

    puts("username: " +user)

    val cursor: Future[List[JsObject]] = hrFuture.flatMap { heartrates =>

      /**Find All Heart Rates for given user*/
      heartrates.find(Json.obj("user" -> user)).

        /**Find All Heart Rates*/
        //  heartrates.find(Json.obj()).

        /**Sort them by creation date*/
        sort(Json.obj("created" -> 1)).

        /**perform the query and get a cursor of JsObject*/
        cursor[JsObject](ReadPreference.primary).collect[List]()
    }


    // everything's ok! Let's reply with a JsValue
    cursor.map { heartrates =>
      //Ok(Json.toJson(heartrates))
      var x = Json.toJson(heartrates)
      var y = x.toString();
      puts("cursor map: " +y)
      Ok("Cardiac Arrest")
    }
  }*/

/**  def deleteAll(user: String){

    heartRate <- hrFuture
    val selector1 = BSONDocument("user" -> user)

    val futureRemove1 = heartRate.remove(selector1)

    futureRemove1.onComplete { // callback
      case Failure(e) => throw e
      case Success(writeResult) => println("successfully removed document")
    }
  }*/

  /**
    * A Scala method to read all heart rates from the master collection
    * in the same order as they were entered.
    */
  def readHistoricalHR(user: String) = Action.async {

      val cursor: Future[List[JsObject]] = masterFuture.flatMap { heartrates =>

          /**Find All Heart Rates for given user*/
          heartrates.find(Json.obj("user" -> user)).

          /**Find All Heart Rates*/
          // heartrates.find(Json.obj()).

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