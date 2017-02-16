/*
 * This file is a part of NaVisuLeapMotion
 * Copyright (C) 2017 Di Falco Nicola
 *
 * NaVisuLeapMotion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NaVisuLeapMotion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bzh.terrevirtuelle.navisuleapmotion.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 * @author serge
 */
@XmlType(name = "arcommand", propOrder = {
    "cmd",
    "arg"
})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class ArCommand {

    private String cmd;
    
    private String arg;

    /**
     * Default Constructor
     */
    public ArCommand() {
    }

    /**
     * Main Constructor
     * 
     * @param cmd The command
     * @param arg The command's argument
     */
    public ArCommand(String cmd, String arg) {
        this.cmd = cmd;
        this.arg = arg;
    }

    /**
     * Gets the command
     * 
     * @return The command
     */
    public String getCmd() {
        return cmd;
    }

    /**
     * Sets the command
     * 
     * @param cmd the new Command value
     */
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    /**
     * Gets the command's argument
     * 
     * @return the argument's value
     */
    public String getArg() {
        return arg;
    }

    /**
     * Sets the argument
     * 
     * @param arg the new argument value
     */
    public void setArg(String arg) {
        this.arg = arg;
    }

    @Override
    public String toString() {
        return "ArCommand{" + "cmd=" + cmd + ", arg=" + arg + '}';
    }


}
