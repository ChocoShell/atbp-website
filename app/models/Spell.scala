package models

case class Spell(spellType: String, name: String, desc:String, 
                 castIndicator: String, range: String, castType: String,
                 castDelay: String, coolDown: String, globalCoolDown: String,
                 damage: String, damageRatio: String, area: String,
                 duration: String, spellImage: String)
