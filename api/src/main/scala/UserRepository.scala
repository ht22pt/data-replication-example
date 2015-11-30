package com.banno

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global //TODO DB operations should be performed on their own ExecutionContext
import org.apache.avro.generic.GenericData

trait UserRepository extends Database with KafkaAvroSerdes with Instrumented
{

  lazy val selectTimer = metrics.timer("select")
  lazy val insertTimer = metrics.timer("insert")
  lazy val updateTimer = metrics.timer("update")

  def getUser(userId: Long): Future[UserResponse] = getUserFromRocksDb(userId)

  def getUserFromDatabase(userId: Long): Future[User] = Future {
    selectTimer.time {
      query(s"SELECT * FROM users WHERE id=$userId") { results => 
        if (!results.next()) throw new IllegalArgumentException(s"User $userId does not exist") //TODO better fail here so GET /api/users/123 returns 404
        User(results.getLong("id"), results.getString("username"), results.getString("name"), results.getString("description"), results.getString("image_url"))
      }
    }
  }

  lazy val usersDb = RocksDbFactory.usersRocksDb
  lazy val recentTweetsDb = RecentTweets.rocksDb

  def getUserFromRocksDb(userId: Long): Future[UserResponse] = Future {
    val user = usersDb.get(userId).getOrElse(throw new NullPointerException("No user in RocksDB for $userId"))
    val recentTweets = recentTweetsDb.get(userId).getOrElse(Nil)
    val ur = UserResponse(user, recentTweets)
    for (t <- recentTweets) println("createdAt = " + t.createdAt)
    ur
  }

  def userExists(userId: Long): Boolean = 
    query(s"SELECT COUNT(*) FROM users WHERE id=$userId") { results => 
      if (results.next()) results.getInt(1) > 0 else false
    }

  def createUser(user: NewUser): Future[User] = Future {
    insertTimer.time {
      insertAndGetGeneratedId(s"INSERT INTO users (username, name, description, image_url) VALUES ('${user.username}', '${user.name}', '${user.description}', '${user.imageUrl}')")
    }
  } flatMap getUserFromDatabase

  def rawCreateUser(userId: Long, user: NewUser): Unit = insertTimer.time {
    update(s"INSERT INTO users (id, username, name, description, image_url) VALUES (${userId}, '${user.username}', '${user.name}', '${user.description}', '${user.imageUrl}')")
  }

  def createUser(userId: Long, user: NewUser): Future[User] = Future {
    rawCreateUser(userId, user)
  } flatMap { _ => getUserFromDatabase(userId) }

  def updateUser(user: User): Future[User] = Future {
    if (!userExists(user.id)) rawCreateUser(user.id, user.toNewUser)
    else updateTimer.time(update(s"UPDATE users SET username='${user.username}', name='${user.name}', description='${user.description}', image_url='${user.imageUrl}' WHERE id=${user.id}"))
    user
  }
}
