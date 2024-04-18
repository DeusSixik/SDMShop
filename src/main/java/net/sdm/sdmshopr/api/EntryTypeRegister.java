package net.sdm.sdmshopr.api;

import net.minecraft.world.item.ItemStack;
import net.sdm.sdmshopr.shop.entry.type.CommandEntryType;
import net.sdm.sdmshopr.shop.entry.type.ItemEntryType;
import net.sdm.sdmshopr.shop.entry.type.integration.QuestEntryType;

import java.util.LinkedHashMap;
import java.util.Map;

public interface EntryTypeRegister {
    Map<String, IEntryType> TYPES = new LinkedHashMap();


    static IEntryType register(IEntryType provider) {
        return (IEntryType) TYPES.computeIfAbsent(provider.getID(), (id) -> {
            return provider;
        });
    }

    IEntryType ITEM = register(ItemEntryType.of(ItemStack.EMPTY));
    IEntryType COMMAND = register(new CommandEntryType("", ""));
//    IEntryType GAME_STAGES = register(new GameStagesEntryType(""));
    IEntryType QUESTS = register(new QuestEntryType(""));
//    IEntryType SKILL_TREE = register(new SkillTreeEntryType());
//    IEntryType MNA_PROGRESSION = register(new MNAProgressionEntryType(1));
//    IEntryType MNA_TIERS = register(new MNATierEntryType(1));
//    IEntryType MNA_FACTION = register(new MNAFactionEntryType(""));
//    IEntryType MNA_LEVEL = register(new MNALevelEntryType());

    static void init(){

    }
}
