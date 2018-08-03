package invonate.cn.ygscanner.Util;

import java.math.BigDecimal;
import java.util.Random;

public class FormatUtil {
	public static boolean isEmpty(String in) {
		if (in == null)
			return true;
		if (in.trim().equals(""))
			return true;
		return false;
	}

	public static String replace(String source, String oldString,
			String newString) {
		if (isEmpty(source) || oldString == null || newString == null)
			return "";
		StringBuffer output = new StringBuffer();
		int lengthOfSource = source.length(); // 源字符串长度
		int lengthOfOld = oldString.length(); // 老字符串长度
		int posStart = 0; // 开始搜索位置

		int pos; // 搜索到老字符串的位置

		while ((pos = source.indexOf(oldString, posStart)) >= 0) {
			output.append(source.substring(posStart, pos));
			output.append(newString);
			posStart = pos + lengthOfOld;
		}
		if (posStart < lengthOfSource)
			output.append(source.substring(posStart));
		return output.toString();
	}

	public static String toHtmlInput(String in) {
		if (in == null)
			return "";
		String html = in;
		html = replace(html, "&", "&amp;");
		html = replace(html, "\"", "&quot;");
		html = replace(html, "<", "&lt;");
		html = replace(html, ">", "&gt;");
		return html;
	}

	public static String toHTML(String in) {
		if (in == null)
			return "";
		String html = in;
		html = toHtmlInput(html);
		html = replace(html, "\n", "<br>"); // [\n,\r\n--><br>]
		html = replace(html, " ", "&nbsp;");
		html = replace(html, "\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		return html;
	}

	public static String toJavascriptVar(String in) {
		if (in == null)
			return "";
		String html = replace(in, "\\", "\\\\");
		html = replace(html, "\r", "\\r");
		html = replace(html, "\n", "\\n");
		html = replace(html, "\'", "\\\'");
		html = replace(html, "\"", "\\\"");
		return html;
	}

	public static String toSQL(String in) {
		return replace(in, "'", "''");
	}

	public static String toDbString(String in) {
		if (in == null)
			return "";
		String newSTR = "";
		try {
			newSTR = new String(in.getBytes(), "ISO-8859-1");
		} catch (Exception e) {
			//System.out.println("ERR:" + e.toString());
		}
		return newSTR;
	}

	public static String toPageString(String in) {
		if (in == null)
			return "";
		String newSTR = "";
		try {
			newSTR = new String(in.getBytes("ISO-8859-1"));
		} catch (Exception e) {
			System.out.println("ERR:" + e.toString());
		}
		return newSTR;
	}

	public static String toAjaxString(String in) {
		return replace(in, "\r\n", ""); // [\n,\r\n-->""]
	}

	/**
	 * 随机产生N位随机数
	 * @param code_len
	 * @return
	 * @author hzh
	 * @date 2012-5-18
	 * @version 1.0
	 */
	public static String validateCode(int code_len) {   
	      int count = 0;   
		        char str[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };   
		      StringBuffer pwd = new StringBuffer("");   
		      Random r = new Random();   
		        while (count < code_len) {   
		            int i = Math.abs(r.nextInt(10));   
		           if (i >= 0 && i < str.length) {   
		               pwd.append(str[i]);   
		               count++;   
		           }   
		       }   
		      return pwd.toString();     
	}
	
	public static double toFourNumber(double in) {
		BigDecimal   b   =   new   BigDecimal(in);
		double   f1   =   b.setScale(4,   BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}
	
	public static void main(String[] args) {
		double s=toFourNumber(2.99145299145299*600);
				System.out.println(s);
	}
}
