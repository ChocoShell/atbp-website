package controllers

import play.api.libs.json._
import play.api.libs.json._

import play.api.mvc.Controller
import play.api.mvc.Action

case class Spell(spellType: String, name: String, spellCastIndicator: String,
                 range: Float, castType: String, castDelay: Float, 
                 coolDown: Int, globalCoolDown: Int, damage: Int, 
                 damageRatio: Float, area: Float, duration: Int)

case class Actor(name: String, desc: String, attackType: String,
                 roles: (String, String), stats: List[Float], 
                 spells: List[Spell])

object Application extends Controller {

    def index = Action { request =>
        Ok(views.html.index())
    }
}