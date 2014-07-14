package models

import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._ // Combinator syntax

case class ActorStats(
  attackDamage: String, attackDamagePerLevel: String, attackSpeed: String,
  attackSpeedPerLevel: String, attackRange: String, armor: String, 
  armorPerLevel: String, health: String, healthPerLevel: String,
  spellResist: String, spellResistPerLevel: String, spellDamage: String, 
  spellDamagePerLevel: String, speed: String
)