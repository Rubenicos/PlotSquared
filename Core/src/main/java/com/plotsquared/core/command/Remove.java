/*
 *       _____  _       _    _____                                _
 *      |  __ \| |     | |  / ____|                              | |
 *      | |__) | | ___ | |_| (___   __ _ _   _  __ _ _ __ ___  __| |
 *      |  ___/| |/ _ \| __|\___ \ / _` | | | |/ _` | '__/ _ \/ _` |
 *      | |    | | (_) | |_ ____) | (_| | |_| | (_| | | |  __/ (_| |
 *      |_|    |_|\___/ \__|_____/ \__, |\__,_|\__,_|_|  \___|\__,_|
 *                                    | |
 *                                    |_|
 *            PlotSquared plot management system for Minecraft
 *                  Copyright (C) 2021 IntellectualSites
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.plotsquared.core.command;

import com.google.inject.Inject;
import com.plotsquared.core.configuration.caption.TranslatableCaption;
import com.plotsquared.core.database.DBFunc;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.permissions.Permission;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.util.EventDispatcher;
import com.plotsquared.core.util.Permissions;
import com.plotsquared.core.util.PlayerManager;
import com.plotsquared.core.util.TabCompletions;
import net.kyori.adventure.text.minimessage.Template;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@CommandDeclaration(command = "remove",
        aliases = {"r", "untrust", "ut", "undeny", "unban", "ud", "pardon"},
        usage = "/plot remove <player | *>",
        category = CommandCategory.SETTINGS,
        requiredType = RequiredType.NONE,
        permission = "plots.remove")
public class Remove extends SubCommand {

    private final EventDispatcher eventDispatcher;

    @Inject
    public Remove(final @NonNull EventDispatcher eventDispatcher) {
        super(Argument.PlayerName);
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public boolean onCommand(PlotPlayer<?> player, String[] args) {
        Location location = player.getLocation();
        Plot plot = location.getPlotAbs();
        if (plot == null) {
            player.sendMessage(TranslatableCaption.of("errors.not_in_plot"));
            return false;
        }
        if (!plot.hasOwner()) {
            player.sendMessage(TranslatableCaption.of("info.plot_unowned"));
            return false;
        }
        if (!plot.isOwner(player.getUUID()) && !Permissions
                .hasPermission(player, Permission.PERMISSION_ADMIN_COMMAND_REMOVE)) {
            player.sendMessage(TranslatableCaption.of("permission.no_plot_perms"));
            return true;
        }

        PlayerManager.getUUIDsFromString(args[0], (uuids, throwable) -> {
            int count = 0;
            if (throwable instanceof TimeoutException) {
                player.sendMessage(TranslatableCaption.of("players.fetching_players_timeout"));
                return;
            } else if (throwable != null) {
                player.sendMessage(
                        TranslatableCaption.of("errors.invalid_player"),
                        Template.of("value", args[0])
                );
                return;
            } else if (!uuids.isEmpty()) {
                for (UUID uuid : uuids) {
                    if (plot.getTrusted().contains(uuid)) {
                        if (plot.removeTrusted(uuid)) {
                            this.eventDispatcher.callTrusted(player, plot, uuid, false);
                            count++;
                        }
                    } else if (plot.getMembers().contains(uuid)) {
                        if (plot.removeMember(uuid)) {
                            this.eventDispatcher.callMember(player, plot, uuid, false);
                            count++;
                        }
                    } else if (plot.getDenied().contains(uuid)) {
                        if (plot.removeDenied(uuid)) {
                            this.eventDispatcher.callDenied(player, plot, uuid, false);
                            count++;
                        }
                    } else if (uuid == DBFunc.EVERYONE) {
                        if (plot.removeTrusted(uuid)) {
                            this.eventDispatcher.callTrusted(player, plot, uuid, false);
                            count++;
                        } else if (plot.removeMember(uuid)) {
                            this.eventDispatcher.callMember(player, plot, uuid, false);
                            count++;
                        } else if (plot.removeDenied(uuid)) {
                            this.eventDispatcher.callDenied(player, plot, uuid, false);
                            count++;
                        }
                    }
                }
            }
            if (count == 0) {
                player.sendMessage(
                        TranslatableCaption.of("errors.invalid_player"),
                        Template.of("value", args[0])
                );
            } else {
                player.sendMessage(
                        TranslatableCaption.of("member.removed_players"),
                        Template.of("amount", count + "")
                );
            }
        });
        return true;
    }

    @Override
    public Collection<Command> tab(final PlotPlayer<?> player, final String[] args, final boolean space) {
        Location location = player.getLocation();
        Plot plot = location.getPlotAbs();
        if (plot == null) {
            return Collections.emptyList();
        }
        return TabCompletions.completeAddedPlayers(plot, String.join(",", args).trim(),
                Collections.singletonList(player.getName())
        );
    }

}
