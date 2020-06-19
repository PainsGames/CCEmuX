package net.clgd.ccemux.init;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.google.gson.*;
import dan200.computercraft.ComputerCraft;
import dan200.computercraft.core.apis.http.options.Action;
import dan200.computercraft.core.apis.http.options.AddressRule;
import net.clgd.ccemux.api.emulation.EmuConfig;
import net.clgd.ccemux.config.JsonAdapter;

public class UserConfig extends EmuConfig {
	public static final String CONFIG_FILE_NAME = "ccemux.json";

	public static final String DEFAULT_FILE_NAME = "ccemux.default.json";

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private final Path dataDir;

	private final Path assetDir;

	private final Path computerDir;

	private final JsonAdapter adapter;

	public UserConfig(Path dataDir, Path assetDir, Path computerDir) {
		this.dataDir = dataDir;
		this.assetDir = assetDir;
		this.computerDir = computerDir;
		this.adapter = new JsonAdapter(gson, this);
	}

	@Nonnull
	@Override
	public Path getDataDir() {
		return dataDir;
	}

	@Nonnull
	public Path getAssetDir() {
		return assetDir;
	}

	@Nonnull
	public Path getComputerDir() {
		return computerDir;
	}

	@Override
	public void load() throws IOException {
		Path configPath = dataDir.resolve(CONFIG_FILE_NAME);
		if (Files.exists(configPath)) {
			try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
				adapter.fromJson(gson.fromJson(reader, JsonElement.class));
			}
		} else {
			JsonObject elements = new JsonObject();
			elements.add("_comment", new JsonPrimitive("This is the config file for CCEmuX. Refer to ccemux.default.json for the various options."));
			adapter.fromJson(elements);
			save();
		}
	}

	@Override
	public void save() throws IOException {
		try (Writer writer = Files.newBufferedWriter(dataDir.resolve(CONFIG_FILE_NAME), StandardCharsets.UTF_8)) {
			gson.toJson(adapter.toJson(), writer);
		}
	}

	public void saveDefault() throws IOException {
		try (Writer writer = Files.newBufferedWriter(dataDir.resolve(DEFAULT_FILE_NAME), StandardCharsets.UTF_8)) {
			gson.toJson(adapter.toDefaultJson(), writer);
		}
	}

	public void setup() {
		// Setup the properties to sync with the original.
		// computerSpaceLimit isn't technically needed, but we do it for consistency's sake.
		ComputerCraft.logComputerErrors = true;
		maxComputerCapacity.addAndFireListener((o, n) -> ComputerCraft.computerSpaceLimit = n.intValue());
		maximumFilesOpen.addAndFireListener((o, n) -> ComputerCraft.maximumFilesOpen = n);
		httpEnabled.addAndFireListener((o, n) -> ComputerCraft.httpEnabled = n);
		httpWhitelist.addAndFireListener(this::updateHttpRules);
		httpBlacklist.addAndFireListener(this::updateHttpRules);
		disableLua51Features.addAndFireListener((o, n) -> ComputerCraft.disableLua51Features = n);
		defaultComputerSettings.addAndFireListener((o, n) -> ComputerCraft.defaultComputerSettings = n);
	}

	private void updateHttpRules(String[] oldValue, String[] newValue) {
		ComputerCraft.httpRules = Collections.unmodifiableList(Stream.concat(
			Stream.of(httpBlacklist.get()).map((x) -> AddressRule.parse(x, Action.DENY.toPartial())).filter(Objects::nonNull),
			Stream.of(httpWhitelist.get()).map((x) -> AddressRule.parse(x, Action.ALLOW.toPartial())).filter(Objects::nonNull)
		).collect(Collectors.toList()));
	}
}
