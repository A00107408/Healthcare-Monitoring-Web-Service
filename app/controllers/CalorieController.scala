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
  * Created by eoghan on 19/03/2017.
  */
class CalorieController @Inject()(val reactiveMongoApi: ReactiveMongoApi)(implicit exec: ExecutionContext) extends Controller
  with MongoController with ReactiveMongoComponents{


  /** Declare what values to read from the Database
    * ie: time and heart rate value
    */
  val transformer: Reads[JsObject] =
    Reads.jsPickBranch[JsString](__ \ "user") and
      Reads.jsPickBranch[JsString](__ \ "time") and
      Reads.jsPickBranch[JsNumber](__ \ "value") and
      Reads.jsPut(__ \ "created", JsNumber(new java.util.Date().getTime())) reduce

  def calFuture: Future[JSONCollection] = database.map(_.collection[JSONCollection]("activities-calorie-intraday"))

  /**
    * A method to write heart rates to MongoDB
    * from json POSTed from the front end using
    * Ajax.
    */
  def createBulkFromJson = Action.async(parse.json) { request =>
    //Transformation silent in case of failures.
    val documents = for {
      calorie        <- request.body.asOpt[JsArray].toStream
      maybeCalorie   <- calorie.value
      validCalorie   <- maybeCalorie.transform(transformer).asOpt.toList
    } yield validCalorie

    for {
      calorie <- calFuture
      multiResult <- calorie.bulkInsert(documents = documents, ordered = true)
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

    val cursor: Future[List[JsObject]] = calFuture.flatMap { calories =>

      /**Find All Heart Rates for given user*/
      calories.find(Json.obj("user" -> user)).

        /**Find All Heart Rates*/
        //  heartrates.find(Json.obj()).

        /**Sort them by creation date*/
        sort(Json.obj("created" -> 1)).

        /**perform the query and get a cursor of JsObject*/
        cursor[JsObject](ReadPreference.primary).collect[List]()
    }

    // everything's ok! Let's reply with a JsValue
    cursor.map { calories =>
      Ok(Json.toJson(calories))
    }
  }
}
