package net.sixik.sdmshop.shop.seller_types;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.WidgetType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.currencies.BaseCurrency;
import net.sixik.sdmshop.currencies.SDMCoin;
import net.sixik.sdmshop.old_api.shop.AbstractEntrySellerType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MoneySellerType extends AbstractEntrySellerType<Double> {

    public static final String DEFAULT_MONEY = SDMCoin.getId();

    protected String money_id;

    public MoneySellerType() {
        this(0D);
    }

    public MoneySellerType(Double objectType) {
        super(objectType);
        this.money_id = DEFAULT_MONEY;
    }

    @Override
    public void _getConfig(ConfigGroup configGroup) {
        configGroup.addEnum("money_id", money_id, (s) -> money_id = s, getList());
    }

    @Override
    public boolean onBuy(Player player, ShopEntry shopEntry, long countSell) {
        double value = ShopUtils.getMoney(player, money_id);
        return ShopUtils.setMoney(player, money_id,  value - (shopEntry.getPrice() * countSell));
    }

    @Override
    public boolean onSell(Player player, ShopEntry shopEntry, long countSell) {
        double value = ShopUtils.getMoney(player, money_id);
        return ShopUtils.setMoney(player, money_id, value + shopEntry.getPrice() * countSell);
    }

    @Override
    public double getMoney(Player player, ShopEntry shopEntry) {
        return ShopUtils.getMoney(player, money_id);
    }

    @Override
    public AbstractEntrySellerType<Double> copy() {
        return new MoneySellerType(objectType);
    }

    @Override
    public String getId() {
        return "money_seller";
    }

    @Override
    public String getEnumName() {
        return "MONEY";
    }

    @Override
    public String moneyToString(ShopEntry entry) {
        return entry.getPrice() + " " + EconomyAPI.getAllCurrency().value.currencies.stream().filter(s -> s.getName().equals(money_id)).findFirst().get().symbol.value;
    }


    @Override
    public CompoundTag _serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("money_id", money_id);

        return nbt;
    }

    @Override
    public void _deserialize(CompoundTag tag) {
        this.money_id = tag.getString("money_id");
    }

    public static NameMap<String> getList(){
        List<String> str = new ArrayList<>();

        for (BaseCurrency currency : EconomyAPI.getAllCurrency().value.currencies) {
            str.add(currency.getName());
        }

        return NameMap.of(DEFAULT_MONEY, str).create();
    }


    @Override
    public int getRenderWight(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, double count, @Nullable Widget widget, int additionSize) {
        return theme.getStringWidth(ShopUtils.moneyToString(count, money_id));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, double count, @Nullable Widget widget, int additionSize) {
        theme.drawString(graphics, ShopUtils.moneyToString(count, money_id), x + 2, y + 1, theme.getContentColor(WidgetType.NORMAL), 2);
    }
}
