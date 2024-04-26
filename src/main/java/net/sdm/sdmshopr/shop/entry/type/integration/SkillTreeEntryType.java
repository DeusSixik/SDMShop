//package net.sdm.sdmshopr.shop.entry.type.integration;
//
//import daripher.skilltree.capability.skill.PlayerSkillsProvider;
//import daripher.skilltree.network.NetworkDispatcher;
//import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
//import dev.ftb.mods.ftblibrary.config.ConfigGroup;
//import dev.ftb.mods.ftblibrary.icon.Icon;
//import dev.ftb.mods.ftblibrary.icon.Icons;
//import dev.ftb.mods.ftblibrary.icon.ItemIcon;
//import dev.ftb.mods.ftbquests.client.ConfigIconItemStack;
//import dev.ftb.mods.ftbquests.item.CustomIconItem;
//import dev.ftb.mods.ftbquests.item.FTBQuestsItems;
//import net.minecraft.client.Minecraft;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.chat.Component;
//import net.minecraft.network.chat.TranslatableComponent;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//import net.minecraftforge.network.PacketDistributor;
//import net.sdm.sdmshopr.SDMShopR;
//import net.sdm.sdmshopr.shop.entry.ShopEntry;
//import net.sdm.sdmshopr.api.IEntryType;
//import net.sdm.sdmshopr.utils.NBTUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SkillTreeEntryType implements IEntryType {
//
//    private ItemStack iconPath = Items.BARRIER.getDefaultInstance();
//
//    public SkillTreeEntryType(){
//
//    }
//
//    @Override
//    public Component getTranslatableForContextMenu() {
//        return new TranslatableComponent("sdm.shop.entry.add.context.integration.passiveskilltree");
//    }
//
//    @Override
//    public String getModNameForContextMenu() {
//        return "Passive Skill Tree";
//    }
//
//    @Override
//    public List<Component> getDescriptionForContextMenu() {
//        List<Component> list = new ArrayList<>();
//        list.add(new TranslatableComponent("sdmr.shop.entry.creator.type.pstType.description"));
//        return list;
//    }
//
//    @Override
//    public boolean isSellable() {
//        return true;
//    }
//
//    @Override
//    public boolean isCountable() {
//        return true;
//    }
//
//    @Override
//    public Icon getIcon() {
//        if(iconPath.is(FTBQuestsItems.CUSTOM_ICON.get())){
//            return CustomIconItem.getIcon(iconPath);
//        }
//        return ItemIcon.getItemIcon(iconPath);
//    }
//
//    @Override
//    public CompoundTag getIconNBT() {
//        return new CompoundTag();
//    }
//
//    @Override
//    public void getConfig(ConfigGroup group) {
//        group.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());
//    }
//
//    @Override
//    public Icon getCreativeIcon() {
//        return Icon.getIcon("skilltree:textures/item/amnesia_scroll.png");
//    }
//
//    @Override
//    public String getModID() {
//        return "skilltree";
//    }
//
//    @Override
//    public String getID() {
//        return "pstType";
//    }
//
//    @Override
//    public IEntryType copy() {
//        return new SkillTreeEntryType();
//    }
//
//    @Override
//    public CompoundTag serializeNBT() {
//        CompoundTag nbt = new CompoundTag();
//        NBTUtils.putItemStack(nbt, "iconPathNew", iconPath);
//        nbt.putString("type", getID());
//        return nbt;
//    }
//
//    @Override
//    public void deserializeNBT(CompoundTag nbt) {
//        iconPath = NBTUtils.getItemStack(nbt, "iconPathNew");
//    }
//
//    @Override
//    public void sell(ServerPlayer player, int countSell, ShopEntry<?> entry) {
//        if(PlayerSkillsProvider.get(player).getSkillPoints() >= entry.count * countSell){
//            PlayerSkillsProvider.get(player).setSkillPoints(PlayerSkillsProvider.get(player).getSkillPoints() - (entry.count * countSell));
//
//            NetworkDispatcher.network_channel.send(
//                    PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
//            SDMShopR.setMoney(player, SDMShopR.getMoney(player) + (long) entry.price * countSell);
//        }
//    }
//
//    @Override
//    public void buy(ServerPlayer player, int countBuy, ShopEntry<?> entry) {
//        if((long) entry.price * countBuy <= SDMShopR.getMoney(player)){
//            PlayerSkillsProvider.get(player).setSkillPoints(PlayerSkillsProvider.get(player).getSkillPoints() + (entry.count * countBuy));
//            NetworkDispatcher.network_channel.send(
//                    PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
//            SDMShopR.setMoney(player, SDMShopR.getMoney(player) - (long) entry.price * countBuy);
//
//        }
//
//    }
//
//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public boolean canExecute(boolean isSell, int countSell, ShopEntry<?> entry) {
//        if(isSell){
//            if(PlayerSkillsProvider.get(Minecraft.getInstance().player).getSkillPoints() >= entry.count * countSell){
//                return true;
//            }
//            return false;
//        }
//        long playerMoney = SDMShopR.getClientMoney();
//        int needMoney = entry.price * countSell;
//        if(playerMoney < needMoney || playerMoney - needMoney < 0) return false;
//        return true;
//    }
//
//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public int howMany(boolean isSell, ShopEntry<?> entry) {
//        if(isSell) {
//            return PlayerSkillsProvider.get(Minecraft.getInstance().player).getSkillPoints() / entry.count;
//        }
//
//        long playerMoney = SDMShopR.getClientMoney();
//        return (int) (playerMoney / entry.price);
//    }
//}
