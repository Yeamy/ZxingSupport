package com.yeamy.support.zxing;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ResultParser;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

public class ScanResult {

	private final String text;
	private final String displayContents;
	private final ParsedResultType type;
	private final BarcodeFormat codeFormat;
	private final Map<ResultMetadataType, Object> metadata;

	public ScanResult(Result rawResult) {
		codeFormat = rawResult.getBarcodeFormat();
		text = rawResult.getText();

		ParsedResult pasedResult = ResultParser.parseResult(rawResult);
		type = pasedResult.getType();
		displayContents = pasedResult.getDisplayResult().replace("\r", "");
		metadata = rawResult.getResultMetadata();
	}

	public String getDisplayContents() {
		return displayContents;
	}

	public ParsedResultType getType() {
		return type;
	}

	public String getTypeText() {
		return type.toString();
	}

	public BarcodeFormat getFormat() {
		return codeFormat;
	}

	public String getRawText() {
		return text;
	}

	public Map<ResultMetadataType, Object> getMetadata() {
		return metadata;
	}

	public CharSequence getMetadataText() {
		final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES = //
		EnumSet.of(ResultMetadataType.ISSUE_NUMBER, //
				ResultMetadataType.SUGGESTED_PRICE, //
				ResultMetadataType.ERROR_CORRECTION_LEVEL, //
				ResultMetadataType.POSSIBLE_COUNTRY);
		if (metadata != null) {
			StringBuilder metadataText = new StringBuilder(20);
			for (Map.Entry<ResultMetadataType, Object> entry : metadata.entrySet()) {
				if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
					metadataText.append(entry.getValue()).append('\n');
				}
			}
			if (metadataText.length() > 0) {
				metadataText.setLength(metadataText.length() - 1);
				return metadataText;
			}
		}
		return null;
	}

}
