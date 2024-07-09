package net.ccbluex.liquidbounce.utils;

import net.ccbluex.liquidbounce.event.MovementEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;

public class MoveUtils {
	private static final Minecraft mc = Minecraft.getMinecraft();
	public static void setMotionWithValues(double speed, float yaw, double forward, double strafe) {
		if (forward == 0.0D && strafe == 0.0D) {
			mc.thePlayer.motionX = 0.0D;
			mc.thePlayer.motionZ = 0.0D;
		} else {
			if (forward != 0.0D) {
				if (strafe > 0.0D) {
					yaw += ((forward > 0.0D) ? -45 : 45);
				} else if (strafe < 0.0D) {
					yaw += ((forward > 0.0D) ? 45 : -45);
				}
				strafe = 0.0D;
				if (forward > 0.0D) {
					forward = 1.0D;
				} else if (forward < 0.0D) {
					forward = -1.0D;
				}
			}
			mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians((yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((yaw + 90.0F)));
			mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians((yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((yaw + 90.0F)));
		}
	}
	public static boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D))
                .isEmpty();
	}

	public static int getSpeedEffect() {
		if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
			return mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
		else
			return 0;
	}

	public static int getJumpEffect() {
		if (mc.thePlayer.isPotionActive(Potion.jump))
			return mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1;
		else
			return 0;
	}

	public static void setMotion(double speed) {
		double forward = mc.thePlayer.movementInput.moveForward;
		double strafe = mc.thePlayer.movementInput.moveStrafe;
		float yaw = mc.thePlayer.rotationYaw;
		if ((forward == 0.0D) && (strafe == 0.0D)) {
		} else {
			if (forward != 0.0D) {
				if (strafe > 0.0D) {
					yaw += (forward > 0.0D ? -45 : 45);
				} else if (strafe < 0.0D) {
					yaw += (forward > 0.0D ? 45 : -45);
				}
				strafe = 0.0D;
				if (forward > 0.0D) {
					forward = 1;
				} else if (forward < 0.0D) {
					forward = -1;
				}
			}
			double cos = Math.cos(Math.toRadians(yaw + 90.0F));
			double sin = Math.sin(Math.toRadians(yaw + 90.0F));
			mc.thePlayer.motionX = forward * speed * cos
					+ strafe * speed * sin;
			mc.thePlayer.motionZ = forward * speed * sin
					- strafe * speed * cos;
		}
	}

	public static Block getBlockUnderPlayer(EntityPlayer inPlayer, double height) {
		return Minecraft.getMinecraft().theWorld
				.getBlockState(new BlockPos(inPlayer.posX, inPlayer.posY - height, inPlayer.posZ)).getBlock();
	}

	public static void setSpeed(MovementEvent movementEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe,
								double pseudoForward) {
		double forward = pseudoForward;
		double strafe = pseudoStrafe;
		double shift = 0;
		float yaw = pseudoYaw;
		if (forward != 0.0) {
			if (strafe > 0.0) {
				yaw += (float) (forward > 0.0 ? -45 : 45);
			} else if (strafe < 0.0) {
				yaw += (float) (forward > 0.0 ? 45 : -45);
			}
			strafe = 0.0;
			if (forward > 0.0) {
				forward = 1.0;
			} else if (forward < 0.0) {
				forward = -1.0;
			}
		}
		if (strafe > 0.0) {
			strafe = 1.0;
		} else if (strafe < 0.0) {
			strafe = -1.0;
		}
		double mx = Math.cos(Math.toRadians(yaw + 90.0f));
		double mz = Math.sin(Math.toRadians(yaw + 90.0f));
		movementEvent.setX(forward * moveSpeed * mx + strafe * moveSpeed * mz + shift);
		movementEvent.setZ(forward * moveSpeed * mz - strafe * moveSpeed * mx - shift);
	}

	public static void setSpeed(MovementEvent movementEvent, double moveSpeed) {
		MoveUtils.setSpeed(movementEvent, moveSpeed, Minecraft.getMinecraft().thePlayer.rotationYaw,
				(double) mc.thePlayer.movementInput.moveStrafe * 0.9, mc.thePlayer.movementInput.moveForward);
	}

	public static double defaultSpeed() {
		double baseSpeed = 0.2873;
		if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
			final int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed)
					.getAmplifier();
			baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
		}
		return baseSpeed;
	}

	public static float getSpeed() {
		return MathUtils.INSTANCE.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
	}

	public static void strafe() {
		strafe((double) getSpeed());
	}

	public static boolean isMoving() {
		return mc.thePlayer != null
				&& (mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F);
	}

	public static boolean hasMotion() {
		return mc.thePlayer.motionX != 0D && mc.thePlayer.motionZ != 0D && mc.thePlayer.motionY != 0D;
	}

	public static void strafes(){
		final double yaw = getDirection();
		mc.thePlayer.motionX = -Math.sin(yaw) * 0.2873;
		mc.thePlayer.motionZ = Math.cos(yaw) * 0.2873;
	}

	public static void strafe(Double double1) {
		if (!isMoving())
			return;
		final double yaw = getDirection();
		mc.thePlayer.motionX = -Math.sin(yaw) * double1;
		mc.thePlayer.motionZ = Math.cos(yaw) * double1;
	}

	public static void forward(final double length) {
		final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
		mc.thePlayer.setPosition(mc.thePlayer.posX + (-Math.sin(yaw) * length), mc.thePlayer.posY,
				mc.thePlayer.posZ + (Math.cos(yaw) * length));
	}

	public static double getDirection() {
		float rotationYaw = mc.thePlayer.rotationYaw;
		if (mc.thePlayer.moveForward < 0F)
			rotationYaw += 180F;
		float forward = 1F;
		if (mc.thePlayer.moveForward < 0F)
			forward = -0.5F;
		else if (mc.thePlayer.moveForward > 0F)
			forward = 0.5F;
		if (mc.thePlayer.moveStrafing > 0F)
			rotationYaw -= 90F * forward;
		if (mc.thePlayer.moveStrafing < 0F)
			rotationYaw += 90F * forward;
		return Math.toRadians(rotationYaw);
	}

	public static void setSpeedWithShift(double speed) {
		double forward = MoveUtils.mc.thePlayer.movementInput.moveForward;
		double strafe = MoveUtils.mc.thePlayer.movementInput.moveStrafe;
		double shift = (Math.random() - Math.asin(Math.random())) * 0.01;
		float yaw = MoveUtils.mc.thePlayer.rotationYaw;

		if (forward == 0.0 && strafe == 0.0) {
			MoveUtils.mc.thePlayer.motionX = 0;
			MoveUtils.mc.thePlayer.motionZ = 0;
		} else {
			if (forward != 0.0) {
				if (strafe > 0.0) {
					yaw += (float) (forward > 0.0 ? -45 : 45);
				} else if (strafe < 0.0) {
					yaw += (float) (forward > 0.0 ? 45 : -45);
				}
				strafe = 0.0;
				if (forward > 0.0) {
					forward = 1.0;
				} else if (forward < 0.0) {
					forward = -1.0;
				}
            }
            mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f))
                    + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)) + shift;
