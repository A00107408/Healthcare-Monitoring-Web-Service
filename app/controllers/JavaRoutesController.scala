package controllers


import play.api.mvc.{Action, Controller}
import play.api.routing.JavaScriptReverseRouter

/**
  * Created by eoghan on 06/03/2017.
  */

class JavaRoutesController extends Controller{

  /** In Play Framework you need to explicitly create a Javascript router for Ajax POSTs.
    * Only these routes will be exposed to scripts for added security.
    */
  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.HRController.createBulkFromJson,
        routes.javascript.HRController.findAll,
        routes.javascript.CalorieController.createBulkFromJson,
        routes.javascript.CalorieController.findAll,
        routes.javascript.SMSController.Warning,
        routes.javascript.HRController.writeHistoricalHR,
        routes.javascript.HRController.readHistoricalHR,
        routes.javascript.UserController.editUser,
        routes.javascript.UserController.deleteUser
      )
    ).as("text/javascript")
  }

}
