package net.sixik.sdmshop.shop.entry_types;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.util.KnownServerRegistries;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.sixik.sdmshop.old_api.CustomIcon;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.old_api.shop.EntryTypeProperty;
import net.sixik.sdmshop.shop.ShopEntry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdvancementEntryType extends AbstractEntryType implements CustomIcon {
    protected static final ResourceLocation DEFAULT = new ResourceLocation("minecraft:story/root");

    protected ResourceLocation advancement;
    protected boolean useIconFromAdvancement;

    public AdvancementEntryType(ShopEntry shopEntry) {
        this(shopEntry, DEFAULT);
    }

    public AdvancementEntryType(ShopEntry shopEntry, ResourceLocation advancement) {
        this(shopEntry, advancement, true);
    }

    public AdvancementEntryType(ShopEntry shopEntry, ResourceLocation advancement, boolean useIconFromAdvancement) {
        super(shopEntry);
        this.advancement = advancement;
        this.useIconFromAdvancement = true;
    }

    @Override
    public AbstractEntryType copy() {
        return new AdvancementEntryType(shopEntry, advancement, useIconFromAdvancement);
    }

    @Override
    public boolean onBuy(Player player, ShopEntry entry, int countBuy) {
        if(player instanceof ServerPlayer serverPlayer) {
            Optional<Advancement> opt = getAdvancement(player, advancement);
            if(opt.isEmpty()) return false;

            if(entry.getPrice() * countBuy > entry.getEntrySellerType().getMoney(player, entry)) return false;

            Advancement value = opt.get();

            for (String s : value.getCriteria().keySet()) {
                serverPlayer.getAdvancements().award(value, s);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onSell(Player player, ShopEntry entry, int countBuy) {
        if(player instanceof ServerPlayer serverPlayer) {
            Optional<Advancement> opt = getAdvancement(player, advancement);
            if(opt.isEmpty()) return false;

            Advancement value = opt.get();

            for (String s : value.getCriteria().keySet()) {
                serverPlayer.getAdvancements().revoke(value, s);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean canExecute(Player player, ShopEntry entry, int countBuy) {
        if(entry.getType().isBuy()) {
            double money = entry.getEntrySellerType().getMoney(player, entry);
            return !hasAdvancement(player, advancement) && entry.getPrice() * countBuy <= money;
        } else {
            return hasAdvancement(player, advancement);
        }
    }

    @Override
    public int howMany(Player player, ShopEntry entry) {
        if(entry.getType().isSell())
            return hasAdvancement(player, advancement) ? 1 : 0;
        else {
            if(entry.getPrice() > entry.getEntrySellerType().getMoney(player, entry)) return 0;

            return hasAdvancement(player, advancement) ? 0 : 1;
        }

    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.creator.type.advancement");
    }

    @Override
    public List<Component> getDescriptionForContextMenu() {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("sdm.shop.entry.creator.type.advancement.description"));
        return list;
    }

    @Override
    public void getConfig(ConfigGroup group) {
        group.addEnum("advancement", this.advancement, (v) -> this.advancement = v,
                NameMap.of(KnownServerRegistries.client.advancements.keySet().iterator().next(),
                        KnownServerRegistries.client.advancements.keySet()
                                .toArray(new ResourceLocation[0]))
                                .icon((resourceLocation) ->
                                        ItemIcon.getItemIcon(KnownServerRegistries.client.advancements.get(resourceLocation).icon))
                                            .name((resourceLocation) -> KnownServerRegistries.client.advancements
                                                    .get(resourceLocation).name).create())
                .setNameKey("ftbquests.reward.ftbquests.advancement");

        group.addBool("useIconFromAdvancement", this.useIconFromAdvancement, (v) -> this.useIconFromAdvancement = v, true);
    }

    @Override
    public String getId() {
        return "advancementType";
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean isSearch(String search) {
        Optional<Advancement> opt = getAdvancement(Minecraft.getInstance().player, advancement);
        return opt.map(value -> value.getChatComponent().getString().contains(search)).orElse(false);
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("advancement", this.advancement.toString());
        nbt.putBoolean("useIconFromAdvancement", useIconFromAdvancement);

        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        this.advancement = new ResourceLocation(nbt.getString("advancement"));
        this.useIconFromAdvancement = nbt.getBoolean("useIconFromAdvancement");
    }

    @Override
    public void addEntryTooltip(TooltipList list, ShopEntry entry) {
        list.add(Component.translatable("sdm.shop.entry.info.advancement",
                KnownServerRegistries.client.advancements.get(this.advancement).name.copy().withStyle(ChatFormatting.GREEN))
        );
    }

    @Override
    public @Nullable Icon getCustomIcon(ShopEntry entry, int tick) {
        if(useIconFromAdvancement)
            return ItemIcon.getItemIcon(KnownServerRegistries.client.advancements.get(this.advancement).icon);
        return null;
    }

    public static boolean hasAdvancement(Player player, ResourceLocation id) {
        if(player.isLocalPlayer()) {
            return Minecraft.getInstance().getConnection().getAdvancements().getAdvancements().get(id) != null;
        }

        if(player instanceof ServerPlayer serverPlayer) {

            Advancement adv =serverPlayer.server.getAdvancements().getAdvancement(id);
            if(adv == null) return false;

            return serverPlayer.getAdvancements().getOrStartProgress(adv).isDone();
        }

        return false;
    }

    public static Optional<Advancement> getAdvancement(Player player, ResourceLocation id) {
        if(player.isLocalPlayer()) {
            return Optional.ofNullable(Minecraft.getInstance().getConnection().getAdvancements().getAdvancements().get(id));
        }

        if(player instanceof ServerPlayer serverPlayer) {
            return Optional.ofNullable(serverPlayer.server.getAdvancements().getAdvancement(id));
        }

        return Optional.empty();
    }

    @Override
    public EntryTypeProperty getProperty() {
        return EntryTypeProperty.DEFAULT;
    }

    @Override
    public Icon getCreativeIcon() {
        return ItemIcon.getItemIcon(Items.WHEAT);
    }
}
