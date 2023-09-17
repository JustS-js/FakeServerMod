package net.just_s.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.just_s.FSMClient;
import net.minecraft.command.CommandRegistryAccess;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class CommandStop {
    public static void register(CommandDispatcher<FabricClientCommandSource> fabricClientCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        fabricClientCommandSourceCommandDispatcher.register( //
                literal("fsm").then(literal("stop").executes(CommandStop::run)));
    }

    private static int run(CommandContext<FabricClientCommandSource> context) {
        if (FSMClient.isRunning) {
            FSMClient.stopFaking();
            FSMClient.isRunning = false;
        }
        return 1;
    }
}
