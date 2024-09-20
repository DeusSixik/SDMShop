package net.sixik.sdmshoprework.forge.shop.type;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import net.sixik.sdmshoprework.SDMShopR;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntryType;
import net.sixik.sdmshoprework.common.integration.FTBQuests.ConfigIconItemStack;
import net.sixik.sdmshoprework.common.register.CustomIconItem;
import net.sixik.sdmshoprework.common.register.ItemsRegister;
import net.sixik.sdmshoprework.common.utils.SDMItemHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShopTagEntryType extends AbstractShopEntryType {


    public static ResourceLocation DEFAULT = new ResourceLocation("minecraft", "anvil");

    public ResourceLocation tagKey;
    public ItemStack iconPath;


    protected ShopTagEntryType(ResourceLocation tagKey){
        this.tagKey = tagKey;
        this.iconPath = Items.BLACK_BANNER.getDefaultInstance();
    }

    protected ShopTagEntryType(ResourceLocation tagKey, ItemStack iconPath){
        this.tagKey = tagKey;
        this.iconPath = iconPath;
    }

    public static ShopTagEntryType of(ResourceLocation tagKey){
        return new ShopTagEntryType(tagKey);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void getConfig(ConfigGroup group) {
        group.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());
        group.addEnum("tags", tagKey.toString(), v -> tagKey = new ResourceLocation(v), getTags());
    }

    @Override
    public Icon getCreativeIcon() {
        return ItemIcon.getItemIcon(Items.NAME_TAG);
    }

    public NameMap<String> getTags(){
        List<String> str = new ArrayList<>();

        ForgeRegistries.ITEMS.tags().getTagNames().forEach(s -> {
            str.add(s.location().toString());
        });
        return NameMap.of(DEFAULT.toString(), str).create();
    }

    @Override
    public AbstractShopEntryType copy() {
        return new ShopTagEntryType(tagKey, iconPath);
    }

    @Override
    public Component getTranslatableForCreativeMenu() {
        return Component.translatable("sdm.shop.entry.add.context.itemtag");
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putString("tagKey", tagKey.toString());
        nbt.put("iconPath", iconPath.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.tagKey = new ResourceLocation(nbt.getString("tagKey"));
        this.iconPath = ItemStack.of(nbt.getCompound("iconPath"));
    }

    @Override
    public String getId() {
        return "itemTag";
    }

    @Override
    public void sell(Player player, int countSell, AbstractShopEntry entry) {
        List<ItemStack> stackList = new ArrayList<>();

        @NotNull ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(TagKey.create(Registries.ITEM, tagKey));


        for (int index = 0; index < player.getInventory().getContainerSize(); index++) {
            if(player.getInventory().getItem(index).is(tag.getKey())){
                stackList.add(player.getInventory().getItem(index));
            }
        }

        int amountItems = 0;
        for (ItemStack item : stackList){
            amountItems += item.getCount();
        }

        int amount = amountItems >= entry.entryCount * countSell ? entry.entryCount * countSell : 0;
        if(amountItems == 0 || amount == 0) return;

        if (amount <= 0) return;
        if(SDMItemHelper.sellItem(player, amount, tag.getKey())) SDMShopR.addMoney(player, entry.entryPrice * (countSell));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canExecute(Player player, boolean isSell, int countSell, AbstractShopEntry entry) {
        if(isSell){
            @NotNull ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(TagKey.create(Registries.ITEM, tagKey));
            int countItems = 0;
            Inventory inventory = Minecraft.getInstance().player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if(inventory.getItem(i).is(tag.getKey())) {
                    countItems += inventory.getItem(i).getCount();
                }
            }
            if(countItems < (entry.entryCount * countSell)) return false;
            return true;
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int howMany(Player player, boolean isSell, AbstractShopEntry entry) {
        if(isSell) {
            int countItems = 0;
            Inventory inventory = Minecraft.getInstance().player.getInventory();
            @NotNull ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(TagKey.create(Registries.ITEM, tagKey));


            if(entry.entryPrice == 0) return Byte.MAX_VALUE;
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if(inventory.getItem(i).is(tag.getKey())) {
                    countItems += inventory.getItem(i).getCount();
                }
            }
            return countItems / entry.entryCount;
        }

        return 0;
    }

    @Override
    public SellType getSellType() {
        return SellType.ONLY_SELL;
    }

    @Override
    public Icon getIcon() {
        if(iconPath.is(ItemsRegister.CUSTOM_ICON.get())){
            return CustomIconItem.getIcon(iconPath);
        }
        return ItemIcon.getItemIcon(iconPath);
    }



    public static class Constructor implements IConstructor<AbstractShopEntryType> {
        @Override
        public AbstractShopEntryType createDefaultInstance() {
            return new ShopTagEntryType(ShopTagEntryType.DEFAULT);
        }
    }
}
