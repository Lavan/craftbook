// $Id$
/*
 * CraftBook
 * Copyright (C) 2010 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

import com.sk89q.craftbook.*;

/**
 * Handler for the light switch feature.
 * 
 * @author sk89q
 */
public class LightSwitch {
    /**
     * Store a list of recent light toggles to prevent spamming. Someone
     * clever can just use two signs though.
     */
    private HistoryHashMap<BlockVector,Long> recentLightToggles
            = new HistoryHashMap<BlockVector,Long>(20);

    /**
     * Toggle lights in the immediate area.
     * 
     * @param ox
     * @param oy
     * @param oz
     * @return
     */
    public boolean toggleLights(Vector origin) {
        int aboveID = CraftBook.getBlockID(origin);

        if (aboveID == BlockType.TORCH || aboveID == BlockType.REDSTONE_TORCH_OFF
                || aboveID == BlockType.REDSTONE_TORCH_ON) {
            // We will take the status we want to switch to from the torch
            // above the switch
            boolean on = aboveID != BlockType.TORCH;

            // Prevent spam
            BlockVector bvec = origin.toBlockVector();
            Long lastUse = recentLightToggles.remove(bvec);
            long now = System.currentTimeMillis();
            if (lastUse != null && now - lastUse < 500) {
                recentLightToggles.put(bvec, lastUse);
                return true;
            }
            recentLightToggles.put(bvec, now);

            int ox = origin.getBlockX();
            int oy = origin.getBlockY();
            int oz = origin.getBlockZ();
            int changed = 0;

            for (int x = -10 + ox; x <= 10 + ox; x++) {
                for (int y = -10 + oy; y <= 10 + oy; y++) {
                    for (int z = -5 + oz; z <= 5 + oz; z++) {
                        int id = CraftBook.getBlockID(x, y, z);

                        if (id == BlockType.TORCH || id == BlockType.REDSTONE_TORCH_OFF
                                || id == BlockType.REDSTONE_TORCH_ON) {
                            // Limit the maximum number of changed lights
                            if (changed >= 20) {
                                return true;
                            }

                            if (on) {
                                CraftBook.setBlockID(x, y, z, BlockType.TORCH);
                            } else {
                                CraftBook.setBlockID(x, y, z, BlockType.REDSTONE_TORCH_OFF);
                            }

                            changed++;
                        }
                    }
                }
            }

            return true;
        }

        return false;
    }
}