package com.banno

import slick.driver.PostgresDriver.api._
import org.joda.time.DateTime
import java.sql.Timestamp
import scala.language.implicitConversions

object SlickDatabase {
  val db = Database.forURL("jdbc:postgresql://192.168.59.103:5432/postgres?user=example&password=example", driver = "org.postgresql.Driver")

  import Tables._

  implicit def dateTimeToTimestamp(dt: DateTime): Timestamp = new Timestamp(dt.getMillis)
  implicit def timestampToDateTime(t: Timestamp): DateTime = new DateTime(t)

  def userToRow(user: User): UsersRow = UsersRow(
    id = user.id,
    username = user.username,
    name = Option(user.name),
    description = Option(user.description),
    imageUrl = Option(user.imageUrl),
    createdAt = user.createdAt,
    updatedAt = user.updatedAt)

  def userToRow(user: NewUser): UsersRow = UsersRow(
    id = 0, //Slick will use auto-generated primary key on insert
    username = user.username,
    name = Option(user.name),
    description = Option(user.description),
    imageUrl = Option(user.imageUrl),
    createdAt = new DateTime,
    updatedAt = new DateTime)

  def rowToUser(row: UsersRow): User = User(
    id = row.id,
    username = row.username,
    name = row.name getOrElse "",
    description = row.description getOrElse "",
    imageUrl = row.imageUrl getOrElse "",
    createdAt = row.createdAt,
    updatedAt = row.updatedAt)

  def rowToTweet(row: TweetsRow): Tweet = Tweet(
    id = row.id,
    text = row.content getOrElse "",
    userId = row.userId,
    latitude = row.latitude,
    longitude = row.longitude,
    createdAt = row.createdAt,
    updatedAt = row.updatedAt)

  def tweetToRow(tweet: NewTweet): TweetsRow = TweetsRow(
    id = 0, //Slick will use auto-generated primary key on insert
    content = Option(tweet.text),
    latitude = tweet.latitude,
    longitude = tweet.longitude,
    userId = tweet.userId,
    createdAt = new DateTime,
    updatedAt = new DateTime)

  def tweetToRow(tweet: Tweet): TweetsRow = TweetsRow(
    id = tweet.id,
    content = Option(tweet.text),
    latitude = tweet.latitude,
    longitude = tweet.longitude,
    userId = tweet.userId,
    createdAt = tweet.createdAt,
    updatedAt = tweet.updatedAt)
}
