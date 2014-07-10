package controllers

import play.api.libs.json._
import play.api.libs.json._

import play.api.mvc.Controller
import play.api.mvc.Action

case class Spell(spellType: String, name: String, castIndicator: String,
                 range: Float, castType: String, castDelay: Float, 
                 coolDown: Int, globalCoolDown: Int, damage: Int, 
                 damageRatio: Float, area: Float, duration: Int)

case class Actor(name: String, desc: String, attackType: String,
                 roles: (String, String), stats: List[Float], 
                 spells: List[Spell])

implicit object SpellReads extends Reads[Spell] {
  def reads(json: JsValue) = Spell(
    (json \ "spellType").as[String],
    (json \ "name").as[String],
    (json \ "castIndicator").as[String],
    (json \ "range").as[Float],
    (json \ "castType").as[String],
    (json \ "castDelay").as[Float],
    (json \ "coolDown").as[Int],
    (json \ "globalCoolDown").as[Int],
    (json \ "damage").as[Int],
    (json \ "damageRatio").as[Float],
    (json \ "area").as[Float],
    (json \ "duration").as[Int]
  )
}

object Application extends Controller {

  val jsonString = scala.io.Source.fromFile("public/files/atbp.json").getLines.mkString
  val json: JsValue = Json.parse(jsonString)

  def index = Action { request =>
    Ok(views.html.index())
  }

  def characaters = Action { request =>
    Ok(views.html.character())
  }
}