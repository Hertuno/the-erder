package ru.alastar.main.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Connection;

import ru.alastar.database.DatabaseClient;
import ru.alastar.enums.ActionType;
import ru.alastar.enums.EntityType;
import ru.alastar.enums.EquipType;
import ru.alastar.game.Attributes;
import ru.alastar.game.CraftInfo;
import ru.alastar.game.Entity;
import ru.alastar.game.Inventory;
import ru.alastar.game.Item;
import ru.alastar.game.PlantsType;
import ru.alastar.game.Skill;
import ru.alastar.game.Skills;
import ru.alastar.game.Statistic;
import ru.alastar.game.Stats;
import ru.alastar.game.security.Crypt;
import ru.alastar.game.spells.Heal;
import ru.alastar.game.systems.CraftSystem;
import ru.alastar.game.systems.GardenSystem;
import ru.alastar.game.systems.MagicSystem;
import ru.alastar.game.worldwide.Location;
import ru.alastar.game.worldwide.LocationFlag;
import ru.alastar.main.Configuration;
import ru.alastar.main.Main;
import ru.alastar.main.handlers.*;
import ru.alastar.main.net.requests.CommandRequest;
import ru.alastar.main.net.responses.AddSkillResponse;
import ru.alastar.main.net.responses.AddStatResponse;
import ru.alastar.main.net.responses.InventoryResponse;
import ru.alastar.main.net.responses.LoginResponse;
import ru.alastar.main.net.responses.MessageResponse;
import ru.alastar.main.net.responses.RegisterResponse;
import ru.alastar.main.net.responses.SetData;
import ru.alastar.world.ServerWorld;

public class Server
{

    public static com.esotericsoftware.kryonet.Server           server;
    public static int                                           port = 25565;
    public static Hashtable<InetSocketAddress, ConnectedClient> clients;
    public static Hashtable<Integer, ServerWorld>               worlds;
    public static Hashtable<Integer, Inventory>                 inventories;
    public static Hashtable<Integer, Entity>                    entities;
    public static Hashtable<String, Handler>                    commands;
    public static Hashtable<String, Float>                      plantsGrowTime;

    public static Random                                        random;

    public static void startServer()
    {
        try
        {
            server = new com.esotericsoftware.kryonet.Server();
            server.start();
            server.bind(Integer.parseInt(Configuration.GetEntryValue("port")), Integer.parseInt(Configuration.GetEntryValue("port")) + 1);
            server.addListener(new TListener(server));
            Init();
        } catch (IOException e)
        {
            handleError(e);
        }
    }

    public static void Init()
    {
        try
        {
            random = new Random();
            clients = new Hashtable<InetSocketAddress, ConnectedClient>();
            worlds = new Hashtable<Integer, ServerWorld>();
            inventories = new Hashtable<Integer, Inventory>();
            entities = new Hashtable<Integer, Entity>();
            commands = new Hashtable<String, Handler>();
            plantsGrowTime = new Hashtable<String, Float>();

            DatabaseClient.Start();
            LoadWorlds();
            LoadEntities();
            LoadInventories();
            LoadPlants();
            GardenSystem.StartGrowTimer();
            FillWoods();
            FillMiningItems();
            SetupSpells();
            FillCommands();
            FillPlants();
            FillCrafts();
        } catch (InstantiationException e)
        {
            Main.Log("[ERROR]", e.getLocalizedMessage());
        } catch (IllegalAccessException e)
        {
            Main.Log("[ERROR]", e.getLocalizedMessage());
        } catch (ClassNotFoundException e)
        {
            Main.Log("[ERROR]", e.getLocalizedMessage());
        }
        try
        {
            ExecutorService service = Executors.newCachedThreadPool();
            service.submit(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

                        for (;;)
                        {
                            String line;

                            line = in.readLine();

                            if (line == null)
                            {
                                continue;
                            }

                            if ("save".equals(line.toLowerCase()))
                            {
                                Save();
                                continue;
                            }

                            if ("stop".equals(line.toLowerCase()))
                            {
                                Save();
                                server.close();
                                Main.writer.close();
                                break;
                            }

                            if ("encrypt".equals(line.split(" ")[0]
                                    .toLowerCase()))
                            {
                                System.out.println(Crypt.encrypt(line
                                        .split(" ")[1]));
                                continue;
                            }

                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                private void Save()
                {

                }
            });
        } catch (Exception e)
        {
            handleError(e);
        }
    }

    private static void FillCrafts()
    {
        ArrayList<String> neededItems = new ArrayList<String>();
        Attributes attrs = new Attributes();

        neededItems.add("plain wood");
        neededItems.add("amber");
        attrs.addAttribute("Charges", 10);
        attrs.addAttribute("Durability", 100);

        CraftSystem.registerCraft("wooden_totem", new CraftInfo(neededItems,
                "Carpentry", 0, "Wooden Totem", EquipType.None,
                ActionType.Cast, attrs));
    }

