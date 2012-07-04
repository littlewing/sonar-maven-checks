/*
 * Sonar maven checks plugin
 * Copyright (C) 2012 ${owner}
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package de.lgohlke.sonar.maven.handler;

import de.lgohlke.sonar.maven.plugin.versions.DependencyVersionMavenRule;
import de.lgohlke.sonar.maven.plugin.versions.PluginVersionMavenRule;

import de.lgohlke.sonar.plugin.MavenRule;


public enum GOAL {
  DISPLAY_DEPENDENCY_UPDATES {
    @Override
    public String goal() {
      return "versions:display-dependency-updates";
    }

    @Override
    public Class<? extends UpdateHandler> handler() {
      return DisplayDependencyUpdatesHandler.class;
    }

    @Override
    public MavenRule rule() {
      return new DependencyVersionMavenRule();
    }
  },
  DISPLAY_PLUGIN_UPDATES
  {
    @Override
    public String goal() {
      return "versions:display-plugin-updates";
    }

    @Override
    public Class<? extends UpdateHandler> handler() {
      return DisplayPluginUpdatesHandler.class;
    }

    @Override
    public MavenRule rule() {
      return new PluginVersionMavenRule();
    }
  };
  public abstract String goal();

  public abstract MavenRule rule();

  public abstract Class<? extends UpdateHandler> handler();
}