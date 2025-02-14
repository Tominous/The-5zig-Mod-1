/*
 * Original: Copyright (c) 2015-2019 5zig [MIT]
 * Current: Copyright (c) 2019 5zig Reborn [GPLv3+]
 *
 * This file is part of The 5zig Mod
 * The 5zig Mod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The 5zig Mod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with The 5zig Mod.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.the5zig.mod.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.Version;
import eu.the5zig.mod.config.items.*;
import eu.the5zig.mod.gui.elements.ButtonRow;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.util.ColorSelectorCallback;
import eu.the5zig.mod.util.Keyboard;
import eu.the5zig.mod.util.SliderCallback;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiSettings extends Gui {

	private final String category;

	private IGuiList buttonList;
	private List<ButtonRow> buttons = Lists.newArrayList();
	private HashMap<IButton, ConfigItem> configItems = Maps.newHashMap();

	private long lastMouseMoved;
	private int lastMouseX, lastMouseY;

	public GuiSettings(Gui lastScreen, String category) {
		super(lastScreen);
		this.category = category;
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 - 100, getHeight() - 27, The5zigMod.getVars().translate("gui.done")));

		buttonList = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 32, getHeight() - 32, 0, getWidth(), buttons);
		buttonList.setDrawSelection(false);
		buttonList.setRowWidth(310);
		buttonList.setBottomPadding(2);
		buttonList.setScrollX(getWidth() / 2 + 160);
		addGuiList(buttonList);

		buttons.clear();
		configItems.clear();
		List<ConfigItem> items = The5zigMod.getConfig().getItems(category);
		for (int i = 0, itemsSize = items.size(); i < itemsSize; i++) {
			IButton button1 = getButton(items.get(i), i);
			if (button1 != null) {
				button1.setEnabled(!items.get(i).isRestricted());
				configItems.put(button1, items.get(i));
			}
			i++;
			IButton button2 = i >= itemsSize ? null : getButton(items.get(i), i);
			if (button2 != null) {
				button2.setEnabled(!items.get(i).isRestricted());
				configItems.put(button2, items.get(i));
			}

			buttons.add(new ButtonRow(button1, button2));
		}

		if ("main".equals(category)) {
			addButton(The5zigMod.getVars().createButton(0xacdc, 2, 6, 80, 20,
					I18n.translate("config.main.plugins", The5zigMod.getAPI().getPluginManager().getPlugins().size())));
			addButton(The5zigMod.getVars().createButton(999, getWidth() - 52, 6, 50, 20, I18n.translate("config.main.credits")));
			addButton(The5zigMod.getVars().createButton(0xcafe, getWidth() - 104, 6, 50, 20, I18n.translate("gui.search")));
		}
	}

	private IButton getButton(ConfigItem item, int id) {
		return getButton(item, id, getWidth());
	}

	public static IButton getButton(ConfigItem item, int id, int width) {
		if (item instanceof PlaceholderItem) {
			return null;
		}
		if (item instanceof SliderItem) {
			return The5zigMod.getVars().createSlider(id, width / 2 + (id % 2 == 0 ? -155 : 5), 0, mapSlider((SliderItem) item));
		}
		if (item instanceof SelectColorItem) {
			return The5zigMod.getVars().createColorSelector(id, width / 2 + (id % 2 == 0 ? -155 : 5), 0, 150, 20, item.translate(), mapColor((SelectColorItem) item));
		}

		return The5zigMod.getVars().createButton(id, width / 2 + (id % 2 == 0 ? -155 : 5), 0, 150, 20, item.translate());
	}

	public static SliderCallback mapSlider(final SliderItem sliderItem) {
		return new SliderCallback() {
			@Override
			public String translate() {
				return sliderItem.translate();
			}

			@Override
			public float get() {
				return sliderItem.get();
			}

			@Override
			public void set(float value) {
				sliderItem.set(value);
			}

			@Override
			public float getMinValue() {
				return sliderItem.getMinValue();
			}

			@Override
			public float getMaxValue() {
				return sliderItem.getMaxValue();
			}

			@Override
			public int getSteps() {
				return sliderItem.getSteps();
			}

			@Override
			public String getCustomValue(float value) {
				return sliderItem.getCustomValue(value);
			}

			@Override
			public String getSuffix() {
				return sliderItem.getSuffix();
			}

			@Override
			public void action() {
				sliderItem.setChanged(true);
				sliderItem.action();
				The5zigMod.getConfig().save();
			}
		};
	}

	public static ColorSelectorCallback mapColor(final SelectColorItem item) {
		return new ColorSelectorCallback() {
			@Override
			public ChatColor getColor() {
				return item.get();
			}

			@Override
			public void setColor(ChatColor color) {
				item.set(color);
			}
		};
	}

	@Override
	public void drawScreen0(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen0(mouseX, mouseY, partialTicks);

		if (lastMouseX != mouseX || lastMouseY != mouseY) {
			lastMouseX = mouseX;
			lastMouseY = mouseY;
			lastMouseMoved = System.currentTimeMillis();
		}
		if (System.currentTimeMillis() - lastMouseMoved > 700) {
			IButton hovered = getHoveredButton(mouseX, mouseY);
			if (hovered != null) {
				ConfigItem item = configItems.get(hovered);
				if (item != null) {
					String hoverText = item.getHoverText();

					List<String> lines = Lists.newArrayList();
					lines.add(hovered.getLabel());
					if (item instanceof ListItem) {
						int size = ((ListItem<?>) item).get().size();
						if (size == 1) {
							lines.add(I18n.translate("config.list.entry"));
						} else {
							lines.add(I18n.translate("config.list.entries", size));
						}
					}
					lines.addAll(The5zigMod.getVars().splitStringToWidth(hoverText, 150));
					if (!(item instanceof NonConfigItem) && Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
						lines.add(ChatColor.DARK_GRAY.toString() + ChatColor.ITALIC + I18n.translate("config.click_to_reset"));
					}
					drawHoveringText(lines, mouseX, mouseY);
				}
			}
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		tryReset();
	}

	@Override
	protected void mouseReleased(int x, int y, int state) {
		super.mouseReleased(x, y, state);
		tryReset();
	}

	private void tryReset() {
		if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
			IButton hovered = getHoveredButton(lastMouseX, lastMouseY);
			if (hovered != null && !(hovered instanceof NonConfigItem)) {
				ConfigItem item = configItems.get(hovered);
				item.reset();
				hovered.setLabel(item.translate());
				if (item.hasChanged())
					The5zigMod.getConfig().save();
			}
		}
	}

	private IButton getHoveredButton(int mouseX, int mouseY) {
		for (ButtonRow button : buttons) {
			if (button.button1 != null && isHovered(button.button1, mouseX, mouseY)) {
				return button.button1;
			} else if (button.button2 != null && isHovered(button.button2, mouseX, mouseY)) {
				return button.button2;
			}
		}
		return null;
	}

	private boolean isHovered(IButton button, int lastMouseX, int lastMouseY) {
		return lastMouseX >= button.getX() && lastMouseX <= button.getX() + button.getWidth() && lastMouseY >= button.getY() && lastMouseY <= button.getY() + button.getHeight();
	}

	@Override
	protected void actionPerformed(final IButton button) {
		if (button.getId() == 0xacdc) {
			The5zigMod.getVars().displayScreen(new GuiPlugins(this));
		} else if (button.getId() == 999) {
			The5zigMod.getVars().displayScreen(new GuiCredits(this));
		} else if (button.getId() == 0xcafe) {
			The5zigMod.getVars().displayScreen(new GuiSettingsSearch(this));
		} else if (configItems.containsKey(button)) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
				return;
			}
			ConfigItem item = configItems.get(button);
			if (item instanceof StringItem) {
				final StringItem stringItem = (StringItem) item;
				The5zigMod.getVars().displayScreen(new GuiCenteredTextfield(this, new CenteredTextfieldCallback() {
					@Override
					public void onDone(String text) {
						stringItem.set(text);
						stringItem.action();
						button.setLabel(stringItem.translate());
						if (stringItem.hasChanged())
							The5zigMod.getConfig().save();
					}

					@Override
					public String title() {
						return stringItem.translate();
					}
				}, stringItem.get(), stringItem.getMinLength(), stringItem.getMaxLength()));
			} else if (item instanceof ListItem) {
				The5zigMod.getVars().displayScreen(new GuiSettingsList(this, (ListItem<?>) item));
			} else if (!(item instanceof SliderItem)) {
				item.next();
				item.action();
				button.setLabel(item.translate());
				if (item.hasChanged())
					The5zigMod.getConfig().save();
			}
		}
	}

	@Override
	protected void tick() {
		for (Map.Entry<IButton, ConfigItem> entry : configItems.entrySet()) {
			entry.getKey().setEnabled(!entry.getValue().isRestricted());
		}
	}

	@Override
	protected void onKeyType(char character, int key) {
		if (key == Keyboard.KEY_F && The5zigMod.isCtrlKeyDown()) {
			The5zigMod.getVars().displayScreen(new GuiSettingsSearch(this));
		}
	}

	@Override
	public String getTitleName() {
		return "main".equals(category) ? "The 5zig Mod v" + Version.VERSION : null;
	}

	@Override
	public String getTitleKey() {
		return "config." + category + ".title";
	}
}
