package me.shedaniel.cloth.mixin;

import me.shedaniel.cloth.api.Cancelable;
import me.shedaniel.cloth.api.ScreenHooks;
import me.shedaniel.cloth.events.ClientDrawScreenEvent;
import me.shedaniel.cloth.events.ClientInitScreenEvent;
import me.shedaniel.cloth.events.ClientScreenAddButtonEvent;
import me.shedaniel.cloth.hooks.ClothHooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.InputListener;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(Screen.class)
public abstract class MixinScreen implements ScreenHooks {
    
    @Shadow protected MinecraftClient client;
    
    @Shadow @Final protected List<ButtonWidget> buttons;
    
    @Shadow public abstract List<? extends InputListener> getInputListeners();
    
    @Override
    public List<ButtonWidget> getButtonWidgets() {
        return buttons;
    }
    
    @Override
    public List<InputListener> getInputs() {
        return (List) getInputListeners();
    }
    
    @Inject(method = "draw(IIF)V", at = @At("HEAD"), cancellable = true)
    public void onPreDraw(int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (!info.isCancelled())
            for(Consumer<ClientDrawScreenEvent.Pre> sortedListener : ClothHooks.CLIENT_PRE_DRAW_SCREEN.getSortedListeners()) {
                Cancelable cancelable = new Cancelable();
                sortedListener.accept(new ClientDrawScreenEvent.Pre(client, (Screen) (Object) this, mouseX, mouseY, delta, cancelable));
                if (cancelable.isCancelled()) {
                    info.cancel();
                    return;
                }
            }
    }
    
    @Inject(method = "draw(IIF)V", at = @At("RETURN"))
    public void onPostDraw(int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (!info.isCancelled())
            ClothHooks.CLIENT_POST_DRAW_SCREEN.invoke(new ClientDrawScreenEvent.Post(client, (Screen) (Object) this, mouseX, mouseY, delta));
    }
    
    @Inject(method = "onInitialized()V", at = @At("HEAD"), cancellable = true)
    public void onPreInit(CallbackInfo info) {
        if (!info.isCancelled())
            for(Consumer<ClientInitScreenEvent.Pre> sortedListener : ClothHooks.CLIENT_PRE_INIT_SCREEN.getSortedListeners()) {
                Cancelable cancelable = new Cancelable();
                sortedListener.accept(new ClientInitScreenEvent.Pre(client, (Screen) (Object) this, cancelable));
                if (cancelable.isCancelled()) {
                    info.cancel();
                    return;
                }
            }
    }
    
    @Inject(method = "onInitialized()V", at = @At("RETURN"))
    public void onPostInit(CallbackInfo info) {
        if (!info.isCancelled())
            ClothHooks.CLIENT_POST_INIT_SCREEN.invoke(new ClientInitScreenEvent.Post(client, (Screen) (Object) this));
    }
    
    @Inject(method = "addButton", at = @At("HEAD"))
    public void onAddButton(ButtonWidget widget, CallbackInfoReturnable<ButtonWidget> info) {
        if (!info.isCancelled())
            for(Consumer<ClientScreenAddButtonEvent> sortedListener : ClothHooks.CLIENT_SCREEN_ADD_BUTTON.getSortedListeners()) {
                Cancelable cancelable = new Cancelable();
                sortedListener.accept(new ClientScreenAddButtonEvent(client, (Screen) (Object) this, widget, cancelable));
                if (cancelable.isCancelled()) {
                    info.cancel();
                    return;
                }
            }
    }
    
}