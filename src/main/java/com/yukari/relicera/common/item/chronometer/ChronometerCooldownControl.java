package com.yukari.relicera.common.item.chronometer;

import net.minecraft.world.entity.player.Player;

public interface ChronometerCooldownControl {
    void relicera$setCooldownOwner(Player player);

    void relicera$clearChronometerCooldowns();
}
