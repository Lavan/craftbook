package com.sk89q.craftbook.mech;
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

import java.util.Random;
import java.io.*;
import sun.security.krb5.Config;
import com.sk89q.craftbook.LocalPlayer;
import com.sk89q.craftbook.MechanismsConfiguration;
import com.sk89q.worldedit.BlockVector;

/**
 * This mechanism allow players to read bookshelves and get a random line
 * from a file as as "book."
 *
 * @author sk89q
 */
public class Bookcase {

    /**
     * Used for picking random lines.
     */
    protected static Random rand = new Random();
    
    /**
     * Holds the location of the bookcase.
     */
    protected BlockVector pt;
    
    /**
     * Configuration.
     */
    protected MechanismsConfiguration config;
    
    /**
     * Construct a bookcase for a location.
     * 
     * @param pt
     * @param config 
     */
    public Bookcase(BlockVector pt, MechanismsConfiguration config) {
        this.pt = pt;
        this.config = config;
    }
    
    /**
     * Reads a book.
     * 
     * @param player
     * @param bookReadLine message to print to the user
     */
    public void read(LocalPlayer player, String bookReadLine) {
        try {
            String text = getBookLine();

            if (text != null) {
                player.print(bookReadLine);
                player.printRaw(text);
            } else {
                player.printError("Failed to fetch a line from the books file");
            }
        } catch (IOException e) {
            player.printError("Failed to read the books fiel.");
        }
    }

    /**
     * Get a line from the book lines file.
     * 
     * @return
     * @throws IOException
     */
    protected String getBookLine() throws IOException {
        RandomAccessFile file = new RandomAccessFile(
                new File(config.dataFolder, "books.txt"), "r");

        long len = file.length();
        byte[] data = new byte[500];

        for (int tries = 0; tries < 3; tries++) {
            int j = rand.nextInt((int)len);
            if (tries == 2) { // File is too small
                j = 0;
            }
            file.seek(j);
            file.read(data);

            StringBuilder buffer = new StringBuilder();
            boolean found = j == 0;
            byte last = 0;

            for (int i = 0; i < data.length; i++) {
                if (found) {
                    if (data[i] == 10 || data[i] == 13 || i >= len) {
                        if (last != 10 && last != 13) {
                            return buffer.toString();
                        }
                    } else {
                        buffer.appendCodePoint(data[i]);
                    }
                } else if (data[i] == 10 || data[i] == 13) { // Line feeds
                    found = true;
                }

                last = data[i];
            }
        }

        return null;
    }

}
