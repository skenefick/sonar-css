/*
 * SonarQube CSS Plugin
 * Copyright (C) 2013 Tamas Kende
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

import org.junit.Test;
import org.sonar.css.CssAstScanner;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.checks.CheckMessagesVerifier;

import java.io.File;

public class BewareOfBoxModelTest {

  @Test
  public void test() {
    BewareOfBoxModel check = new BewareOfBoxModel();
    SourceFile file = CssAstScanner.scanSingleFile(new File(
      "src/test/resources/checks/boxSizing.css"), check);
    CheckMessagesVerifier.verify(file.getCheckMessages()).next()
      .atLine(14).withMessage("Check this potential box sizing issue").next()
      .atLine(33).withMessage("Check this potential box sizing issue").noMore();
  }

  @Test
  public void test2() {
    BewareOfBoxModel check = new BewareOfBoxModel();
    SourceFile file = CssAstScanner.scanSingleFile(new File(
      "src/test/resources/checks/fontface.css"), check);
    CheckMessagesVerifier.verify(file.getCheckMessages()).noMore();
  }

}
