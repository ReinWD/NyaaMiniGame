package moe.reinwd.nmg;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.List;

public class EventListener implements Listener {
    private final static ChatColor blue = ChatColor.BLUE,
            red = ChatColor.RED,
            aqua = ChatColor.AQUA,
            green = ChatColor.GREEN;
    private static final String bold = ChatColor.BOLD.toString();
    private NyaaMiniGame plugin;

    EventListener(NyaaMiniGame plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerOnBoat(VehicleEnterEvent event) {
        if(getBoatGameEnabled()==false)return;
        Vehicle vehicle = event.getVehicle();
        Entity player = event.getEntered();
        Server server = vehicle.getServer();

        if (player instanceof Player) {
            Team team = this.getTeam(player);
            if (team == null) return;
            if (vehicle.isGlowing()) {
                this.broadcastMessage(server, MessageType.CAPTURE_FLAG, team, player.getName());
            }
        }
    }


    @EventHandler
    public void onPlayerLeaveBoat(VehicleExitEvent event) {
        if(getBoatGameEnabled()==false)return;
        Vehicle vehicle = event.getVehicle();
        Entity player = event.getExited();
        Server server = vehicle.getServer();
        if (player instanceof Player && vehicle instanceof Boat) {
            Team team = this.getTeam(player);
            if (team == null) return;
            if (vehicle.isGlowing()) {
                //旗舰事件
                this.broadcastMessage(server, MessageType.FLAG_DROP, team, player.getName());
            } else {
                if (((Player) player).isSneaking())
                    this.broadcastMessage(server, MessageType.SUICIDE, team, player.getName());
            }
        }
    }

    @EventHandler
    public void onBoatDestroy(VehicleDestroyEvent event) {
        if(getBoatGameEnabled()==false)return;
        Entity attacker = event.getAttacker();
        Entity vehicle = event.getVehicle();
        Server server = vehicle.getServer();
        List<Entity> passengers = vehicle.getPassengers();
        Team attackerTeam = this.getTeam(attacker);
        if (attackerTeam == null) return;

        //如果被摧毁的船上有玩家
        if (passengers != null && passengers.size() > 0)
            for (Entity e :
                    passengers) {
                //对船上每一个乘客判断关系
                Team entityTeam = this.getTeam(e);
                if (entityTeam != null)
                    if (attackerTeam.equals(entityTeam)) {
                        //友军
                        this.broadcastMessage(server, MessageType.FRIENDLY_FIRE, attackerTeam, attacker.getName(), e.getName());
                    } else {
                        //敌军
                        this.broadcastMessage(server, MessageType.KILL, attackerTeam, attacker.getName(), e.getName());
                    }
            }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if(getBoatGameEnabled()==false)return;
        Entity arrow = event.getDamager();
        Entity victim = event.getEntity();
        Server server = victim.getServer();

        Entity vehicle = victim.getVehicle();
        if (!(vehicle instanceof Boat) || !vehicle.isGlowing()) return;

        if (victim instanceof Player && arrow instanceof Arrow) {
            ProjectileSource shooter = ((Arrow) arrow).getShooter();
            if (shooter instanceof Player) {
                Team team = getTeam(((Player) shooter));

                if (team != null)
                    if ( team.equals(getTeam(victim)) ){
                        broadcastMessage(server, MessageType.FRIENDLY_FIRE, team, ((Player) shooter).getName(), victim.getName());
                    }else {
                        broadcastMessage(server, MessageType.KILL, team, ((Player) shooter).getName(), victim.getName());
                    }
            }
        }
    }

    @EventHandler
    public void onStartGlide(EntityToggleGlideEvent event){
        if (!plugin.isElytraEnabled()){
            if (event.isGliding())
                event.setCancelled(true);
        }
    }

//    @EventHandler
//    public void onPlayerEvent(PlayerInteractEvent event){
//        Player player = event.getPlayer();
//        Server server = player.getServer();
//        PlayerInventory inventory = player.getInventory();
//        if (inventory != null){
//            ItemStack itemInMainHand = inventory.getItemInMainHand();
//            if (itemInMainHand != null){
//                if (itemInMainHand.getType()!= Material.STICK)return;
//                ItemMeta itemMeta = itemInMainHand.getItemMeta();
//                if (itemMeta != null && itemMeta.hasLore()){
//                    for (MessageType t :
//                            MessageType.values()) {
//                            broadcastMessage(server, t, Team.BLUE, player.getName(), player.getName());
//                    }
//                }
//            }
//        }
//    }

    private Team getTeam(Entity player) {
        //todo judge team
        Team result = null;
        if (player == null) return null;
        ScoreboardManager scoreboard = player.getServer().getScoreboardManager();
        org.bukkit.scoreboard.Team entryTeam = player instanceof Boat ? scoreboard.getMainScoreboard().getEntryTeam(player.getUniqueId().toString()) : scoreboard.getMainScoreboard().getEntryTeam(player.getName());
        if (entryTeam != null) {
            ChatColor color = entryTeam.getColor();
            switch (color) {
                case RED:
                    result = Team.RED;
                    break;
                case BLUE:
                    result = Team.BLUE;
                    break;
                default:
                    result = null;
                    break;
            }
        }
        return result;
    }

    private void broadcastMessage(Server server, MessageType type, Team team, String... args) {
        Messages message = null;
        switch (team) {
            case RED:
                switch (type) {
                    case CAPTURE_FLAG:
                        message = Messages.RED_CAPTURE_FLAG;
                        break;
                    case FLAG_DROP:
                        message = Messages.RED_FLAG_DROP;
                        break;
                    case KILL:
                        message = Messages.RED_KILL;
                        break;
                    case SUICIDE:
                        message = Messages.RED_SUICIDE;
                        break;
                    case FRIENDLY_FIRE:
                        message = Messages.RED_FRENDLY_FIRE;
                        break;
                }
                break;
            case BLUE:
                switch (type) {
                    case CAPTURE_FLAG:
                        message = Messages.BLUE_CAPTURE_FLAG;
                        break;
                    case FLAG_DROP:
                        message = Messages.BLUE_FLAG_DROP;
                        break;
                    case KILL:
                        message = Messages.BLUE_KILL;
                        break;
                    case SUICIDE:
                        message = Messages.BLUE_SUICIDE;
                        break;
                    case FRIENDLY_FIRE:
                        message = Messages.BLUE_FRENDLY_FIRE;
                        break;
                }
                break;
            default:
                return;
        }
        switch (type){
            case SUICIDE:
            case FLAG_DROP:
            case CAPTURE_FLAG:
                server.broadcastMessage(String.format(message.message, args[0]));
                break;
            case KILL:
            case FRIENDLY_FIRE:
                server.broadcastMessage(String.format(message.message, args));
                break;
        }
    }

    @EventHandler
    public void onClickBoat(PlayerInteractEntityEvent event) {
        if(getBoatGameEnabled()==false)return;
        Entity vehicle = event.getRightClicked();
        Player player = event.getPlayer();
        Server server = vehicle.getServer();
        Team playerTeam1 = getTeam(vehicle);

        if (!(vehicle instanceof Boat)) {
            return;
        }
        //友方拒绝
        if (playerTeam1 != null && playerTeam1.equals(this.getTeam(player))) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "你不能登上自己的旗舰");
            return;
        }
        //小棒棒的判断
//        ItemStack itemStack = player.getInventory().getItemInMainHand();
//        if (itemStack != null) {
//            List<String> lore = null;
//            ItemMeta itemMeta = itemStack.getItemMeta();
//            if (itemMeta.hasLore())
//                lore = itemStack.getItemMeta().getLore();
//            if (lore != null && lore.size() > 0)
//                if (lore.contains("放下一个可以使用的旗舰")) {
//                    Scoreboard manager = server.getScoreboardManager().getMainScoreboard();
//                    org.bukkit.scoreboard.Team playerTeam = manager.getEntryTeam(player.getName());
//                    String s = String.valueOf(vehicle.getUniqueId());
//                    String team = playerTeam.getColor().equals(ChatColor.RED) ? ChatColor.RED + "红" + ChatColor.AQUA + "队" : ChatColor.BLUE + "蓝" + ChatColor.AQUA + "队";
//                    ChatColor teamColor = playerTeam.getColor();
//                    if (playerTeam.hasEntry(s)) {
//                        player.sendMessage(ChatColor.AQUA + "船 " + teamColor + vehicle.getUniqueId() + ChatColor.AQUA + " 已经位于" + team);
//                    } else {
//                        org.bukkit.scoreboard.Team entryTeam = manager.getEntryTeam(vehicle.getUniqueId().toString());
//                        if (entryTeam != null) entryTeam.removeEntry(vehicle.getUniqueId().toString());
//                        playerTeam.addEntry(s);
//                        player.sendMessage(ChatColor.AQUA + "成功添加船 " + teamColor + vehicle.getUniqueId() + ChatColor.AQUA + " 到" + team);
//                    }
//                    event.setCancelled(true);
//                    return;
//                }
//        }

    }

    boolean getBoatGameEnabled(){
        return this.plugin.getBoatGameEnabled();
    }
    enum Team {RED, BLUE}

    enum MessageType {CAPTURE_FLAG, FLAG_DROP, KILL, SUICIDE, FRIENDLY_FIRE}

    private enum Messages {
        //todo: remove these ugly code and replace it with a String formatter and a resource manager
        BLUE_CAPTURE_FLAG(green + bold + "蓝方 " + blue + bold + "%s " + green + bold + "成功登上" + red + bold + "红方" + green + bold + "旗舰"),
        BLUE_FLAG_DROP(green + bold + "蓝方 " + blue + bold + "%s " + green + bold + "失去了" + red + bold + "红方" + green + bold + "旗舰的控制"),

        BLUE_KILL(aqua + "蓝方 " + blue + "%s " + aqua + "击沉了 红方 " + red + "%s"),
        BLUE_SUICIDE(aqua + "蓝方 " + blue + "%s " + aqua + "放弃治疗, 投河自尽"),
        BLUE_FRENDLY_FIRE(aqua + "蓝方 " + blue + "%s " + aqua + "误伤了友军 " + blue + "%s"),

        RED_CAPTURE_FLAG(green + bold + "红方 " + red + bold + "%s " + green + bold + "成功登上" + blue + bold + "蓝方" + green + bold + "旗舰"),
        RED_FLAG_DROP(green + bold + "红方 " + red + bold + "%s " + green + bold + "失去了" + blue + bold + "蓝方" + green + bold + "旗舰的控制"),

        RED_KILL(aqua + "红方 " + red + "%s " + aqua + "击沉了 蓝方 " + blue + "%s"),
        RED_SUICIDE(aqua + "红方 " + red + "%s " + aqua + "放弃治疗, 投河自尽"),
        RED_FRENDLY_FIRE(aqua + "红方 " + red + "%s " + aqua + "误伤了友军 " + red + "%s");


        String message;

        Messages(String message) {
            this.message = message;
        }
    }
}
