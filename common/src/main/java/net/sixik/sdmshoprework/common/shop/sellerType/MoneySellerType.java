package net.sixik.sdmshoprework.common.shop.sellerType;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.WidgetType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshoprework.SDMShopR;
import net.sixik.sdmshoprework.SDMShopRework;
import net.sixik.sdmshoprework.api.IConstructor;
import net.sixik.sdmshoprework.api.shop.AbstractShopSellerType;
import net.sixik.sdmshoprework.api.shop.AbstractShopEntry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MoneySellerType extends AbstractShopSellerType<Long> {

    public static final String DEFAULT = "base_money";

    public String moneyID = DEFAULT;

    public MoneySellerType() {
        super(0L);
    }

    public MoneySellerType(Long type) {
        super(type);
    }

    @Override
    public long getCount(Player player) {
        return SDMShopR.getMoney(player);
    }

    @Override
    public boolean hasConfig() {
        return true;
    }

    @Override
    public void getConfig(ConfigGroup configGroup) {
        configGroup.addEnum("money_id", moneyID, (s) -> moneyID = s, getList());
    }

    @Override
    public String getEnumName() {
        return "MONEY";
    }

    @Override
    public boolean buy(Player player, AbstractShopEntry shopEntry, long countSell) {
        SDMShopR.addMoney(player, countSell);
        return true;
    }

    @Override
    public String getId() {
        return "money";
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putString("moneyID", moneyID);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("moneyID")) this.moneyID = nbt.getString("moneyID");
    }

    public NameMap<String> getList(){
        List<String> str = new ArrayList<>();
        str.add(DEFAULT);
        return NameMap.of(DEFAULT.toString(), str).create();
    }

    @Override
    public void draw(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, long count, @Nullable Widget widget, int additionSize) {
        theme.drawString(graphics, SDMShopRework.moneyString(count), x, y + 1, theme.getContentColor(WidgetType.NORMAL), 2);
    }

    public static class Constructor implements IConstructor<AbstractShopSellerType<?>> {
        @Override
        public AbstractShopSellerType<?> createDefaultInstance() {
            return new MoneySellerType(0L);
        }
    }
}
