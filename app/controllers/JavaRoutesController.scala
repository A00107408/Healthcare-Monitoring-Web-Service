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
        routes.javascript.SMSController.HRHigh,
        routes.javascript.SMSController.HRLow,
        routes.javascript.HRController.writeHistoricalHR,
        routes.javascript.HRController.readHistoricalHR
      )
    ).as("text/javascript")
  }

}
