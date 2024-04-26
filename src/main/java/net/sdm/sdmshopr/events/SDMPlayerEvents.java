package net.sdm.sdmshopr.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.sdm.sdmshopr.shop.entry.ShopEntry;

public class SDMPlayerEvents {

    @Cancelable
    public static class CustomBuyEvent extends PlayerEvent{
        public long countMoney;
        public long playerMoney;
        public ShopEntry<?> entry;
        public CustomBuyEvent(Player player, long countMoneyGet, long playerMoney, ShopEntry<?> entry) {
            super(player);
            this.countMoney = countMoneyGet;
            this.playerMoney = playerMoney;
            this.entry = entry;
        }
    }

    @Cancelable
    public static class AddMoneyEvent extends PlayerEvent{
        public long countMoney;
        public long playerMoney;
        public AddMoneyEvent(Player player, long countMoneyGet, long playerMoney) {
            super(player);
            this.countMoney = countMoneyGet;
            this.playerMoney = playerMoney;
        }

        public long getCountMoney() {
            return countMoney;
        }

        public long getPlayerMoney() {
            return playerMoney;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    @Cancelable
    public static class SetMoneyEvent extends PlayerEvent {
        public long countMoney;
        public long playerMoney;
        public SetMoneyEvent(Player player, long countMoneyGet, long playerMoney) {
            super(player);
            this.countMoney = countMoneyGet;
            this.playerMoney = playerMoney;
        }

        public long getCountMoney() {
            return countMoney;
        }

        public long getPlayerMoney() {
            return playerMoney;
        }

        public void setCountMoney(long countMoney) {
            this.countMoney = countMoney;
        }

        public void setPlayerMoney(long playerMoney) {
            this.playerMoney = playerMoney;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    @Cancelable
    public static class PayEvent extends PlayerEvent{

        public long countMoney;
        public Player payablePlayer;

        public PayEvent(Player player, Player payablePlayer, long countMoney) {
            super(player);
            this.countMoney = countMoney;
            this.payablePlayer = payablePlayer;
        }

        public long getCountMoney() {
            return countMoney;
        }

        public Player getPayablePlayer() {
            return payablePlayer;
        }

        public void setCountMoney(long countMoney) {
            this.countMoney = countMoney;
        }

        public void setPayablePlayer(Player payablePlayer) {
            this.payablePlayer = payablePlayer;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }
}
