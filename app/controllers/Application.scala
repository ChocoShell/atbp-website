package controllers

import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._ // Combinator syntax

import play.api.mvc.Controller
import play.api.mvc.Action

import models._

case class ActorStats(
  attackDamage: String, attackDamagePerLevel: String, attackSpeed: String,
  attackSpeedPerLevel: String, attackRange: String, armor: String, 
  armorPerLevel: String, health: String, healthPerLevel: String,
  spellResist: String, spellResistPerLevel: String, spellDamage: String, 
  spellDamagePerLevel: String, speed: String
)

case class Junk(name: String, mod1: String, mod2: String, mod3: String,
                slot: String, mods: Map[String, (String, String)])

case class Backpack(name: String, description: String, icon: String, 
                    junk: Map[String, Junk])

object Application extends Controller {

  implicit object JunkReads extends Reads[Junk] {
    def reads(json: JsValue) = {
      val jsonMods = json \ "mods" \\ "mod"
      val mods = (for(mod <- jsonMods)  yield {
        (mod \ "points").toString -> ((mod \ "stat").toString, (mod \ "value").toString)
      }).toMap
     
      JsSuccess(Junk(
        (json \ "name").as[String],
        (json \ "modDescription1").as[String],
        (json \ "modDescription2").as[String],
        (json \ "modDescription3").as[String],
        (json \ "category").as[String],
        mods
        )
      ) 
    }
  }
  
  implicit object BackpackReads extends Reads[Backpack] {
    def reads(json: JsValue) = {
      val junk: List[String]

      JsSuccess(Backpack(
        (json \ "name").as[String],
        (json \ "description").as[String],
        (json \ "icon").as[String],
        junk
        )
      )
    }
  }  

  implicit val SpellReads: Reads[Spell]  = (
    (JsPath \ "spellType").read[String] and
    (JsPath \ "spellName").read[String] and
    (JsPath \ "spellShortDescription").read[String] and
    (JsPath \ "spellCastIndicator").read[String] and
    (JsPath \ "spellRange").read[String] and
    (JsPath \ "castType").read[String] and
    (JsPath \ "castDelay").read[String] and
    (JsPath \ "spellCoolDown").read[String] and
    (JsPath \ "spellGlobalCoolDown").read[String] and
    (JsPath \ "damage").read[String] and
    (JsPath \ "damageRatio").read[String] and
    (JsPath \ "spellArea").read[String] and
    (JsPath \ "spellDuration").read[String] and
    (JsPath \ "spellIconImage").read[String]
  )(Spell.apply _)

  implicit val ActorStatsReads: Reads[ActorStats]  = (
    (JsPath \ "attackDamage").read[String] and
    (JsPath \ "attackDamagePerLevel").read[String] and
    (JsPath \ "attackSpeed").read[String] and
    (JsPath \ "attackSpeedPerLevel").read[String] and
    (JsPath \ "attackRange").read[String] and
    (JsPath \ "armor").read[String] and
    (JsPath \ "armorPerLevel").read[String] and
    (JsPath \ "health").read[String] and
    (JsPath \ "healthPerLevel").read[String] and
    (JsPath \ "spellResist").read[String] and
    (JsPath \ "spellResistPerLevel").read[String] and
    (JsPath \ "spellDamage").read[String] and
    (JsPath \ "spellDamagePerLevel").read[String] and
    (JsPath \ "speed").read[String]
  )(ActorStats.apply _)


  implicit val ActorReads: Reads[Actor] = (
    (JsPath \ "actorName").read[String] and
    (JsPath \ "playerData" \ "playerDisplayName").read[String] and
    (JsPath \ "playerData" \ "playerDescription").read[String] and
    (JsPath \ "attackType").read[String] and
    (JsPath \ "role1").read[String] and 
    (JsPath \ "role2").read[String] and
    (JsPath \ "actorStats").read[ActorStats] and
    (JsPath \ "spell1").read[Spell] and
    (JsPath \ "spell2").read[Spell] and
    (JsPath \ "spell3").read[Spell] and
    (JsPath \ "spell4").read[Spell]
  )(Actor.apply _)

  implicit val ActorWrites: Writes[Actor] = (
    (JsPath \ "actorName").write[String] and
    (JsPath \ "playerData" \ "playerDisplayName").write[String] and
    (JsPath \ "playerData" \ "playerDescription").write[String] and
    (JsPath \ "attackType").write[String] and
    (JsPath \ "role1").write[String] and 
    (JsPath \ "role2").write[String] and
    (JsPath \ "actorStats").write[ActorStats] and
    (JsPath \ "spell1").write[Spell] and
    (JsPath \ "spell1").write[Spell] and
    (JsPath \ "spell1").write[Spell] and
    (JsPath \ "spell1").write[Spell]    
  )(unlift(Actor.unapply))

  val jsonString = scala.io.Source.fromFile("public/files/atbp.json").getLines.mkString
  val jsonActors: List[JsObject] = (Json.parse(jsonString) \ "Actors").as[List[JsObject]]
  val jsonBelts:  List[JsObject] = (Json.parse(jsonString) \ "Belts").as[List[JsObject]]
  val jsonJunk:   List[JsObject] = (Json.parse(jsonString) \ "Junk").as[List[JsObject]]

  def index = Action { request =>
    Ok(views.html.index())
  }

  def champion(name: String) = Action {
    val charJson = jsonActors.filter(x => x.keys.last == name)
    if(charJson.isEmpty)
      Ok(views.html.index())
    else {
      ((charJson(0) \ name)(0) \ "ActorData").validate[Actor] match {
        case  s: JsSuccess[Actor] => {
          val actor = s.get
          Ok(views.html.champion(actor))
        }
        case _ => {
          Ok(views.html.index())
        }
      }
    }
  }

  def spell(name: String, id: Int) = Action {
    val charJson = jsonActors.filter(x => x.keys.last == name)
    if(charJson.isEmpty)
      Ok(views.html.index())
    else {
      val spellId: String = "spell"+id.toString
      (((charJson(0) \ name)(0) \ "ActorData") \ spellId).validate[Spell] match {
        case  s: JsSuccess[Spell] => {
          val spell = s.get
          Ok(views.html.spell(spell))
        }
        case _ => {
          Ok(views.html.index())
        }
      }
    }
  }

  def build(name: String) = Action {
    val charJson = jsonBelts.filter(x => x.keys.last == "belt_" + name)
    if(charJson.isEmpty)
      Ok(views.html.index())
    else {
      (charJson(0) \ "belt").validate[Backpack] match {
        case  s: JsSuccess[Backpack] => {
          Ok(views.html.build(s.get))
        }
        case _ => {
          Ok(views.html.index())
        }
      }
    }
  }

}