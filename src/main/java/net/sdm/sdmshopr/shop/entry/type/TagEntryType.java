package net.sdm.sdmshopr.shop.entry.type;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import dev.ftb.mods.ftbquests.client.ConfigIconItemStack;
import dev.ftb.mods.ftbquests.item.CustomIconItem;
import dev.ftb.mods.ftbquests.item.FTBQuestsItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import net.sdm.sdmshopr.SDMShopR;
import net.sdm.sdmshopr.api.IEntryType;
import net.sdm.sdmshopr.shop.entry.ShopEntry;
import net.sdm.sdmshopr.utils.NBTUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TagEntryType implements IEntryType {

    public static ResourceLocation DEFAULT = new ResourceLocation("minecraft", "anvil");

    public ResourceLocation tagKey;
    public ItemStack iconPath;

    protected TagEntryType(ResourceLocation tagKey){
        this.tagKey = tagKey;
        this.iconPath = Items.BLACK_BANNER.getDefaultInstance();
    }

    protected TagEntryType(ResourceLocation tagKey, ItemStack iconPath){
        this.tagKey = tagKey;
        this.iconPath = iconPath;
    }

    public static TagEntryType of(ResourceLocation tagKey){
        return new TagEntryType(tagKey);
    }


    //        @NotNull ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(TagKey.create(Registries.ITEM, tagKey));


    @Override
    public boolean isOnlySell() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canExecute(boolean isSell, int countSell, ShopEntry<?> entry) {
        if(isSell){
            @NotNull ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(TagKey.create(Registries.ITEM, tagKey));
            int countItems = 0;
            Inventory inventory = Minecraft.getInstance().player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if(inventory.getItem(i).is(tag.getKey())) {
                    countItems += inventory.getItem(i).getCount();
                }
            }
            if(countItems < (entry.count * countSell)) return false;
            return true;
        }
        return false;
    }

    @Override
    public void sell(ServerPlayer player, int countSell, ShopEntry<?> entry) {
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

        int amount = amountItems >= entry.count * countSell ? entry.count * countSell : 0;
        if(amountItems == 0 || amount == 0) return;

        if (amount <= 0) return;
        if(this.sellItem(player, amount, tag.getKey())) SDMShopR.addMoney(player, entry.price * (countSell));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int howMany(boolean isSell, ShopEntry<?> entry) {
        if(isSell) {
            int countItems = 0;
            Inventory inventory = Minecraft.getInstance().player.getInventory();
            @NotNull ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(TagKey.create(Registries.ITEM, tagKey));

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if(inventory.getItem(i).is(tag.getKey())) {
                    countItems += inventory.getItem(i).getCount();
                }
            }
            return countItems / entry.count;
        }

        return 0;
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
        if(iconPath.is(FTBQuestsItems.CUSTOM_ICON.get())){
            return CustomIconItem.getIcon(iconPath);
        }
        return ItemIcon.getItemIcon(iconPath);
    }


    @Override
    public void getConfig(ConfigGroup group) {
        group.add("iconPath", new ConfigIconItemStack(), iconPath, v -> iconPath = v, Items.BARRIER.getDefaultInstance());
        group.addEnum("tags", tagKey.toString(), v -> tagKey = new ResourceLocation(v), getTags());
    }

    public NameMap<String> getTags(){
        List<String> str = new ArrayList<>();

        ForgeRegistries.ITEMS.tags().getTagNames().forEach(s -> {
            str.add(s.location().toString());
        });
        return NameMap.<String>of(DEFAULT.toString(), str).create();
    }

    @Override
    public Icon getCreativeIcon() {
        return ItemIcon.getItemIcon(Items.NAME_TAG);
    }

    @Override
    public String getID() {
        return "itemTag";
    }

    @Override
    public IEntryType copy() {
        return new TagEntryType(tagKey, iconPath);
    }

    @Override
    public Component getTranslatableForContextMenu() {
        return Component.translatable("sdm.shop.entry.add.context.itemtag");
    }

    @Override
    public CompoundTag serializeNBT() {
        SNBTCompoundTag data = SNBTCompoundTag.of(IEntryType.super.serializeNBT());
        data.putString("tagKey", tagKey.toString());
        NBTUtils.putItemStack(data, "iconPath", iconPath);
        return data;
    }

    @Override
    public void deserializeNBT(CompoundTag data) {
        this.tagKey = new ResourceLocation(data.getString("tagKey"));
        this.iconPath = NBTUtils.getItemStack(data, "iconPath");
    }


    public static boolean sellItem(ServerPlayer p, int amm, TagKey<Item> item) {
        int totalamm = 0; //общее количество вещей в инвентаре
        for (int a = 0; a<p.getInventory().getContainerSize(); a++) { //считаем эти вещи
            if (p.getInventory().getItem(a)!=null){
                /*весь ItemStack можно описать тремя параметрами. item.getData, item.getItemMeta и item.getAmmaount.
                 *При item.equas(item2)ammount тоже сравнивается, поэтому видим такое сравнение
                 */
                if (p.getInventory().getItem(a).is(item)){
                    totalamm += p.getInventory().getItem(a).getCount();
                }
            }
        }
        if (totalamm==0) {
            return false;
        }
        if (totalamm<amm) {
            return false;
        }
        int ammountleft =amm; //эта переменная не очень нужна, но мне с ней удобнее
        for (int a = 0; a<p.getInventory().getContainerSize(); a++) {
            if (ammountleft==0){return true;}
            if (p.getInventory().getItem(a)==null) continue;
            if (p.getInventory().getItem(a).is(item)) {
                if (p.getInventory().getItem(a).getCount()<ammountleft) {
                    ammountleft-=p.getInventory().getItem(a).getCount();
                    p.getInventory().setItem(a, ItemStack.EMPTY);
                }
                if (p.getInventory().getItem(a)!=null&&p.getInventory().getItem(a).getCount()==ammountleft) {
                    p.getInventory().setItem(a, ItemStack.EMPTY);
                    return true;
                }

                if (p.getInventory().getItem(a).getCount()>ammountleft&&p.getInventory().getItem(a)!=null) {
                    p.getInventory().getItem(a).setCount(p.getInventory().getItem(a).getCount()-ammountleft);
                    return true;
                }
            }
        }
        return false;
    }
}
