package models

case class Backpack(name: String, description: String, icon: String, 
                    junk: List[(String, Junk)])