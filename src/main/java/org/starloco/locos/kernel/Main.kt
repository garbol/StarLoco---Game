package org.starloco.locos.kernel

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.fusesource.jansi.AnsiConsole
import org.slf4j.LoggerFactory
import org.starloco.locos.area.map.GameMap
import org.starloco.locos.area.map.entity.InteractiveObject
import org.starloco.locos.database.Database
import org.starloco.locos.entity.mount.Mount
import org.starloco.locos.event.EventManager
import org.starloco.locos.exchange.ExchangeClient
import org.starloco.locos.game.GameServer
import org.starloco.locos.game.scheduler.entity.WorldPlayerOption
import org.starloco.locos.game.scheduler.entity.WorldPub
import org.starloco.locos.game.scheduler.entity.WorldSave
import org.starloco.locos.game.world.World
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.*

object Main {


    var logger = LoggerFactory.getLogger(Main::class.java) as Logger
    val runnables: MutableList<Runnable> = LinkedList()

    var mapAsBlocked = false
    var fightAsBlocked = false
    var tradeAsBlocked = false

    lateinit var gameServer: GameServer
    var exchangeClient: ExchangeClient? = null

    @Throws(SQLException::class)
    @JvmStatic fun main(args: Array<String>) {
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                if (Config.isRunning) {
                    Config.isRunning = false

                    GameServer.setState(0)
                    WorldSave.cast(0)
                    if (!Config.HEROIC) {
                        Database.getDynamics().heroicMobsGroups.deleteAll()
                        for (map in World.world.maps) {
                            map.mobGroups.values.filterNot { it.isFix }.forEach { Database.getDynamics().heroicMobsGroups.insert(map.id, it, null) }
                        }
                    }
                    GameServer.setState(0)

                    Main.gameServer.kickAll(true)
                    Logging.getInstance().stop()
                    Database.getStatics().serverData.loggedZero()
                }
                Main.logger.info("The server is now closed.")
            }
        })

        try {
            System.setOut(PrintStream(System.out, true, "IBM850"))
            if (!File("Logs/Error").exists()) File("Logs/Error").mkdir()
            System.setErr(PrintStream(FileOutputStream("Logs/Error/" + SimpleDateFormat("dd-MM-yyyy - HH-mm-ss", Locale.FRANCE).format(Date()) + ".log")))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Main.start()
    }

    fun start() {
        Main.setTitle("StarLoco - Loading data..")
        Main.logger.info("You use ${System.getProperty("java.vendor")} with the version ${System.getProperty("java.version")}")
        Main.logger.debug("Starting of the server : ${SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.FRANCE).format(Date())}")
        Main.logger.debug("Current timestamp ms : ${System.currentTimeMillis()}")
        Main.logger.debug("Current timestamp ns : ${System.nanoTime()}")

        Logging.getInstance().initialize()

        if (Database.launchDatabase()) {
            Config.isRunning = true
            World.world.createWorld()

            this.gameServer = GameServer();
            gameServer.initialize()
            ExchangeClient().initialize()

            Main.refreshTitle()
            Main.logger.info("The server is ready ! Waiting for connection..\n")

            if (!Config.DEBUG) {
                val root = org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
                root.level = Level.OFF
            }

            while (Config.isRunning) {
                try {
                    WorldSave.updatable.update()
                    GameMap.updatable.update()
                    InteractiveObject.updatable.update()
                    Mount.updatable.update()
                    WorldPlayerOption.updatable.update()
                    WorldPub.updatable.update()
                    EventManager.getInstance().update()

                    if (!Main.runnables.isEmpty()) {
                        for (runnable in LinkedList(Main.runnables)) {
                            try {
                                if (runnable != null) {
                                    runnable.run()
                                    Main.runnables.remove(runnable)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    }

                    Thread.sleep(100)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        } else {
            Main.logger.error("An error occurred when the server have try a connection on the Mysql server. Please verify your identification.")
        }
    }

    fun stop(reason: String) {
        Logging.getInstance().write("Error", reason)
        System.exit(0)
    }

    private fun setTitle(title: String) {
        AnsiConsole.out.printf("%c]0;%s%c", '\u001b', title, '\u0007')
    }

    fun refreshTitle() {
        if (Config.isRunning)
            Main.setTitle("${Main.gameServer.clients.size} joueur(s) | Id: ${Config.SERVER_ID}| Port: ${Config.gamePort}")
    }

    fun clear() { //~30ms
        AnsiConsole.out.print("\u001b[H\u001b[2J")
    }
}
