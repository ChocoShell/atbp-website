package models

import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._ // Combinator syntax

case class Actor(name:String, displayName: String, desc: String, 
                 attackType: String, role1: String, role2: String, 
                 stats: ActorStats,  spell1: Spell, spell2: Spell, 
                 spell3: Spell, passive: Spell)
