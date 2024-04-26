package net.sdm.sdmshopr.shop.entry.type.integration.ManaAndArtifice;

import com.mna.api.capabilities.IPlayerMagic;
import com.mna.capabilities.playerdata.magic.PlayerMagicProvider;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftbquests.client.ConfigIconItemStack;
import dev.ftb.mods.ftbquests.item.CustomIconItem;
import dev.ftb.mods.ftbquests.item.FTBQuestsItems;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.api.IEntryType;
import net.sdm.sdmshopr.utils.NBTUtils;

public class MNALevelEntryType implements IEntryType {
    protected static int maxLevel = 75;

    public int level;

    private ItemStack iconPath = Items.BARRIER.getDefaultInstance();


    public MNALevelEntryType(){
    }

    protected MNALevelEntryType(int level, ItemStack iconPath){
        this.level = level;
        this.iconPath = iconPath;
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
        group.addInt("mnalevel", level, v -> level = v, 1, 1, 75);
        group.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());
    }

    @Override
    public Icon getCreativeIcon() {
        return Icon.getIcon("mna:textures/item/eldrin_sight_unguent.png");
    }

    @Override
    public String getModID() {
        return "mna";
    }

    @Override
    public String getID() {
        return "mnaLevelType";
    }

    @Override
    public IEntryType copy() {
        return new MNALevelEntryType(level, iconPath);
    }

    @Override
    public Component getTranslatableForContextMenu() {
        return new TranslatableComponent("sdm.shop.entry.add.context.mnamagiclevel");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = IEntryType.super.serializeNBT();
        nbt.putInt("level", level);
        NBTUtils.putItemStack(nbt, "iconPathNew", iconPath);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        level = nbt.getInt("level");
        iconPath = NBTUtils.getItemStack(nbt, "iconPathNew");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int howMany(boolean isSell, ShopEntry<?> entry) {
        IPlayerMagic playerMagic = (IPlayerMagic) Minecraft.getInstance().player.getCapability(PlayerMagicProvider.MAGIC).resolve().get();
        if(playerMagic != null) {
            if (isSell) {
                return playerMagic.getMagicLevel() / level;
            }
            long playerMoney = SDMShopR.getClientMoney();
            int count = (int) (playerMoney / entry.price);
            if ((playerMagic.getMagicLevel() + count * level) > 75)
                return ((playerMagic.getMagicLevel() + count * level) - playerMagic.getMagicLevel()) / level;
            return count;
        }
        return 0;
    }

    @Override
    public String getModNameForContextMenu() {
        return "Mana And Artifice";
    }

    @Override
    public void buy(ServerPlayer player, int countBuy, ShopEntry<?> entry) {
        IPlayerMagic playerMagic = (IPlayerMagic) player.getCapability(PlayerMagicProvider.MAGIC).resolve().get();
        if(playerMagic != null) {
            int d1 = playerMagic.getMagicLevel() + (level * countBuy);
            if (d1 > maxLevel) {
                d1 = 75;
            }
            playerMagic.setMagicLevel(player, d1);
            playerMagic.setMagicXP(playerMagic.getXPForLevel(d1));
            long playerMoney = SDMShopR.getMoney(player);
            SDMShopR.setMoney(player, playerMoney - entry.price);
        }

    }

    @Override
    public void sell(ServerPlayer player, int countSell, ShopEntry<?> entry) {
        IPlayerMagic playerMagic = (IPlayerMagic) player.getCapability(PlayerMagicProvider.MAGIC).resolve().get();
        if(playerMagic != null) {
            int d1 = playerMagic.getMagicLevel() - (level * countSell);
            if (d1 < 0) d1 = 0;
            playerMagic.setMagicLevel(player, d1);
            playerMagic.setMagicXP(playerMagic.getXPForLevel(d1));
            long playerMoney = SDMShopR.getMoney(player);
            SDMShopR.setMoney(player, playerMoney + entry.price);
        }
    }
}
