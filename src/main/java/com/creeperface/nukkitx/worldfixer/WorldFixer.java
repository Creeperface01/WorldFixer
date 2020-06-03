package com.creeperface.nukkitx.worldfixer;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;

import java.util.HashMap;
import java.util.HashSet;

public class WorldFixer extends PluginBase implements Listener {

    public HashMap<String, Selection> selectors = new HashMap<>();

    private HashSet<String> levels = new HashSet<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        if (cmd.getName().toLowerCase().equals("wf")) {
            if (args.length == 0) {
                sender.sendMessage(TextFormat.RED + "Use /wf help for help");
                return true;
            }

            Level level = null;

            if (args.length >= 2) {
                getServer().loadLevel(args[1]);

                level = getServer().getLevelByName(args[1]);

                if (level == null) {
                    sender.sendMessage(TextFormat.RED + "Level " + TextFormat.YELLOW + args[1] + TextFormat.RED + " doesn't exist");
                    return true;
                }
            }

            switch (args[0].toLowerCase()) {
                case "fix":
                    if (!sender.hasPermission("wf.command.fix") && !sender.isOp()) {
                        sender.sendMessage(cmd.getPermissionMessage());
                        return false;
                    }

                    if (!isPosSet((Player) sender)) {
                        sender.sendMessage(TextFormat.RED + "You must select both positions first!");
                        return false;
                    }

                    Selection selection = selectors.get(sender.getName().toLowerCase());
                    Position pos1 = selection.pos1;
                    Position pos2 = selection.pos2;

                    if (pos1.level != pos2.level) {
                        sender.sendMessage(TextFormat.RED + "Both positions must be in the same level!");
                        return false;
                    }

                    fix(selection);

                    selectors.remove(sender.getName().toLowerCase());
                    sender.sendMessage(TextFormat.GREEN + "Selected area has been successfully fixed!");
                    break;
                case "fixlevel":
                    if (!sender.hasPermission("wf.command.fix") && !sender.isOp()) {
                        sender.sendMessage(cmd.getPermissionMessage());
                        return false;
                    }

                    if (args.length < 2) {
                        sender.sendMessage(TextFormat.YELLOW + "Use /wf fixlevel <level name> [fast]");
                        return false;
                    }

                    boolean fast = false;
                    if (args.length > 2) {
                        fast = Boolean.parseBoolean(args[2]);
                    }

                    fixWorld(level, fast);
                    break;
                /*case "removetiles":
                    if(!sender.hasPermission("wf.command.fixtiles") && !sender.isOp()){
                        sender.sendMessage(cmd.getPermissionMessage());
                        return false;
                    }

                    if(!isPosSet((Player) sender)){
                        sender.sendMessage(TextFormat.RED+"You must select both positions first!");
                        return false;
                    }

                    selection = selectors.get(sender.getName().toLowerCase());
                    pos1 = selection.pos1;
                    pos2 = selection.pos2;

                    if(pos1.level != pos2.level){
                        sender.sendMessage(TextFormat.RED+"Both positions must be in the same level!");
                        return false;
                    }

                    fix(selection, false, false, true);

                    sender.sendMessage(TextFormat.GREEN+"Tiles have been successfully fixed!");
                    break;
                case "removeentities":
                    if(!sender.hasPermission("wf.command.removeentities") && !sender.isOp()){
                        sender.sendMessage(cmd.getPermissionMessage());
                        return false;
                    }

                    int count = 0;

                    for(Entity e : ((Player) sender).getLevel().getEntities()){
                        e.despawnFromAll();
                        e.close();
                        count++;
                    }

                    sender.sendMessage(TextFormat.GREEN+"Entities have been successfully removed! "+TextFormat.BLUE+"("+count+")");
                    break;*/
                case "wand":
                    if (!sender.hasPermission("wf.command.wand") && !sender.isOp()) {
                        sender.sendMessage(cmd.getPermissionMessage());
                        break;
                    }

                    if (isSelector((Player) sender)) {
                        Selection selection1 = this.selectors.get(sender.getName().toLowerCase());
                        selection1.pos1 = null;
                        selection1.pos2 = null;
                        break;
                    }

                    selectors.put(sender.getName().toLowerCase(), new Selection());
                    sender.sendMessage(TextFormat.GREEN + "Now select two blocks");
                    break;
                case "help":
                    sender.sendMessage(TextFormat.YELLOW + "> WorldFixer help <\n" + TextFormat.GREEN + "/wf wand " + TextFormat.GRAY + "select two positions\n" + TextFormat.GREEN + "/wf fixslabs " + TextFormat.GRAY + "Fix all slabs in the world\n" + TextFormat.GREEN + "/wf fixcolor " + TextFormat.GRAY + "change grass color to green\n" + TextFormat.GREEN + "/wf fix " + TextFormat.GRAY + "fix all slabs in the world and set grass color to green");
                    break;
                default:
                    sender.sendMessage(TextFormat.RED + "Use /wf help for help");
                    break;
            }
        }

