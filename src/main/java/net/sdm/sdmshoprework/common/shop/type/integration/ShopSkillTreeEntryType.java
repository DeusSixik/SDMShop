package net.sdm.sdmshoprework.common.shop.type.integration;

import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.PacketDistributor;
import net.sdm.sdmshoprework.SDMShopR;
import net.sdm.sdmshoprework.api.IConstructor;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntry;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sdm.sdmshoprework.common.integration.FTBQuests.ConfigIconItemStack;
import net.sdm.sdmshoprework.common.register.CustomIconItem;
import net.sdm.sdmshoprework.common.register.ItemsRegister;
import net.sdm.sdmshoprework.common.utils.NBTUtils;

import java.util.ArrayList;
import java.util.List;

public class ShopSkillTreeEntryType extends AbstractShopEntryType {

    private ItemStack iconPath = Items.BARRIER.getDefaultInstance();

    public ShopSkillTreeEntryType() {}

    @Override
    public void getConfig(ConfigGroup group) {
        group.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());
    }

    @Override
    public AbstractShopEntryType copy() {
        return new ShopSkillTreeEntryType();
    }

    @Override
    public String getModNameForContextMenu() {
        return "Passive Skill Tree";
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.add.context.integration.passiveskilltree");
    }

    @Override
    public List<Component> getDescriptionForContextMenu() {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("sdmr.shop.entry.creator.type.pstType.description"));
        return list;
    }

    @Override
    public Icon getIcon() {
        if(iconPath.is(ItemsRegister.CUSTOM_ICON.get())){
            return CustomIconItem.getIcon(iconPath);
        }
        return ItemIcon.getItemIcon(iconPath);
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        NBTUtils.putItemStack(nbt, "iconPathNew", iconPath);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        iconPath = NBTUtils.getItemStack(nbt, "iconPathNew");
    }

    @Override
    public Icon getCreativeIcon() {
        return Icon.getIcon("skilltree:textures/item/amnesia_scroll.png");
    }

    @Override
    public String getModId() {
        return "skilltree";
    }

    @Override
    public String getId() {
        return "pstType";
    }

    @Override
    public void sell(Player player, int countSell, AbstractShopEntry entry) {
        if(player instanceof ServerPlayer serverPlayer) {
            if (PlayerSkillsProvider.get(player).getSkillPoints() >= entry.entryCount * countSell) {
                PlayerSkillsProvider.get(player).setSkillPoints(PlayerSkillsProvider.get(player).getSkillPoints() - (entry.entryCount * countSell));

                NetworkDispatcher.network_channel.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncPlayerSkillsMessage(player));
                SDMShopR.setMoney(player, SDMShopR.getMoney(player) + (long) entry.entryPrice * countSell);
            }
        }
    }

    @Override
    public void buy(Player player, int countBuy, AbstractShopEntry entry) {
        if(player instanceof ServerPlayer serverPlayer) {
            if ((long) entry.entryPrice * countBuy <= SDMShopR.getMoney(player)) {
                PlayerSkillsProvider.get(player).setSkillPoints(PlayerSkillsProvider.get(player).getSkillPoints() + (entry.entryCount * countBuy));
                NetworkDispatcher.network_channel.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncPlayerSkillsMessage(player));
                SDMShopR.setMoney(player, SDMShopR.getMoney(player) - (long) entry.entryPrice * countBuy);

            }
        }
    }

    @Override
    public boolean canExecute(Player player, boolean isSell, int countSell, AbstractShopEntry entry) {
        if(isSell){
            if(PlayerSkillsProvider.get(player).getSkillPoints() >= entry.entryCount * countSell){
                return true;
            }
            return false;
        }
        long playerMoney = SDMShopR.getMoney(player);
        long needMoney = entry.entryPrice * countSell;
        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
        return true;
    }

    @Override
    public int howMany(Player player, boolean isSell, AbstractShopEntry entry) {
        if(isSell) {
            return PlayerSkillsProvider.get(player).getSkillPoints() / entry.entryCount;
        }

        long playerMoney = SDMShopR.getMoney(player);
        if(entry.entryPrice == 0) return Byte.MAX_VALUE;
        return (int) (playerMoney / entry.entryPrice);
    }

    public static class Constructor implements IConstructor<AbstractShopEntryType> {

        @Override
        public AbstractShopEntryType createDefaultInstance() {
            return new ShopSkillTreeEntryType();
        }
    }
}
