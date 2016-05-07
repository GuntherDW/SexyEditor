package net.intellij.plugins.sexyeditor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NonNls;

import javax.swing.Icon;
import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Sexy editor plugin.
 */
@State(
		name = SexyEditor.COMPONENT_NAME,
		storages = {@Storage(id = "sexyeditor", file = "$APP_CONFIG$/sexyeditor.xml")})
public class SexyEditor implements ApplicationComponent, Configurable, PersistentStateComponent<SexyEditor.State> {

	public static final String COMPONENT_NAME = "SexyEditor";

	private static SexyEditor sexyEditor;

	/**
	 * Returns {@link net.intellij.plugins.sexyeditor.SexyEditor} instance.
	 */
	public static SexyEditor getInstance() {
		if (sexyEditor == null) {
			Application app = ApplicationManager.getApplication();
			if (app == null) {
				sexyEditor = new SexyEditor();
			} else {
				sexyEditor = app.getComponent(SexyEditor.class);
			}
		}
		return sexyEditor;
	}

	private SexyEditorListener editorListener;

	/**
	 * Registers new editor factory listener.
	 */
	public void initComponent() {
		if (state.allConfigs.isEmpty()) {
			state.allConfigs.add(new BackgroundConfiguration());
		}
		editorListener = new SexyEditorListener(getState());
		EditorFactory.getInstance().addEditorFactoryListener(editorListener);
	}

	/**
	 * Removes editor listener.
	 */
	public void disposeComponent() {
		EditorFactory.getInstance().removeEditorFactoryListener(editorListener);
		editorListener = null;
	}

	/**
	 * Returns component name.
	 */
	@NotNull
	public String getComponentName() {
		return COMPONENT_NAME;
	}

	// ---------------------------------------------------------------- configurable

	private SexyEditorConfigurationPanel configurationComponent;
	private Icon[] pluginIcons;
	private static Random rnd = new Random();

	/**
	 * Returns display name.
	 */
	@Nls
	public String getDisplayName() {
		return COMPONENT_NAME;
	}

	/**
	 * Returns random 32x32 icon representing the plugin.
	 */
	@Nullable
	public Icon getIcon() {
		if (pluginIcons == null) {
			pluginIcons = new Icon[5];
			for (int i = 0; i < pluginIcons.length; i++) {
				pluginIcons[i] = IconLoader.getIcon("/net/intellij/plugins/sexyeditor/gfx/girl" + (i + 1) +".png");
			}
		}
		return pluginIcons[rnd.nextInt(pluginIcons.length)];
	}

	/**
	 * No help is available. 
	 */
	@Nullable
	@NonNls
	public String getHelpTopic() {
		return null;
	}

	/**
	 * Returns the user interface component for editing the configuration.
	 */
	public JComponent createComponent() {
		if (configurationComponent == null) {
			configurationComponent = new SexyEditorConfigurationPanel();
			configurationComponent.load(state.allConfigs);
		}
		return configurationComponent.getPanel();
	}

	/**
	 * Checks if the settings in the configuration panel were modified by the user and
	 * need to be saved.
	 */
	public boolean isModified() {
		return configurationComponent.isModified();
	}

	/**
	 * Store the settings from configurable to other components.
	 * Repaints all editors.
	 */
	public void apply() throws ConfigurationException {
		state.allConfigs = configurationComponent.save();
		for (BackgroundConfiguration cfg : state.allConfigs) {
			cfg.repaintAllEditors();
		}
	}

	/**
	 * Load settings from other components to configurable.
	 */
	public void reset() {
		configurationComponent.load(state.allConfigs);
	}

	/**
	 * Disposes the Swing components used for displaying the configuration.
	 */
	public void disposeUIResources() {
		configurationComponent = null;
	}

	// ---------------------------------------------------------------- state

	/**
	 * Configuration state.
	 */
	public static final class State {
		public List<BackgroundConfiguration> allConfigs = new ArrayList<BackgroundConfiguration>();
	}

	private final State state = new State();

	/**
	 * Returns plugin state.
	 */
	public SexyEditor.State getState() {
		return state;
	}

	/**
	 * Loads state from configuration file.
	 */
	public void loadState(SexyEditor.State state) {
		XmlSerializerUtil.copyBean(state, this.state);
	}
}
