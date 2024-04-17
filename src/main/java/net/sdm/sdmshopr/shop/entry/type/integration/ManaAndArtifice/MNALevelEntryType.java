package net.sdm.sdmshopr.shop.entry.type.integration.ManaAndArtifice;

import com.mna.api.capabilities.IPlayerMagic;
import com.mna.capabilities.playerdata.magic.PlayerMagicProvider;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.api.IEntryType;

public class MNALevelEntryType implements IEntryType {
    protected static int maxLevel = 75;

    public int level;

    private String iconPath = "minecraft:item/barrier";


    public MNALevelEntryType(){
    }

    protected MNALevelEntryType(int level, String iconPath){
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
        Icon getted = Icon.getIcon(iconPath);
        if(getted.isEmpty()) return Icons.BARRIER;
        return getted;
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.addInt("mnalevel", level, v -> level = v, 1, 1, 75);
        group.addString("iconPath", iconPath, v -> iconPath = v, "minecraft:item/barrier");
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
        return Component.translatable("sdm.shop.entry.add.context.mnamagiclevel");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = IEntryType.super.serializeNBT();
        nbt.putInt("level", level);
        nbt.putString("iconPath", iconPath);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        level = nbt.getInt("level");
        iconPath = nbt.getString("iconPath");
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
