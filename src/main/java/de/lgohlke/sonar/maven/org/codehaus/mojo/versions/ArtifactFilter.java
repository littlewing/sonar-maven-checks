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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.fest.util.Preconditions;

import java.util.List;

/**
 * User: lars
 */
@Slf4j
public class ArtifactFilter {
  @Getter
  private final List<String> whitelistRegexList = Lists.newArrayList();
  @Getter
  private final List<String> blacklistRegexList = Lists.newArrayList();
  private String whitelistRegex;
  private String blacklistRegex;

  public ArtifactFilter() {
  }

  public ArtifactFilter(String whiteListRegex) {
    whitelistRegexList.add(whiteListRegex);
  }

  public ArtifactFilter(String whiteListRegex, String blackListRegex) {
    addWhitelistRegex(whiteListRegex);
    addBlacklistRegex(blackListRegex);
  }

  public boolean acceptArtifact(String groupIdArtifactIdVersion) {
    if (whitelistRegex == null) {
      whitelistRegex = buildRegex(whitelistRegexList);
    }
    if (blacklistRegex == null) {
      blacklistRegex = buildRegex(blacklistRegexList);
    }

    boolean whitelistMatches = groupIdArtifactIdVersion.matches(whitelistRegex);
    boolean blacklistMatches = groupIdArtifactIdVersion.matches(blacklistRegex);

    log.debug("testing \"{}\"", groupIdArtifactIdVersion);
    log.debug("\t whitelist regex: {}", whitelistRegex);
    log.debug("\t matches whitelist: {}", whitelistMatches);
    log.debug("\t blacklist regex: {}", blacklistRegex);
    log.debug("\t matches blacklist: {}", blacklistMatches);

    return whitelistMatches && !blacklistMatches;
  }

  private String buildRegex(List<String> regexList) {
    return regexList.isEmpty() ? "" : "(" + Joiner.on(")|(").join(regexList) + ")";
  }

  public ArtifactFilter addWhitelistRegex(String regex) {
    Preconditions.checkNotNullOrEmpty(regex);
    log.debug("adding whitelist regex {}", regex);
    whitelistRegexList.add(regex);
    whitelistRegex = null;
    return this;
  }

  public ArtifactFilter addBlacklistRegex(String regex) {
    Preconditions.checkNotNullOrEmpty(regex);
    log.debug("adding blacklist regex {}", regex);
    blacklistRegexList.add(regex);
    blacklistRegex = null;
    return this;
  }
}
