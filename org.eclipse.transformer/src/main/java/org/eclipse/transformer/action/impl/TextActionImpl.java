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

package org.eclipse.transformer.action.impl;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.eclipse.transformer.TransformException;
import org.eclipse.transformer.action.ActionType;
import org.eclipse.transformer.action.Changes;
import org.eclipse.transformer.action.InputBuffer;
import org.eclipse.transformer.action.ByteData;
import org.eclipse.transformer.action.SelectionRule;
import org.eclipse.transformer.action.SignatureRule;
import org.slf4j.Logger;

import aQute.lib.io.ByteBufferInputStream;
import aQute.lib.io.ByteBufferOutputStream;

public class TextActionImpl extends ActionImpl<Changes> {

	public TextActionImpl(Logger logger, InputBuffer buffer, SelectionRule selectionRule, SignatureRule signatureRule) {

		super(logger, buffer, selectionRule, signatureRule);
	}

	//

	@Override
	public String getName() {
		return "Text Action";
	}

	@Override
	public ActionType getActionType() {
		return ActionType.TEXT;
	}

	@Override
	public String getAcceptExtension() {
		throw new UnsupportedOperationException("Text does not use this API");
	}

	@Override
	public boolean accept(String resourceName, File resourceFile) {
		if (signatureRule.getTextSubstitutions(resourceName) != null) {
			return true;
		}
		return false;
	}

	//

	@Override
	public ByteData apply(ByteData inputData) throws TransformException {

		String outputName = inputData.name();

		setResourceNames(inputData.name(), outputName);

		InputStream inputStream = new ByteBufferInputStream(inputData.buffer());
		InputStreamReader inputReader = new InputStreamReader(inputStream, UTF_8);

		BufferedReader reader = new BufferedReader(inputReader);

		ByteBufferOutputStream outputStream = new ByteBufferOutputStream(inputData.length());
		OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream, UTF_8);

		BufferedWriter writer = new BufferedWriter(outputWriter);

		try {
			transform(inputData.name(), reader, writer); // throws IOException
		} catch (IOException e) {
			getLogger().error("Failed to transform [ {} ]", inputData.name(), e);
			return null;
		}

		try {
			writer.flush(); // throws
		} catch (IOException e) {
			getLogger().error("Failed to flush [ {} ]", inputData.name(), e);
			return null;
		}

		if (!hasNonResourceNameChanges()) {
			return null;
		}

		ByteData outputData = new ByteDataImpl(outputName, outputStream.toByteBuffer());
		return outputData;
	}

	//

	protected void transform(String inputName, BufferedReader reader, BufferedWriter writer) throws IOException {

		String inputLine;
		while ((inputLine = reader.readLine()) != null) {
			String outputLine = replaceText(inputName, inputLine);
			if (outputLine == null) {
				outputLine = inputLine;
			} else {
				addReplacement();
			}
			writer.write(outputLine);
			writer.write('\n');
		}
	}
}