    private static void FillPlants()
    {
        plantsGrowTime.put("wheat", (float) (24 * 60 * 60 * 1000));
    }

    private static void FillCommands()
    {
        registerCommand("login", new LoginHandler());
        registerCommand("register", new RegisterHandler());
        registerCommand("move", new MoveHandler());
        registerCommand("act", new ActionHandler());
        registerCommand("say", new ChatHandler());
        registerCommand("cast", new CastHandler());
        registerCommand("attack", new AttackHandler());
        registerCommand("help", new HelpHandler());
        registerCommand("craft", new CraftHandler());

    }

    public static void registerCommand(String key, Handler h)
    {
        try
        {
            commands.put(key, h);
        } catch (Exception e)
        {
            handleError(e);
        }
    }

    private static void SetupSpells()
    {
        MagicSystem.addSpell("heal", new Heal());
    }

    private static void FillWoods()
    {
        Location.woods.put(0, "plain wood");
        Location.woods.put(10, "oak wood");
        Location.woods.put(25, "yew wood");
        Location.woods.put(45, "ash wood");
        Location.woods.put(50, "greatwood");
    }

    private static void FillMiningItems()
    {
        Location.miningItems.put(0, "copper ore");
        Location.miningItems.put(10, "iron ore");
        Location.miningItems.put(25, "shadow metal ore");
        Location.miningItems.put(25, "amber");
        Location.miningItems.put(30, "old corrored golem core");
        Location.miningItems.put(35, "emerald");
        Location.miningItems.put(45, "gold ore");
        Location.miningItems.put(50, "valor ore");
        Location.miningItems.put(50, "diamond");
        Location.miningItems.put(50, "swiftstone");
    }

    private static void LoadPlants()
    {

    }

