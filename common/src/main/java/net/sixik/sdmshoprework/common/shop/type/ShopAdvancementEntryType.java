package net.sixik.sdmshoprework.common.shop.type;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.util.KnownServerRegistries;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sixik.sdmshoprework.SDMShopR;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sixik.sdmshoprework.common.integration.FTBQuests.ConfigIconItemStack;
import net.sixik.sdmshoprework.common.register.CustomIconItem;
import net.sixik.sdmshoprework.common.register.ItemsRegister;
import net.sixik.sdmshoprework.common.utils.NBTUtils;

public class ShopAdvancementEntryType extends AbstractShopEntryType {

    public ResourceLocation advancement;
    public ItemStack iconPath = Items.WHEAT.getDefaultInstance();

    public ShopAdvancementEntryType(ResourceLocation advancement){
        this.advancement = advancement;
    }

    @Override
    public void buy(Player player, int countBuy, AbstractShopEntry entry) {
        if(player instanceof ServerPlayer serverPlayer) {
//            long playerMoney = SDMShopR.getMoney(player);
            long needMoney = entry.entryPrice;
            Advancement a = serverPlayer.server.getAdvancements().getAdvancement(advancement);

            if (a != null) {
                for (String s : a.getCriteria().keySet()) {
                    serverPlayer.getAdvancements().award(a, s);
                }

                entry.shopSellerType.buy(player, entry, -needMoney);
            }
        }
    }

    @Override
    public void sell(Player player, int countSell, AbstractShopEntry entry) {
        if(player instanceof ServerPlayer serverPlayer) {
            Advancement a = serverPlayer.server.getAdvancements().getAdvancement(advancement);

            if (a != null) {
                for (String s : a.getCriteria().keySet()) {
                    serverPlayer.getAdvancements().revoke(a, s);
                }

                entry.shopSellerType.buy(player, entry, entry.entryPrice);
//                SDMShopR.addMoney(player, entry.entryPrice);
            }
        }
    }

    @Override
    public int howMany(Player player, boolean isSell, AbstractShopEntry entry) {
        if(player.isLocalPlayer()) {
            if (isSell) {
                if (Minecraft.getInstance().getConnection().getAdvancements().getAdvancements().get(advancement) == null)
                    return 0;
                return 1;
            }

            if(entry.entryPrice == 0) return 1;
            if (Minecraft.getInstance().getConnection().getAdvancements().getAdvancements().get(advancement) != null)
                return 0;
            return 1;
        }
        return 0;
    }

    @Override
    public boolean canExecute(Player player, boolean isSell, int countSell, AbstractShopEntry entry) {
        if(isSell){
            return Minecraft.getInstance().getConnection().getAdvancements().getAdvancements().get(advancement) != null;
        }

        long playerMoney = entry.shopSellerType.getCount(player);
        long needMoney = entry.entryPrice * countSell;
        if((playerMoney < needMoney || playerMoney - needMoney < 0) && Minecraft.getInstance().getConnection().getAdvancements().getAdvancements().get(advancement) == null) return false;
        return true;
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());
        group.addEnum("advancement", advancement, v -> advancement = v, NameMap.of(KnownServerRegistries.client.advancements.keySet().iterator().next(), KnownServerRegistries.client.advancements.keySet().toArray(new ResourceLocation[0]))
                .icon(resourceLocation -> ItemIcon.getItemIcon(KnownServerRegistries.client.advancements.get(resourceLocation).icon))
                .name(resourceLocation -> KnownServerRegistries.client.advancements.get(resourceLocation).name)
                .create()).setNameKey("ftbquests.reward.ftbquests.advancement");
    }

    @Override
    public Icon getIcon() {
        if(iconPath.is(ItemsRegister.CUSTOM_ICON.get())){
            return CustomIconItem.getIcon(iconPath);
        }
        return ItemIcon.getItemIcon(iconPath);
    }

    @Override
    public AbstractShopEntryType copy() {
        return new ShopAdvancementEntryType(advancement);
    }

    @Override
    public Icon getCreativeIcon() {
        return ItemIcon.getItemIcon(Items.WHEAT);
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.add.context.advancement");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putString("advancement", advancement.toString());
        NBTUtils.putItemStack(nbt, "iconPath", iconPath);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.advancement = new ResourceLocation(nbt.getString("advancement"));
        this.iconPath = ItemStack.of(nbt.getCompound("iconPath"));
    }

    @Override
    public String getId() {
        return "advancementType";
    }

    public static class Constructor implements IConstructor<AbstractShopEntryType> {
        @Override
        public AbstractShopEntryType createDefaultInstance() {
            return new ShopAdvancementEntryType(new ResourceLocation("minecraft:story/root"));
        }
    }
}
