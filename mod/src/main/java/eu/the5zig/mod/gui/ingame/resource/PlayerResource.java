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

package eu.the5zig.mod.gui.ingame.resource;

import java.util.List;

public class PlayerResource {

	private CapeResource capeResource;
	private List<ItemModelResource> itemModelResources;

	public PlayerResource() {
	}

	public CapeResource getCapeResource() {
		return capeResource;
	}

	public void setCapeResource(CapeResource capeResource) {
		this.capeResource = capeResource;
	}

	public List<ItemModelResource> getItemModelResources() {
		return itemModelResources;
	}

	public void setItemModelResources(List<ItemModelResource> itemModelResources) {
		this.itemModelResources = itemModelResources;
	}
}
