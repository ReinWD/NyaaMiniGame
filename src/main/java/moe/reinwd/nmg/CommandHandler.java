package moe.reinwd.nmg;

import cat.nyaa.nyaacore.CommandReceiver;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class CommandHandler extends CommandReceiver {
    private final NyaaMiniGame plugin;
    ItemStack stick;

    CommandHandler(NyaaMiniGame plugin){
        super(plugin,null);
        this.plugin = plugin;
        stick = new ItemStack(Material.STICK,1);
        ArrayList<String>loreList = new ArrayList<>();
        loreList.add("放下一个可以使用的旗舰");

        ItemMeta meta = stick.getItemMeta();
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,10,true);
        meta.setLore(loreList);
        meta.setDisplayName("旗舰召唤器");
        stick.setItemMeta(meta);

    }

    @SubCommand("get")
    public void getTool(CommandSender sender, Arguments arguments){
        Player player = sender.getServer().getPlayer(sender.getName());
        if (player==null){
            sender.sendMessage("only players can use command \"get\"");
        }else {
            PlayerInventory inventory = player.getInventory();
            ItemStack[] itemStack = inventory.getContents();

            for (int i = 0; i < 36; i++) {
                ItemStack temp;
                temp = itemStack[i];
                if (temp == null){
                    int heldItem = inventory.getHeldItemSlot();
                    if (heldItem != i) {
                        ItemStack mainHand = itemStack[heldItem];
                        inventory.setItem(i, mainHand);
                    }
                    inventory.setItem(heldItem, stick);
                    return;
                }
            }
            //when inventory is full
            sender.sendMessage("inventory is full");
            player.getWorld().dropItem(player.getLocation(),stick);
        }
    }

    @SubCommand("boat")
    public void boatGame(CommandSender sender, Arguments arguments){
        String string = arguments.nextString();
        if (string == null || string.equals("toggle")){
            sendToggleMessage(sender,toggleGame());
            return;
        }
        sender.sendMessage("");
    }

    @SubCommand("elytra")
    public void toggleElytra(CommandSender sender, Arguments arguments){
        if (sender.isOp()){
            if (plugin.toggleElytra())sender.sendMessage("已开启鞘翅功能");
            else sender.sendMessage("已关闭鞘翅功能");
        }else {
            msg(sender,"message.worldguard.permission");
            return;
        }
    }

    private void sendToggleMessage(CommandSender sender, boolean toggleGame) {
        if (toggleGame) {
            sender.sendMessage("已开始夺船游戏");
        }else {
            sender.sendMessage("夺船游戏结束");
        }
    }

    private boolean toggleGame() {
        boolean boatGameEnabled = !plugin.getBoatGameEnabled();
        plugin.toggleBoatGame();
        return boatGameEnabled;
    }

    @Override
    public String getHelpPrefix() {
        return "mini_game";
    }
}
