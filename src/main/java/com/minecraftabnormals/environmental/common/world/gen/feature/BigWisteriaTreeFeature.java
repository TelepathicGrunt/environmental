package com.minecraftabnormals.environmental.common.world.gen.feature;

import com.minecraftabnormals.environmental.common.world.EnvironmentalFeatureConfigs;
import com.minecraftabnormals.environmental.common.world.gen.util.WisteriaTreeUtils;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;
import java.util.function.Supplier;

public class BigWisteriaTreeFeature extends Feature<BaseTreeFeatureConfig> {
	private Supplier<BlockState> VINE_UPPER;
	private Supplier<BlockState> VINE_LOWER;

	public BigWisteriaTreeFeature(Codec<BaseTreeFeatureConfig> configFactoryIn) {
		super(configFactoryIn);
	}

	@Override
	public boolean generate(ISeedReader world, ChunkGenerator generator, Random random, BlockPos pos, BaseTreeFeatureConfig config) {

		if (config.leavesProvider.getBlockState(random, pos) == EnvironmentalFeatureConfigs.BLUE_WISTERIA_LEAVES) {
			VINE_UPPER = () -> EnvironmentalFeatureConfigs.BLUE_HANGING_WISTERIA_LEAVES_TOP;
			VINE_LOWER = () -> EnvironmentalFeatureConfigs.BLUE_HANGING_WISTERIA_LEAVES_BOTTOM;
		}
		if (config.leavesProvider.getBlockState(random, pos) == EnvironmentalFeatureConfigs.PINK_WISTERIA_LEAVES) {
			VINE_UPPER = () -> EnvironmentalFeatureConfigs.PINK_HANGING_WISTERIA_LEAVES_TOP;
			VINE_LOWER = () -> EnvironmentalFeatureConfigs.PINK_HANGING_WISTERIA_LEAVES_BOTTOM;
		}
		if (config.leavesProvider.getBlockState(random, pos) == EnvironmentalFeatureConfigs.PURPLE_WISTERIA_LEAVES) {
			VINE_UPPER = () -> EnvironmentalFeatureConfigs.PURPLE_HANGING_WISTERIA_LEAVES_TOP;
			VINE_LOWER = () -> EnvironmentalFeatureConfigs.PURPLE_HANGING_WISTERIA_LEAVES_BOTTOM;
		}
		if (config.leavesProvider.getBlockState(random, pos) == EnvironmentalFeatureConfigs.WHITE_WISTERIA_LEAVES) {
			VINE_UPPER = () -> EnvironmentalFeatureConfigs.WHITE_HANGING_WISTERIA_LEAVES_TOP;
			VINE_LOWER = () -> EnvironmentalFeatureConfigs.WHITE_HANGING_WISTERIA_LEAVES_BOTTOM;
		}

		int height = random.nextInt(7) + 5;
		boolean flag = true;
		if (pos.getY() >= 1 && pos.getY() + height + 1 <= world.getHeight()) {
			for (int j = pos.getY(); j <= pos.getY() + 1 + height; ++j) {
				int k = 1;
				if (j == pos.getY()) {
					k = 0;
				}
				if (j >= pos.getY() + 1 + height - 2) {
					k = 2;
				}
				BlockPos.Mutable blockpos$mutableblockpos = new BlockPos.Mutable();
				for (int l = pos.getX() - k; l <= pos.getX() + k && flag; ++l) {
					for (int i1 = pos.getZ() - k; i1 <= pos.getZ() + k && flag; ++i1) {
						if (j >= 0 && j < world.getHeight()) {
							if (!WisteriaTreeUtils.isAirOrLeaves(world, blockpos$mutableblockpos.setPos(l, j, i1))) {
								flag = false;
							}
						} else {
							flag = false;
						}
					}
				}
			}
			if (!flag) {
				return false;
			} else if (WisteriaTreeUtils.isValidGround(world, pos.down()) && pos.getY() < world.getHeight() - height - 1) {
				WisteriaTreeUtils.setDirtAt(world, pos.down());
				for (int k = 0; k < 3; ++k) {
					BlockPos position = new BlockPos(pos.add(0, random.nextInt(3) + 4, 0));
					boolean xNeg = random.nextBoolean();
					boolean zNeg = random.nextBoolean();
					int size = random.nextInt(3) + 5;
					for (int j = 1; j <= size; j++) {
						position = position.add(random.nextInt(2) - (xNeg ? 1 : 0), random.nextInt(2), random.nextInt(2) - (zNeg ? 1 : 0));
						WisteriaTreeUtils.setForcedState(world, position, config.trunkProvider.getBlockState(random, pos));
						if (j == size) {
							for (int y = 4; y > -4; --y) {
								for (int x = 4; x > -4; --x) {
									for (int z = 4; z > -4; --z) {
										if (Math.sqrt((x * x) + (y > 0 ? (y * y) : 0) + (z * z)) <= 4) {
											BlockPos leafPos = position.add(x, y, z);
											boolean place = true;
											if (y < 0) {
												place = world.hasBlockState(leafPos.add(0, 1, 0), (state) -> {
													return state.isIn(BlockTags.LEAVES);
												});
												if (place && random.nextInt(Math.abs(y) + 1) != 0) {
													place = false;
													if (random.nextInt(5) == 0 && !WisteriaTreeUtils.isLog(world, leafPos)) {
														WisteriaTreeUtils.placeVines(world, random, leafPos, config.leavesProvider.getBlockState(random, pos), VINE_LOWER.get(), VINE_UPPER.get());
													}
												}
											}
											if (place) {
												WisteriaTreeUtils.placeLeafAt(world, leafPos, config.leavesProvider.getBlockState(random, pos));
											}
										}
									}
								}
							}
						}
					}
				}
				for (int i2 = 0; i2 < height; ++i2) {
					if (WisteriaTreeUtils.isAirOrLeavesOrVines(world, pos.up(i2))) {
						WisteriaTreeUtils.setForcedState(world, pos.up(i2), config.trunkProvider.getBlockState(random, pos));
					}
				}

				BlockPos startPos = pos.up(height);

				for (BlockPos blockpos : BlockPos.getAllInBoxMutable(startPos.getX() - 10, startPos.getY() - 10, startPos.getZ() - 10, startPos.getX() + 10, startPos.getY() + 10, startPos.getZ() + 10)) {
					if (WisteriaTreeUtils.isAir(world, blockpos) && isLeaves(world, blockpos.up(), config, random) && random.nextInt(4) == 0) {
						if (WisteriaTreeUtils.isAir(world, blockpos))
							WisteriaTreeUtils.setForcedState(world, blockpos, VINE_UPPER.get());
						if (WisteriaTreeUtils.isAir(world, blockpos.down()) && random.nextInt(2) == 0)
							WisteriaTreeUtils.setForcedState(world, blockpos.down(), VINE_LOWER.get());
					}
				}

				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}


	public static boolean isLeaves(IWorldGenerationBaseReader worldIn, BlockPos pos, BaseTreeFeatureConfig config, Random random) {
		if (worldIn instanceof net.minecraft.world.IWorldReader) // FORGE: Redirect to state method when possible
			return worldIn.hasBlockState(pos, state -> state == config.leavesProvider.getBlockState(random, pos));
		return worldIn.hasBlockState(pos, (p_227223_0_) -> {
			return config.leavesProvider.getBlockState(random, pos) == p_227223_0_;
		});
	}
}