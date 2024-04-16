package net.sdm.sdmshopr;

import com.mojang.logging.LogUtils;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.client.KnownClientPlayer;
import dev.ftb.mods.ftbteams.data.ClientTeamManagerImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.sdm.sdmshopr.api.ConditionRegister;
import net.sdm.sdmshopr.converter.ConverterOldShopData;
import net.sdm.sdmshopr.network.SDMShopNetwork;
import net.sdm.sdmshopr.network.SyncShop;
import net.sdm.sdmshopr.network.UpdateEditMode;
import net.sdm.sdmshopr.network.UpdateMoney;
import net.sdm.sdmshopr.api.EntryTypeRegister;
import net.sdm.sdmshopr.shop.Shop;
import org.slf4j.Logger;


import java.nio.file.Path;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SDMShopR.MODID)
public class SDMShopR {

    public static final String MODID = "sdmshop";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static Path getFile() {
        return FMLPaths.CONFIGDIR.get().resolve("sdmshop.snbt");
    }


    public SDMShopR() {
        SDMShopNetwork.init();
        SDMShopRIntegration.init();
        EntryTypeRegister.init();
        ConditionRegister.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        Config.init(FMLPaths.CONFIGDIR.get().resolve(SDMShopR.MODID + "-client.toml"));

        DistExecutor.safeRunForDist(() -> SDMShopRClient::new, () -> SDMShopRCommon::new).preInit();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(SDMShopRClient.class);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

    }


    private void registerCommands(RegisterCommandsEvent event) {
        SDMShopCommands.registerCommands(event.getDispatcher());
    }
    private void commonSetup(final FMLCommonSetupEvent event) {}


    @SubscribeEvent
    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event){
        if(event.getEntity().level().isClientSide) return;

        if(event.getEntity() instanceof ServerPlayer player && Shop.SERVER != null) {
            new SyncShop(Shop.SERVER.serializeNBT()).sendTo(player);
        }
    }

    @SubscribeEvent
    public void onLevelSavedEvent(LevelEvent.Save event){
        if(event.getLevel() instanceof Level && Shop.SERVER != null && Shop.SERVER.needSave && !event.getLevel().isClientSide() && ((Level) event.getLevel()).dimension() == Level.OVERWORLD){
            Shop.SERVER.needSave = false;
            SNBT.write(getFile(), Shop.SERVER.serializeNBT());
        }
    }

    @SubscribeEvent
    public void onWorldLoaded(LevelEvent.Load event) {
        if (event.getLevel() instanceof Level && !event.getLevel().isClientSide() && ((Level) event.getLevel()).dimension() == Level.OVERWORLD) {
            Shop.SERVER = new Shop();
            Shop.SERVER.needSave();


            CompoundTag nbt = SNBT.read(getFile());

            CompoundTag data =  ConverterOldShopData.convertToNewData();
            if(data != null) {
                Shop.SERVER.deserializeNBT(data);
                Shop.SERVER.needSave();
                return;
            }

            if (nbt != null) {
                Shop.SERVER.deserializeNBT(nbt);

            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }
    }

    public static long getMoney(Player player) {
        Team team = FTBTeamsAPI.api().getManager().getPlayerTeamForPlayerID(player.getUUID()).get();
        if(team != null)
            return team.getExtraData().getLong("Money");
        return 0;
    }

    public static void setMoney(ServerPlayer player, long money) {
        Team team = FTBTeamsAPI.api().getManager().getPlayerTeamForPlayerID(player.getUUID()).get();
        if(team != null) {
            if (money != team.getExtraData().getLong("Money")) {
                team.getExtraData().putLong("Money", money);
                team.markDirty();
                new UpdateMoney(player.getUUID(), money).sendToAll(player.server);
            }
        }
    }

    public static void addMoney(ServerPlayer player, long money) {
        Team team = FTBTeamsAPI.api().getManager().getPlayerTeamForPlayerID(player.getUUID()).get();
        if(team != null) {
            long balance = team.getExtraData().getLong("Money");
            long current = balance + money;
            team.getExtraData().putLong("Money", current);
            team.markDirty();
            new UpdateMoney(player.getUUID(), current).sendToAll(player.server);
        }
    }

    public static void setEditMode(KnownClientPlayer player, boolean value){
        player.extraData().putBoolean("sdm_edit_mobe", value);
    }
    public static void setEditMode(ServerPlayer player, boolean value){
        Team team = FTBTeamsAPI.api().getManager().getPlayerTeamForPlayerID(player.getUUID()).get();
        if(team != null){
            team.getExtraData().putBoolean("sdm_edit_mobe", value);
            team.markDirty();
            new UpdateEditMode(player.getUUID(), value).sendToAll(player.server);
        }
    }

    public static long getMoney(KnownClientPlayer player) {
        return player.extraData().getLong("Money");
    }

    public static void setMoney(KnownClientPlayer player, long money) {
        player.extraData().putLong("Money", money);
    }

    public static void addMoney(KnownClientPlayer player, long money) {
        player.extraData().putLong("Money", player.extraData().getLong("Money") + money);
    }

    public static long getClientMoney() {
        return ClientTeamManagerImpl.getInstance().getKnownPlayer(Minecraft.getInstance().player.getUUID()).get().extraData().getLong("Money");
    }

    public static boolean isEditModeClient(){
        return ClientTeamManagerImpl.getInstance().getKnownPlayer(Minecraft.getInstance().player.getUUID()).get().extraData().getBoolean("sdm_edit_mobe");
    }

    public static boolean isEditMode(Player player) {
        Team team = FTBTeamsAPI.api().getManager().getPlayerTeamForPlayerID(player.getUUID()).get();
        if(team != null)
            return team.getExtraData().getBoolean("sdm_edit_mobe");
        return false;
    }

    public static String moneyString(long money) {
        return String.format("◎ %,d", money);
    }
}
