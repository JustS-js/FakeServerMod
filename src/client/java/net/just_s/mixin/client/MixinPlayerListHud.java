package net.just_s.mixin.client;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.just_s.FSM;
import net.just_s.FSMClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {
    private static final Identifier ICONS_TEXTURE = new Identifier("textures/gui/icons.png");
    @Shadow
    private Text header;
    @Shadow
    private Text footer;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
        if (!FSMClient.isRunning) return;
        if (!FSMClient.isReceiver) return;
        ci.cancel();

        List<Text> list = FSMClient.fakePlayers.stream().toList();
        int i = 0;
        Iterator var8 = list.iterator();

        int k;
        while(var8.hasNext()) {
            Text playerName = (Text)var8.next();
            k = FSMClient.MC.textRenderer.getWidth(playerName);
            i = Math.max(i, k);
        }

        int l = list.size();
        int m = l;

        for(k = 1; m > 20; m = (l + k - 1) / k) {
            ++k;
        }

        int o = Math.min(k * (i + 13), scaledWindowWidth - 50) / k;
        int p = scaledWindowWidth / 2 - (o * k + (k - 1) * 5) / 2;
        int q = 10;
        int r = o * k + (k - 1) * 5;
        List<OrderedText> list2 = null;
        if (this.header != null) {
            list2 = FSMClient.MC.textRenderer.wrapLines(this.header, scaledWindowWidth - 50);

            OrderedText orderedText;
            for(Iterator var18 = list2.iterator(); var18.hasNext(); r = Math.max(r, FSMClient.MC.textRenderer.getWidth(orderedText))) {
                orderedText = (OrderedText)var18.next();
            }
        }

        List<OrderedText> list3 = null;
        OrderedText orderedText2;
        Iterator var35;
        if (this.footer != null) {
            list3 = FSMClient.MC.textRenderer.wrapLines(this.footer, scaledWindowWidth - 50);

            for(var35 = list3.iterator(); var35.hasNext(); r = Math.max(r, FSMClient.MC.textRenderer.getWidth(orderedText2))) {
                orderedText2 = (OrderedText)var35.next();
            }
        }

        int var10002;
        int var10003;
        int var10005;
        int s;
        int var33;
        if (list2 != null) {
            var33 = scaledWindowWidth / 2 - r / 2 - 1;
            var10002 = q - 1;
            var10003 = scaledWindowWidth / 2 + r / 2 + 1;
            var10005 = list2.size();
            Objects.requireNonNull(FSMClient.MC.textRenderer);
            context.fill(var33, var10002, var10003, q + var10005 * 9, -2147483648);

            for(var35 = list2.iterator(); var35.hasNext(); q += 9) {
                orderedText2 = (OrderedText)var35.next();
                s = FSMClient.MC.textRenderer.getWidth(orderedText2);
                context.drawTextWithShadow(FSMClient.MC.textRenderer, orderedText2, scaledWindowWidth / 2 - s / 2, q, -1);
                Objects.requireNonNull(FSMClient.MC.textRenderer);
            }

            ++q;
        }

        context.fill(scaledWindowWidth / 2 - r / 2 - 1, q - 1, scaledWindowWidth / 2 + r / 2 + 1, q + m * 9, -2147483648);
        int t = FSMClient.MC.options.getTextBackgroundColor(553648127);

        int v;
        for(int u = 0; u < l; ++u) {
            s = u / m;
            v = u % m;
            int w = p + s * o + s * 5;
            int x = q + v * 9;
            context.fill(w, x, w + o, x + 8, t);
            RenderSystem.enableBlend();
            if (u < list.size()) {
                Text playerName = (Text)list.get(u);

                context.drawTextWithShadow(FSMClient.MC.textRenderer, playerName, w, x, -1);

                context.getMatrices().push();
                context.getMatrices().translate(0.0F, 0.0F, 100.0F);
                int j = playerName.getString().length() % 6;
                context.drawTexture(ICONS_TEXTURE, w + o - 11, x, 0, 176 + j * 8, 10, 8);
                context.getMatrices().pop();
            }
        }

        if (list3 != null) {
            q += m * 9 + 1;
            var33 = scaledWindowWidth / 2 - r / 2 - 1;
            var10002 = q - 1;
            var10003 = scaledWindowWidth / 2 + r / 2 + 1;
            var10005 = list3.size();
            Objects.requireNonNull(FSMClient.MC.textRenderer);
            context.fill(var33, var10002, var10003, q + var10005 * 9, -2147483648);

            for(Iterator var38 = list3.iterator(); var38.hasNext(); q += 9) {
                OrderedText orderedText3 = (OrderedText)var38.next();
                v = FSMClient.MC.textRenderer.getWidth(orderedText3);
                context.drawTextWithShadow(FSMClient.MC.textRenderer, orderedText3, scaledWindowWidth / 2 - v / 2, q, -1);
                Objects.requireNonNull(FSMClient.MC.textRenderer);
            }
        }
    }

    @Shadow
    private List<PlayerListEntry> collectPlayerEntries() {
        return null;
    }
}
