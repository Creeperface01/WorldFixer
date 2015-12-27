<?php

namespace WorldFixer;

use pocketmine\Player;
use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\event\block\BlockBreakEvent;
use pocketmine\event\Listener;
use pocketmine\utils\TextFormat;
use pocketmine\plugin\PluginBase;
use pocketmine\utils\Cache;
use pocketmine\level\Level;

class WorldFixer extends PluginBase implements Listener{

    public $selectors = [];

    public function onEnable(){
        $this->getServer()->getPluginManager()->registerEvents($this, $this);
    }

    public function onCommand(CommandSender $sender, Command $cmd, $label, array $args){

        if(!$sender instanceof Player){
            return;
        }

        if(strtolower($cmd->getName()) == "wf"){
            if(!isset($args[0])){
                $sender->sendMessage(TextFormat::RED."Use /wf help for help");
                return;
            }
            switch(strtolower($args[0])){
                case "fixcolor":
                    if(!$sender->hasPermission("wf.command.fixcolor") && !$sender->isOp()){
                        $sender->sendMessage($cmd->getPermissionMessage());
                        break;
                    }

                    if(!$this->isPosSet($sender)){
                        $sender->sendMessage(TextFormat::RED."You must select both positions first!");
                        return;
                    }

                    list($x1, $y1, $z1, $level1) = explode(':', $this->selectors[strtolower($sender->getName())]['pos1']);
                    list($x2, $y2, $z2, $level2) = explode(':', $this->selectors[strtolower($sender->getName())]['pos2']);

                    if($level1 !== $level2){
                        $sender->sendMessage(TextFormat::RED."Both positions must be in the same level!");
                        return;
                    }

                    $level = $level1;

                    $this->fix($level, $x1, $y1, $z1, $x2, $y2, $z2, true, false);

                    $sender->sendMessage(TextFormat::GREEN."Grass color sucessfully fixed!");
                    break;
                case "fix":
                    if(!$sender->hasPermission("wf.command.fix") && !$sender->isOp()){
                        $sender->sendMessage($cmd->getPermissionMessage());
                        break;
                    }
                    if(!$this->isPosSet($sender)){
                        $sender->sendMessage(TextFormat::RED."You must select both positions first!");
                        return;
                    }

                    list($x1, $y1, $z1, $level1) = explode(':', $this->selectors[strtolower($sender->getName())]['pos1']);
                    list($x2, $y2, $z2, $level2) = explode(':', $this->selectors[strtolower($sender->getName())]['pos2']);
                    if($level1 !== $level2){
                        $sender->sendMessage(TextFormat::RED."Both positions must be in the same level!");
                        return;
                    }

                    $level = $level1;

                    $this->fix($level, $x1, $y1, $z1, $x2, $y2, $z2, false, true);

                    $sender->sendMessage(TextFormat::GREEN."Selected area successfully fixed!");
                    break;
                case "fixslabs":
                    if(!$sender->hasPermission("wf.command.fixslabs") && !$sender->isOp()){
                        $sender->sendMessage($cmd->getPermissionMessage());
                        break;
                    }
                    if(!$this->isPosSet($sender)){
                        $sender->sendMessage(TextFormat::RED."You must select both positions first!");
                        return;
                    }

                    list($x1, $y1, $z1, $level1) = explode(':', $this->selectors[strtolower($sender->getName())]['pos1']);
                    list($x2, $y2, $z2, $level2) = explode(':', $this->selectors[strtolower($sender->getName())]['pos2']);
                    if($level1 !== $level2){
                        $sender->sendMessage(TextFormat::RED."Both positions must be in the same level!");
                        return;
                    }

                    $level = $level1;

                    $this->fix($level, $x1, $y1, $z1, $x2, $y2, $z2, false, true);

                    $sender->sendMessage(TextFormat::GREEN."Slabs sucessfully fixed!");
                    break;
                case "wand":
                    if(!$sender->hasPermission("wf.command.wand") && !$sender->isOp()){
                        $sender->sendMessage($cmd->getPermissionMessage());
                        break;
                    }
                    if($this->isSelector($sender)){
                        break;
                    }
                    $this->selectors[strtolower($sender->getName())]['ins'] = $sender;
                    $this->selectors[strtolower($sender->getName())]['block'] = 1;
                    $sender->sendMessage(TextFormat::GREEN."Now select two blocks");
                    break;
                case "help":
                    $sender->sendMessage(TextFormat::YELLOW."> WorldFixer help <\n".TextFormat::GREEN."/wf wand ".TextFormat::GRAY."select two positions\n".TextFormat::GREEN."/wf fixslabs ".TextFormat::GRAY."Fix all slabs in the world\n".TextFormat::GREEN."/wf fixcolor ".TextFormat::GRAY."change grass color to green\n".TextFormat::GREEN."/wf fix ".TextFormat::GRAY."fix all slabs in the world and set grass color to green");
                    break;
                default:
                    $sender->sendMessage(TextFormat::RED."Use /wf help for help");
                    break;
            }
        }
    }

    public function isSelector(Player $p){
        return isset($this->selectors[strtolower($p->getName())]['block']) && $this->selectors[strtolower($p->getName())]['block'] > 0;
    }

    public function onBlockBreak(BlockBreakEvent $e){
        $p = $e->getPlayer();
        $b = $e->getBlock();
        if($this->isSelector($p)){
            if($this->selectors[strtolower($p->getName())]['block'] === 1){
                $e->setCancelled();
                $this->selectors[strtolower($p->getName())]['pos1'] = "$b->x:$b->y:$b->z:{$b->level->getName()}";
                $this->selectors[strtolower($p->getName())]['block'] = 2;
                $p->sendMessage(TextFormat::GREEN."Selected the first position at $b->x, $b->y, $b->z");
                return;
            }
            if($this->selectors[strtolower($p->getName())]['block'] === 2){
                $e->setCancelled();
                $this->selectors[strtolower($p->getName())]['pos2'] = "$b->x:$b->y:$b->z:{$b->level->getName()}";
                $this->selectors[strtolower($p->getName())]['block'] = 0;
                $p->sendMessage(TextFormat::GREEN."Selected the second position at $b->x, $b->y, $b->z");
                return;
            }
        }
    }

    public function isPosSet(Player $p){
        if(isset($this->selectors[strtolower($p->getName())]['pos1']) && isset($this->selectors[strtolower($p->getName())]['pos2'])){
            return true;
        }
        return false;
    }

    public function fix(Level $level, $x1, $y1, $z1, $x2, $y2, $z2, $color = true, $slabs = true){
        if(!$color && !$slabs){
            return false;
        }

        /*if($color === true && !$slabs){
            for($x = min($x1, $x2); $x < max($x1, $x2); $x++){
                for($z = min($z1, $z2); $z < max($z1, $z2); $z++){
                    $level->setBiomeColor($x, $z, 108, 151, 47);
                }
            }
            return true;
        }*/

        for($x = min($x1, $x2); $x < max($x1, $x2); $x++){
            for($y = min($y1, $y2); $y < max($y1, $y2); $y++) {
                for ($z = min($z1, $z2); $z < max($z1, $z2); $z++) {
                    if($slabs && $level->getBlockIdAt($x, $y, $z) === 126){
                        $level->setBlockIdAt($x, $y, $z, 158);
                    }
                    if($color){
                        $level->setBiomeColor($x, $z, 108, 151, 47);
                    }
                }
            }
        }

        return true;
    }
}