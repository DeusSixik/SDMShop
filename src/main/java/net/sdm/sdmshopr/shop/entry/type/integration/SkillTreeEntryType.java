package net.sdm.sdmshopr.shop.entry.type.integration;

import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.skill.PassiveSkillTree;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraftforge.network.PacketDistributor;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.shop.entry.type.IEntryType;

public class SkillTreeEntryType implements IEntryType {

    private String iconPath = "minecraft:item/barrier";

    public SkillTreeEntryType(){

    }

    @Override
    public boolean isSellable() {
        return true;
    }

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public Icon getIcon() {
        Icon getted = Icon.getIcon(iconPath);
        if(getted.isEmpty()) return Icons.BARRIER;
        return getted;
    }

    @Override
    public CompoundTag getIconNBT() {
        return new CompoundTag();
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.addString("iconPath", iconPath, v -> iconPath = v, "minecraft:item/barrier");
    }

    @Override
    public Icon getCreativeIcon() {
        return Icon.getIcon("skilltree:textures/item/amnesia_scroll.png");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("type", "pstType");
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }

    @Override
    public void sell(ServerPlayer player, int countSell, ShopEntry<?> entry) {
        if(PlayerSkillsProvider.get(player).getSkillPoints() >= entry.count * countSell){
            PlayerSkillsProvider.get(player).setSkillPoints(PlayerSkillsProvider.get(player).getSkillPoints() - (entry.count * countSell));

            NetworkDispatcher.network_channel.send(
                    PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
            SDMShopR.setMoney(player, SDMShopR.getMoney(player) + (long) entry.price * countSell);
        }
    }

    @Override
    public void buy(ServerPlayer player, int countBuy, ShopEntry<?> entry) {
        if((long) entry.price * countBuy <= SDMShopR.getMoney(player)){
            PlayerSkillsProvider.get(player).setSkillPoints(PlayerSkillsProvider.get(player).getSkillPoints() + (entry.count * countBuy));
            NetworkDispatcher.network_channel.send(
                    PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
            SDMShopR.setMoney(player, SDMShopR.getMoney(player) - (long) entry.price * countBuy);

        }

    }

    @Override
    public boolean canExecute(boolean isSell, int countSell, ShopEntry<?> entry) {
        if(isSell){
            if(PlayerSkillsProvider.get(Minecraft.getInstance().player).getSkillPoints() >= entry.count * countSell){
                return true;
            }
            return false;
        }
        long playerMoney = SDMShopR.getClientMoney();
        int needMoney = entry.price * countSell;
        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
        return true;
    }

    @Override
    public int howMany(boolean isSell, ShopEntry<?> entry) {
        if(isSell) {
            return PlayerSkillsProvider.get(Minecraft.getInstance().player).getSkillPoints() / entry.count;
        }

        long playerMoney = SDMShopR.getClientMoney();
        return (int) (playerMoney / entry.price);
    }
}
