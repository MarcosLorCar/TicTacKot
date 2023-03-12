import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.net.URL
import kotlin.random.Random

class TictacGame(private val hosting: Boolean, private val opponent: Socket) {

    private var writer = PrintWriter(opponent.getOutputStream())
    private var reader = BufferedReader(InputStreamReader(opponent.getInputStream()))

    companion object {
        private lateinit var game: TictacGame

        fun hostGame() {
            val server: ServerSocket
            try {
                server = ServerSocket(0)
                val ip = BufferedReader(InputStreamReader(URL("https://checkip.amazonaws.com").openStream())).readLine()
                println("Hosting server on IP: $ip Port: ${server.localPort}")
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
            val ip = prompt("Ip: ").toString()
            val port = prompt("Port: ").toString()
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

    private var board = arrayOf(
        arrayOf(" ", " ", " "),
        arrayOf(" ", " ", " "),
        arrayOf(" ", " ", " "),
    )
    private var myTurn = false

    private fun printBoard() {
        println("""
                 |     |     
              ${board[0][0]}  |  ${board[0][1]}  |  ${board[0][2]}  
            _____|_____|_____
                 |     |     
              ${board[1][0]}  |  ${board[1][1]}  |  ${board[1][2]}  
            _____|_____|_____
                 |     |     
              ${board[2][0]}  |  ${board[2][1]}  |  ${board[2][2]}  
                 |     |     
        """.trimIndent())
    }

    private val won: Boolean? // Returns : true-won false-lost null-stillPlaying
        get() {
            // Check for rows
            for (row in board) {
                if (row[0] == row[1] && row[1] == row[2] && row[0] == "X")
                    return true
                if (row[0] == row[1] && row[1] == row[2] && row[0] == "O")
                    return false
            }
            // Check for columns
            for (i in board[0].indices) {
                if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] == "X")
                    return true
                if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] == "O")
                    return false
            }
            // Check for diagonals
            if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] == "X")
                return true
            if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] == "X")
                return true
            if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] == "O")
                return false
            if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] == "O")
                return false
            return null
        }

    private fun play() {
        //Decide first turn
        if (hosting) {
            myTurn = Random.nextBoolean()
            writer.println(!myTurn)
            writer.flush()
        } else {
            myTurn = reader.readLine().toBoolean()
        }

        // Game loop
        while (won == null) {
            if (myTurn) {
                var row: Int?
                var column: Int?
                do {

                    printBoard() // Show board

                    do { // Get a row 1-3
                        row = prompt("Row: ").toString().toIntOrNull()
                    } while ((row == null || (row < 1 || row > 3))
                            .also {
                                if (it) println("You need to input a number 1-3")
                            })

                    do { // Get a column 1-3
                        column = prompt("Column: ").toString().toIntOrNull()
                    } while ((column == null || (column < 1 || column > 3))
                            .also {
                                if (it) println("You need to input a number 1-3")
                            })

                    row = row!! - 1
                    column = column!! -1

                } while ((board[row!!][column!!] != " ")
                        .also {
                            if (it) println("That slot is occupied")
                        }) // Repeat if that slot is not empty
                board[row][column] = "X" // Set slot
                printBoard() // Show board again
                writer.println("$row:$column")
                writer.flush()
            } else {
                println("Waiting for opponent to place")
                val row: Int
                val column: Int
                with(reader.readLine().split(":")) {
                    row = this[0].toInt()
                    column = this[1].toInt()
                }
                board[row][column] = "O"
            }
            myTurn = !myTurn
        }
        if (won as Boolean) {
            printBoard() //Show won board
            println("""
                --------
                You won!
                --------
            """.trimIndent())
        } else {
            printBoard() //Show lost board
            println("""
                -------------
                Opponent wins
                -------------
            """.trimIndent())
        }
        opponent.close()
    }
}