package WorldFixer;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.Listener;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.level.Level;

import java.util.*;

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
            if (args.length != 1 && args.length != 2) {
                sender.sendMessage(TextFormat.RED + "Use /wf help for help");
                return true;
            }

            Level level = null;

            if (args.length == 2) {
                getServer().loadLevel(args[1]);

                level = getServer().getLevelByName(args[1]);

                if (level == null) {
                    sender.sendMessage(TextFormat.RED + "Level " + TextFormat.YELLOW + args[1] + TextFormat.RED + " doesn't exist");
                    return true;
                }
            }

            switch (args[0].toLowerCase()) {
                case "fixcolor":
                    if (!sender.hasPermission("wf.command.fixcolor") && !sender.isOp()) {
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

                    fix(selection, true, false, false);

                    selectors.remove(sender.getName().toLowerCase());
                    sender.sendMessage(TextFormat.GREEN + "Grass color successfully fixed!");
                    break;
                case "fix":
                    if (!sender.hasPermission("wf.command.fix") && !sender.isOp()) {
                        sender.sendMessage(cmd.getPermissionMessage());
                        return false;
                    }

                    if (!isPosSet((Player) sender)) {
                        sender.sendMessage(TextFormat.RED + "You must select both positions first!");
                        return false;
                    }

                    selection = selectors.get(sender.getName().toLowerCase());
                    pos1 = selection.pos1;
                    pos2 = selection.pos2;

                    if (pos1.level != pos2.level) {
                        sender.sendMessage(TextFormat.RED + "Both positions must be in the same level!");
                        return false;
                    }

                    fix(selection, true, true, true);

                    selectors.remove(sender.getName().toLowerCase());
                    sender.sendMessage(TextFormat.GREEN + "Selected area has been successfully fixed!");
                    break;
                case "fixslabs":
                    if (!sender.hasPermission("wf.command.fixslabs") && !sender.isOp()) {
                        sender.sendMessage(cmd.getPermissionMessage());
                        return false;
                    }

                    if (!isPosSet((Player) sender)) {
                        sender.sendMessage(TextFormat.RED + "You must select both positions first!");
                        return false;
                    }

                    selection = selectors.get(sender.getName().toLowerCase());
                    pos1 = selection.pos1;
                    pos2 = selection.pos2;

                    if (pos1.level != pos2.level) {
                        sender.sendMessage(TextFormat.RED + "Both positions must be in the same level!");
                        return false;
                    }

                    fix(selection, false, true, false);

                    selectors.remove(sender.getName().toLowerCase());
                    sender.sendMessage(TextFormat.GREEN + "Slabs have been successfully fixed!");
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

    public boolean fix(Selection s, boolean color, boolean slabs, boolean tiles) {
        if (!color && !slabs && !tiles) {
            return false;
        }


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

        /*if($color === true && !$slabs){
            for($x = min($x1, $x2); $x < max($x1, $x2); $x++){
                for($z = min($z1, $z2); $z < max($z1, $z2); $z++){
                    $level->setBiomeColor($x, $z, 108, 151, 47);
                }
            }
            return true;
        }*/

        //Vector3 temporalVector = new Vector3();

        for (int x = Math.min(x1, x2); x < Math.max(x1, x2); x++) {
            for (int z = Math.min(z1, z2); z < Math.max(z1, z2); z++) {
                if (slabs) {
                    for (int y = Math.min(y1, y2); y < Math.max(y1, y2); y++) {
                        int id = level.getBlockIdAt(x, y, z);
                        //int meta = level.getBlockDataAt(x, y, z);

                        switch (id) {
                            case 3:
                                if(level.getBlockDataAt(x, y, z) == 2) {
                                    level.setBlockIdAt(x, y, z, Item.PODZOL);
                                }
                                break;
                            case 125:
                                level.setBlockIdAt(x, y, z, Item.DOUBLE_WOODEN_SLAB);
                                break;
                            case 126:
                                level.setBlockIdAt(x, y, z, Item.WOOD_SLAB);
                                break;
                            case 95:
                                level.setBlockIdAt(x, y, z, Item.GLASS);
                                break;
                            case 160:
                                level.setBlockIdAt(x, y, z, Item.GLASS_PANE);
                                level.setBlockDataAt(x, y, z, 0);
                                break;
                            case 166:
                                level.setBlockIdAt(x, y, z, Item.INVISIBLE_BEDROCK);
                                break;
                            case 177:
                                level.setBlockIdAt(x, y, z, Item.AIR);
                                break;
                            case 188:
                                level.setBlockIdAt(x, y, z, Item.FENCE);
                                level.setBlockDataAt(x, y, z, 1);
                                break;
                            case 189:
                                level.setBlockIdAt(x, y, z, Item.FENCE);
                                level.setBlockDataAt(x, y, z, 2);
                                break;
                            case 190:
                                level.setBlockIdAt(x, y, z, Item.FENCE);
                                level.setBlockDataAt(x, y, z, 3);
                                break;
                            case 191:
                                level.setBlockIdAt(x, y, z, Item.FENCE);
                                level.setBlockDataAt(x, y, z, 4);
                                break;
                            case 192:
                                level.setBlockIdAt(x, y, z, Item.FENCE);
                                level.setBlockDataAt(x, y, z, 5);
                                break;
                            case 198:
                                level.setBlockIdAt(x, y, z, Item.END_ROD);
                                break;
                            case 199:
                                level.setBlockIdAt(x, y, z, Item.CHORUS_PLANT);
                                break;
                            case 202:
                            case 204:
                                level.setBlockIdAt(x, y, z, Item.PURPUR_BLOCK);
                                break;
                            case 208:
                                level.setBlockIdAt(x, y, z, Item.GRASS_PATH);
                                break;
                        }
                    }
                }


                if (color) {
                    level.setBiomeColor(x, z, 108, 151, 47);
                }

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

    private void fixWorld(String world) {
        Level level = getServer().getLevelByName(world);

        if(level != null) {
            level.unload();
        }

        levels.add(world.toLowerCase());


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