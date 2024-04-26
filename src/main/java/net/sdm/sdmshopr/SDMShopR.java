package net.sdm.sdmshopr;

import com.mojang.logging.LogUtils;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.data.ClientTeamManager;
import dev.ftb.mods.ftbteams.data.KnownClientPlayer;
import dev.ftb.mods.ftbteams.data.Team;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
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
import net.sdm.sdmshopr.api.EntryTypeRegister;
import net.sdm.sdmshopr.api.register.ShopEntryButtonsRegister;
import net.sdm.sdmshopr.api.tags.ITag;
import net.sdm.sdmshopr.config.ClientShopData;
import net.sdm.sdmshopr.converter.ConverterOldShopData;
import net.sdm.sdmshopr.events.SDMPlayerEvents;
import net.sdm.sdmshopr.network.mainshop.SDMShopNetwork;
import net.sdm.sdmshopr.network.mainshop.SyncShop;
import net.sdm.sdmshopr.network.mainshop.UpdateEditMode;
import net.sdm.sdmshopr.network.mainshop.UpdateMoney;
import net.sdm.sdmshopr.shop.Shop;
import net.sdm.sdmshopr.tags.TagFileParser;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SDMShopR.MODID)
public class SDMShopR {

    public static final String MODID = "sdmshop";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static Path getModFolder(){
        return FMLPaths.CONFIGDIR.get().resolve("SDMShop");
    }

    public static Path getOldFile(){
        return FMLPaths.CONFIGDIR.get().resolve("sdmshop.snbt");
    }

    public static Path getTagFile(){
        return getModFolder().resolve("customization.json");
    }
    public static Path getFile() {
        return getModFolder().resolve("sdmshop.snbt");
    }
    public static Path getFileClient() {
        return getModFolder().resolve("sdmshop-data-client.snbt");
    }


    public SDMShopR() {
        if(!getModFolder().toFile().exists()){
            getModFolder().toFile().mkdir();
        }

        if(!getTagFile().toFile().exists()) {
            try {
                getTagFile().toFile().createNewFile();
                TagFileParser.writeNewFile();

            } catch (IOException e) {
                LOGGER.error(e.toString());
            }
        }

        SDMShopNetwork.init();
        SDMShopRIntegration.init();
        EntryTypeRegister.init();
        ConditionRegister.init();
        ShopEntryButtonsRegister.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        Config.init(getModFolder().resolve(SDMShopR.MODID + "-client.toml"));

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
        if(event.getPlayer().level.isClientSide) return;

        if(event.getEntity() instanceof ServerPlayer player && Shop.SERVER != null) {
            new SyncShop(Shop.SERVER.serializeNBT()).sendTo(player);
        }
    }

    @SubscribeEvent
    public void onLevelSavedEvent(WorldEvent.Save event){
        if(event.getWorld() instanceof Level && Shop.SERVER != null && Shop.SERVER.needSave && !event.getWorld().isClientSide() && ((Level) event.getWorld()).dimension() == Level.OVERWORLD){
            Shop.SERVER.needSave = false;
            SNBT.write(getFile(), Shop.SERVER.serializeNBT());
        }
    }

    @SubscribeEvent
    public void onWorldLoaded(WorldEvent.Load event) {
        if (event.getWorld() instanceof Level && !event.getWorld().isClientSide() && ((Level) event.getWorld()).dimension() == Level.OVERWORLD) {
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
            } else {
                if(getOldFile().toFile().exists()) {
                    nbt = SNBT.read(getOldFile());
                    if (nbt != null) {
                        getOldFile().toFile().delete();
                        Shop.SERVER.deserializeNBT(nbt);
                        Shop.SERVER.saveToFile();
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        public static ClientShopData creator;
        public static Map<String, ITag> tags = new HashMap<>();

        public static void parse(){
            tags = TagFileParser.getTags();
            for (Map.Entry<String, ITag> stringITagEntry : tags.entrySet()) {
                LOGGER.info(stringITagEntry.getKey());
            }
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            creator = new ClientShopData(getFileClient());
            SNBTCompoundTag d1 = SNBT.read(getFileClient());
            if(d1 != null) {
                creator.deserializeNBT(d1);
            }



            parse();
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
                SDMPlayerEvents.SetMoneyEvent giveMoneyEvent = new SDMPlayerEvents.SetMoneyEvent(player, money, getMoney(player));
                MinecraftForge.EVENT_BUS.post(giveMoneyEvent);

                if(!giveMoneyEvent.isCanceled()) {

                    team.getExtraData().putLong("Money", giveMoneyEvent.getCountMoney());
                    team.save();
                    new UpdateMoney(player.getUUID(), giveMoneyEvent.getCountMoney()).sendToAll(player.server);
                }
            }
        }
    }

    public static void addMoney(ServerPlayer player, long money) {
        Team team = FTBTeamsAPI.getManager().getPlayerTeam(player.getUUID());
        if(team != null) {
            long balance = team.getExtraData().getLong("Money");

            SDMPlayerEvents.AddMoneyEvent event = new SDMPlayerEvents.AddMoneyEvent(player, money, balance);
            MinecraftForge.EVENT_BUS.post(event);

            if(!event.isCanceled()) {
                long current = event.playerMoney + event.countMoney;
                team.getExtraData().putLong("Money", current);
                team.save();
                new UpdateMoney(player.getUUID(), current).sendToAll(player.server);
            }
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

    public static Component getMoneyComponent(String money){
        return new TextComponent(money).withStyle(SDMShopRClient.shopTheme.getMoneyTextColor().toStyle());
    }
}
