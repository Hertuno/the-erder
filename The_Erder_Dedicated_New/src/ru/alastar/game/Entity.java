package ru.alastar.game;

import java.util.ArrayList;
import java.util.Hashtable;

import com.alastar.game.Tile;
import com.badlogic.gdx.math.Vector3;

import ru.alastar.enums.EntityType;
import ru.alastar.game.systems.gui.NetGUIAnswer;
import ru.alastar.game.systems.gui.NetGUIInfo;
import ru.alastar.game.systems.gui.hadlers.GUIHandler;
import ru.alastar.game.systems.gui.hadlers.InvButtonGUIHandler;
import ru.alastar.main.net.ConnectedClient;
import ru.alastar.main.net.Server;
import ru.alastar.world.ServerWorld;

public class Entity extends Transform
{

    public int               id        = -1;
    public String            caption   = "Generic Entity";
    public EntityType        type      = EntityType.Human;
    public Stats             stats;
    public Skills            skills;
    public ArrayList<String> knownSpells;

    public boolean           invul     = false;
    public float             invulTime = 15;              // in seconds
    public static int        startHits = 15;
    public ServerWorld       world;
    public int               height    = 2;
    public long              lastMoveTime = System.currentTimeMillis();
    public Hashtable<String, NetGUIInfo> gui = new Hashtable<String, NetGUIInfo>();
    public Hashtable<String, GUIHandler> handlingGUIs = new Hashtable<String, GUIHandler>();

    
    public Entity(int i, String c, EntityType t, int x, int y, int z, Skills sk,
            Stats st, ArrayList<String> k, ServerWorld w)
    {
        super(new Vector3(x,y,z));
        this.id = i;
        this.caption = c;
        this.type = t;
        this.skills = sk;
        this.stats = st;
        this.knownSpells = k;
        this.world = w;
    }

    public void RemoveYourself(int aId)
    {
        Server.saveEntity(this, aId);
        this.world.RemoveEntity(this);
        Server.entities.remove(id);
    }
    
    public void setRebirthHitsAmount()
    {
        this.stats.set("Hits", startHits, this, true);
    }
    
    public boolean knowSpell(String s)
    {
        for (String str : knownSpells)
        {
            if (str.equals(s))
            {
                return true;
            }
        }
        return false;
    }
    
   public boolean tryMove(int x, int y)
    {
     //  Main.Log("[DEBUG]", "Try move");
       if ((System.currentTimeMillis() - lastMoveTime) > 250) {
           int obstacleHeight = 0;
           for (int i = 0; i < height; ++i) {
               if (world.GetTile(((int) this.pos.x + x),
                       ((int) this.pos.y + y), (int) this.pos.z + i) != null)
                   ++obstacleHeight;
           }
         //  Main.Log("[INPUT]","obstacle height: " + obstacleHeight);
           if (obstacleHeight < height) {
               this.pos.x += x;
               this.pos.y += y;
               this.pos.z += obstacleHeight;
               CheckIfInAir();
               Server.UpdateEntityPosition(this);
               lastMoveTime = System.currentTimeMillis();
          //     Main.Log("[INPUT]","player moved");
               return true;
           } else {
               Tile t = world.GetTile(((int) this.pos.x + x),
                       ((int) this.pos.y + y), (int) this.pos.z);
             //   Main.Log("[INPUT]","Tile is not null");
               if (t != null) {
                   if (t.passable) {
                  //     Main.Log("[INPUT]","Tile is passable!");

                       this.pos.x += x;
                       this.pos.y += y;
                       this.pos.z += 1;
                       CheckIfInAir();

                       Server.UpdateEntityPosition(this);
                       lastMoveTime = System.currentTimeMillis();
                 //      Main.Log("[INPUT]","player moved");
                      return true;
                   } else {
                   //    Main.Log("[INPUT]","Tile is not passable!");

                       return false;
                   }
               } else {
                 //  Main.Log("[INPUT]","path is passable!");

                   this.pos.x += x;
                   this.pos.y += y;
                   CheckIfInAir();

                   Server.UpdateEntityPosition(this);
                   lastMoveTime = System.currentTimeMillis();
                //   Main.Log("[INPUT]","player moved");
                   return true;
               }

           }
       }
       // else
       // {
       // this.x += x;
       // this.y += y;
       // Server.UpdateEntityPosition(this);
       // lastMoveTime = DateTime.Now;
       // Server.AddConsoleEntry("[INPUT]: Staff move");

       // }
       // }
       else {
           // Server.Log("[INPUT]: Too early");
           return false;

       }

    }
   
