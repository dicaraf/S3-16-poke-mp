package distributed

import org.scalatest.FunSuite

class DistributedConnectionTest extends FunSuite {

  def fixture =
    new {
      val distributedConnection = DistributedConnection()
    }

  test("A connection is created") {
    val f = fixture
    assert(f.distributedConnection.connection.isOpen)
  }

  test("A connection is closed") {
    val f = fixture
    f.distributedConnection.close()
    assert(!f.distributedConnection.connection.isOpen)
  }

}
