package com.cbwln.bunnyaddons.mixin;

import com.cbwln.bunnyaddons.modules.misc.AutoSprint;
import com.cbwln.bunnyaddons.modules.skyblock.AutoUndercut;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignTextSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Input;sprint()Z"))
    private boolean autoSprint(boolean original) {
        return AutoSprint.INSTANCE.getEnabled() || original;
    }

    @Inject(method = "openTextEdit", at = @At("HEAD"))
    private void autoUndercut(SignBlockEntity sign, SignTextSlot slot, CallbackInfo ci) {
        if (!AutoUndercut.INSTANCE.getEnabled()) return;
        AutoUndercut.INSTANCE.fill(sign, slot);
    }
}