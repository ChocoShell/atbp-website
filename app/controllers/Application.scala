package controllers

import play.api.libs.json._

import play.api.libs.functional.syntax._

import play.api.mvc.Controller
import play.api.mvc.Action
import models._

case class Spell (spellType: String, name: String, castIndicator: String,
                 range: String, castType: String, castDelay: String, 
                 coolDown: String, globalCoolDown: String, damage: String, 
                 damageRatio: String, area: String, duration: String)

case class Actor(name: String, desc: String, attackType: String,
                 roles: (String, String), stats: scala.collection.Map[String, JsValue], 
                 spells: List[Spell])

object Application extends Controller {

  implicit val SpellReads: Reads[Spell]  = (
    (JsPath \ "spellType").read[String] and
    (JsPath \ "spellName").read[String] and
    (JsPath \ "spellCastIndicator").read[String] and
    (JsPath \ "spellRange").read[String] and
    (JsPath \ "castType").read[String] and
    (JsPath \ "castDelay").read[String] and
    (JsPath \ "spellCoolDown").read[String] and
    (JsPath \ "spellGlobalCoolDown").read[String] and
    (JsPath \ "damage").read[String] and
    (JsPath \ "damageRatio").read[String] and
    (JsPath \ "spellArea").read[String] and
    (JsPath \ "spellDuration").read[String]
  )(Spell.apply _)

  implicit object ActorReads extends Reads[Actor] {
    def reads(json: JsValue) = {
      val stats = (json \ "actorStats").asInstanceOf[JsObject].value
      val spells: List[Spell] = List(
        json2Spell(json \ "spell1"),
        json2Spell(json \ "spell2"),
        json2Spell(json \ "spell3"),
        json2Spell(json \ "spell4")
      )
      JsSuccess(Actor(
        (json \ "playerData" \ "playerDisplayName").as[String],
        (json \ "playerData" \ "playerDescription").as[String],
        (json \ "attackType").as[String],
        ((json \ "role1").as[String], (json \ "role2").as[String]),
        stats,
        spells
        )
      )
    }
  }

  def json2Spell(json: JsValue): Spell = json.validate[Spell].get
  

  val jsonString = scala.io.Source.fromFile("public/files/atbp.json").getLines.mkString
  val json: JsValue = (Json.parse(jsonString) \ "Actors")(0)

  def index = Action { request =>
    Ok(views.html.index())
  }

  def champion(name: String) = Action {
    (json \ name).asOpt[JsValue] match {
      case Some(thing) => {
        (thing(0) \ "ActorData").validate[Actor] match {
          case  s: JsSuccess[Actor] => {
            val actor = s.get
            Ok(views.html.champion(actor))
          }
          case _ => {
            Ok(views.html.index())
          }
        }
      }
      case None => {
        Ok(views.html.index())
      }
    }
  }

}