        return true;
    }

    public boolean isSelector(Player p) {
        return selectors.containsKey(p.getName().toLowerCase());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        if (this.isSelector(p)) {
            Selection set = selectors.get(p.getName().toLowerCase());

            if (set.pos1 == null) {
                e.setCancelled();
                set.pos1 = b.clone();
                p.sendMessage(TextFormat.GREEN + "Selected the first position at " + TextFormat.BLUE + b.x + TextFormat.GREEN + ", " + TextFormat.BLUE + b.y + TextFormat.GREEN + ", " + TextFormat.BLUE + b.z + TextFormat.GREEN);
            } else if (set.pos2 == null) {
                e.setCancelled();
                set.pos2 = b.clone();
                p.sendMessage(TextFormat.GREEN + "Selected the second position at " + TextFormat.BLUE + b.x + TextFormat.GREEN + ", " + TextFormat.BLUE + b.y + TextFormat.GREEN + ", " + TextFormat.BLUE + b.z + TextFormat.GREEN);
            }
        }
    }

    public boolean isPosSet(Player p) {
        return isSelector(p) && selectors.get(p.getName().toLowerCase()).pos1 != null && selectors.get(p.getName().toLowerCase()).pos2 != null;
    }

    public boolean fix(Selection s) {
        /*if (!color && !slabs && !tiles) {
            return false;
        }*/


        int x1 = s.pos1.getFloorX();
        int y1 = s.pos1.getFloorY();
        int z1 = s.pos1.getFloorZ();
        int x2 = s.pos2.getFloorX();
        int y2 = s.pos2.getFloorY();
        int z2 = s.pos2.getFloorZ();

        Level level = s.pos1.level;

        if (!getServer().isLevelLoaded(level.getName())) {
            getServer().loadLevel(level.getFolderName());
        }

        for (int x = Math.min(x1, x2); x < Math.max(x1, x2); x++) {
            for (int z = Math.min(z1, z2); z < Math.max(z1, z2); z++) {
                //if (slabs) {
                for (int y = Math.min(y1, y2); y < Math.max(y1, y2); y++) {
                    int id = level.getBlockIdAt(x, y, z);
                    //int meta = level.getBlockDataAt(x, y, z);

                    fixId(level, x, y, z, id);
                    //}
                }


                /*if (color) {
                    level.setBiomeColor(x, z, 108, 151, 47);
                }*/

                    /*if (tiles) {
                        BlockEntity blockEntity = level.getBlockEntity(temporalVector.setComponents(x, y, z));

                        if (blockEntity != null) {
                            blockEntity.close();
                        }
                    }*/

            }
        }

        return true;
    }

    private void fixWorld(Level level, boolean fast) {
        /*Level level = getServer().getLevelByName(world);

        if (level == null) {
            getServer().loadLevel(world);
            level = getServer().getLevelByName(world);
        }

        if(level == null) {
            getLogger().error("Level "+world+" not found");
            return;
        }*/

        String folder = level.getFolderName();
        getServer().unloadLevel(level);
        getServer().loadLevel(folder);

        LevelConverter.convert(this, getServer().getLevelByName(folder), fast);
    }

    public static boolean fixId(ChunkManager cm, int x, int y, int z, int id) {
        boolean changed = true;

        switch (id) {
            case 3:
                if (cm.getBlockDataAt(x, y, z) == 2) {
                    cm.setBlockIdAt(x, y, z, Item.PODZOL);
                }
                break;
            case 125:
                cm.setBlockIdAt(x, y, z, Item.DOUBLE_WOODEN_SLAB);
                break;
            case 126:
                cm.setBlockIdAt(x, y, z, Item.WOOD_SLAB);
                break;
            case 95:
                cm.setBlockIdAt(x, y, z, 241);
                break;
            case 157:
                cm.setBlockIdAt(x, y, z, 126);
                break;
            case 158:
                cm.setBlockIdAt(x, y, z, 125);
                break;
            case 160:
                cm.setBlockIdAt(x, y, z, Item.GLASS_PANE);
                cm.setBlockDataAt(x, y, z, 0);
                break;
            case 166:
                cm.setBlockIdAt(x, y, z, Item.INVISIBLE_BEDROCK);
                break;
            case 177:
                cm.setBlockIdAt(x, y, z, Item.AIR);
                break;
            case 188:
                cm.setBlockIdAt(x, y, z, Item.FENCE);
                cm.setBlockDataAt(x, y, z, 1);
                break;
            case 189:
                cm.setBlockIdAt(x, y, z, Item.FENCE);
                cm.setBlockDataAt(x, y, z, 2);
                break;
            case 190:
                cm.setBlockIdAt(x, y, z, Item.FENCE);
                cm.setBlockDataAt(x, y, z, 3);
                break;
            case 191:
                cm.setBlockIdAt(x, y, z, Item.FENCE);
                cm.setBlockDataAt(x, y, z, 4);
                break;
            case 192:
                cm.setBlockIdAt(x, y, z, Item.FENCE);
                cm.setBlockDataAt(x, y, z, 5);
                break;
            case 198:
                cm.setBlockIdAt(x, y, z, Item.END_ROD);
                break;
            case 199:
                cm.setBlockIdAt(x, y, z, Item.CHORUS_PLANT);
                break;
            case 202: //pillar
            case 204: //double slab
            case 205: //slab
                cm.setBlockIdAt(x, y, z, Item.PURPUR_BLOCK);
                break;
            case 207:
                cm.setBlockIdAt(x, y, z, Item.BEETROOT_BLOCK);
                break;
            case 208:
                cm.setBlockIdAt(x, y, z, Item.GRASS_PATH);
                break;
            case 210: //repeating command block
                cm.setBlockIdAt(x, y, z, 188);
                break;
            case 211: //chain command block
                cm.setBlockIdAt(x, y, z, 189);
                break;
            case 218:
                cm.setBlockIdAt(x, y, z, Item.OBSERVER);
                break;
            case 235:
                cm.setBlockIdAt(x, y, z, Item.WHITE_GLAZED_TERRACOTTA);
                break;
            case 236:
                cm.setBlockIdAt(x, y, z, Item.ORANGE_GLAZED_TERRACOTTA);
                break;
            case 237:
                cm.setBlockIdAt(x, y, z, Item.MAGENTA_GLAZED_TERRACOTTA);
                break;
            case 238:
                cm.setBlockIdAt(x, y, z, Item.LIGHT_BLUE_GLAZED_TERRACOTTA);
                break;
            case 239:
                cm.setBlockIdAt(x, y, z, Item.YELLOW_GLAZED_TERRACOTTA);
                break;
            case 240:
                cm.setBlockIdAt(x, y, z, Item.LIME_GLAZED_TERRACOTTA);
                break;
            case 241:
                cm.setBlockIdAt(x, y, z, Item.PINK_GLAZED_TERRACOTTA);
                break;
            case 242:
                cm.setBlockIdAt(x, y, z, Item.GRAY_GLAZED_TERRACOTTA);
                break;
            case 243:
                cm.setBlockIdAt(x, y, z, Item.SILVER_GLAZED_TERRACOTTA);
                break;
            case 244:
                cm.setBlockIdAt(x, y, z, Item.CYAN_GLAZED_TERRACOTTA);
                break;
            case 245:
                cm.setBlockIdAt(x, y, z, Item.PURPLE_GLAZED_TERRACOTTA);
                break;
            case 246:
                cm.setBlockIdAt(x, y, z, Item.BLUE_GLAZED_TERRACOTTA);
                break;
            case 247:
                cm.setBlockIdAt(x, y, z, Item.BROWN_GLAZED_TERRACOTTA);
                break;
            case 248:
                cm.setBlockIdAt(x, y, z, Item.GREEN_GLAZED_TERRACOTTA);
                break;
            case 249:
                cm.setBlockIdAt(x, y, z, Item.RED_GLAZED_TERRACOTTA);
                break;
            case 250:
                cm.setBlockIdAt(x, y, z, Item.BLACK_GLAZED_TERRACOTTA);
                break;
            case 251:
                cm.setBlockIdAt(x, y, z, Item.CONCRETE);
                break;
            case 252:
                cm.setBlockIdAt(x, y, z, Item.CONCRETE_POWDER);
                break;
            case Item.STONE_BUTTON:
            case Item.WOODEN_BUTTON:
                int data = cm.getBlockDataAt(x, y, z);
                int face = data & 0b111;

                int meta = 0;
                switch (face) {
                    case 0: //down
                        meta = BlockFace.DOWN.getIndex();
                        break;
                    case 1: //east
                        meta = BlockFace.EAST.getIndex();
                        break;
                    case 2: //west
                        meta = BlockFace.WEST.getIndex();
                        break;
                    case 3: //south
                        meta = BlockFace.SOUTH.getIndex();
                        break;
                    case 4: //north
                        meta = BlockFace.NORTH.getIndex();
                        break;
                    case 5: //up
                        meta = BlockFace.UP.getIndex();
                        break;
                }

                if ((data & 0x08) == 0x08) {
                    meta |= 0x08;
                }

                cm.setBlockDataAt(x, y, z, meta);
                break;
//            case Block.CHEST:
//                chests.add(new Vector3(cm.getX() << 4 + x, y, cm.getZ() << 4 + z));
//                break;
            default:
                changed = false;
                break;
        }

        if (id >= 219 && id <= 234) { //shulker box
            cm.setBlockIdAt(x, y, z, Item.SHULKER_BOX);
            cm.setBlockDataAt(x, y, z, id - 219);
            changed = true;
        }

        return changed;
    }

    /*@EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        for (Entity en : new ArrayList<>(e.getChunk().getEntities().values())) {
            if (en instanceof Player) {
                continue;
            }
            en.close();
        }
    }*/
}