//			Helper.sendMessage(shift);

            mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f))
					- strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)) - shift;
		}
	}
	
	public static void setSpeedNoShift(double speed) {
		double forward = MoveUtils.mc.thePlayer.movementInput.moveForward;
		double strafe = MoveUtils.mc.thePlayer.movementInput.moveStrafe;
		float yaw = MoveUtils.mc.thePlayer.rotationYaw;

		if (forward == 0.0 && strafe == 0.0) {
			MoveUtils.mc.thePlayer.motionX = 0;
			MoveUtils.mc.thePlayer.motionZ = 0;
		} else {
			if (forward != 0.0) {
				if (strafe > 0.0) {
					yaw += (float) (forward > 0.0 ? -45 : 45);
				} else if (strafe < 0.0) {
					yaw += (float) (forward > 0.0 ? 45 : -45);
				}
				strafe = 0.0;
				if (forward > 0.0) {
					forward = 1.0;
				} else if (forward < 0.0) {
					forward = -1.0;
				}
			}
//			Helper.sendMessage(shift);
			mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f))
					+ strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f));

			mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f))
					- strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f));
		}
	}

	public static double getBaseMoveSpeed() {
		double baseSpeed = 0.1675;
		if (MoveUtils.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
			baseSpeed *= 1.0
					+ 0.2 * (double) (MoveUtils.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
		}
		return baseSpeed;
	}

	public static double getJumpBoostModifier(double baseJumpHeight) {
		if (MoveUtils.mc.thePlayer.isPotionActive(Potion.jump)) {
			int amplifier = MoveUtils.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
			baseJumpHeight += (float) (amplifier + 1) * 0.1f;
		}
		return baseJumpHeight;
	}
	public static double getJumpBoostModifier(double baseJumpHeight,boolean a) {
		if (a) {
			int amplifier = MoveUtils.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
			baseJumpHeight += (float) (amplifier + 1) * 0.1f;
		}
		return baseJumpHeight;
	}

	public static boolean isInLiquid() {
		if (MoveUtils.mc.thePlayer == null) {
			return false;
		}
		if (MoveUtils.mc.thePlayer.isInWater()) {
			return true;
		}
		boolean var1 = false;
		int var2 = (int) MoveUtils.mc.thePlayer.getEntityBoundingBox().minY;
		for (double var3 = Math.floor(MoveUtils.mc.thePlayer.getEntityBoundingBox().minX); var3 < Math
				.floor(MoveUtils.mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++var3) {
			for (double var4 = Math.floor(MoveUtils.mc.thePlayer.getEntityBoundingBox().minZ); var4 < Math
					.floor(MoveUtils.mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++var4) {
				Block var5 = MoveUtils.mc.theWorld.getBlockState(new BlockPos(var3, var2, var4)).getBlock();
				if (var5 == null || var5.getMaterial() == Material.air)
					continue;
				if (!(var5 instanceof BlockLiquid)) {
					return false;
				}
				var1 = true;
			}
		}
		return var1;
	}

	public static void setSpeedWithEvent(MovementEvent event, double speed) {
		double forward = mc.thePlayer.movementInput.moveForward;
		double strafe = mc.thePlayer.movementInput.moveStrafe;
		float yaw = mc.thePlayer.rotationYaw;
		if ((forward == 0.0D) && (strafe == 0.0D)) {
			event.setX(0.0D);
			event.setZ(0.0D);
		} else {
			if (forward != 0.0D) {
				if (strafe > 0.0D) {
					yaw += (forward > 0.0D ? -45 : 45);
				} else if (strafe < 0.0D) {
					yaw += (forward > 0.0D ? 45 : -45);
				}
				strafe = 0.0D;
				if (forward > 0.0D) {
					forward = 1;
				} else if (forward < 0.0D) {
					forward = -1;
				}
			}
			event.setX(mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 88.0D))
					+ strafe * speed * Math.sin(Math.toRadians(yaw + 87.9000815258789D)));
			event.setZ(mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 88.0D))
					- strafe * speed * Math.cos(Math.toRadians(yaw + 87.9000815258789D)));

		}
	}

	public static boolean MovementInput() {
        return mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindLeft.pressed
                || mc.gameSettings.keyBindRight.pressed
                || mc.gameSettings.keyBindBack.pressed;
    }

	public static boolean isAirUnder(Entity ent) {
		return mc.theWorld.getBlockState(new BlockPos(ent.posX, ent.posY - 1.0, ent.posZ))
				.getBlock() == Blocks.air;
	}

	public static boolean isBlockUnderneath(BlockPos pos) {
		for (int k = 0; k < pos.getY() + 1; k++) {
			if (mc.theWorld.getBlockState(new BlockPos(pos.getX(), k, pos.getZ())).getBlock().getMaterial() != Material.air)
				return true;
		}
		return false;
	}
}
