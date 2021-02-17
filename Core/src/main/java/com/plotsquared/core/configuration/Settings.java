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
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.plotsquared.core.configuration;

import com.plotsquared.core.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Settings extends Config {

    /*
    START OF CONFIGURATION SECTION:
    NOTE: Fields are saved in declaration order, classes in reverse order
     */

    @Comment("This value is not configurable. It shows the platform you are using.") // This is a comment
    @Final public static String PLATFORM; // These values are set from P2 before loading

    @Comment({"Show additional information in console. It helps us at IntellectualSites to find out more about an issue.",
    "Leave it off if you don't need it, it can spam your console."}) public static boolean DEBUG = false;
    @Comment({"The big text that appears when you enter a plot.",
        "For a single plot set `/plot flag set titles false` to disable it.", "For just you run `/plot toggle titles` to disable it.",
        "For all plots: Add `titles: false` in the worlds.yml flags block to disable it."}) public static boolean
        TITLES = true;

    @Create // This value will be generated automatically
    public static ConfigBlock<Auto_Clear> AUTO_CLEAR = null;
    // A ConfigBlock is a section that can have multiple instances e.g. multiple expiry tasks

    public static void save(File file) {
        save(file, Settings.class);
    }

    public static void load(File file) {
        load(file, Settings.class);
    }

    public static boolean convertLegacy(File file) {
        if (!file.exists()) {
            return false;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Protection
        Redstone.DISABLE_OFFLINE = config.getBoolean("protection.redstone.disable-offline");
        Redstone.DISABLE_UNOCCUPIED = config
            .getBoolean("protection.redstone.disable-unoccupied", Redstone.DISABLE_UNOCCUPIED);

        // UUID
        UUID.USE_SQLUUIDHANDLER =
            config.getBoolean("uuid.use_sqluuidhandler", UUID.USE_SQLUUIDHANDLER);
        UUID.OFFLINE = config.getBoolean("UUID.offline", UUID.OFFLINE);
        UUID.FORCE_LOWERCASE = config.getBoolean("UUID.force-lowercase", UUID.FORCE_LOWERCASE);

        // Mob stuff
        Enabled_Components.KILL_ROAD_MOBS =
            config.getBoolean("kill_road_mobs", Enabled_Components.KILL_ROAD_MOBS);
        Enabled_Components.KILL_ROAD_VEHICLES =
            config.getBoolean("kill_road_vehicles", Enabled_Components.KILL_ROAD_VEHICLES);

        // Clearing + Expiry
        //FAST_CLEAR = config.getBoolean("clear.fastmode");
        Enabled_Components.PLOT_EXPIRY =
            config.getBoolean("clear.auto.enabled", Enabled_Components.PLOT_EXPIRY);
        if (Enabled_Components.PLOT_EXPIRY) {
            Enabled_Components.BAN_DELETER = config.getBoolean("clear.on.ban");
            AUTO_CLEAR = new ConfigBlock<>();
            AUTO_CLEAR.put("task1", new Auto_Clear());
            Auto_Clear task = AUTO_CLEAR.get("task1");
            task.CALIBRATION = new Auto_Clear.CALIBRATION();

            task.DAYS = config.getInt("clear.auto.days", task.DAYS);
            task.THRESHOLD = config.getInt("clear.auto.threshold", task.THRESHOLD);
            task.CONFIRMATION = config.getBoolean("clear.auto.confirmation", task.CONFIRMATION);
            task.CALIBRATION.CHANGES =
                config.getInt("clear.auto.calibration.changes", task.CALIBRATION.CHANGES);
            task.CALIBRATION.FACES =
                config.getInt("clear.auto.calibration.faces", task.CALIBRATION.FACES);
            task.CALIBRATION.DATA =
                config.getInt("clear.auto.calibration.data", task.CALIBRATION.DATA);
            task.CALIBRATION.AIR =
                config.getInt("clear.auto.calibration.air", task.CALIBRATION.AIR);
            task.CALIBRATION.VARIETY =
                config.getInt("clear.auto.calibration.variety", task.CALIBRATION.VARIETY);
            task.CALIBRATION.CHANGES_SD =
                config.getInt("clear.auto.calibration.changes_sd", task.CALIBRATION.CHANGES_SD);
            task.CALIBRATION.FACES_SD =
                config.getInt("clear.auto.calibration.faces_sd", task.CALIBRATION.FACES_SD);
            task.CALIBRATION.DATA_SD =
                config.getInt("clear.auto.calibration.data_sd", task.CALIBRATION.DATA_SD);
            task.CALIBRATION.AIR_SD =
                config.getInt("clear.auto.calibration.air_sd", task.CALIBRATION.AIR_SD);
            task.CALIBRATION.VARIETY_SD =
                config.getInt("clear.auto.calibration.variety_sd", task.CALIBRATION.VARIETY_SD);
        }

        // Done
        Done.REQUIRED_FOR_RATINGS =
            config.getBoolean("approval.ratings.check-done", Done.REQUIRED_FOR_RATINGS);
        Done.COUNTS_TOWARDS_LIMIT =
            config.getBoolean("approval.done.counts-towards-limit", Done.COUNTS_TOWARDS_LIMIT);
        Done.RESTRICT_BUILDING =
            config.getBoolean("approval.done.restrict-building", Done.RESTRICT_BUILDING);
        Done.REQUIRED_FOR_DOWNLOAD =
            config.getBoolean("approval.done.required-for-download", Done.REQUIRED_FOR_DOWNLOAD);

        // Schematics
        Paths.SCHEMATICS = config.getString("schematics.save_path", Paths.SCHEMATICS);

        // Web
        Web.URL = config.getString("web.url", Web.URL);

        // Caching
        Enabled_Components.PERMISSION_CACHE =
            config.getBoolean("cache.permissions", Enabled_Components.PERMISSION_CACHE);
        Enabled_Components.RATING_CACHE =
            config.getBoolean("cache.ratings", Enabled_Components.RATING_CACHE);

        // Rating system
        Ratings.CATEGORIES = config.contains("ratings.categories") ?
            config.getStringList("ratings.categories") :
            Ratings.CATEGORIES;

        // Titles
        TITLES = config.getBoolean("titles", TITLES);

        // Update Notifications
        Enabled_Components.UPDATE_NOTIFICATIONS =
            config.getBoolean("update-notifications", Enabled_Components.UPDATE_NOTIFICATIONS);

        // Teleportation
        Teleport.DELAY = config.getInt("teleport.delay", Teleport.DELAY);
        Teleport.ON_LOGIN = config.getBoolean("teleport.on_login", Teleport.ON_LOGIN);
        Teleport.ON_DEATH = config.getBoolean("teleport.on_death", Teleport.ON_DEATH);

        // WorldEdit
        //WE_ALLOW_HELPER = config.getBoolean("worldedit.enable-for-helpers");

        // Chunk processor
        Enabled_Components.CHUNK_PROCESSOR =
            config.getBoolean("chunk-processor.enabled", Enabled_Components.CHUNK_PROCESSOR);
        Chunk_Processor.AUTO_TRIM =
            config.getBoolean("chunk-processor.auto-unload", Chunk_Processor.AUTO_TRIM);
        Chunk_Processor.MAX_TILES =
            config.getInt("chunk-processor.max-blockstates", Chunk_Processor.MAX_TILES);
        Chunk_Processor.MAX_ENTITIES =
            config.getInt("chunk-processor.max-entities", Chunk_Processor.MAX_ENTITIES);
        Chunk_Processor.DISABLE_PHYSICS =
            config.getBoolean("chunk-processor.disable-physics", Chunk_Processor.DISABLE_PHYSICS);

        // Comments
        Enabled_Components.COMMENT_NOTIFIER = config
            .getBoolean("comments.notifications.enabled", Enabled_Components.COMMENT_NOTIFIER);

        // Plot limits
        Claim.MAX_AUTO_AREA = config.getInt("claim.max-auto-area", Claim.MAX_AUTO_AREA);
        Limit.MAX_PLOTS = config.getInt("max_plots", Limit.MAX_PLOTS);
        Limit.GLOBAL = config.getBoolean("global_limit", Limit.GLOBAL);

        // Misc
        DEBUG = config.getBoolean("debug", DEBUG);
        Chat.CONSOLE_COLOR = config.getBoolean("console.color", Chat.CONSOLE_COLOR);
        Chat.INTERACTIVE = config.getBoolean("chat.fancy", Chat.INTERACTIVE);

        Enabled_Components.DATABASE_PURGER =
            config.getBoolean("auto-purge", Enabled_Components.DATABASE_PURGER);
        return true;
    }

    @Comment("This is an auto clearing task called `task1`")
    @BlockName("task1")
    // The name for the default block
    public static final class Auto_Clear extends ConfigBlock {
        @Create // This value has to be generated since an instance isn't static
        public CALIBRATION CALIBRATION = null;
        public int THRESHOLD = -1;
        public int REQUIRED_PLOTS = -1;
        public boolean CONFIRMATION = true;
        public int DAYS = 90;
        public int SKIP_ACCOUNT_AGE_DAYS = -1;
        public List<String> WORLDS = new ArrayList<>(Collections.singletonList("*"));


        @Comment("See: https://wiki.intellectualsites.com/en/plotsquared/optimization/plot-analysis for a description of each value.")
        public static final class CALIBRATION {
            public int VARIETY = 0;
            public int VARIETY_SD = 0;
            public int CHANGES = 0;
            public int CHANGES_SD = 1;
            public int FACES = 0;
            public int FACES_SD = 0;
            public int DATA_SD = 0;
            public int AIR = 0;
            public int AIR_SD = 0;
            public int DATA = 0;
        }
    }


    @Comment({"Chunk processor related settings",
        "See https://wiki.intellectualsites.com/en/plotsquared/optimization/chunk-processor for more information."})
    public static class Chunk_Processor {
        @Comment("Auto trim will not save chunks which aren't claimed") public static boolean
            AUTO_TRIM = false;
        @Comment("Max tile entities per chunk") public static int MAX_TILES = 4096;
        @Comment("Max entities per chunk") public static int MAX_ENTITIES = 512;
        @Comment("Disable block physics") public static boolean DISABLE_PHYSICS = false;
    }


    @Comment({"UUID settings",
    "DO NOT EDIT them unless you know what you are doing."})
    public static class UUID {
        @Comment("Force using offline UUIDs (it usually detects the right mode)")
        public static boolean OFFLINE = false;
        @Comment("Force using lowercase UUIDs") public static boolean FORCE_LOWERCASE = false;
        @Comment("Use a database to store UUID/name info") public static boolean
            USE_SQLUUIDHANDLER = false;
        @Comment("How many UUIDs that may be stored in the cache")
        public static int UUID_CACHE_SIZE = 100000;
        @Comment("Rate limit (per 10 minutes) for background UUID fetching from the Mojang API")
        public static int BACKGROUND_LIMIT = 200;
        @Comment("Rate limit (per 10 minutes) for random UUID fetching from the Mojang API")
        public static int IMPROMPTU_LIMIT = 300;
        @Comment("Timeout (in milliseconds) for non-blocking UUID requests (mostly commands)")
        public static long NON_BLOCKING_TIMEOUT = 3000L;
        @Comment("Timeout (in milliseconds) for blocking UUID requests (events)")
        public static long BLOCKING_TIMEOUT = 10L;
        @Comment("Whether or not PlotSquared should read from the legacy database")
        public static boolean LEGACY_DATABASE_SUPPORT = true;
        @Comment("Whether or not PlotSquared should return Unknown if it fails to fulfill a request")
        public static boolean UNKNOWN_AS_DEFAULT = true;
        @Comment("Whether or not automatic background caching should be enabled. It is HIGHLY recommended to keep this turned on."
            + " This should only be disabled if the server has a very large number of plots (>100k)")
        public static boolean BACKGROUND_CACHING_ENABLED = true;
        @Comment("Whether the PaperMC service is enabled")
        public static boolean SERVICE_PAPER = true;
        @Comment("Whether the LuckPerms service is enabled")
        public static boolean SERVICE_LUCKPERMS = true;
        @Comment("Whether the Bukkit service is enabled")
        public static boolean SERVICE_BUKKIT = true;
        @Comment("Whether the EssentialsX service is enabled")
        public static boolean SERVICE_ESSENTIALSX = true;
    }


    @Comment("General settings")
    public static final class General {
        @Comment("Display scientific numbers (4.2E8)") public static boolean SCIENTIFIC = false;
        @Comment("Replace wall when merging") public static boolean MERGE_REPLACE_WALL = true;
        @Comment({"Blocks that may not be used in plot components",
        "Checkout the wiki article regarding plot components before modifying: https://wiki.intellectualsites.com/en/plotsquared/installation/plot-components"}) public static List<String>
            INVALID_BLOCKS = Arrays.asList(
            // Acacia Stuff
            "acacia_button", "acacia_fence_gate", "acacia_door", "acacia_pressure_plate",
            "acaia_trapdoor", "acacia_sapling", "acacia_sign", "acacia_wall_sign", "acacia_leaves",
            // Birch Stuff
            "birch_button", "birch_fence_gate", "birch_door", "birch_pressure_plate",
            "birch_trapdoor", "birch_sapling", "birch_sign", "birch_wall_sign", "birch_leaves",
            // Dark Oak Stuff
            "dark_oak_button", "dark_oak_fence_gate", "dark_oak_door", "dark_oak_pressure_plate",
            "dark_oak_trapdoor", "dark_oak_sapling", "dark_oak_sign", "dark_oak_wall_sign",
            "dark_oak_leaves",
            // Jungle Stuff
            "jungle_button", "jungle_fence_gate", "jungle_door", "jungle_pressure_plate",
            "jungle_trapdoor", "jungle_sapling", "jungle_sign", "jungle_wall_sign", "jungle_leaves",
            // Oak Stuff
            "oak_button", "oak_fence_gate", "oak_door", "oak_pressure_plate", "oak_trapdoor",
            "oak_sapling", "oak_sign", "oak_wall_sign", "oak_leaves",
            // Spruce Stuff
            "spruce_button", "spruce_fence_gate", "spruce_door", "spruce_pressure_plate",
            "spruce_trapdoor", "spruce_sapling", "spruce_sign", "spruce_wall_sign", "spruce_leaves",
            // Rails
            "activator_rail", "detector_rail", "rail",
            // Flowers
            "allium", "azure_bluet", "blue_orchid", "dandelion", "lilac", "orange_tulip",
            "oxeye_daisy", "peony", "pink_tulip", "poppy", "potted_allium", "potted_azure_bluet",
            "potted_birch_sapling", "potted_blue_orchid", "potted_brown_mushroom", "potted_cactus",
            "potted_fern", "potted_jungle_sapling", "potted_oak_sapling", "potted_orange_tulip",
            "potted_oxeye_daisy", "potted_pink_tulip", "potted_red_mushroom", "potted_red_tulip",
            "red_mushroom", "red_tulip", "potted_spruce_sapling", "potted_white_tulip", "rose_bush",
            "sunflower", "white_tulip", "cornflower", "wither_rose",
            // Stems
            "attached_melon_stem", "attached_pumpkin_stem", "melon_stem", "pumpkin_stem",
            "mushroom_stem",
            // Plants
            "beetroots", "brown_mushroom", "cactus", "carrots", "chorus_flower", "chorus_plant",
            "cocoa", "dead_bush", "fern", "kelp_plant", "large_fern", "lily_pad", "potatoes",
            "sea_pickle", "seagrass", "sugar_cane", "tall_grass", "tall_seagrass", "vine", "wheat",
            "bamboo",
            // Misc
            "anvil", "barrier", "beacon", "brewing_stand", "bubble_column", "cake", "cobweb",
            "comparator", "creeper_head", "creeper_wall_header", "damaged_anvil",
            "daylight_detector", "dragon_egg", "dragon_head", "dragon_wall_head",
            "enchanting_table", "end_gateway", "end_portal", "end_rod", "ender_chest", "chest",
            "flower_pot", "grass", "heavy_weighted_pressure_plate", "lever",
            "light_weighted_pressure_plate", "player_head", "redstone_wire", "repeater",
            "comparator", "redstone_torch", "torch", "redstone_wall_torch", "wall_torch", "sign",
            "skeleton_skull", "skeleton_wall_skull", "snow", "stone_pressure_plate",
            "trapped_chest", "tripwire", "tripwire_hook", "turtle_egg", "wall_sign", "zombie_head",
            "zombie_wall_head", "bell",
            // Black Stuff
            "black_bed", "black_banner", "black_carpet", "black_concrete_powder",
            "black_wall_banner",
            // Blue Stuff
            "blue_bed", "blue_banner", "blue_carpet", "blue_concrete_powder", "blue_wall_banner",
            // Brown Stuff
            "brown_bed", "brown_banner", "brown_carpet", "brown_concrete_powder",
            "brown_wall_banner",
            // Cyan Stuff
            "cyan_bed", "cyan_banner", "cyan_concrete_powder", "cyan_carpet", "cyan_wall_banner",
            // Gray Stuff
            "gray_bed", "gray_banner", "gray_concrete_powder", "gray_carpet", "gray_wall_banner",
            // Green Stuff
            "green_bed", "green_banner", "green_concrete_powder", "green_carpet",
            "green_wall_banner",
            // Light blue Stuff
            "light_blue_bed", "light_blue_banner", "light_blue_concrete_powder",
            "light_blue_carpet", "light_blue_wall_banner",
            // Light Gray Stuff
            "light_gray_bed", "light_gray_banner", "light_gray_concrete_powder",
            "light_gray_carpet", "light_gray_wall_banner",
            // Lime Stuff
            "lime_bed", "lime_banner", "lime_concrete_powder", "lime_carpet", "lime_wall_banner",
            // Magenta Stuff
            "magenta_bed", "magenta_banner", "magenta_concrete_powder", "magenta_carpet",
            "magenta_wall_banner",
            // Orange Stuff
            "orange_bed", "orange_banner", "orange_concrete_powder", "orange_carpet",
            "orange_wall_banner",
            // Pink Stuff
            "pink_bed", "pink_banner", "pink_concrete_powder", "pink_carpet", "pink_wall_banner",
            // Purple Stuff
            "purple_bed", "purple_banner", "purple_concrete_powder", "purple_carpet",
            "purple_wall_banner",
            // Red Stuff
            "red_bed", "red_banner", "red_concrete_powder", "red_carpet", "red_wall_banner",
            // White Stuff
            "white_bed", "white_banner", "white_concrete_powder", "white_carpet",
            "white_wall_banner",
            // Yellow Stuff
            "yellow_bed", "yellow_banner", "yellow_concrete_powder", "yellow_carpet",
            "yellow_wall_banner",
            // Corals
            "brain_coral", "brain_coral_fan", "brain_coral_wall_fan", "bubble_coral",
            "bubble_coral_block", "bubble_coral_fan", "bubble_coral_wall_fan", "dead_brain_coral",
            "dead_brain_coral_block", "dead_brain_coral_fan", "dead_brain_coral_wall_fan",
            "dead_bubble_coral", "dead_bubble_coral_fan", "dead_bubble_coral_wall_fan",
            "dead_fire_coral", "dead_fire_coral_block", "dead_fire_coral_fan",
            "dead_fire_coral_wall_fan", "dead_horn_coral", "dead_horn_coral_block",
            "dead_horn_coral_fan", "dead_tube_coral", "dead_tube_coral_wall_fan",
            "dried_kelp_block", "horn_coral", "horn_coral_block", "horn_coral_fan",
            "horn_coral_wall_fan", "tube_coral", "tube_coral_block", "tube_coral_fan",
            "tube_coral_wall_fan");
    }


    @Comment("Update checker settings")
    public static final class UpdateChecker {
        @Comment("How often to poll for updates (in minutes)") public static int POLL_RATE = 360;
        @Comment("Only notify console once after an update is found") public static boolean
            NOTIFY_ONCE = true;
    }


    @Comment({"Schematic Settings",
            "See https://wiki.intellectualsites.com/en/plotsquared/schematics/on-claim for more information."})
    public static final class Schematics {
        @Comment(
            "Whether schematic based generation should paste schematic on top of plots, or from Y=1")
        public static boolean PASTE_ON_TOP = true;
        @Comment(
            "Whether schematic based road generation should paste schematic on top of roads, or from Y=1")
        public static boolean PASTE_ROAD_ON_TOP = true;
    }


    @Comment("Configure the paths that will be used")
    public static final class Paths {
        public static String SCHEMATICS = "schematics";
        public static String SCRIPTS = "scripts";
        public static String TEMPLATES = "templates";
        public static String TRANSLATIONS = "translations";
    }


    @Comment("Schematic and Asset interface related settings")
    public static class Web {
        @Comment({"The web interface for schematics", " - All schematics are anonymous and private",
            " - Downloads can be deleted by the user",
            " - Supports plot uploads, downloads and saves",}) public static String URL =
            "https://schem.intellectualsites.com/plots/";
        @Comment({"The web interface for assets", " - All schematics are organized and public",
            " - Assets can be searched, selected and downloaded",}) public static String ASSETS =
            "https://empcraft.com/assetpack/";

    }


    @Comment("Misc settings")
    public static final class Done {
        @Comment("Require a plot marked as done to download (/plot download)") public static boolean
            REQUIRED_FOR_DOWNLOAD = false;
        @Comment("Only plots marked as done can be rated") public static boolean
            REQUIRED_FOR_RATINGS = false;
        @Comment("Restrict building when a plot is marked as done") public static boolean
            RESTRICT_BUILDING = false;
        @Comment("The limit being how many plots a player can claim") public static boolean
            COUNTS_TOWARDS_LIMIT = true;
    }


    @Comment("Chat related settings")
    public static final class Chat {
        @Comment("Sometimes console color doesn't work, you can disable it here")
        public static boolean CONSOLE_COLOR = true;
        @Comment("Should the chat be interactive?") public static boolean INTERACTIVE = true;
    }


    @Comment("Relating to how many plots someone can claim")
    public static final class Limit {
        @Comment("Should the limit be global (over multiple worlds)") public static boolean GLOBAL =
            false;
        @Comment({"The max. range of permissions to check e.g. plots.plot.127",
            "The value covers the range to check only, you need to assign the permission to players/groups still",
            "Modifying the value does NOT change the amount of plots players can claim"})
        public static int MAX_PLOTS = 127;
    }


    @Comment({"Backup related settings",
            "See https://wiki.intellectualsites.com/en/plotsquared/backups for more information."})
    public static final class Backup {
        @Comment("Automatically backup plots when destructive commands are performed, e.g. /plot clear")
        public static boolean AUTOMATIC_BACKUPS = true;
        @Comment("Maximum amount of backups associated with a plot") public static int
            BACKUP_LIMIT = 3;
        @Comment("Whether or not backups should be deleted when the plot is unclaimed")
        public static boolean DELETE_ON_UNCLAIM = true;
    }


    @Comment("Confirmation timeout related settings")
    public static final class Confirmation {
        @Comment("Timeout before a confirmation prompt expires") public static int
            CONFIRMATION_TIMEOUT_SECONDS = 20;
    }


    @Comment("Teleportation related settings")
    public static final class Teleport {
        @Comment("Teleport to your plot on death") public static boolean ON_DEATH = false;
        @Comment("Teleport to your plot on login") public static boolean ON_LOGIN = false;
        @Comment("Teleport to your plot on claim") public static boolean ON_CLAIM = true;
        @Comment({"Add a delay to all teleport commands",
            "Assign `plots.teleport.delay.bypass` to bypass the cooldown."}) public static int
            DELAY = 0;
        @Comment("The visit command is ordered by world instead of globally") public static boolean
            PER_WORLD_VISIT = false;
    }


    @Comment("Redstone related settings")
    public static final class Redstone {
        @Comment("Disable redstone in unoccupied plots") public static boolean DISABLE_UNOCCUPIED =
            false;
        @Comment("Disable redstone when all owners/trusted/members are offline")
        public static boolean DISABLE_OFFLINE = false;
        @Comment(
            "Detect and cancel invalid pistons on the edge of plots (e.g. placed with WorldEdit)")
        public static boolean DETECT_INVALID_EDGE_PISTONS = false;
    }


    @Comment("Claim related settings")
    public static final class Claim {
        @Comment("The max plots claimed in a single `/plot auto <size>` command") public static int
            MAX_AUTO_AREA = 4;
    }


    @Comment("Rating related settings")
    public static final class Ratings {
        @Comment("Replace the rating system with a like system. Will add /plot like/dislike,"
            + " and remove the rating command") public static boolean USE_LIKES = false;
        @Comment("Rating categories") public static List<String> CATEGORIES = new ArrayList<>();
    }


    @Comment("Enable or disable parts of the plugin specific to using Paper")
    public static final class Paper_Components {
        @Comment("Enable Paper's listeners.") public static boolean PAPER_LISTENERS = true;
        @Comment("Prevent entities from leaving plots") public static boolean ENTITY_PATHING = true;
        @Comment(
            "Cancel entity spawns when the chunk is loaded if the PlotArea's mob spawning is off")
        public static boolean CANCEL_CHUNK_SPAWN = true;
        @Comment("Use paper's PlayerLaunchProjectileEvent to cancel projectiles")
        public static boolean PLAYER_PROJECTILE = true;
        @Comment("Cancel entity spawns from spawners before they happen (performance buff)")
        public static boolean SPAWNER_SPAWN = true;
        @Comment("Cancel entity spawns from tick spawn rates before they happen (performance buff)")
        public static boolean CREATURE_SPAWN = true;
        @Comment("Check the tile entity limit on block placement")
        public static boolean TILE_ENTITY_CHECK = true;
        @Comment("Use Paper's async tab completion")
        public static boolean ASYNC_TAB_COMPLETION;
    }

    @Comment("Settings relating to PlotSquared's GlobalBlockQueue")
    public static final class QUEUE {
        @Comment({"Average time per tick spent completing chunk tasks in ms.",
        "Waits (chunk task time / target_time) ticks before completely the next task."})
        public static int TARGET_TIME = 65;
    }

    @Comment("Settings related to tab completion")
    public static final class Tab_Completions {
        @Comment({"The time in seconds how long tab completions should remain in cache.",
                 "0 will disable caching. Lower values may be less performant."})
        public static int CACHE_EXPIRATION = 15;
    }


    @Comment({"Enable or disable parts of the plugin",
        "Note: A cache will use some memory if enabled"})
    public static final class Enabled_Components { // Group the following values into a new config section
        @Comment("The database stores all the plots") public static boolean DATABASE = true;
        @Comment("Events are needed to track a lot of things") public static boolean EVENTS = true;
        @Comment("Commands are used to interact with the plugin") public static boolean COMMANDS =
            true;
        @Comment("Whether we should notify you about updates or not.") public static boolean
            UPDATE_NOTIFICATIONS = true;
        @Comment("Stores user metadata in a database") public static boolean PERSISTENT_META = true;
        @Comment("Optimizes permission checks") public static boolean PERMISSION_CACHE = true;
        @Comment("Optimizes block changing code") public static boolean BLOCK_CACHE = true;
        @Comment("Getting a rating won't need the database") public static boolean RATING_CACHE =
            true;
        @Comment("Allow WorldEdit to be restricted to plots") public static boolean
            WORLDEDIT_RESTRICTIONS = true;
        @Comment("Allow economy to be used to sell, claim or buy plots.") public static boolean ECONOMY = true;
        @Comment("Expiry will clear old or simplistic plots") public static boolean PLOT_EXPIRY =
            false;
        @Comment("Processes chunks (trimming, or entity/tile limits) ") public static boolean
            CHUNK_PROCESSOR = false;
        @Comment("Kill mobs on roads (Chicken, Cow, etc.)") public static boolean KILL_ROAD_MOBS = false;
        @Comment("Kill items on roads (Stick, Paper, etc.)") public static boolean KILL_ROAD_ITEMS = false;
        @Comment("Kill vehicles on roads (Boat, Minecart, etc.)") public static boolean KILL_ROAD_VEHICLES = false;
        @Comment("Notify a player of any missed plot comments upon plot entry") public static boolean
            COMMENT_NOTIFIER = false;
        @Comment("Let players claim entire worlds with PlotSquared") public static boolean WORLDS =
            false;
        @Comment("Actively purge invalid database entries") public static boolean DATABASE_PURGER =
            false;
        @Comment({"Delete plots when a player is banned.",
        "Note: This only works with the /minecraft:ban command. Any punishment plugin is not supported."}) public static boolean BAN_DELETER = false;
        @Comment("Allows PlaceholderAPI placeholders to be used in captions, flags, etc.")
        public static boolean EXTERNAL_PLACEHOLDERS = true;
        @Comment("Make road regeneration persistent across restarts") public static boolean
            PERSISTENT_ROAD_REGEN = false;
        @Comment({"Enable the `/plot component` preset GUI",
        "Read more about components here: https://wiki.intellectualsites.com/en/plotsquared/installation/plot-components"}) public static boolean COMPONENT_PRESETS = true;
        @Comment("Use UUID cache to complete usernames")
        public static boolean EXTENDED_USERNAME_COMPLETION = true;
        @Comment("Command aliases that will be tab completed")
        public static List<String> TAB_COMPLETED_ALIASES = Arrays.asList("plot", "plots", "p", "plotsquared", "plot2", "p2", "ps", "2", "plotme", "plotz", "ap");
        @Comment("Whether PlotSquared should hook into MvDWPlaceholderAPI or not")
        public static boolean USE_MVDWAPI = true;
    }

}