   private void CheckIfInAir() {
       Tile t = world.GetTile(new Vector3(pos.x, pos.y,
               pos.z - 1));
       for (int z = (int) pos.z; z > world.zMin; --z) {
           t = world.GetTile(new Vector3(pos.x, pos.y, z));
           if (t == null) {
               pos.z = z;
           } else
               break;
       }
   }

public void AddGUI(NetGUIInfo info)
{
    this.gui.put(info.name, info);
}

public boolean haveGUI(String name)
{
   if(getGUI(name) != null) return true;
   else return false;
}

public NetGUIInfo getGUI(String name)
{
   return this.gui.get(name);
}
    /*
    public void tryGrow(String seed)
    {
        // Main.Log("[DEBUG]", "Grow action! Seed: " + seed);
        if (loc.haveFlag("Plough"))
        {
            if (GardenSystem.getGrowsFromLoc(loc) == null)
            {
                Date d = new Date();
                d.setTime((long) (d.getTime() + Server.getPlantGrowTime(seed)));
                GardenSystem.addGrowingPlant(new PlantsType(seed, d, this.loc));
                Server.consumeItem(this, seed);
                Server.warnEntity(this, "Plant begin to grow. It will grow on "
                        + d.toString());
                SkillsSystem.tryRaiseSkill(this, this.skills.get("Herding"));
            } else
                Server.warnEntity(this, "There's already growing plants!");
        } else
            Server.warnEntity(this, "There's no plough in this location!");
    }

    public void tryHerd()
    {
        if (loc.haveFlag("Plants"))
        {
            Item item = new Item(Server.getFreeItemId(), id,
                    this.loc.getFlag("Plants").value, Server.random.nextInt(2)
                            + 1
                            + SkillsSystem.getSkillBonus(this.skills
                                    .get("Herding")), this.loc, EquipType.None,
                    ActionType.None, new Attributes());
            Inventory inv = Server.getInventory(this);
            if (inv != null)
                inv.AddItem(item);
            this.loc.removeFlag("Plants");
            SkillsSystem.tryRaiseSkill(this, this.skills.get("Herding"));

        } else
            Server.warnEntity(this, "There's no plants in this location!");
    }

    public void tryMine()
    {
        if (loc.haveFlag("Mine"))
        {
            Item pickaxe = Server.checkInventory(this, ActionType.Mine);
            if (pickaxe != null)
            {
                Server.warnEntity(this, "You start to mine...");
                pickaxe.diffValue("Durability", 1);
                if (pickaxe.getAttributeValue("Durability") <= 0)
                {
                    Server.DestroyItem(Server.getInventory(this), pickaxe);
                }
                if (SkillsSystem.getChanceFromSkill(skills.get("Mining")) > Server.random
                        .nextFloat())
                {
                    loc.getRandomMaterial(this, skills.get("Mining"),
                            Location.miningItems);
                    SkillsSystem.tryRaiseSkill(this, skills.get("Mining"));
                    Server.warnEntity(this, "You found something!");
                } else
                {
                    Server.warnEntity(this,
                            "You failed to get any useful material");
                }
            } else
            {
                Server.warnEntity(this,
                        "You dont have any instrument to perform this action");
            }
        } else
        {
            Server.warnEntity(this, "There's no mine");
        }
    }

    public void tryCast(String spellname, int id2)
    {
        if (knowSpell(spellname))
        {
            if (stats.get("Mana").value >= MagicSystem.getSpell(spellname).manaRequired)
            {
                if (Server.haveItemSet(this,
                        MagicSystem.getSpell(spellname).reagentsNeeded))
                {
                    stats.set(
                            "Mana",
                            stats.get("Mana").value
                                    - MagicSystem.getSpell(spellname).manaRequired,
                            this, true);
                    for (String s : MagicSystem.getSpell(spellname).reagentsNeeded)
                    {
                        Server.consumeItem(this, s);
                    }
                    MagicSystem.tryCast(this, Server.getEntity(id2), spellname);
                } else
                {
                    Server.warnEntity(this, "You dont have enough reagents!");
                }
            } else
                Server.warnEntity(this, "You dont have enough mana!");
        } else
            Server.warnEntity(this, "You dont know that spell!");
    }



    public void tryAttack(Entity e)
    {
        if (e != null)
        {
            if (SkillsSystem.getChanceFromSkill(this.skills.vals.get("Swords")) > Server.random
                    .nextFloat())
            {
                e.dealDamage(this, BattleSystem.calculateDamage(this, e));
                Server.warnEntity(this, "You trying to hit " + e.caption
                        + "...");
                SkillsSystem
                        .tryRaiseSkill(this, this.skills.vals.get("Swords"));
            } else
            {
                Server.warnEntity(this, "You miss!");
                SkillsSystem
                        .tryRaiseSkill(this, this.skills.vals.get("Swords"));
            }
        }
    }

    private void dealDamage(Entity entity, int calculateDamage)
    {
        if (SkillsSystem.getChanceFromSkill(this.skills.vals.get("Parrying")) > Server.random
                .nextFloat())
        {
            Server.warnEntity(this, "You successfully parried "
                    + entity.caption + "'s attack!");
            SkillsSystem.tryRaiseSkill(this, this.skills.vals.get("Parrying"));
            startAttack(entity.id);

        } else
        {
            int curHits = this.stats.get("Hits").value;
            if ((curHits - calculateDamage) > 0)
            {
                this.stats.set("Hits", curHits - calculateDamage, this, true);
                Server.warnEntity(
                        this,
                        entity.caption + " the " + entity.type.name()
                                + " hit you! Your hits now is: "
                                + this.stats.get("Hits").value);
                SkillsSystem.tryRaiseSkill(this,
                        this.skills.vals.get("Parrying"));

                if (target == null)
                    startAttack(entity.id);

            } else
            {
                Server.EntityDead(this, entity);
            }
        }
    }



    public void tryStopAttack()
    {
        if (battleTimer != null)
            battleTimer.cancel();
    }

    public void startAttack(final int id2)
    {
        // System.out.println("Start Attack. Weapon speed: " +
        // (long)BattleSystem.getWeaponSpeed(this)*1000);
        if (battleTimer != null)
            battleTimer.cancel();

        battleTimer = new Timer();
        battleTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                // System.out.println("Attack run");
                if (loc.getEntityById(id2) != null)
                {
                    target = loc.getEntityById(id2);
                    if (target.stats.get("Hits").value > 0)
                    {
                        if (!target.invul)
                        {
                            // System.out.println("hit it");
                            tryAttack(target);
                        } else
                        {
                            target = null;
                            battleTimer.cancel();
                        }
                    } else
                    {
                        target = null;
                        battleTimer.cancel();
                    }
                } else
                {
                    // System.out.println("entity null");
                    target = null;
                    battleTimer.cancel();
                }
            }
        }, 0, (long) BattleSystem.getWeaponSpeed(this) * 1000);
    }*/

public void AddGUIHandler(String string, InvButtonGUIHandler guiHandler)
{
    handlingGUIs.put(string, guiHandler);
}
public void RemoveGUIHandler(String string)
{
    handlingGUIs.remove(string);
}

public void invokeGUIHandler(NetGUIAnswer r, ConnectedClient c)
{
    if(handlingGUIs.containsKey(r.name))
        handlingGUIs.get(r.name).handle(r.value.split(" "), c);
}
}
