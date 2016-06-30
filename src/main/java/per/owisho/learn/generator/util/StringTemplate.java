package per.owisho.learn.generator.util;

import java.util.Iterator;
import java.util.Map;

public class StringTemplate {

	private String str;
	
	@SuppressWarnings("rawtypes")
	private Map params;
	
	@SuppressWarnings("rawtypes")
	public StringTemplate(String str, Map params) {
		this.str = str;
		this.params = params;
	}
	
	@SuppressWarnings("rawtypes")
	public String toString() {
		String  result = str;
		for(Iterator it = params.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry)it.next();
			String key = (String)entry.getKey();
			Object value = entry.getValue();
			String strValue = value == null ? "" : value.toString();
			result = StringHelper.replace(result, "${"+key+"}", strValue);
		}
		return result;
	}
	
}
