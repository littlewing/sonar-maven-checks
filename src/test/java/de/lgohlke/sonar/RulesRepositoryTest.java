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
package de.lgohlke.sonar;

import com.google.common.collect.Lists;
import de.lgohlke.sonar.maven.org.codehaus.mojo.versions.rules.DependencyVersionMavenRule;
import de.lgohlke.sonar.maven.org.codehaus.mojo.versions.rules.PluginVersionMavenRule;
import org.sonar.api.rules.AnnotationRuleParser;
import org.sonar.api.rules.Rule;
import org.testng.annotations.Test;
import java.util.List;
import static org.fest.assertions.api.Assertions.assertThat;


/**
 * Created with IntelliJ IDEA.
 * User: lgohlke
 */
public class RulesRepositoryTest {
  private List<Class<? extends MavenRule>> expectedRules = Lists.newArrayList(DependencyVersionMavenRule.class, PluginVersionMavenRule.class);

  @Test
  public void shouldHaveCompleteRuleSet() throws Exception {
    AnnotationRuleParser ruleParser = new AnnotationRuleParser();
    RulesRepository rulesRepository = new RulesRepository(ruleParser);
    List<Rule> rules = rulesRepository.createRules();

    assertThat(rules).hasSameSizeAs(expectedRules);
  }
}