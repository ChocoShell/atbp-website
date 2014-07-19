package controllers

import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._ // Combinator syntax

import play.api.mvc.Controller
import play.api.mvc.Action

import models._

object Application extends Controller {

  val jsonString = scala.io.Source.fromFile("public/files/atbp.json").mkString
  val jsonActors: List[JsObject] = (Json.parse(jsonString) \ "Actors").as[List[JsObject]]
  val jsonBelts:  List[JsObject] = (Json.parse(jsonString) \ "Belts").as[List[JsObject]]
  val jsonJunk:   List[JsObject] = (Json.parse(jsonString) \ "Junk").as[List[JsObject]]

  implicit object JunkReads extends Reads[Junk] {
    def reads(json: JsValue) = {
      val jsonMods = json \ "mods" \ "mod"
      val allMods = jsonMods.as[List[JsObject]] groupBy {x: JsObject => (x  \ "point")}
      val allMod = for(mod <- allMods) yield (mod._1.as[String].toInt, for(x <- mod._2) yield ((x \ "stat").asOpt[String], (x \ "value").asOpt[String]))


      JsSuccess(Junk(
        (json \ "name").as[String],
        (json \ "modDescription1").as[String],
        (json \ "modDescription2").asOpt[String],
        (json \ "modDescription3").asOpt[String],
        (json \ "category").as[String],
        allMod.toList.sortBy(_._1)
        )
      ) 
    }
  }

  def getJunk(junks: List[String]): List[(String, Junk)] = {
    (for(junk <- junks) 
      yield junk -> ((jsonJunk.filter(x => x.keys.last == junk)(0) \ junk)(0) \ "junk")
      .validate[Junk].get).sortBy(_._2.slot)
  }

  
  implicit object BackpackReads extends Reads[Backpack] {
    def reads(json: JsValue) = {
      
      val junk: List[String] = {
        (for (value <- (json \ "junk").as[JsObject].values)
          yield (value \ "junk_id").as[String]).toList
      }
      val icon = {
        if((json \ "name").as[String] == "Meta-Science Sack")
          "iconJunk"
        else
          "icon"
        }
      JsSuccess(Backpack(
        (json \ "name").as[String],
        (json \ "description").as[String],
        (json \ icon).as[String],
        getJunk(junk)
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


  implicit object ActorReads extends Reads[Actor] {
    def reads(json: JsValue) = {
      
      JsSuccess(
        Actor(
          (json \ "actorName").as[String],
          (json \ "playerData" \ "playerDisplayName").as[String],
          (json \ "playerData" \ "playerDescription").as[String],
          (json \ "attackType").as[String],
          (json \ "role1").as[String], 
          (json \ "role2").as[String],
          (json \ "actorStats").as[ActorStats],
          List(
            (json \ "spell1").as[Spell],
            (json \ "spell2").as[Spell],
            (json \ "spell3").as[Spell]
          ),
          (json \ "spell4").as[Spell]
        )
      )
    }
  }
    

  def index = Action { request =>
    Ok(views.html.index())
  }

  def test = Action { request =>
    Ok(views.html.test())
  }

  def champs = Action {
    val actors = for(x <- 0 until jsonActors.length) yield {
      val key = jsonActors(x).keys.last;
      (key, ((jsonActors(x) \ key)(0) \ "ActorData" \ "playerData" \ "playerDisplayName").as[String])
    }
    Ok(views.html.champs(actors.toList))
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
    val spellJson = jsonActors.filter(x => x.keys.last == name)
    if(spellJson.isEmpty)
      Ok(views.html.index())
    else {
      val spellId: String = "spell"+id.toString
      (((spellJson(0) \ name)(0) \ "ActorData") \ spellId).validate[Spell] match {
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

  def belts = Action { request =>
    val belts = for(x <- 0 until jsonBelts.length) yield {
      val key = jsonBelts(x).keys.last;
      (key.drop(5), ((jsonBelts(x) \ key)(0) \ "belt" \ "name").as[String])
    }
    Ok(views.html.belts(belts.toList))
  }

  def blog = Action {
    Ok(views.html.blog())
  }

  def build(name: String) = Action {
    val beltName: String = "belt_" + name
    val beltJson = jsonBelts.filter(x => beltName == x.keys.last)
    if(beltJson.isEmpty)
      Ok(views.html.index())

    ((beltJson(0) \ beltName)(0) \ "belt").validate[Backpack] match {
      case  s: JsSuccess[Backpack] => {
        Ok(views.html.build(s.get)(None))
      }
      case _ => {
        Ok(views.html.index())
      }
    }
  }

  def charBuild(hero: String, belt: String) = Action {
    val beltName: String = "belt_" + belt
    val beltJson = jsonBelts.filter(x => beltName == x.keys.last)
    val charJson = jsonActors.filter(x => x.keys.last == hero)
    if(charJson.isEmpty)
      Ok(views.html.index())

    if(beltJson.isEmpty)
      Ok(views.html.index())

    val char: Actor = ((charJson(0) \ hero)(0) \ "ActorData").validate[Actor].get

    ((beltJson(0) \ beltName)(0) \ "belt").validate[Backpack] match {
      case  s: JsSuccess[Backpack] => {
        Ok(views.html.build(s.get)(Some(char)))
      }
      case _ => {
        Ok(views.html.index())
      }
    }
  }
}
