/*
 * SonarQube CSS Plugin
 * Copyright (C) 2013 Tamas Kende and David RACODON
 * kende.tamas@gmail.com
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
package org.sonar.css.checks;

import com.sonar.sslr.api.AstNode;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.css.parser.CssGrammar;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.sslr.parser.LexerlessGrammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * https://github.com/stubbornella/csslint/wiki/Disallow-overqualified-elements
 *
 * @author tkende
 */
@Rule(
  key = "overqualified-elements",
  name = "Name of overqualified element should be removed",
  priority = Priority.MAJOR,
  tags = {Tags.DESIGN, Tags.PERFORMANCE})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.MEMORY_EFFICIENCY)
@SqaleConstantRemediation("1h")
@ActivatedByDefault
public class DisallowOverqualifiedElements extends SquidCheck<LexerlessGrammar> {

  List<Selectors> selectors = new ArrayList<DisallowOverqualifiedElements.Selectors>();

  @Override
  public void init() {
    subscribeTo(CssGrammar.CLASS_SELECTOR);
  }

  @Override
  public void visitFile(AstNode astNode) {
    selectors.clear();
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (astNode.getPreviousSibling() != null && astNode.getPreviousSibling().is(CssGrammar.identNoWS)) {
      Selectors newSelector = new Selectors(CssChecksUtil.getStringValue(astNode),
        astNode.getPreviousSibling().getTokenValue(), astNode.getTokenLine());
      int pos = selectors.indexOf(newSelector);
      if (pos >= 0) {
        selectors.get(pos).addElement(newSelector);
      } else {
        selectors.add(newSelector);
      }
    }
  }

  @Override
  public void leaveFile(AstNode astNode) {
    for (Selectors key : selectors) {
      if (key.getElements().size() == 1) {
        getContext().createLineViolation(this, "Remove the name of this overqualified element.",
          key.getElements().entrySet().iterator().next().getValue());
      }
    }
  }

  private static class Selectors {
    String className;
    Map<String, Integer> elements = new HashMap<String, Integer>();

    public void addElement(Selectors newSelector) {
      Entry<String, Integer> elementEntry = newSelector.getElements().entrySet().iterator().next();
      addElement(elementEntry.getKey(), elementEntry.getValue());
    }

    public Selectors(String className, String element, int line) {
      this.className = className;
      elements.put(element, line);
    }

    public String getClassName() {
      return className;
    }

    public Map<String, Integer> getElements() {
      return elements;
    }

    public void addElement(String element, int line) {
      elements.put(element, line);
    }

    @Override
    public String toString() {
      return className;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (!(obj instanceof Selectors)) {
        return false;
      }
      return this.className.equals(((Selectors) obj).getClassName());
    }

    @Override
    public int hashCode() {
      return className.hashCode();
    }
  }
}
