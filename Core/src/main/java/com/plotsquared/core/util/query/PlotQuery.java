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
 *                  Copyright (C) 2020 IntellectualSites
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
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.plotsquared.core.util.query;

import com.google.common.base.Preconditions;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This represents a plot query, and can be used to
 * search for plots matching certain criteria.
 * <p>
 * The queries can be reused as no results are stored
 * in the query itself
 */
public final class PlotQuery {

    private PlotProvider plotProvider = new GlobalPlotProvider();
    private final Collection<PlotFilter> filters = new LinkedList<>();

    private PlotQuery() {
    }

    /**
     * Create a new plot query instance
     *
     * @return New query
     */
    public static PlotQuery newQuery() {
        return new PlotQuery();
    }

    /**
     * Query for plots in a single area
     *
     * @param area Area
     * @return The query instance
     */
    @NotNull public PlotQuery inArea(@NotNull final PlotArea area) {
        Preconditions.checkNotNull(area, "Area may not be null");
        this.plotProvider = new AreaLimitedPlotProvider(Collections.singletonList(area));
        return this;
    }

    /**
     * Query for plots in all areas in a world
     *
     * @param world World name
     * @return The query instance
     */
    @NotNull public PlotQuery inWorld(@NotNull final String world) {
        Preconditions.checkNotNull(world, "World may not be null");
        this.plotProvider = new AreaLimitedPlotProvider(PlotSquared.get().getPlotAreas(world));
        return this;
    }

    /**
     * Query for plots in specific areas
     *
     * @param areas Plot areas
     * @return The query instance
     */
    @NotNull public PlotQuery inAreas(@NotNull final Collection<PlotArea> areas) {
        Preconditions.checkNotNull(areas, "Areas may not be null");
        Preconditions.checkState(!areas.isEmpty(), "At least one area must be provided");
        this.plotProvider = new AreaLimitedPlotProvider(Collections.unmodifiableCollection(areas));
        return this;
    }

    /**
     * Query for base plots only
     *
     * @return The query instance
     */
    @NotNull public PlotQuery whereBasePlot() {
        return this.addFilter(new PredicateFilter(Plot::isBasePlot));
    }

    /**
     * Query for plots owned by a specific player
     *
     * @param owner Owner UUID
     * @return The query instance
     */
    @NotNull public PlotQuery ownedBy(@NotNull final UUID owner) {
        Preconditions.checkNotNull(owner, "Owner may not be null");
        return this.addFilter(new OwnerFilter(owner));
    }

    /**
     * Query for plots owned by a specific player
     *
     * @param owner Owner
     * @return The query instance
     */
    @NotNull public PlotQuery ownedBy(@NotNull final PlotPlayer owner) {
        Preconditions.checkNotNull(owner, "Owner may not be null");
        return this.addFilter(new OwnerFilter(owner.getUUID()));
    }

    /**
     * Query for plots with a specific alias
     *
     * @param alias Plot alias
     * @return The query instance
     */
    @NotNull public PlotQuery withAlias(@NotNull final String alias) {
        Preconditions.checkNotNull(alias, "Alias may not be null");
        return this.addFilter(new AliasFilter(alias));
    }

    /**
     * Query for plots with a specific member (added/trusted/owner)
     *
     * @param member Member UUID
     * @return The query instance
     */
    @NotNull public PlotQuery withMember(@NotNull final UUID member) {
        Preconditions.checkNotNull(member, "Member may not be null");
        return this.addFilter(new MemberFilter(member));
    }

    /**
     * Query for plots that passes a given predicate
     *
     * @param predicate Predicate
     * @return The query instance
     */
    @NotNull public PlotQuery thatPasses(@NotNull final Predicate<Plot> predicate) {
        Preconditions.checkNotNull(predicate, "Predicate may not be null");
        return this.addFilter(new PredicateFilter(predicate));
    }

    /**
     * Get all plots that match the given criteria
     *
     * @return Matching plots
     */
    @NotNull public Stream<Plot> asStream() {
        Stream<Plot> plots = this.plotProvider.getPlots();
        for (final PlotFilter filter : this.filters) {
            plots = plots.filter(filter);
        }
        return plots;
    }

    /**
     * Get all plots that match the given criteria
     *
     * @return Matching plots as an immutable list
     */
    @NotNull public List<Plot> asList() {
        return Collections.unmodifiableList(this.asStream().collect(Collectors.toList()));
    }

    /**
     * Get all plots that match the given criteria
     *
     * @return Matching plots as an immutable set
     */
    @NotNull public Set<Plot> asSet() {
        return Collections.unmodifiableSet(this.asStream().collect(Collectors.toSet()));
    }

    /**
     * Get all plots that match the given criteria
     * in the form of a {@link PaginatedPlotResult}
     *
     * @param pageSize The size of the pages. Must be positive.
     * @return Paginated plot result
     */
    @NotNull public PaginatedPlotResult getPaginated(final int pageSize) {
        Preconditions.checkState(pageSize > 0, "Page size must be greater than 0");
        return new PaginatedPlotResult(this.asList(), pageSize);
    }

    /**
     * Get all plots that match the given criteria
     *
     * @return Matching plots as an immutable collection
     */
    @NotNull public Collection<Plot> asCollection() {
        return this.asList();
    }

    @NotNull private PlotQuery addFilter(@NotNull final PlotFilter filter) {
        this.filters.add(filter);
        return this;
    }


}