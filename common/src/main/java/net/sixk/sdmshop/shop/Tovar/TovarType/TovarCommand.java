package net.sixk.sdmshop.shop.Tovar.TovarType;

import dev.ftb.mods.ftblibrary.icon.Icon;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixk.sdmshop.shop.Tovar.AbstractTovar;

import java.util.UUID;

public class TovarCommand extends AbstractTovar {
    public String command;
    public boolean elevatePerms;
    public boolean silent;

    public TovarCommand(UUID uuid, Icon icon, String tab, String currency, Integer cost, long limit, boolean toSell, String command) {
        super(uuid, icon, tab, currency, cost, limit, toSell);
        this.command = command;
    }

    public TovarCommand(UUID uuid, Icon icon, String tab, String currency, Integer cost, long limit, boolean toSell) {
        super(uuid, icon, tab, currency, cost, limit, toSell);
    }

    public void buy(Player player, AbstractTovar tovar, long count) {
        if (tovar.limit >= count || tovar.limit == -1L) {
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                if (this.command.isEmpty()) {
                    return;
                }

                CommandSourceStack source = serverPlayer.createCommandSourceStack();
                if (this.elevatePerms) {
                    source = source.withPermission(2);
                }

                if (this.silent) {
                    source = source.withSuppressedOutput();
                }

                try {
                    player.getServer().getCommands().performPrefixedCommand(source, this.command);
                    EconomyAPI.getPlayerCurrencyServerData().setCurrencyValue(serverPlayer, tovar.currency, (Double)EconomyAPI.getPlayerCurrencyServerData().getBalance(serverPlayer, tovar.currency).value - (double)((long)tovar.cost * count));
                } catch (Exception var8) {
                    Exception e = var8;
                    e.printStackTrace();
                }
            }

            if (tovar.limit != -1L) {
                tovar.limit -= count;
            }

        }
    }

    public void sell(Player player, AbstractTovar tovar, long count) {
    }

    public String getTitel() {
        return "Command : " + this.command;
    }

    public Icon getIcon() {
        return this.icon;
    }

    public Object getItemStack() {
        return null;
    }

    public TagKey getTag() {
        return null;
    }

    public AbstractTovar copy() {
        return null;
    }

    public String getID() {
        return "CommandType";
    }

    public boolean getisXPLVL() {
        return false;
    }

    @Override
    public void _serializeNBT(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putString("command", this.command);
    }

    @Override
    public void _deserializeNBT(CompoundTag nbt, HolderLookup.Provider provider) {
        this.command = nbt.getString("command");
    }
}
