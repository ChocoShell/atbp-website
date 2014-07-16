package models

case class Junk(name: String, mod1: String, mod2: Option[String], mod3: Option[String],
                slot: String, mods: List[(Int,List[(Option[String], Option[String])])])