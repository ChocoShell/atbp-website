package models

case class Guide(
  id: Integer,
  userID: Integer,
  name: String,
  body: String,
  buildIDs: List[Integer]
  )