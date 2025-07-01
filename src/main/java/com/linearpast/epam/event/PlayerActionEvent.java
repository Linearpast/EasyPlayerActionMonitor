package com.linearpast.epam.event;

import com.linearpast.epam.CustomLog;
import com.linearpast.epam.EasyPlayerActionMonitor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

@OnlyIn(Dist.DEDICATED_SERVER)
@Mod.EventBusSubscriber(modid = EasyPlayerActionMonitor.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerActionEvent {

	static Logger logger = CustomLog.LOGGER;
	static HashMap<UUID,HashMap<ResourceLocation, Long>> lastBreak = new HashMap<>();

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void logBreakBlock(BlockEvent.@NotNull BreakEvent event){
		if(event.isCanceled()) return;
		long now = System.currentTimeMillis();
		Player player = event.getPlayer();
		if(player instanceof FakePlayer) return;
		if(player != null){
			UUID uuid = player.getUUID();
			HashMap<ResourceLocation, Long> orDefault = lastBreak.getOrDefault(uuid, new HashMap<>());
			ResourceLocation blockName = event.getState().getBlock().getRegistryName();
			if(now - orDefault.getOrDefault(blockName, 0L) < 1000) return;

			logger.info("player "
					+ player.getName().getString()
					+ " break block("
					+ blockName
					+ ", {"
					+ event.getPos().toShortString()
					+ "}) in "
					+ player.level.dimension().location());
			if(lastBreak.containsKey(uuid)){
				lastBreak.get(uuid).put(blockName, now);
			}else {
				lastBreak.put(
						uuid, new HashMap<>() {{
							put(blockName, now);
						}});
			}

		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void logLogin(PlayerEvent.PlayerLoggedInEvent event){
		if(event.isCanceled()) return;
		Player player = event.getPlayer();
		if(player instanceof FakePlayer) return;
		if(player != null){
			logger.info("player "
					+ player.getName().getString()
					+ " log in world("
					+ player.level.dimension().location()
					+ ", {"
					+ player.getOnPos().toShortString()
					+ "})"
			);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void logLogout(PlayerEvent.PlayerLoggedOutEvent event){
		if(event.isCanceled()) return;
		Player player = event.getPlayer();
		if(player instanceof FakePlayer) return;
		if(player != null){
			logger.info("player "
					+ player.getName().getString()
					+ " logout in world("
					+ player.level.dimension().location()
					+ ", {"
					+ player.getOnPos().toShortString()
					+ "})"
			);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void logInteractBlock(PlayerInteractEvent.RightClickBlock event){
		if(event.isCanceled()) return;
		Player player = event.getPlayer();
		if(player instanceof FakePlayer) return;
		if(player != null){
			BlockState block = player.level.getBlockState(event.getHitVec().getBlockPos());
			logger.info("player "
					+ player.getName().getString()
					+ " interact with block("
					+ block.getBlock().getRegistryName()
					+ ", {"
					+ event.getHitVec().getBlockPos().toShortString()
					+ "}) in "
					+ player.level.dimension().location()
					+ " with Hand "
					+ event.getHand()
			);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void logAttackEntity(AttackEntityEvent event){
		if(event.isCanceled()) return;
		Entity entity = event.getEntity();
		if(entity instanceof FakePlayer) return;
		if(entity instanceof Player player){
			Entity target = event.getTarget();
			logger.info("player "
					+ player.getName().getString()
					+ " attack Entity("
					+ target.getName().getString()
					+ ", player pos{"
					+ player.getOnPos().toShortString()
					+ "}, target pos{"
					+ target.getOnPos().toShortString()
					+ "}) in "
					+ player.level.dimension().location()
			);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void logInteractWithEntity(PlayerInteractEvent.EntityInteract event){
		if(event.isCanceled()) return;
		Player player = event.getPlayer();
		if(player instanceof FakePlayer) return;
		if(player != null){
			Entity target = event.getTarget();
			logger.info("player "
					+ player.getName().getString()
					+ " interact with Entity("
					+ target.getName().getString()
					+ ", player pos{"
					+ player.getOnPos().toShortString()
					+ "}, target pos{"
					+ target.getOnPos().toShortString()
					+ "}) in "
					+ player.level.dimension().location()
			);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void logPlaceBlock(BlockEvent.EntityPlaceEvent event){
		if(event.isCanceled()) return;
		Entity entity = event.getEntity();
		if(entity instanceof FakePlayer) return;
		if(entity instanceof Player player){
			BlockState block = event.getPlacedBlock();
			logger.info("player "
					+ player.getName().getString()
					+ " place block("
					+ block.getBlock().getRegistryName()
					+ ", {"
					+ event.getPos().toShortString()
					+ "}) in "
					+ player.level.dimension().location()
			);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void logContainerOpen(PlayerContainerEvent.Open event){
		if(event.isCanceled()) return;
		try {
			Player player = event.getPlayer();
			if(player instanceof FakePlayer) return;
			if(player != null){
				AbstractContainerMenu container = event.getContainer();
				logger.info("player "
						+ player.getName().getString()
						+ " open container("
						+ container.getType().getRegistryName()
						+ ", {"
						+ event.getEntity().getOnPos().toShortString()
						+ "}) in "
						+ player.level.dimension().location()
				);
			}
		}catch (Exception ignored){
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void logContainerClose(PlayerContainerEvent.Close event){
		if(event.isCanceled()) return;
		try {
			Player player = event.getPlayer();
			if(player instanceof FakePlayer) return;
			if(player != null){
				AbstractContainerMenu container = event.getContainer();
				logger.info("player "
						+ player.getName().getString()
						+ " close container("
						+ container.getType().getRegistryName()
						+ ", {"
						+ event.getEntity().getOnPos().toShortString()
						+ "}) in "
						+ player.level.dimension().location()
				);
			}
		}catch (Exception ignored){
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void logDropItem(ItemTossEvent event){
		if(event.isCanceled()) return;
		Player player = event.getPlayer();
		if(player instanceof FakePlayer) return;
		if(player != null){
			ItemEntity item = event.getEntityItem();
			logger.info("player "
					+ player.getName().getString()
					+ " toss item("
					+ item.getItem().getItem().getRegistryName()
					+ " *"
					+ item.getItem().getCount()
					+ ", {"
					+ player.getOnPos().toShortString()
					+ "}) in "
					+ player.level.dimension().location()
			);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void logPickUpItem(EntityItemPickupEvent event){
		if(event.isCanceled()) return;
		Player player = event.getPlayer();
		if(player instanceof FakePlayer) return;
		if (player != null){
			ItemEntity item = event.getItem();
			logger.info("player "
					+ player.getName().getString()
					+ " pickup item("
					+ item.getItem().getItem().getRegistryName()
					+ " *"
					+ item.getItem().getCount()
					+ ", {"
					+ item.getOnPos().toShortString()
					+ "}) in "
					+ player.level.dimension().location()
			);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void logEntityKilledByPlayer(LivingDeathEvent event){
		if(event.isCanceled()) return;
		Entity entity = event.getSource().getEntity();
		if(entity instanceof FakePlayer) return;
		if(entity instanceof Player player){
			LivingEntity target = event.getEntityLiving();
			logger.info("player "
					+ player.getName().getString()
					+ " kill Entity("
					+ target.getName().getString()
					+ ", player pos{"
					+ player.getOnPos().toShortString()
					+ "}, target pos{"
					+ target.getOnPos().toShortString()
					+ "}) in "
					+ player.level.dimension().location()
			);
		}
	}
}
