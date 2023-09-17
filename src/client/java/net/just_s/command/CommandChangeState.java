package net.just_s.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.just_s.FSMClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class CommandChangeState {
    public static void register(CommandDispatcher<FabricClientCommandSource> fabricClientCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        fabricClientCommandSourceCommandDispatcher.register( //
                literal("fsm").then(literal("state").then(
                        argument("state", StringArgumentType.word()).suggests(CommandChangeState::suggestTypes).executes(CommandChangeState::run)
                )));
    }

    private static CompletableFuture<Suggestions> suggestTypes(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        builder.suggest("real-server");
        builder.suggest("fake-server");
        return builder.buildFuture();
    }

    private static int run(CommandContext<FabricClientCommandSource> context) {
        if (FSMClient.isRunning) {
            context.getSource().sendError(Text.of("You can't change state while running"));
            return 0;
        }
        String state = context.getArgument("state", String.class);
        if (state.equals("real-server")) {
            FSMClient.isReceiver = false;
        } else if (state.equals("fake-server")) {
            FSMClient.isReceiver = true;
        } else {
            context.getSource().sendError(Text.of("This state does not exist."));
            return 0;
        }
        context.getSource().sendFeedback(Text.of("State has been set. Press F3+D to clear chat messages, if you wish so."));
        return 1;
    }
}
