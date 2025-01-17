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

import java.io.File;

import org.junit.Test;
import org.sonar.css.CssAstScanner;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.checks.CheckMessagesVerifier;

public class DuplicatedPropertiesTest {

  @Test
  public void should_find_some_duplicated_properties_and_raise_some_issues() {
    SourceFile file = CssAstScanner.scanSingleFile(
      new File("src/test/resources/checks/duplicatedProperties.css"),
      new DuplicateProperties());
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(9).withMessage("Remove this duplicated property: \"border\".")
      .next().atLine(14).withMessage("Remove this duplicated property: \"border\".")
      .next().atLine(15).withMessage("Remove this duplicated property: \"border\".")
      .next().atLine(20).withMessage("Remove this duplicated property: \"color\".")
      .next().atLine(22).withMessage("Remove this duplicated property: \"border\".")
      .next().atLine(23).withMessage("Remove this duplicated property: \"border\".")
      .next().atLine(29).withMessage("Remove this duplicated property: \"border\".")
      .next().atLine(48).withMessage("Remove this duplicated property: \"border\".")
      .noMore();
  }

  @Test
  public void should_not_fin_any_duplicated_properties_and_not_raise_any_issues() {
    DuplicateProperties check = new DuplicateProperties();
    SourceFile file = CssAstScanner.scanSingleFile(new File(
      "src/test/resources/checks/noDuplication.css"), check);
    CheckMessagesVerifier.verify(file.getCheckMessages()).noMore();
  }

}
