package com.linearpast.epam;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;


// The value here should match an entry in the META-INF/mods.toml file
@Mod("easy_player_action_monitor")
public class EasyPlayerActionMonitor {
	public static final String MODID = "easy_player_action_monitor";


	public EasyPlayerActionMonitor() {
		if(FMLEnvironment.dist != Dist.DEDICATED_SERVER) return;
		CustomLog.init();
	}
}
