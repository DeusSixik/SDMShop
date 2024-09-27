package net.sixik.sdmshoprework.common.shop;

import dev.ftb.mods.ftblibrary.snbt.SNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshoprework.SDMShopPaths;
import net.sixik.sdmshoprework.api.INBTSerializable;
import net.sixik.sdmshoprework.common.serializer.SerializerControl;
import net.sixik.sdmshoprework.network.client.SyncShopS2C;

import java.util.*;
import java.util.concurrent.Future;

public class ShopBase implements INBTSerializable<CompoundTag> {

    public static ShopBase SERVER;
    public static ShopBase CLIENT = new ShopBase();

    private final LinkedList<Runnable> saveTasks = new LinkedList<Runnable>();
    private final LinkedList<Runnable> deserializeTask = new LinkedList<Runnable>();

    private final List<ShopTab> shopTabs = new ArrayList<>();


    public List<ShopTab> getShopTabs() {
        return shopTabs;
    }

    public ShopTab getShopTab(UUID uuid){
        for (ShopTab shopTab : shopTabs) {
            if(Objects.equals(shopTab.shopTabUUID, uuid))
                return shopTab;
        }
        return null;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        SerializerControl.serializeVersion(nbt);

        ListTag tagShopTabs = new ListTag();
        for (ShopTab shopTab : shopTabs) {
            tagShopTabs.add(shopTab.serializeNBT());
        }
        nbt.put("shopTabs", tagShopTabs);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        Runnable runnable = () -> {
            if (SerializerControl.isOldVersion(nbt)) {
                SerializerControl.deserializeVersion(nbt, this);
            } else {
                shopTabs.clear();
                ListTag tagShopTabs = nbt.getList("shopTabs", 10);
                for (int i = 0; i < tagShopTabs.size(); i++) {
                    ShopTab tab = new ShopTab(this);
                    tab.deserializeNBT(tagShopTabs.getCompound(i));
                    shopTabs.add(tab);
                }
            }
        };

        deserializeTask.add(runnable);

        Iterator<Runnable> runnableIterator = deserializeTask.iterator();
        if(runnableIterator.hasNext()) {
            runnableIterator.next().run();
            runnableIterator.remove();
        }
    }

    public void saveShopToFile() {

        Runnable runnable = () -> {
            try {
                SNBT.write(SDMShopPaths.getFile(), serializeNBT());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        saveTasks.add(runnable);

        Iterator<Runnable> runnableIterator = saveTasks.iterator();

        while (runnableIterator.hasNext()) {
            runnableIterator.next().run();
            runnableIterator.remove();
        }
//        if(ServerLifecycleHooks.getCurrentServer() != null) {
//            syncShop(ServerLifecycleHooks.getCurrentServer());
//        }
    }

    public void syncShop(MinecraftServer server) {
        new SyncShopS2C(serializeNBT()).sendToAll(server);
    }

    public void syncShop(ServerPlayer player) {
        new SyncShopS2C(serializeNBT()).sendTo(player);
    }
}
