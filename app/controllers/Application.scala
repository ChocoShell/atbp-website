package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.Play.current
import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._ // Combinator syntax

import play.api.mvc.Controller
import play.api.mvc.Action

// Reactive Mongo imports
import play.modules.reactivemongo._
import reactivemongo.api._

// Reactive Mongo plugin, including the JSON-specialized collection
import play.modules.reactivemongo.{ MongoController, ReactiveMongoPlugin }
import play.modules.reactivemongo.json.collection.JSONCollection

import models._

object Application extends Controller with MongoController {

  // Collections to check for: Guides, Users, Builds
  val guideCollection: JSONCollection = db.collection[JSONCollection]("guides")
  
  
  val userCollection: JSONCollection  = db.collection[JSONCollection]("users")
  val buildCollection: JSONCollection = db.collection[JSONCollection]("builds")
  //Reading Json so it available to all Json to Model Functions.
  val jsonString = scala.io.Source.fromFile("public/files/atbp.json").mkString

  //Separating JSON for different views.
  val jsonActors: List[JsObject] = (Json.parse(jsonString) \ "Actors").as[List[JsObject]]
  val jsonBelts:  List[JsObject] = (Json.parse(jsonString) \ "Belts").as[List[JsObject]]
  val jsonJunk:   List[JsObject] = (Json.parse(jsonString) \ "Junk").as[List[JsObject]]

  /* Json Parsing Functions - Will probably change this all to angular on the client side */

  // This reads the json value given to it into the Junk Model.
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
  // Retrieves backpack item data given the names.
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
  
  /* Actions Start Here */
  // Homepage
  def index = Action { request =>
    Ok(views.html.index())
  }
  // For news about the site
  def blog = Action {
    Ok(views.html.blog())
  }
  // Shows all backpacks and items.
  def belts = Action { request =>
    val belts = for(x <- 0 until jsonBelts.length) yield {
      val key = jsonBelts(x).keys.last;
      (key.drop(5), ((jsonBelts(x) \ key)(0) \ "belt" \ "name").as[String])
    }
    Ok(views.html.belts(belts.toList))
  }
  // Prototype for guides - will probably delete soon
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
  // Character selection Page
  def champs = Action {
    Ok(views.html.champs())
  }
  // Character Stat Table
  def champTable = Action {
    Ok(views.html.champtable())
  }
  // Individual character page with details about powers and stats
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
  // Spell Detail Page - May keep as an alternative to tooltips
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
  // For news about the game
  def news = Action { request =>
    Ok(views.html.news())
  }
  // Current Streamers who are playing Adventure Time Battle Party
  def streams = Action {
    Ok(views.html.streams())
  } 
  // Page for handling form submission of guides
  def newGuide = Action {
    Ok(views.html.newguide())
  }

  def apiBuild = Action(parse.json) { implicit request =>
    Ok("Got request [" + request + "]")
  }
  
  def apiGuide = Action(parse.json) { implicit request =>
    Ok("Got request [" + request + "]")
  }
}
