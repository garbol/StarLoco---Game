package org.starloco.locos.kernel;

import org.starloco.locos.database.Database;
import org.starloco.locos.util.Points;

import java.io.*;

public class Config {

    private static final Config singleton = new Config();
    public static int config = 0;

    public final long startTime = System.currentTimeMillis();
    public boolean HALLOWEEN = false, NOEL = false, HEROIC = false, TEAM_MATCH = false, DEATH_MATCH = false, AUTO_EVENT = false;
    public boolean AUTO_REBOOT = true, ALL_ZAAP = true, ALL_EMOTE = false, ONLY_LOCAL = false, ENCRYPT_PACKET = true, RESET_LIMIT = false;
    public short TIME_PER_EVENT = 60;

    public String NAME, url, startMessage = "", colorMessage = "B9121B";

    public int START_MAP = 0, START_CELL = 0;
    public int RATE_KAMAS = 1, RATE_DROP = 1, RATE_HONOR = 1, RATE_JOB = 1, RATE_FM = 1;
    public float RATE_XP = 1;

    public Points points = new Points() {
        @Override
        public int load(String user) {
            return Database.getStatics().getAccountData().loadPointsWithoutUsersDb(user);
        }

        @Override
        public void update(int id, int points) {
            Database.getStatics().getAccountData().updatePointsWithoutUsersDb(id, points);
        }
    };

    public static Config getInstance() {
        return singleton;
    }

    public void set(int i) {
        config = i;
        switch (i) {
            case 10://Local
                //Exchange
                Main.exchangePort = 666;
                Main.exchangeIp = "127.0.0.1";
                //BD
                Main.loginHostDB = "127.0.0.1";
                Main.loginNameDB = "login";
                Main.loginUserDB = "root";
                Main.loginPassDB = "";
                Main.loginPortDB = "3306";
                //Game
                Main.gamePort = 451;
                Main.hostDB = "127.0.0.1";
                Main.nameDB = "game";
                Main.userDB = "root";
                Main.passDB = "";
                Main.portDB = "3306";
                Main.Ip = "127.0.0.1";
                this.NAME = "Jiva";
                this.url = "nashira";
                this.AUTO_REBOOT = true;
                break;

            case 1://test
                Main.exchangePort = 666;
                Main.exchangeIp = "127.0.0.1";
                //Bdd
                Main.loginHostDB = "127.0.0.1";
                Main.loginNameDB = "login";
                Main.loginUserDB = "elements";
                Main.loginPassDB = "elements02";
                Main.loginPortDB = "3306";
                //Game
                Main.gamePort = 5444;
                Main.hostDB = "127.0.0.1";
                Main.nameDB = "game";
                Main.userDB = "elements";
                Main.passDB = "elements02";
                Main.portDB = "3306";
                Main.Ip = "176.150.39.220";
                this.NAME = "Test";
                this.url = "www.elements-games.eu";
                this.AUTO_REBOOT = true;
                this.RESET_LIMIT = true;
                break;

        }
    }

    public void load() {
        FileReader file = null;
        try {
            file = new FileReader("config.txt");
        } catch (FileNotFoundException ignored) {}
        if (file != null) {
            try {
                BufferedReader config = new BufferedReader(new FileReader("config.txt"));
                String line;
                while ((line = config.readLine()) != null) {
                    if (line.split("=").length == 1)
                        continue;

                    String param = line.split("=")[0].trim().replace(" ", "");
                    String value = line.split("=")[1].trim();

                    if (value.isEmpty() || value.equals(" "))
                        continue;

                    switch (param.toUpperCase()) {
                        case "SERVER_ID":
                            Main.serverId = Integer.parseInt(value);
                            break;
                        case "SERVER_KEY":
                            Main.key = value;
                            break;
                        case "CONFIG_ID":
                            this.set(Integer.parseInt(value));
                            break;
                        case "DEBUG":
                            Main.modDebug = value.equals("true");
                            break;
                        case "USE_LOG":
                            Logging.USE_LOG = value.equals("true");
                            break;
                        case "SUBSCRIBER":
                            Main.useSubscribe = value.equals("true");
                            break;
                        case "START_PLAYER":
                            try {
                                this.START_MAP = Integer.parseInt(value.split("\\,")[0]);
                                this.START_CELL = Integer.parseInt(value.split("\\,")[1]);
                            } catch (Exception e) {
                                // ok
                            }
                            break;
                        case "ALL_ZAAP":
                            this.ALL_ZAAP = value.equals("true");
                            break;
                        case "ALL_EMOTE":
                            this.ALL_EMOTE = value.equals("true");
                            break;
                        case "RATE_XP":
                            this.RATE_XP = Float.parseFloat(value);
                            break;
                        case "RATE_DROP":
                            this.RATE_DROP = Integer.parseInt(value);
                            break;
                        case "RATE_JOB":
                            this.RATE_JOB = Integer.parseInt(value);
                            break;
                        case "RATE_KAMAS":
                            this.RATE_KAMAS = Integer.parseInt(value);
                            break;
                        case "RATE_FM":
                            this.RATE_FM = Integer.parseInt(value);
                            break;
                        case "MESSAGE":
                            this.startMessage = value;
                            break;
                        case "NOEL":
                            this.NOEL = value.equals("true");
                            break;
                        case "HALLOWEEN":
                            this.HALLOWEEN = value.equals("true");
                            break;
                        case "HEROIC":
                            this.HEROIC = value.equals("true");
                            break;
                        case "CRYPT":
                            this.ENCRYPT_PACKET = value.equals("true");
                            break;
                        case "TIME_PER_EVENT":
                            this.TIME_PER_EVENT = Short.parseShort(value);
                            break;
                        case "AUTO_EVENT":
                            this.AUTO_EVENT = value.equals("true");
                            break;

                    }
                }
                config.close();
                file.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                BufferedWriter config = new BufferedWriter(new FileWriter("config.txt", true));
                String str = "## Configuration file of StarLoco ##\n\n"
                        + "## Server information : \n" + "SERVER_ID = 1\n"
                        + "SERVER_KEY = jiva\n" + "CONFIG_ID = 1\n"
                        + "DEBUG = false\n" + "USE_LOG = true\n"
                        + "SUBSCRIBER = false\n" + "PUB1 = \n" + "PUB2 = \n"
                        + "PUB3 = \n" + "START_PLAYER = 0,0\n"
                        + "ALL_ZAAP = false\n" + "ALL_EMOTE = false\n"
                        + "MESSAGE = Bienvenue sur <b>StarLoco</b> !\n\n"
                        + "## Server rate : \n" + "RATE_XP = 1\n"
                        + "RATE_DROP = 1\n" + "RATE_JOB = 1\n"
                        + "RATE_KAMAS = 1\n" + "RATE_FM = 1";
                config.write(str);
                config.newLine();
                config.flush();
                config.close();

                Main.logger.info("The configuration file was created.");
                this.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}