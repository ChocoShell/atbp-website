package models

case class Build(
  id: Integer,
  guideID: Integer,
  backpackName: String,
  order: List[String]
)