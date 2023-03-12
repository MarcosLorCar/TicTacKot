var port = 3003
fun main(args: Array<String>) {
    port = if (args.size>1) args[0].toInt() else 3003
    Menus.mainMenu()
}