/********************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: (EPL-2.0 OR Apache-2.0)
 ********************************************************************************/

package org.eclipse.transformer.action;

import org.slf4j.Logger;

public interface Changes {
	String getInputResourceName();

	void setInputResourceName(String inputResourceName);

	String getOutputResourceName();

	void setOutputResourceName(String outputResourceName);

	int getReplacements();

	void addReplacement();

	void addReplacements(int additions);

	void addNestedInto(ContainerChanges containerChanges);

	boolean hasChanges();

	boolean hasNonResourceNameChanges();

	boolean hasResourceNameChange();

	void clearChanges();

	void log(Logger logger, String inputPath, String outputPath);
}
