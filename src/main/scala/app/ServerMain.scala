package app

import distributed.server._
import distributed.{ConnectedPlayers, DistributedConnection}

object ServerMain extends App{
  val connection = DistributedConnection().connection
  val connectedPlayers = ConnectedPlayers()

  CommunicationService(CommunicationService.Service.PlayerLogin, connection, connectedPlayers).start()
  CommunicationService(CommunicationService.Service.PlayerPosition, connection, connectedPlayers).start()
  CommunicationService(CommunicationService.Service.PlayerInBuilding, connection, connectedPlayers).start()
  CommunicationService(CommunicationService.Service.PlayerLogout, connection, connectedPlayers).start()
  CommunicationService(CommunicationService.Service.PlayerIsBusy, connection, connectedPlayers).start()
}
