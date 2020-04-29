package ryoryo.ppa.asm;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModHooks
{
	public static boolean pumpkinHook(World world, BlockPos pos)
	{
		return world.getBlockState(pos).getBlock().isReplaceable(world, pos);
	}
}