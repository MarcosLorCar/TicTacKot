object Menus {

    private fun menuOf(vararg options: Pair<String,() -> Unit>) { // Helper method

        for ((index, pair) in options.withIndex()) {
            println("(${index+1}) ${pair.first}") // Display options with format " (x) Y "
        }

        var response: Int?
        do {
            response = prompt("-> ").toString().toIntOrNull() //Prompt -> and get response
        } while (((response == null) || (response > options.size) || (response < 1)) // Check if valid and if not loop
                .also { if (it) println("Invalid option") }) // also; if it's not valid, display error

        options[response!!-1].second() // Run the func paired with response
    }

    // Menus

    fun mainMenu() {
        menuOf(
            "Host" to TictacGame::hostGame,
            "Join" to TictacGame::joinGame
        )
        mainMenu()
    }
}

fun prompt(prompt: String = ""): Any {
    print(prompt)
    return readln()
}