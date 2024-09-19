package net.sdm.sdmshoprework.common.shop.type.integration.ManaAndArtifice;

import com.mna.api.capabilities.IPlayerMagic;
import com.mna.capabilities.playerdata.magic.PlayerMagicProvider;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sdm.sdmshoprework.SDMShopR;
import net.sdm.sdmshoprework.api.IConstructor;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntry;
import net.sdm.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sdm.sdmshoprework.common.integration.FTBQuests.ConfigIconItemStack;
import net.sdm.sdmshoprework.common.register.CustomIconItem;
import net.sdm.sdmshoprework.common.register.ItemsRegister;
import net.sdm.sdmshoprework.common.utils.NBTUtils;

public class ShopMNALevelEntryType extends AbstractShopEntryType {
    protected static int maxLevel = 75;

    public int level;

    private ItemStack iconPath = Items.BARRIER.getDefaultInstance();

    public ShopMNALevelEntryType(){
    }

    protected ShopMNALevelEntryType(int level, ItemStack iconPath){
        this.level = level;
        this.iconPath = iconPath;
    }

    @Override
    public boolean isCountable() {
        return false;
    }

    @Override
    public Icon getIcon() {
        if(iconPath.is(ItemsRegister.CUSTOM_ICON.get())){
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
    public String getModId() {
        return "mna";
    }

    @Override
    public AbstractShopEntryType copy() {
        return new ShopMNALevelEntryType(level, iconPath);
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.add.context.mnamagiclevel");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
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
    public String getId() {
        return "mnaLevelType";
    }

    @Override
    public String getModNameForContextMenu() {
        return "Mana And Artifice";
    }

    @Override
    public int howMany(Player player, boolean isSell, AbstractShopEntry entry) {
        IPlayerMagic playerMagic = (IPlayerMagic) player.getCapability(PlayerMagicProvider.MAGIC).resolve().get();
        if(playerMagic != null) {
            if (isSell) {
                return playerMagic.getMagicLevel() / level;
            }
            long playerMoney = SDMShopR.getMoney(player);
            int count = (int) (playerMoney / entry.entryPrice);
            if ((playerMagic.getMagicLevel() + count * level) > 75)
                return ((playerMagic.getMagicLevel() + count * level) - playerMagic.getMagicLevel()) / level;
            return count;
        }
        return 0;
    }

    @Override
    public void buy(Player player, int countBuy, AbstractShopEntry entry) {
        IPlayerMagic playerMagic = (IPlayerMagic) player.getCapability(PlayerMagicProvider.MAGIC).resolve().get();
        if(playerMagic != null) {
            int d1 = playerMagic.getMagicLevel() + (level * countBuy);
            if (d1 > maxLevel) {
                d1 = 75;
            }
            playerMagic.setMagicLevel(player, d1);
            playerMagic.setMagicXP(playerMagic.getXPForLevel(d1));
            long playerMoney = SDMShopR.getMoney(player);
            SDMShopR.setMoney(player, playerMoney - entry.entryPrice);
        }

    }

    @Override
    public void sell(Player player, int countSell, AbstractShopEntry entry) {
        IPlayerMagic playerMagic = (IPlayerMagic) player.getCapability(PlayerMagicProvider.MAGIC).resolve().get();
        if(playerMagic != null) {
            int d1 = playerMagic.getMagicLevel() - (level * countSell);
            if (d1 < 0) d1 = 0;
            playerMagic.setMagicLevel(player, d1);
            playerMagic.setMagicXP(playerMagic.getXPForLevel(d1));
            long playerMoney = SDMShopR.getMoney(player);
            SDMShopR.setMoney(player, playerMoney + entry.entryPrice);
        }
    }

    public static class Constructor implements IConstructor<AbstractShopEntryType> {

        @Override
        public AbstractShopEntryType createDefaultInstance() {
            return new ShopMNALevelEntryType();
        }
    }
}
