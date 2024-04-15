package net.sdm.sdmshopr;

import com.mojang.logging.LogUtils;
import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.data.ClientTeamManager;
import dev.ftb.mods.ftbteams.data.KnownClientPlayer;
import dev.ftb.mods.ftbteams.data.Team;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
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
import net.sdm.sdmshopr.events.ModEvents;
import net.sdm.sdmshopr.network.SDMShopNetwork;
import net.sdm.sdmshopr.network.UpdateEditMode;
import net.sdm.sdmshopr.network.UpdateMoney;
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

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        Config.init(FMLPaths.CONFIGDIR.get().resolve(SDMShopR.MODID + "-client.toml"));

        DistExecutor.safeRunForDist(() -> SDMShopRClient::new, () -> SDMShopRCommon::new).preInit();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(SDMShopRClient.class);
        MinecraftForge.EVENT_BUS.register(ModEvents.class);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

    }



    private void registerCommands(RegisterCommandsEvent event) {
        SDMShopCommands.registerCommands(event.getDispatcher());
    }
    private void commonSetup(final FMLCommonSetupEvent event) {}
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    public static long getMoney(Player player) {
        Team team = FTBTeamsAPI.getManager().getPlayerTeam(player.getUUID());
        if(team != null)
            return team.getExtraData().getLong("Money");
        return 0;
    }

    public static void setMoney(ServerPlayer player, long money) {
        Team team = FTBTeamsAPI.getManager().getPlayerTeam(player.getUUID());
        if(team != null) {
            if (money != team.getExtraData().getLong("Money")) {
                team.getExtraData().putLong("Money", money);
                team.save();
                new UpdateMoney(player.getUUID(), money).sendToAll(player.server);
            }
        }
    }

    public static void addMoney(ServerPlayer player, long money) {
        Team team = FTBTeamsAPI.getManager().getPlayerTeam(player.getUUID());
        if(team != null) {
            long balance = team.getExtraData().getLong("Money");
            long current = balance + money;
            team.getExtraData().putLong("Money", current);
            team.save();
            new UpdateMoney(player.getUUID(), current).sendToAll(player.server);
        }
    }

    public static void setEditMode(KnownClientPlayer player, boolean value){
        player.getExtraData().putBoolean("sdm_edit_mobe", value);
    }
    public static void setEditMode(ServerPlayer player, boolean value){
        Team team = FTBTeamsAPI.getManager().getPlayerTeam(player.getUUID());
        if(team != null){
            team.getExtraData().putBoolean("sdm_edit_mobe", value);
            team.save();
            new UpdateEditMode(player.getUUID(), value).sendToAll(player.server);
        }
    }

    public static long getMoney(KnownClientPlayer player) {
        return player.getExtraData().getLong("Money");
    }

    public static void setMoney(KnownClientPlayer player, long money) {
        player.getExtraData().putLong("Money", money);
    }

    public static void addMoney(KnownClientPlayer player, long money) {
        player.getExtraData().putLong("Money", player.getExtraData().getLong("Money") + money);
    }

    public static long getClientMoney() {
        return ClientTeamManager.INSTANCE.getKnownPlayer(Minecraft.getInstance().player.getUUID()).getExtraData().getLong("Money");
    }

    public static boolean isEditModeClient(){
        return ClientTeamManager.INSTANCE.getKnownPlayer(Minecraft.getInstance().player.getUUID()).getExtraData().getBoolean("sdm_edit_mobe");
    }

    public static boolean isEditMode(Player player) {
        Team team = FTBTeamsAPI.getManager().getPlayerTeam(player.getUUID());
        if(team != null)
            return team.getExtraData().getBoolean("sdm_edit_mobe");
        return false;
    }

    public static String moneyString(long money) {
        return String.format("◎ %,d", money);
    }
}
