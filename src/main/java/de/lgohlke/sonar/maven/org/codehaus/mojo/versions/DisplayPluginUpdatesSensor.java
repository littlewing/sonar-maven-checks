/*
 * Sonar maven checks plugin
 * Copyright (C) 2012 Lars Gohlke
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
package de.lgohlke.sonar.maven.org.codehaus.mojo.versions;

import de.lgohlke.sonar.maven.MavenBaseSensor;
import de.lgohlke.sonar.maven.Rules;
import de.lgohlke.sonar.maven.SensorConfiguration;
import de.lgohlke.sonar.maven.org.codehaus.mojo.versions.rules.IncompatibleMavenVersion;
import de.lgohlke.sonar.maven.org.codehaus.mojo.versions.rules.MissingPluginVersion;
import de.lgohlke.sonar.maven.org.codehaus.mojo.versions.rules.NoMinimumMavenVersion;
import de.lgohlke.sonar.maven.org.codehaus.mojo.versions.rules.PluginVersion;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.File;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.Violation;
import org.sonar.batch.MavenPluginExecutor;

import java.util.List;

import static de.lgohlke.sonar.maven.org.codehaus.mojo.versions.Configuration.BASE_IDENTIFIER;


@Rules(
    values = {
        IncompatibleMavenVersion.class, MissingPluginVersion.class, PluginVersion.class, NoMinimumMavenVersion.class
    }
)
@SensorConfiguration(
    bridgeMojo = DisplayPluginUpdatesBridgeMojo.class,
    resultTransferHandler = DisplayPluginUpdatesSensor.ResultTransferHandler.class,
    mavenBaseIdentifier=BASE_IDENTIFIER
)
public class DisplayPluginUpdatesSensor extends MavenBaseSensor<DisplayPluginUpdatesSensor.ResultTransferHandler> {

  @Setter
  @Getter
  public static class ResultTransferHandler implements de.lgohlke.sonar.maven.ResultTransferHandler {
    private List<ArtifactUpdate> pluginUpdates;
    private List<Dependency> missingVersionPlugins;
    private boolean warninNoMinimumVersion;
    private DisplayPluginUpdatesBridgeMojo.IncompatibleParentAndProjectMavenVersion incompatibleParentAndProjectMavenVersion;
  }

  public DisplayPluginUpdatesSensor(RulesProfile rulesProfile, MavenPluginExecutor mavenPluginExecutor,
                                    MavenProject mavenProject) {
    super(rulesProfile, mavenPluginExecutor, mavenProject);
  }

  @Override
  public void analyse(final Project project, final SensorContext context) {
    ResultTransferHandler resultTransferHandler = getMojoMapper().getResultTransferHandler();

    final File file = new File("", getMavenProject().getFile().getName());

    // minimum version warning
    if (resultTransferHandler.isWarninNoMinimumVersion()) {
      Rule rule = createRuleFrom(NoMinimumMavenVersion.class);
      Violation violation = Violation.create(rule, file);
      violation.setLineId(1);
      violation.setMessage("Project does not define minimum Maven version, default is: 2.0");
      context.saveViolation(violation);
    }

    // incompatible minimum versions
    DisplayPluginUpdatesBridgeMojo.IncompatibleParentAndProjectMavenVersion incompatibleParentAndProjectMavenVersion =
        resultTransferHandler.getIncompatibleParentAndProjectMavenVersion();
    if (incompatibleParentAndProjectMavenVersion != null) {
      Rule rule = createRuleFrom(IncompatibleMavenVersion.class);
      Violation violation = Violation.create(rule, file);
      violation.setLineId(1);

      ArtifactVersion parentVersion = incompatibleParentAndProjectMavenVersion.getParentVersion();
      ArtifactVersion projectVersion = incompatibleParentAndProjectMavenVersion.getProjectVersion();
      violation.setMessage("Project does define incompatible minimum versions:  in parent pom " + parentVersion +
          " and in project pom " + projectVersion);
      context.saveViolation(violation);
    }

    // missing versions
    if (!resultTransferHandler.getMissingVersionPlugins().isEmpty()) {
      Rule rule = createRuleFrom(MissingPluginVersion.class);
      for (Dependency dependency : resultTransferHandler.getMissingVersionPlugins()) {
        Violation violation = Violation.create(rule, file);
        violation.setLineId(1);

        String artifact = dependency.getGroupId() + ":" + dependency.getArtifactId();
        violation.setMessage(artifact + " has no version");
        context.saveViolation(violation);
      }
    }

    // updates
    Rule rule = createRuleFrom(PluginVersion.class);
    for (ArtifactUpdate update : resultTransferHandler.getPluginUpdates()) {
      Violation violation = Violation.create(rule, file);
      violation.setLineId(1);
      violation.setMessage(update.toString());
      context.saveViolation(violation);
    }
  }
}
