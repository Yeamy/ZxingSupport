package com.yeamy.support.zxing.decode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import static com.google.zxing.BarcodeFormat.CODABAR;
import static com.google.zxing.BarcodeFormat.CODE_128;
import static com.google.zxing.BarcodeFormat.CODE_39;
import static com.google.zxing.BarcodeFormat.CODE_93;
import static com.google.zxing.BarcodeFormat.EAN_13;
import static com.google.zxing.BarcodeFormat.EAN_8;
import static com.google.zxing.BarcodeFormat.ITF;
import static com.google.zxing.BarcodeFormat.QR_CODE;
import static com.google.zxing.BarcodeFormat.RSS_14;
import static com.google.zxing.BarcodeFormat.RSS_EXPANDED;
import static com.google.zxing.BarcodeFormat.UPC_A;
import static com.google.zxing.BarcodeFormat.UPC_E;

public class DecodeConfig {

    public static DecodeConfig defaultConfig() {
        DecodeConfig config = new DecodeConfig();
        config.setDecodeFormats(DEFAULT_DECODE_FORMATS);
        return config;
    }

    public static final EnumSet<BarcodeFormat> DEFAULT_DECODE_FORMATS = EnumSet.of(//
            UPC_A, UPC_E, EAN_13, EAN_8, RSS_14, RSS_EXPANDED, // PRODUCT_FORMATS
            CODE_39, CODE_93, CODE_128, ITF, CODABAR, // INDUSTRIAL_FORMATS
            QR_CODE);

    private final Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
    private Collection<BarcodeFormat> decodeFormats = EnumSet.noneOf(BarcodeFormat.class);

//	public void putBaseHints(Map<DecodeHintType, ?> baseHints) {
//		if (baseHints != null) {
//			hints.putAll(baseHints);
//		}
//	}

    public void setDecodeFormats(Collection<BarcodeFormat> decodeFormats) {
        this.decodeFormats = decodeFormats;
    }

    public void addDecodeFormat(BarcodeFormat format) {
        decodeFormats.add(format);
    }

    public void setCharacterSet(String characterSet) {
        if (characterSet != null) {
            hints.put(DecodeHintType.CHARACTER_SET, characterSet);
        }
    }

    void setCallback(ResultPointCallback resultPointCallback) {
        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
    }

    Map<DecodeHintType, Object> build() {
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        return hints;
    }

}
