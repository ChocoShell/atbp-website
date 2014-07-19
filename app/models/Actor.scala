package models

case class Actor(name:String, displayName: String, desc: String, 
                 attackType: String, role1: String, role2: String, 
                 stats: ActorStats,  spells: List[Spell], passive: Spell)
