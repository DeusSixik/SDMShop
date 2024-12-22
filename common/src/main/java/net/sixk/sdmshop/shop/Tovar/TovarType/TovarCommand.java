package net.sixk.sdmshop.shop.Tovar.TovarType;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.sixik.sdm_economy.api.CurrencyHelper;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;
import net.sixk.sdmshop.api.IConstructor;
import net.sixk.sdmshop.shop.Tovar.AbstractTovar;
import net.sixk.sdmshop.shop.Tovar.Tovar;

public class TovarCommand extends AbstractTovar {

    private Icon icon = ItemIcon.getItemIcon(Items.BARRIER);
    public String command;
    public boolean elevatePerms;
    public boolean silent;

   public TovarCommand(String command){
        this.command = command;
   }

    @Override
    public void buy(Player player, Tovar tovar, long count) {

        if ((tovar.limit < count && tovar.limit != -1)) return;

        if(player instanceof ServerPlayer serverPlayer){
            if(command.isEmpty()) return;
            CommandSourceStack source = serverPlayer.createCommandSourceStack();
            if (elevatePerms) source = source.withPermission(2);
            if (silent) source = source.withSuppressedOutput();

            try {
                player.getServer().getCommands().performPrefixedCommand(source, command);
                CurrencyHelper.setMoney(serverPlayer, tovar.currency,CurrencyHelper.getMoney(serverPlayer, tovar.currency) - tovar.cost * count);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (tovar.limit != -1) tovar.limit -= count;

    }

    @Override
    public void sell(Player player, Tovar tovar, long count) {
            return;
    }

    @Override
    public String getTitel() {
        return "Command : " + command;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public Object getItemStack() {
        return null;
    }

    @Override
    public TagKey getTag() {
        return null;
    }

    @Override
    public AbstractTovar copy() {
        return null;
    }

    @Override
    public String getID() {
        return "CommandType";
    }

    @Override
    public boolean getisXPLVL() {
        return false;
    }

    @Override
    public KeyData serialize(HolderLookup.Provider provider) {

       KeyData data = new KeyData();

       data.put("id",getID());
       data.put("command",command);

       return data;
    }

    @Override
    public void deserialize(KeyData data, HolderLookup.Provider provider) {

       command = data.getData("command").asString();

    }

    public static class Constructor implements IConstructor<AbstractTovar> {
        @Override
        public AbstractTovar create() {
            return new TovarCommand(" ");
        }
    }

}
