package jp.co.sss.shop.util;

/**
 * 日本語文字列の操作を行うユーティリティクラス
 */
public class JPStringUtil {

	/**
	 * ひらがなをカタカナに変換する
	 * @param input 変換対象の文字列
	 * @return 変換後の文字列（ひらがながカタカナになったもの）
	 */
	public static String hiraganaToKatakana(String input) {
		if (input == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c >= '\u3041' && c <= '\u3096') {
				sb.append((char) (c + 0x60));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
