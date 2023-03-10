import kotlin.system.exitProcess

object Menus {

    private fun menuOf(vararg options: Pair<String,() -> Unit>) {

        fun prompt(prompt: String = ""): Int? {
            print(prompt)
            return readln().toIntOrNull()
        }

        for ((index, pair) in options.withIndex()) {
            println("(${index+1}) ${pair.first}") // Display options with format " (x) Y "
        }

        var response: Int?
        do {
            response = prompt("-> ") //Prompt -> and get response
        } while (((response == null) || (response > options.size) || (response < 1)) // Check if valid and if not loop
                .also { if (it) println("Invalid option") }) // also; if it's not valid, display error

        options[response!!-1].second() // Run the func paired with response
    } // Helper method

    // Menus

    fun mainMenu() {
        menuOf(
            "Test1" to {
                println("test1")
            },
            "Test2" to {
                println("test2")
            },
            "Exit" to {
                exitProcess(0)
            },
        )
        mainMenu()
    }
}
