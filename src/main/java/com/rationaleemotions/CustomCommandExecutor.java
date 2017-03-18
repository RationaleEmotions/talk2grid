package com.rationaleemotions;


import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.Response;

import java.io.IOException;


/**
 * A custom implementation of {@link CommandExecutor} that sends requests to the Hub only for quit session commands
 * and talks directly to the node for everything else.
 */
class CustomCommandExecutor implements CommandExecutor {
    private CommandExecutor grid;
    private CommandExecutor node;

    CustomCommandExecutor(CommandExecutor grid, CommandExecutor node) {
        this.grid = grid;
        this.node = node;
    }

    @Override
    public Response execute(Command command) throws IOException {
        Response response;
        if (DriverCommand.QUIT.equals(command.getName())) {
            response = grid.execute(command);
        } else {
            response = node.execute(command);
        }
        return response;
    }
}
