package invonate.cn.ygscanner.Util;
 
public class TextUtil {

  public static String nullToString(String str){
    return str==null?"":str;    
  }
  
  public static boolean isEmpty(String str){    
    if(str==null) return true;
    if(str.equals("")) return true;
    return false;      
  }
  
  public static String toHTML(String str){
      if (str == null) {
          return null;
      }
      String html=str;
      //html = toInputHTML(html);
      html = replace(html, "\n", "<br>");      
      html = replace(html, " ", "&nbsp;");
      //html = replace(html, "\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
      return html;  
  }
  
  public static String toInputHTML(String str){
      if (str == null) {
          return null;
      }
      String html = new String(str);
      html = replace(html, "&", "&amp;");
      html = replace(html, "\"", "&quot;");
      html = replace(html, "<", "&lt;");
      html = replace(html, ">", "&gt;");
      return html;
  }
  
  public static String replace(String source, String oldString, String newString) {
      StringBuffer output = new StringBuffer();
      int lengthOfSource = source.length(); 
      int lengthOfOld = oldString.length(); 
      int posStart = 0;  
      int pos;  
      while ( (pos = source.indexOf(oldString, posStart)) >= 0) {
          output.append(source.substring(posStart, pos));

          output.append(newString);
          posStart = pos + lengthOfOld;
      }
      if (posStart < lengthOfSource) {
          output.append(source.substring(posStart));
      }
      return output.toString();
  }
  
  public static String repeatChar(String c,int rnum){
    if(c==null || rnum<1) return "";
    String rstring="";
    for(int i=0;i<rnum;i++){
      rstring+=c;  
    }
    return rstring;
  }
	public static String copyChar(String c, int n) {
		if (c == null || n < 1)
			return "";
		String rtnStr = "";
		for (int i = 0; i < n; i++) {
			rtnStr = rtnStr + c;
		}
		return rtnStr;
	}
}