    public static void SavePlant(PlantsType p)
    {
        // Main.Log("[DEBUG]","save plant grow");

        ResultSet plantsRs = DatabaseClient
                .commandExecute("SELECT * FROM plants WHERE locationId="
                        + p.loc.id);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        String time = sdf.format(p.finish);
        try
        {
            if (plantsRs.next())
            {
                DatabaseClient.commandExecute("UPDATE plants SET name='"
                        + p.plantName + "', growTime='" + time
                        + "' WHERE locationId=" + p.loc.id);

            } else
            {
                DatabaseClient
                        .commandExecute("INSERT INTO plants(name, growTime, locationId) VALUES('"
                                + p.plantName
                                + "','"
                                + time
                                + "',"
                                + p.loc.id
                                + ");");
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    private static void LoadInventories()
    {
        ResultSet inventoriesRs = DatabaseClient
                .commandExecute("SELECT * FROM inventories");
        Inventory i;
        try
        {
            while (inventoriesRs.next())
            {
                i = new Inventory(inventoriesRs.getInt("entityId"),
                        inventoriesRs.getInt("max"),
                        LoadItems(inventoriesRs.getInt("entityId")));
                inventories.put(i.entityId, i);
            }
        } catch (SQLException e)
        {
            handleError(e);
        }
    }

    private static ArrayList<Item> LoadItems(int i)
    {
        ArrayList<Item> items = new ArrayList<Item>();
        Attributes attrs;
        ResultSet itemsRS = DatabaseClient
                .commandExecute("SELECT * FROM items WHERE entityId=" + i);
        ResultSet attrsRS;
        try
        {
            while (itemsRS.next())
            {
                attrsRS = DatabaseClient
                        .commandExecute("SELECT * FROM attributes WHERE itemId="
                                + itemsRS.getInt("id"));
                // Loading attributes
                attrs = new Attributes();
                while (attrsRS.next())
                {
                    attrs.addAttribute(attrsRS.getString("name"),
                            attrsRS.getInt("value"));
                }

                items.add(new Item(itemsRS.getInt("id"), itemsRS
                        .getInt("entityId"), itemsRS.getString("caption"),
                        itemsRS.getInt("amount"), itemsRS
                                .getInt("x"), itemsRS
                                .getInt("y"),itemsRS
                                .getInt("z"), EquipType
                                .valueOf(itemsRS.getString("type")), ActionType
                                .valueOf(itemsRS.getString("actionType")),
                        attrs, getWorld(itemsRS.getInt("worldId"))));
            }
        } catch (SQLException e)
        {
            handleError(e);
        }

        return items;
    }

    private static void LoadEntities()
    {
        try
        {
            Hashtable<Integer, String> playersEntities = new Hashtable<Integer, String>();
            ResultSet accountsEntities = DatabaseClient
                    .commandExecute("SELECT * FROM accounts");
            ResultSet allEntities = DatabaseClient
                    .commandExecute("SELECT * FROM entities");
            Stats stats;
            Skills skills;
            ResultSet skillsRS;
            ResultSet statsRS;
            ArrayList<String> knownSpells;
            ResultSet spellsRS;
            Entity e;

            while (accountsEntities.next())
            {
                playersEntities.put(accountsEntities.getInt("entityId"),
                        accountsEntities.getString("login"));
            }

            while (allEntities.next())
            {
                if (!playersEntities.containsKey(allEntities.getInt("id")))
                {
                    stats = new Stats();

                    skills = new Skills();

                    knownSpells = new ArrayList<String>();

                    skillsRS = DatabaseClient
                            .commandExecute("SELECT * FROM skills WHERE entityId="
                                    + allEntities.getInt("id"));
                    statsRS = DatabaseClient
                            .commandExecute("SELECT * FROM stats WHERE entityId="
                                    + allEntities.getInt("id"));
                    spellsRS = DatabaseClient
                            .commandExecute("SELECT * FROM knownSpells WHERE entityId="
                                    + allEntities.getInt("id"));
                    while (skillsRS.next())
                    {
                        skills.put(
                                skillsRS.getString("name"),
                                new Skill(skillsRS.getString("name"), skillsRS
                                        .getInt("sValue"), skillsRS
                                        .getInt("mValue"), skillsRS
                                        .getFloat("hardness"), skillsRS
                                        .getString("primaryStat"), skillsRS
                                        .getString("secondaryStat")));
                    }

                    while (statsRS.next())
                    {
                        stats.put(
                                statsRS.getString("name"),
                                new Statistic(statsRS.getString("name"),
                                        statsRS.getInt("sValue"), statsRS
                                                .getInt("mValue"), statsRS
                                                .getFloat("hardness")));
                    }

                    while (spellsRS.next())
                    {
                        knownSpells.add(spellsRS.getString("spellName"));
                    }

                    e = new Entity(allEntities.getInt("id"),
                            allEntities.getString("caption"),
                            EntityType.valueOf(allEntities.getString("type")),
                            allEntities.getInt("x"),allEntities.getInt("y"),allEntities.getInt("z"),
                            skills, stats, knownSpells, getWorld(allEntities.getInt("worldId")));
                    entities.put(e.id, e);
                }
            }
        } catch (SQLException e)
        {
            handleError(e);
        }

    }

    protected static void SaveLocation(Location l)
    {
        // Location info
        DatabaseClient.commandExecute("UPDATE locations SET name='" + l.name
                + "' WHERE id=" + l.id);
        // Near Locations
        String s = "";
        for (int id : l.nearLocationsIDs)
        {
            s += id + ";";
        }
        DatabaseClient.commandExecute("UPDATE locations SET nearLocationsIDs='"
                + s + "' WHERE id=" + l.id);

        // Entities
        for (Entity e : l.entities.values())
        {
            saveEntity(e);
        }

        // Flags
        for (String s1 : l.flags.keySet())
        {
            saveFlag(l.id, s1, l.flags.get(s1));
        }
    }

    public static void saveFlag(int id, String s, LocationFlag e)
    {
        ResultSet plantsRs = DatabaseClient
                .commandExecute("SELECT * FROM locationflags WHERE locationId="
                        + id + " AND flag='" + s + "'");
        try
        {
            if (plantsRs.next())
            {
                DatabaseClient.commandExecute("UPDATE locationflags SET val='"
                        + e.value + "' WHERE locationId=" + id + " AND flag='"
                        + s + "'");

            } else
            {
                DatabaseClient
                        .commandExecute("INSERT INTO locationflags(locationId, flag, val) VALUES("
                                + id + ",'" + s + "','" + e.value + "')");
            }
        } catch (SQLException e1)
        {
            handleError(e1);
        }
    }

    private static void LoadWorlds()
    {
        try {
            File worldDir = new File(System.getProperty("user.dir")
                    + "\\worlds\\");
            if(!worldDir.exists())
                worldDir.mkdir();

            String fileName = "";
            ServerWorld w;
            com.alastar.game.World clientW;
            FileInputStream f_in = null;
            ObjectInputStream obj_in = null;
            File worldFile;
            if (worldDir.listFiles().length > 0) {
                for (int i = 0; i < worldDir.listFiles().length; ++i) {
                    worldFile = worldDir.listFiles()[i];
                    fileName = worldFile.getName();
                    fileName = fileName.replaceAll(".bin", "");
                    f_in = new FileInputStream(worldFile);
                    obj_in = new ObjectInputStream(f_in);
                    w = new ServerWorld(i, fileName);
                    clientW = (com.alastar.game.World) obj_in.readObject();
                    w.tiles = clientW.tiles;
                    w.id = clientW.id;
                    w.version = clientW.version;
                    w.zMax = clientW.zMax;
                    w.zMin = clientW.zMin;
                    Main.Log("[LOAD]","Loaded world " + w.name + " id: " + w.id);
                    worlds.put(w.id, w);
                }
                obj_in.close();
                f_in.close();
            } else {
                Main.Log("[LOAD]","World files not found!");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasClient(Connection connection)
    {
        if (clients.containsKey(connection.getRemoteAddressUDP()))
            return true;
        else
            return false;
    }

    public static void addClient(Connection connection)
    {
        clients.put(connection.getRemoteAddressUDP(), new ConnectedClient(
                connection));
    }

    public static void removeClient(Connection connection)
    {
        ConnectedClient c = getClient(connection);
        if (c.controlledEntity != null)
        {
            // Main.Log("[LOGIN]",
            // "Controlled entity is not null, saving it...");
            c.controlledEntity.RemoveYourself();
        }// else
         // Main.Log("[LOGIN]", "Controlled entity is null, skipping save");

        clients.remove(connection.getRemoteAddressUDP());
    }

    public static void Login(String login, String pass, Connection c)
    {
        try
        {
            // Main.Log("[SERVER]", "Process auth...");
            ResultSet l = DatabaseClient
                    .commandExecute("SELECT * FROM accounts WHERE login='"
                            + login + "' AND password='" + Crypt.encrypt(pass)
                            + "'");
            if (l.next())
            {
                // Main.Log("[SERVER]", "...auth succesful!");

                LoginResponse r = new LoginResponse();
                r.succesful = true;
                SendTo(c, r);

                ConnectedClient client = getClient(c);
                if (!client.logged)
                {
                    client.login = l.getString("login");
                    client.pass = l.getString("password");
                    client.mail = l.getString("mail");
                    client.logged = true;
                    LoadPlayer(l.getInt("entityId"), client);
                } else
                {
                    MessageResponse r1 = new MessageResponse();
                    r1.msg = "This account is already logged in!";
                    SendTo(c, r1);
                }
            } else
            {
                // Main.Log("[SERVER]", "...auth unsuccesful(");

                LoginResponse r = new LoginResponse();
                r.succesful = false;
                SendTo(c, r);
            }

        } catch (SQLException e)
        {
            handleError(e);
        }
    }

    private static void LoadPlayer(int i, ConnectedClient c)
    {
        try
        {
            ResultSet e = DatabaseClient
                    .commandExecute("SELECT * FROM entities WHERE id=" + i);

            if (e.next())
            {
                SetData sd = new SetData();
                // Main.Log("[SERVER]", "Creating entity...");
                Stats stats = new Stats();
                Skills skills = new Skills();
                ArrayList<String> knownSpells = new ArrayList<String>();

                ResultSet skillsRS = DatabaseClient
                        .commandExecute("SELECT * FROM skills WHERE entityId="
                                + e.getInt("id"));
                ResultSet statsRS = DatabaseClient
                        .commandExecute("SELECT * FROM stats WHERE entityId="
                                + e.getInt("id"));
                ResultSet spellsRS = DatabaseClient
                        .commandExecute("SELECT * FROM knownSpells WHERE entityId="
                                + e.getInt("id"));

                while (skillsRS.next())
                {
                    skills.put(
                            skillsRS.getString("name"),
                            new Skill(skillsRS.getString("name"), skillsRS
                                    .getInt("sValue"), skillsRS
                                    .getInt("mValue"), skillsRS
                                    .getFloat("hardness"), skillsRS
                                    .getString("primaryStat"), skillsRS
                                    .getString("secondaryStat")));
                }

                while (statsRS.next())
                {
                    stats.put(
                            statsRS.getString("name"),
                            new Statistic(statsRS.getString("name"), statsRS
                                    .getInt("sValue"),
                                    statsRS.getInt("mValue"), statsRS
                                            .getFloat("hardness")));
                }

                while (spellsRS.next())
                {
                    knownSpells.add(spellsRS.getString("spellName"));
                }

                Entity entity = new Entity(e.getInt("id"),
                        e.getString("caption"), EntityType.valueOf(e
                                .getString("type")),
                        e.getInt("x"),e.getInt("y"),e.getInt("z"), skills, stats,
                        knownSpells, getWorld(e.getInt("worldId")));
                // Main.Log("[SERVER]",
                // "Assigning it as a controlled to the connected client...");
                c.controlledEntity = entity;
                sd.id = entity.id;
                // Main.Log("[SERVER]", "Sending set data packet...");
                SendTo(c.connection, sd);

                entity.world.AddEntity(entity);
                // Main.Log("[SERVER]", "Sending other entities to it...");
                entity.world.SendEntities(c);
                // Main.Log("[SERVER]", "Sending stats...");
                AddStatResponse r = new AddStatResponse();
                for (String s : entity.stats.vals.keySet())
                {
                    r.name = s;
                    r.sValue = entity.stats.get(s).value;
                    r.mValue = entity.stats.get(s).maxValue;
                    SendTo(c.connection, r);
                }
                // Main.Log("[SERVER]", "Sending skills...");
                AddSkillResponse sr = new AddSkillResponse();
                for (String s : entity.skills.vals.keySet())
                {
                    sr.name = s;
                    sr.sValue = entity.skills.get(s).value;
                    sr.mValue = entity.skills.get(s).maxValue;
                    SendTo(c.connection, sr);

                }
                // Main.Log("[SERVER]", "Sending inventory...");
                InventoryResponse ir = new InventoryResponse();
                for (Item i1 : inventories.get(entity.id).items)
                {
                    ir.id = i1.id;
                    ir.captiion = i1.caption;
                    ir.amount = i1.amount;
                    ir.attrs = i1.attributes.values;
                    SendTo(c.connection, ir);
                }

                // Main.Log("[SERVER]", "Data was sent to player. Fuf...");
                entities.put(entity.id, entity);
            }
        } catch (SQLException e1)
        {
            handleError(e1);
        }
    }

    private static ServerWorld getWorld(int int1)
    {
        try{
        return worlds.get(int1);}catch(Exception e)
        {
            Server.handleError(e);
        }
        return null;
    }

    public static void warnEntity(Entity e, String m)
    {
        try
        {
            MessageResponse r = new MessageResponse();
            r.msg = m;
            SendTo(getClientByEntity(e).connection, r);
        } catch (Exception er)
        {
            handleError(er);
        }
    }

    public static ConnectedClient getClient(Connection c)
    {
        return clients.get(c.getRemoteAddressUDP());
    }

    public static void SendTo(Connection c, Object o)
    {
        server.sendToUDP(c.getID(), o);
    }

    public static ConnectedClient getClientByEntity(Entity e1)
    {
        for (ConnectedClient c : clients.values())
        {
            if (c.controlledEntity != null)
            {
                if (c.controlledEntity.id == e1.id)
                {
                    return c;
                }
            }
        }
        return null;
    }

    public static void ProcessChat(String msg, Connection connection)
    {
        ConnectedClient c = getClient(connection);
        if (c.controlledEntity != null)
        {
            c.controlledEntity.world.sendAll(msg, c.controlledEntity.caption);
        }
    }

    public static void saveEntity(Entity entity)
    {
        try
        {
            // Main.Log("[SAVE]", "Saving entity...");
            // Entity Main
            ResultSet entityEqRS = DatabaseClient
                    .commandExecute("SELECT * FROM entities WHERE id="
                            + entity.id);
            if (entityEqRS.next())
                DatabaseClient.commandExecute("UPDATE entities SET caption='"
                        + entity.caption + "', type='" + entity.type.name()
                        + "', worldId=" + entity.world.id + ", x=" + entity.pos.x + ", y=" + entity.pos.y + ", z=" + entity.pos.z+ " WHERE id="
                        + entity.id);
            else
                DatabaseClient
                        .commandExecute("INSERT INTO entities(id, caption, type, worldId, ai, x, y, z) VALUES("
                                + entity.id
                                + ",'"
                                + entity.caption
                                + "', '"
                                + entity.type.name()
                                + "',"
                                + entity.world.id
                                + ", '', "
                                + entity.pos.x
                                + ","
                                + entity.pos.y
                                + ","
                                + entity.pos.z
                                +")");
            // Main.Log("[SAVE]", "Saving stats...");

            // Stats
            ResultSet statEqRS;
            for (Statistic s : entity.stats.vals.values())
            {
                statEqRS = DatabaseClient
                        .commandExecute("SELECT * FROM stats WHERE entityId="
                                + entity.id + " AND name='" + s.name + "'");

                if (statEqRS.next())
                    DatabaseClient.commandExecute("UPDATE stats SET sValue="
                            + s.value + ", mValue=" + s.maxValue
                            + ", hardness=" + s.hardness + " WHERE entityId="
                            + entity.id + " AND name='" + s.name + "'");
                else
                    DatabaseClient
                            .commandExecute("INSERT INTO stats (sValue, mValue, hardness, entityId, name) VALUES("
                                    + s.value
                                    + ","
                                    + s.maxValue
                                    + ","
                                    + s.hardness
                                    + ","
                                    + entity.id
                                    + ",'"
                                    + s.name + "')");

            }
            // Main.Log("[SAVE]", "Saving skills...");

            // Skills
            ResultSet skillsEqRS;

            for (Skill s : entity.skills.vals.values())
            {
                skillsEqRS = DatabaseClient
                        .commandExecute("SELECT * FROM skills WHERE entityId="
                                + entity.id + " AND name='" + s.name + "'");
                if (skillsEqRS.next())
                    DatabaseClient.commandExecute("UPDATE skills SET sValue="
                            + s.value + ", mValue=" + s.maxValue
                            + ", hardness=" + s.hardness + ", primaryStat='"
                            + s.primaryStat + "', secondaryStat='"
                            + s.secondaryStat + "' WHERE entityId=" + entity.id
                            + " AND name='" + s.name + "'");
                else
                    DatabaseClient
                            .commandExecute("INSERT INTO skills (sValue, mValue, hardness, entityId, name, primaryStat, secondaryStat) VALUES("
                                    + s.value
                                    + ","
                                    + s.maxValue
                                    + ","
                                    + s.hardness
                                    + ","
                                    + entity.id
                                    + ",'"
                                    + s.name
                                    + "','"
                                    + s.primaryStat
                                    + "','"
                                    + s.secondaryStat + "')");
            }
            // Main.Log("[SAVE]", "Saving inventory...");

            // Inventory
            Inventory inv = inventories.get(entity.id);
            if (inv != null)
            {
                saveInventory(inv);
            }

        } catch (SQLException e)
        {
            handleError(e);
        }
    }

    public static void ProcessRegister(String login, String pass, String mail,
            String name, String race, Connection connection)
    {
        try
        {
            ResultSet regRS = DatabaseClient
                    .commandExecute("SELECT * FROM accounts WHERE login='"
                            + login + "' AND mail='" + mail + "'");
            RegisterResponse r = new RegisterResponse();

            if (regRS.next())
            {
                r.successful = false;
                Server.SendTo(connection, r);
            } else
            {
                CreateAccount(login, pass, mail, name, race,
                        getClient(connection));
                r.successful = true;
                Server.SendTo(connection, r);
            }
        } catch (SQLException e)
        {
            handleError(e);
        }
    }

    private static void CreateAccount(String login, String pass, String mail,
            String name, String race, ConnectedClient client)
    {
        try
        {
            Entity e = new Entity(getFreeId(), name, EntityType.valueOf(race),
                    0,0,0,
                    Server.getStandardSkillsSet(),
                    Server.getStandardStatsSet(), new ArrayList<String>(), getWorld(1));
            client.controlledEntity = e;
            entities.put(e.id, e);
            createInventory(e.id);
            saveInventory(inventories.get(e.id));
            saveEntity(e);
            DatabaseClient
                    .commandExecute("INSERT INTO accounts(login, password, mail, entityId) VALUES('"
                            + login
                            + "','"
                            + Crypt.encrypt(pass)
                            + "','"
                            + mail + "'," + e.id + ")");
        } catch (Exception e)
        {
            handleError(e);
        }
    }

    private static void createInventory(int id)
    {
        Inventory i = new Inventory(id, 20);
        i.AddItem(new Item(getFreeItemId(), id, "Coin", 1, 0,0,0,
                EquipType.None, ActionType.None, new Attributes(), getWorld(1)));
        inventories.put(id, i);
    }

    private static void saveInventory(Inventory i)
    {
        ResultSet entityEqRS = DatabaseClient
                .commandExecute("SELECT * FROM inventories WHERE entityId="
                        + i.entityId);
        try
        {
            if (entityEqRS.next())
            {
                DatabaseClient.commandExecute("UPDATE inventories SET max="
                        + i.maxItems + " WHERE entityId=" + i.entityId);

            } else
            {
                DatabaseClient
                        .commandExecute("INSERT INTO inventories(entityId, max) VALUES("
                                + i.entityId + "," + i.maxItems + ")");
            }

            for (Item item : i.items)
            {
                SaveItem(item);
            }
        } catch (SQLException e)
        {
            handleError(e);
        }
    }

    public static void SaveItem(Item item)
    {
        ResultSet itemEqRS = DatabaseClient
                .commandExecute("SELECT * FROM items WHERE entityId="
                        + item.entityId + " AND id=" + item.id);
        try
        {
            if (itemEqRS.next())
            {
                DatabaseClient.commandExecute("UPDATE items SET entityId="
                        + item.entityId + ", worldId=" + item.world.id
                        + ", amount=" + item.amount + ", caption='"
                        + item.caption + "', type='" + item.eqType.name()
                        + "', actionType='" + item.aType.name() + "', x="+item.pos.x+", y="+item.pos.y+", z="+item.pos.z+" WHERE id="
                        + item.id);

            } else
            {
                DatabaseClient
                        .commandExecute("INSERT INTO items(id, worldId, caption, amount, entityId, type, actionType, x, y, z) VALUES("
                                + item.id
                                + ","
                                + item.world.id
                                + ",'"
                                + item.caption
                                + "',"
                                + item.amount
                                + ", "
                                + item.entityId
                                + ",'"
                                + item.eqType.name()
                                + "','"
                                + item.aType.name()
                                + "',"
                                + item.pos.x
                                + ", "
                                + item.pos.y
                                + ", "
                                + item.pos.z
                                + ")");
            }
            SaveAttributes(item);
        } catch (SQLException e)
        {
            handleError(e);
        }
    }

    private static void SaveAttributes(Item item)
    {
        ResultSet itemEqRS;
        try
        {
            for (String s : item.attributes.values.keySet())
            {
                itemEqRS = DatabaseClient
                        .commandExecute("SELECT * FROM attributes WHERE itemId="
                                + item.id);

                if (itemEqRS.next())
                {
                    DatabaseClient
                            .commandExecute("UPDATE attributes SET value="
                                    + item.getAttributeValue(s)
                                    + " WHERE name='" + s + "' AND itemId="
                                    + item.id);

                } else
                {
                    DatabaseClient
                            .commandExecute("INSERT INTO attributes(name, itemId, value) VALUES('"
                                    + s
                                    + "',"
                                    + item.id
                                    + ","
                                    + item.getAttributeValue(s) + ")");
                }
            }
        } catch (SQLException e)
        {
            handleError(e);
        }
    }

    private static Stats getStandardStatsSet()
    {
        Stats sts = new Stats();
        sts.put("Hits", new Statistic("Hits", 10, 50, 5));
        sts.put("Stringth", new Statistic("Strength", 5, 50, 5));
        sts.put("Dexterity", new Statistic("Dexterity", 5, 50, 5));
        sts.put("Int", new Statistic("Int", 5, 50, 5));
        sts.put("Mana", new Statistic("Mana", 20, 20, 5));
        return sts;
    }

    public static void LoadConfig() {
        try {            

            File configFile = null;
            FileReader fr;
            BufferedReader br;
            FileWriter fw;
            BufferedWriter bw;
            String s;

            configFile = new File("config.con");

            if(!configFile.exists()){
            configFile.createNewFile();
            fw = new FileWriter(configFile);
            bw = new BufferedWriter(fw);
            
            bw.write("dbName=theerder\n");
            bw.write("dbuser=root\n");
            bw.write("dbpass=\n");
            bw.write("port=25565\n");
            bw.write("version=1.0");

            
            bw.flush();
            fw.close();
            bw.close();
            }

            fr = new FileReader(configFile);
            br = new BufferedReader(fr);

            while ((s = br.readLine()) != null) {
              if(s.split("=").length > 1){
              Configuration.AddEntry(s.split("=")[0], s.split("=")[1]);}
              else{
              Configuration.AddEntry(s.split("=")[0], "");    
              }
             }

             br.close();
             fr.close();
          
             Main.Log("[CONFIG]","Config loaded! ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static Skills getStandardSkillsSet()
    {
        Skills sks = new Skills();
        sks.put("Swords",
                new Skill("Swords", 0, 50, 5, "Strength", "Dexterity"));
        sks.put("Chivalry", new Skill("Chivalry", 0, 50, 5, "Strength", "Int"));
        sks.put("Magery", new Skill("Magery", 0, 50, 5, "Int", "Int"));
        sks.put("Lumberjacking", new Skill("Lumberjacking", 0, 50, 5,
                "Strength", "Dexterity"));
        sks.put("Mining", new Skill("Mining", 0, 50, 5, "Strength", "Int"));
        sks.put("Taming", new Skill("Taming", 0, 50, 5, "Int", "Strength"));
        sks.put("Necromancy", new Skill("Necromancy", 0, 50, 5, "Int", "Int"));
        sks.put("Parrying", new Skill("Parrying", 0, 50, 5, "Dexterity",
                "Strength"));
        sks.put("Herding", new Skill("Herding", 0, 50, 5, "Int", "Int"));
        sks.put("Carpentry",
                new Skill("Carpentry", 0, 50, 5, "Int", "Strength"));

        return sks;
    }

    private static int getFreeId()
    {
        try
        {
            ResultSet rs = DatabaseClient
                    .commandExecute("SELECT max(id) as id FROM entities");
            int i = 0;
            if (rs.next())
            {
                i = rs.getInt("id");
            }
            return i + 1;
        } catch (SQLException e)
        {
            handleError(e);
        }
        return -1;
    }

    public static int getFreeItemId()
    {
        try
        {
            ResultSet rs = DatabaseClient
                    .commandExecute("SELECT max(id) as id FROM items");
            int i = 0;
            if (rs.next())
            {
                i = rs.getInt("id");
            }
            return i + 1;
        } catch (SQLException e)
        {
            handleError(e);
        }
        return -1;
    }

    public static void HandleMove(int id, Connection connection)
    {
        ConnectedClient c = getClient(connection);

        try
        {

        } catch (Exception e)
        {
            handleError(e);
        }
    }

    private static void MovePlayerAt(Vector3 vector3, ConnectedClient c)
    {

    }

    public static void HandleAction(ActionType action, Connection connection,
            String[] args)
    {
        // Main.Log("[DEBUG]", "Handling action " + action.name());
        try
        {
            ConnectedClient c = getClient(connection);
            switch (action)
            {

            }
        } catch (Exception e)
        {
            handleError(e);

        }

    }

    public static void HandleCast(String spellName, int eId,
            Connection connection)
    {
        try
        {
            ConnectedClient c = getClient(connection);
          //  c.controlledEntity.tryCast(spellName.toLowerCase(), eId);
        } catch (Exception e)
        {
            handleError(e);
        }
    }

    public static void HandleAttack(int id, Connection connection)
    {
        try
        {
            ConnectedClient c = getClient(connection);
        } catch (Exception e)
        {
            handleError(e);
        }

    }

    public static void EntityDead(final Entity entity, Entity from)
    {
        /*
         * Handling entitys death
         */
        Main.Log("[MESSAGE]", entity.caption + " the " + entity.type.name()
                + "(" + entity.id + ") dead!");

        warnEntity(entity, "==+[You're dead! Next time be more careful]+==");

        TravelEntity(entity, new Vector3(0,0,0));

        entity.setRebirthHitsAmount();
        entity.invul = true;
        warnEntity(entity, "==+[You cannot be hurt by anyone]+==");

        Timer t = new Timer();
        t.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                entity.invul = false;
                warnEntity(entity, "==+[You can be hurt now]+==");
            }
        }, (long) (entity.invulTime * 1000));
    }

    public static void TravelEntity(Entity e, Vector3 vector3)
    {
        MovePlayerAt(vector3, getClientByEntity(e));
    }

    public static void handleError(Exception e)
    {
        Main.Log("[ERROR]", e.getMessage());
        e.printStackTrace();
    }

    public static Item checkInventory(Entity e, ActionType at)
    {
        for (Item i : inventories.get(e.id).getItems())
        {
            if (i.aType == at)
                return i;
        }
        return null;
    }

    public static Inventory getInventory(Entity entity)
    {
        return inventories.get(entity.id);
    }

    public static Entity getEntity(int entityId)
    {
        return entities.get(entityId);
    }

    public static void DestroyItem(Item item)
    {
        DatabaseClient.commandExecute("DELETE FROM items WHERE id = " + item.id
                + " LIMIT 1;");
        DatabaseClient.commandExecute("DELETE FROM attributes WHERE itemId = "
                + item.id + ";");
    }

    public static void DestroyItem(Inventory i, Item item)
    {
        i.RemoveItem(item.id);
        DestroyItem(item);
    }

    public static boolean haveItemSet(Entity e, ArrayList<String> reagentsNeeded)
    {
        Inventory i = getInventory(e);
        if (i != null)
        {
            for (String s : reagentsNeeded)
            {
                if (haveItem(e, s))
                {
                    continue;
                } else
                {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean haveItem(Entity e, String s)
    {
        Inventory i = getInventory(e);
        if (i != null)
        {
            for (Item item : i.items)
            {
                if (item.caption.equals(s))
                    return true;
            }
        }
        return false;
    }

    public static void consumeItem(Entity entity, String s)
    {
        Inventory i = getInventory(entity);
        if (i != null)
        {
            Item item = i.getItem(s);
            i.consume(item);
        }
    }

    public static void HandleCommand(CommandRequest commandRequest,
            Connection connection)
    {
        try
        {
            String commandKey = commandRequest.args[0];
            if (Server.commands.containsKey(commandKey))
            {
                Server.commands.get(commandKey).execute(commandRequest.args,
                        connection);
            } else
            {
                Server.warnClient(getClient(connection),
                        "Invalid server command");
            }
        } catch (Exception e)
        {
            handleError(e);
        }
    }

    public static void warnClient(ConnectedClient client, String string)
    {
        MessageResponse r = new MessageResponse();
        r.msg = string;
        SendTo(client.connection, r);
    }

    public static ActionType getActionFromString(String string)
    {
        for (ActionType aT : ActionType.values())
        {
            if (aT.name().toLowerCase().contains(string))
            {
                return aT;
            }
        }
        return ActionType.None;
    }

    public static float getPlantGrowTime(String seed)
    {
        try
        {
            return plantsGrowTime.get(seed);
        } catch (Exception e)
        {
            handleError(e);
            return 10;
        }
    }

    public static void DestroyFlag(int id, String string)
    {
        DatabaseClient
                .commandExecute("DELETE FROM locationflags WHERE locationId = "
                        + id + " AND flag='" + string + "' LIMIT 1;");
    }

    public static void HandleCraft(String string, int i, Connection c)
    {
        CraftSystem.tryCraft(string, i, getClient(c).controlledEntity);
    }
}
