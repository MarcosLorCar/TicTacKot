import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.net.URL
import kotlin.random.Random

class TictacGame(val hosting: Boolean, private val opponent: Socket) {

    private var writer = PrintWriter(opponent.getOutputStream())
    private var reader = BufferedReader(InputStreamReader(opponent.getInputStream()))

    companion object {
        private lateinit var game: TictacGame

        fun hostGame() {
            val server: ServerSocket
            try {
                server = ServerSocket(0)
                val ip = BufferedReader(InputStreamReader(URL("https://checkip.amazonaws.com").openStream())).readLine()
                println("Hosting server on $ip:${server.localPort}")
            } catch (_: Exception) {
                println("Error establishing a server")
                return
            }
            println("Waiting for opponent...")
            val opponent = server.accept()
            println("Opponent found")
            game = TictacGame(true, opponent)
                .also { it.play() }
        }
        fun joinGame() {
            val ip = Menus.prompt("Ip: ").toString()
            val port = Menus.prompt("Port: ").toString()
                .toIntOrNull() ?: -1
            val server: Socket
            try {
                server = Socket(ip,port)
                println("Connected to $ip:$port")
            } catch (_: Exception) {
                println("Error connecting to server")
                return
            }
            game = TictacGame(false, server)
                .also { it.play() }
        }
    }

    private var myTurn = false

    private fun play() {
        if (hosting) {
            myTurn = Random.nextBoolean()
            writer.println(!myTurn)
            writer.flush()
        } else {
            myTurn = reader.readLine().toBoolean()
        }
    }
}