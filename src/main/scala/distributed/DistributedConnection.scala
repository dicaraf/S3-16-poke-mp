package distributed

import com.rabbitmq.client.{Connection, ConnectionFactory}
import utilities.Settings

/**
  * A DistributedConnection manages a connection with RabbitMQ
  */
trait DistributedConnection {
  /**
    * @return the connection with RabbitMQ
    */
  def connection: Connection

  /**
    * Closes the connection with RabbitMQ
    */
  def close(): Unit
}

object DistributedConnection {
  def apply(): DistributedConnection = new DistributedConnectionImpl()
}

/**
  * @inheritdoc
  */
class DistributedConnectionImpl extends DistributedConnection{
  private var _connection: Connection = _

  create()

  /**
    * Creates a new connection with RabbitMQ
    */
  private def create() = {
    import Settings._
    val factory = new ConnectionFactory
    factory.setHost(Constants.REMOTE_HOST_ADDRESS)
    factory.setPort(Constants.REMOTE_HOST_PORT)
    factory.setUsername(Constants.REMOTE_HOST_USERNAME)
    factory.setPassword(Constants.REMOTE_HOST_PASSWORD)
    //factory.setHost(Constants.LOCAL_HOST_ADDRESS)
    _connection = factory.newConnection
  }

  override val connection: Connection = _connection

  /**
    * @inheritdoc
    */
  override def close(): Unit = _connection.close()
}