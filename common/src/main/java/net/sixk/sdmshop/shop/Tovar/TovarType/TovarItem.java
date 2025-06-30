package net.sixk.sdmshop.shop.Tovar.TovarType;

import com.mojang.datafixers.util.Pair;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmcore.impl.utils.serializer.SDMSerializerHelper;
import net.sixik.sdmcore.impl.utils.serializer.data.IData;
import net.sixik.sdmcore.impl.utils.serializer.data.KeyData;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixk.sdmshop.shop.Tovar.AbstractTovar;
import net.sixk.sdmshop.utils.item.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class TovarItem extends AbstractTovar {
    public ItemStack item = ItemStack.EMPTY;
    public boolean byTag;
    public TagKey tag;

    public TovarItem(UUID uuid, Icon icon, String tab, String currency, Integer cost, long limit, boolean toSell,ItemStack item, boolean byTag, TagKey tag) {
        super(uuid, icon, tab, currency, cost, limit, toSell);
        this.item = item;
        this.byTag = byTag;
        this.tag = tag;
    }

    public TovarItem(UUID uuid, Icon icon, String tab, String currency, Integer cost, long limit, boolean toSell) {
        super(uuid, icon, tab, currency, cost, limit, toSell);
    }

    public void buy(Player player, AbstractTovar tovar, long count) {
        long currency = (EconomyAPI.getPlayerCurrencyServerData().getBalance(player, tovar.currency).value).longValue();
        if ((tovar.limit >= count || tovar.limit == -1L) && currency >= (long)tovar.cost * count) {
            EconomyAPI.getPlayerCurrencyServerData().addCurrencyValue(player, tovar.currency, (double)(-((long)tovar.cost * count)));

            for(int w = 0; (long)w < count; ++w) {
                ItemHandlerHelper.giveItemToPlayer(player, this.item.copy());
            }

            if (tovar.limit != -1L) {
                tovar.limit -= count;
            }

        }
    }

    public void sell(Player player, AbstractTovar tovar, long count) {
        if (!this.byTag) {
            if (tovar.limit < count && tovar.limit != -1L) {
                return;
            }

            EconomyAPI.getPlayerCurrencyServerData().addCurrencyValue(player, tovar.currency, (double)((long)tovar.cost * count));
            sellItem(player, (int)(count * (long)this.item.getCount()), this.item);
            if (tovar.limit != -1L) {
                tovar.limit -= count;
            }
        } else {
            List<ItemStack> stackList = new ArrayList<>();

            int amountItems;
            for(amountItems = 0; amountItems < player.getInventory().getContainerSize(); ++amountItems) {
                if (player.getInventory().getItem(amountItems).is(tag)) {
                    stackList.add(player.getInventory().getItem(amountItems));
                }
            }

            amountItems = 0;

            Iterator<ItemStack> var7 = stackList.iterator();

            while(true) {
                if (!var7.hasNext()) {
                    int amount = (long)amountItems >= (long)this.item.getCount() * count ? (int)((long)this.item.getCount() * count) : 0;
                    if (amountItems == 0 || amount == 0) {
                        return;
                    }

                    if (tovar.limit < count && tovar.limit != -1L) {
                        return;
                    }

                    if (amount <= 0) {
                        return;
                    }

                    if (sellItem(player, amount,tag)) {
                        EconomyAPI.getPlayerCurrencyServerData().addCurrencyValue(player, tovar.currency, (double)((long)tovar.cost * count));
                    }

                    if (tovar.limit != -1L) {
                        tovar.limit -= count;
                    }
                    break;
                }

                ItemStack item = (ItemStack)var7.next();
                amountItems += item.getCount();
            }
        }

    }

    public String getTitel() {
        return this.item.getDisplayName().getString().replace("[", "").replace("]", "");
    }

    public static boolean sellItem(Player p, int amm, ItemStack item) {
        int totalamm = 0;

        int ammountleft;
        for(ammountleft = 0; ammountleft < p.getInventory().getContainerSize(); ++ammountleft) {
            p.getInventory().getItem(ammountleft);
            if (ItemStack.isSameItem(p.getInventory().getItem(ammountleft), item) && ItemStack.isSameItemSameComponents(item, p.getInventory().getItem(ammountleft))) {
                totalamm += p.getInventory().getItem(ammountleft).getCount();
            }
        }

        if (totalamm == 0) {
            return false;
        } else if (totalamm < amm) {
            return false;
        } else {
            ammountleft = amm;

            for(int a = 0; a < p.getInventory().getContainerSize(); ++a) {
                if (ammountleft == 0) {
                    return true;
                }

                p.getInventory().getItem(a);
                if (ItemStack.isSameItem(p.getInventory().getItem(a), item) && ItemStack.isSameItemSameComponents(item, p.getInventory().getItem(a))) {
                    if (p.getInventory().getItem(a).getCount() < ammountleft) {
                        ammountleft -= p.getInventory().getItem(a).getCount();
                        p.getInventory().setItem(a, ItemStack.EMPTY);
                    }

                    p.getInventory().getItem(a);
                    if (p.getInventory().getItem(a).getCount() == ammountleft) {
                        p.getInventory().setItem(a, ItemStack.EMPTY);
                        return true;
                    }

                    if (p.getInventory().getItem(a).getCount() > ammountleft) {
                        p.getInventory().getItem(a);
                        p.getInventory().getItem(a).setCount(p.getInventory().getItem(a).getCount() - ammountleft);
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static boolean sellItem(Player p, int amm, TagKey<Item> item) {
        int totalamm = 0;

        int ammountleft;
        for(ammountleft = 0; ammountleft < p.getInventory().getContainerSize(); ++ammountleft) {
            p.getInventory().getItem(ammountleft);
            if (p.getInventory().getItem(ammountleft).is(item)) {
                totalamm += p.getInventory().getItem(ammountleft).getCount();
            }
        }

        if (totalamm == 0) {
            return false;
        } else if (totalamm < amm) {
            return false;
        } else {
            ammountleft = amm;

            for(int a = 0; a < p.getInventory().getContainerSize(); ++a) {
                if (ammountleft == 0) {
                    return true;
                }

                p.getInventory().getItem(a);
                if (p.getInventory().getItem(a).is(item)) {
                    if (p.getInventory().getItem(a).getCount() < ammountleft) {
                        ammountleft -= p.getInventory().getItem(a).getCount();
                        p.getInventory().setItem(a, ItemStack.EMPTY);
                    }

                    p.getInventory().getItem(a);
                    if (p.getInventory().getItem(a).getCount() == ammountleft) {
                        p.getInventory().setItem(a, ItemStack.EMPTY);
                        return true;
                    }

                    if (p.getInventory().getItem(a).getCount() > ammountleft) {
                        p.getInventory().getItem(a);
                        p.getInventory().getItem(a).setCount(p.getInventory().getItem(a).getCount() - ammountleft);
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public Icon getIcon() {
        return ItemIcon.getItemIcon(this.item);
    }

    public ItemStack getItemStack() {
        return this.item;
    }

    public TagKey getTag() {
        return this.tag;
    }

    public AbstractTovar copy() {
        return null;
    }

    public String getID() {
        return "ItemType";
    }

    public boolean getisXPLVL() {
        return this.byTag;
    }

    public KeyData serialize(HolderLookup.Provider provider) {
        KeyData data = super.serialize(provider);

        SDMSerializerHelper.serializeItemStack(data, "item", this.item, provider);
        data.put("id", this.getID());
        if (this.tag != null) {
            data.put("tag", this.tag.location().toString());
        }

        data.put("byTag", IData.valueOf(this.byTag ? 1 : 0));
        return data;
    }

    public void deserialize(KeyData data, HolderLookup.Provider provider) {
        if (data.contains("tag")) {
            String t1 = data.getData("tag").asString();

            for (Pair<TagKey<Item>, HolderSet.Named<Item>> tagKeyNamedPair : BuiltInRegistries.ITEM.getTags().toList()) {
                if ((tagKeyNamedPair.getFirst()).location().toString().equals(t1)) {
                    this.tag = tagKeyNamedPair.getFirst();
                    break;
                }
            }
        }

        this.item = SDMSerializerHelper.deserializeItemStack(data, "item", provider);
        this.byTag = data.getData("byTag").asInt() == 1;
    }

}
