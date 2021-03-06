package io.github.fablabsmc.fablabs.mixin.gamerule.client;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import io.github.fablabsmc.fablabs.api.gamerule.v1.FabricGameRuleCategory;
import io.github.fablabsmc.fablabs.impl.gamerule.RuleCategories;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.world.GameRules;

@Mixin(targets = "net/minecraft/client/gui/screen/world/EditGameRulesScreen$RuleListWidget")
public abstract class EditGameRulesScreen$RuleListWidgetMixin extends ElementListWidget<EditGameRulesScreen.AbstractRuleWidget> {
	private EditGameRulesScreen$RuleListWidgetMixin(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	private final Map<FabricGameRuleCategory, ArrayList<EditGameRulesScreen.AbstractRuleWidget>> fabricCategories = new TreeMap<>();

	@SuppressWarnings("InvalidInjectorMethodSignature")
	@Inject(method = "<init>", at = @At("TAIL"))
	private void initializeFabricGameruleCategories(EditGameRulesScreen screen, GameRules gameRules, CallbackInfo ci) {
		fabricCategories.forEach((category, widgetList) -> {
			this.addEntry(screen.new RuleCategoryWidget(category.getName()));

			for (EditGameRulesScreen.AbstractRuleWidget widget : widgetList) {
				this.addEntry(widget);
			}
		});
	}

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "method_27638(Ljava/util/Map$Entry;)V", at = @At("HEAD"), cancellable = true)
	private void dontShowFabric(Map.Entry<GameRules.RuleKey<?>, EditGameRulesScreen.AbstractRuleWidget> entry, CallbackInfo ci) {
		if (RuleCategories.containsKey(entry.getKey())) {
			FabricGameRuleCategory category = RuleCategories.get(entry.getKey());
			fabricCategories.putIfAbsent(category, new ArrayList<>());
			fabricCategories.computeIfAbsent(category, c -> new ArrayList<>()).add(entry.getValue());
			ci.cancel();
		}
	}
}
