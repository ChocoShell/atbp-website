package models

case class Actor(name:String, displayName: String, desc: String, 
                 attackType: String, role1: String, role2: String, 
                 stats: ActorStats,  spell1: Spell, spell2: Spell, 
                 spell3: Spell, passive: Spell)
