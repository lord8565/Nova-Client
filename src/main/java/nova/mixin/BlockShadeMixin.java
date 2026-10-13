package nova.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import nova.modules.ExtraMore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Xray: only the chosen blocks get their faces drawn. (Does nothing with Sodium-style renderers.) */
@Mixin(Block.class)
public abstract class BlockShadeMixin {
    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
    private static void nova$xray(BlockState state, BlockState otherState, Direction side, CallbackInfoReturnable<Boolean> cir) {
        if (ExtraMore.Xray.active()) cir.setReturnValue(ExtraMore.Xray.visible(state));
    }
}
