package models


import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._ // Combinator syntax


case class Spell(spellType: String, name: String, desc:String, 
                 castIndicator: String, range: String, castType: String,
                 castDelay: String, coolDown: String, globalCoolDown: String,
                 damage: String, damageRatio: String, area: String,
                 duration: String, spellImage: String)
