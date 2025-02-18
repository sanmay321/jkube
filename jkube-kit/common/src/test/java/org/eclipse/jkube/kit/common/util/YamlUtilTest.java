/**
 * Copyright (c) 2019 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at:
 *
 *     https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.jkube.kit.common.util;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.entry;
import static org.eclipse.jkube.kit.common.util.YamlUtil.getPropertiesFromYamlString;
import static org.eclipse.jkube.kit.common.util.YamlUtil.splitYamlResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class YamlUtilTest {

  @Test
  void getPropertiesFromYamlStringEmptyStringTest() throws Exception {
    // Given
    final String yamlString = "";
    // When
    final Properties result = getPropertiesFromYamlString(yamlString);
    // Then
    assertThat(result).isNotNull().isEmpty();
  }

  @Test
  void getPropertiesFromYamlStringNullStringTest() throws Exception {
    // When
    final Properties result = getPropertiesFromYamlString(null);
    // Then
    assertThat(result).isNotNull().isEmpty();
  }

  @Test
  void getPropertiesFromYamlStringInvalidStringTest() {
    assertThrows(JsonMappingException.class, () -> getPropertiesFromYamlString("not\na\nvalid\nyaml"));
  }

  @Test
  void getPropertiesFromYamlStringValidStringTest() throws Exception {
    // Given
    final String yamlString = "---\ntest: 1\nlist:\n  - name: item 1\n    value: value 1\nstill-test: 1";
    // When
    final Properties result = getPropertiesFromYamlString(yamlString);
    // Then
    assertThat(result).isNotNull().hasSize(4)
            .containsOnly(
                    entry("test", "1"),
                    entry("list[0].name", "item 1"),
                    entry("list[0].value", "value 1"),
                    entry("still-test", "1")
            );
  }

  // https://bugs.eclipse.org/bugs/show_bug.cgi?id=561261
  @Test
  void getPropertiesFromYamlCWE502Test() throws Exception {
    // Given
    final String yamlString = "maps: !!javax.script.ScriptEngineManager [!!java.net.URLClassLoader [[!!java.net.URL [\\\"http://localhost:9000/\\\"]]]]";
    // When
    final Properties result = getPropertiesFromYamlString(yamlString);
    // Then
    assertThat(result).isNotNull()
            .hasSize(1)
            .containsOnly(entry("maps[0][0][0][0]", "\\\"http://localhost:9000/\\\""));
  }

  @Test
  void splitYamlResourceTest() throws Exception {
    // Given
    final URL resource = YamlUtilTest.class.getResource("/util/yaml-list.yml");
    // When
    final List<String> result = splitYamlResource(resource);
    // Then
    assertThat(result).isNotNull().hasSize(4);
    assertThat(result.get(1)).contains("name: \"YAML --- 2\"");
    assertThat(result.get(3)).startsWith("---\nname: \"Edge case --- 1");
  }

}