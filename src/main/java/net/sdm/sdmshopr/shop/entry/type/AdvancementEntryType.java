package net.sdm.sdmshopr.shop.entry.type;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.util.KnownServerRegistries;
import dev.ftb.mods.ftbquests.client.ConfigIconItemStack;
import dev.ftb.mods.ftbquests.item.CustomIconItem;
import dev.ftb.mods.ftbquests.item.FTBQuestsItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.api.IEntryType;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.utils.NBTUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvancementEntryType implements IEntryType {


    public ResourceLocation advancement;
    public ItemStack iconPath = Items.WHEAT.getDefaultInstance();

    public AdvancementEntryType(ResourceLocation advancement){
        this.advancement = advancement;
    }


    @Override
    public void buy(ServerPlayer player, int countBuy, ShopEntry<?> entry) {
        long playerMoney = SDMShopR.getMoney(player);
        int needMoney = entry.price;
        Advancement a = player.server.getAdvancements().getAdvancement(advancement);

        if (a != null) {
            for (String s : a.getCriteria().keySet()) {
                player.getAdvancements().award(a, s);
            }
            SDMShopR.setMoney(player, playerMoney - needMoney);
        }
    }

    @Override
    public void sell(ServerPlayer player, int countSell, ShopEntry<?> entry) {
        Advancement a = player.server.getAdvancements().getAdvancement(advancement);

        if (a != null) {
            for (String s : a.getCriteria().keySet()) {
                player.getAdvancements().revoke(a, s);
            }
            SDMShopR.addMoney(player, entry.price);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int howMany(boolean isSell, ShopEntry<?> entry) {
        if(isSell){
            if(Minecraft.getInstance().getConnection().getAdvancements().getAdvancements().get(advancement) == null) return 0;
            return 1;
        }

        if(Minecraft.getInstance().getConnection().getAdvancements().getAdvancements().get(advancement) != null) return 0;
        return 1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canExecute(boolean isSell, int countSell, ShopEntry<?> entry) {
        if(isSell){
            return Minecraft.getInstance().getConnection().getAdvancements().getAdvancements().get(advancement) != null;
        }

        long playerMoney = SDMShopR.getClientMoney();
        int needMoney = entry.price * countSell;
        if((playerMoney < needMoney || playerMoney - needMoney < 0) && Minecraft.getInstance().getConnection().getAdvancements().getAdvancements().get(advancement) == null) return false;
        return true;
    }

    @Override
    public boolean isSellable() {
        return true;
    }

    @Override
    public boolean isCountable() {
        return false;
    }

    @Override
    public Icon getIcon() {
        if(iconPath.is(FTBQuestsItems.CUSTOM_ICON.get())){
            return CustomIconItem.getIcon(iconPath);
        }
        return ItemIcon.getItemIcon(iconPath);
    }


    @Override
    public void getConfig(ConfigGroup group) {
        group.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());
        group.addEnum("advancement", advancement, v -> advancement = v, NameMap.of(KnownServerRegistries.client.advancements.keySet().iterator().next(), KnownServerRegistries.client.advancements.keySet().toArray(new ResourceLocation[0]))
                .icon(resourceLocation -> ItemIcon.getItemIcon(KnownServerRegistries.client.advancements.get(resourceLocation).icon))
                .name(resourceLocation -> KnownServerRegistries.client.advancements.get(resourceLocation).name)
                .create()).setNameKey("ftbquests.reward.ftbquests.advancement");
    }

    public NameMap<String> getAdvancements(){
        List<String> str = new ArrayList<>();

        for (Map.Entry<ResourceLocation, KnownServerRegistries.AdvancementInfo> resourceLocationAdvancementInfoEntry : KnownServerRegistries.client.advancements.entrySet()) {
            str.add(resourceLocationAdvancementInfoEntry.getKey().toString());
        }

        return NameMap.<String>of("minecraft:story/root", str).create();
    }

    @Override
    public Icon getCreativeIcon() {
        return ItemIcon.getItemIcon(Items.WHEAT);
    }

    @Override
    public String getID() {
        return "advancementType";
    }

    @Override
    public IEntryType copy() {
        return new AdvancementEntryType(advancement);
    }

    @Override
    public Component getTranslatableForContextMenu() {
        return Component.translatable("sdm.shop.entry.add.context.advancement");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = IEntryType.super.serializeNBT();
        nbt.putString("advancement", advancement.toString());
        NBTUtils.putItemStack(nbt, "iconPath", iconPath);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.advancement = new ResourceLocation(nbt.getString("advancement"));
        this.iconPath = NBTUtils.getItemStack(nbt, "iconPath");

    }
}
