/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package eu.cloudnetservice.sign.module.config;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.utility.document.Document;
import eu.cloudnetservice.sign.core.models.SearchingAnimation;
import eu.cloudnetservice.sign.core.models.SignGroupLayouts;
import eu.cloudnetservice.sign.core.models.SignLayout;
import eu.cloudnetservice.sign.core.models.SignLayoutConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

public class ConfigSignLayout {

    private final Path path = Paths.get("local/signLayout.json");

    public ConfigSignLayout() {
        if (!Files.exists(path)) {
            new Document().append("layout_config",
                Document.GSON.toJsonTree(new SignLayoutConfig(true,
                    false,
                    1D,
                    0.8D,
                    Collections.singletonList(new SignGroupLayouts(
                        "default",
                        Arrays.asList(new SignLayout("empty",
                                new String[] {"%server%", "&e%state%", "%online_players%/%max_players%", "%motd%"},
                                159,
                                "BROWN_TERRACOTTA",
                                0),
                            new SignLayout(
                                "online",
                                new String[] {"%server%", "&e%state%", "%online_players%/%max_players%", "%motd%"},
                                159,
                                "BROWN_TERRACOTTA",
                                0),
                            new SignLayout("full",
                                new String[] {"%server%", "&ePREMIUM", "%online_players%/%max_players%", "%motd%"},
                                159,
                                "BROWN_TERRACOTTA",
                                0),
                            new SignLayout(
                                "maintenance",
                                new String[] {"§8§m---------", "maintenance", "§cmode", "§8§m---------"},
                                159,
                                "BROWN_TERRACOTTA",
                                0)))),
                    new SearchingAnimation(33,
                        11,
                        Arrays.asList(new SignLayout(
                                "loading1",
                                new String[] {"", "server loads...", "o                ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading2",
                                new String[] {"", "server loads...", " o               ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading3",
                                new String[] {"", "server loads...", "  o              ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading4",
                                new String[] {"", "server loads...", "   o             ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading5",
                                new String[] {"", "server loads...", "    o            ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading6",
                                new String[] {"", "server loads...", "o    o           ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading7",
                                new String[] {"", "server loads...", " o    o          ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading8",
                                new String[] {"", "server loads...", "  o    o         ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading9",
                                new String[] {"", "server loads...", "   o    o        ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading10",
                                new String[] {"", "server loads...", "    o    o       ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading11",
                                new String[] {"", "server loads...", "o    o    o      ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading12",
                                new String[] {"", "server loads...", " o    o    o     ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading13",
                                new String[] {"", "server loads...", "  o    o    o    ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading14",
                                new String[] {"", "server loads...", "   o    o    o   ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading15",
                                new String[] {"", "server loads...", "    o    o    o  ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading16",
                                new String[] {"", "server loads...", "o    o    o    o ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading17",
                                new String[] {"", "server loads...", " o    o    o    o", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading18",
                                new String[] {"", "server loads...", "  o    o    o    ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading19",
                                new String[] {"", "server loads...", "   o    o    o   ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading20",
                                new String[] {"", "server loads...", "    o    o    o   ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading21",
                                new String[] {"", "server loads...", "     o    o    o ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading22",
                                new String[] {"", "server loads...", "      o    o    o", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading23",
                                new String[] {"", "server loads...", "       o    o    ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading24",
                                new String[] {"", "server loads...", "        o    o   ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading25",
                                new String[] {"", "server loads...", "         o    o  ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading26",
                                new String[] {"", "server loads...", "          o    o ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading27",
                                new String[] {"", "server loads...", "           o    o", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading28",
                                new String[] {"", "server loads...", "            o    ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading29",
                                new String[] {"", "server loads...", "             o   ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading30",
                                new String[] {"", "server loads...", "              o  ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading31",
                                new String[] {"", "server loads...", "               o ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading32",
                                new String[] {"", "server loads...", "                o", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14),
                            new SignLayout(
                                "loading33",
                                new String[] {"", "server loads...", "                 ", ""},
                                159,
                                "BROWN_TERRACOTTA",
                                14))))))
                          .saveAsConfig(path);
        }
    }

    public ConfigSignLayout saveLayout(SignLayoutConfig signLayoutConfig) {
        Document document = Document.loadDocument(path);
        document.append("layout_config", Document.GSON.toJsonTree(signLayoutConfig));
        document.saveAsConfig(path);
        return this;
    }

    public SignLayoutConfig loadLayout() {
        Document document = Document.loadDocument(path);

        if (!document.getDocument("layout_config").contains("knockbackOnSmallDistance")) {
            Document document1 = document.getDocument("layout_config").append("knockbackOnSmallDistance", false);
            document.append("layout_config", document1);
            document.saveAsConfig(path);
        }

        if (!document.getDocument("layout_config").contains("distance")) {
            Document document1 = document.getDocument("layout_config").append("distance", 1D);
            document.append("layout_config", document1);
            document.saveAsConfig(path);
        }

        if (!document.getDocument("layout_config").contains("strength")) {
            Document document1 = document.getDocument("layout_config").append("strength", 0.8D);
            document.append("layout_config", document1);
            document.saveAsConfig(path);
        }
        SignLayoutConfig signLayoutConfig = document.getObject("layout_config", TypeToken.get(SignLayoutConfig.class).getType());

        boolean injectable = false;

        for (SignGroupLayouts groupLayouts : signLayoutConfig.getGroupLayouts()) {
            SignLayout signLayout = groupLayouts.getLayouts().stream().filter(signLayout1 -> signLayout1.getName().equalsIgnoreCase("empty")).findFirst().orElse(
                null);
            if (signLayout == null) {
                groupLayouts.getLayouts().add(new SignLayout("empty",
                    new String[] {"%server%", "&6%state%", "%online_players%/%max_players%", "%motd%"},
                    159,
                    "BROWN_TERRACOTTA",
                    1));
                injectable = true;
            }
        }

        if (injectable) {
            document.append("layout_config", Document.GSON.toJsonTree(signLayoutConfig)).saveAsConfig(path);
        }

        return signLayoutConfig;
    }
}
