package per.owisho.learn.generator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class PropertiesProvider {

	static Properties props;
	
	private PropertiesProvider(){}
	
	private static void initProperties(){
		try {
			props = loadAllProperties("generator.properties");
			String basepackage = props.getProperty("basepackage");
			String basepackageDir = basepackage.replace(".", "/");
			props.put("basepackeage_dir", basepackageDir);
			for(Iterator<Entry<Object,Object>> it = props.entrySet().iterator();it.hasNext();){
				Entry<Object,Object> entry = it.next(); 
				System.out.println("[property]"+entry.getKey()+"="+entry.getValue());
			}
			System.out.println();
		} catch (Exception e) {
			throw new RuntimeException("Load Properties error",e);
		}
	}
	
	public static Properties getProperties(){
		if(props==null){
			initProperties();
		}
		return props;
	}
	
	public static String getProperty(String key,String defaultValue){
		return getProperties().getProperty(key, defaultValue);
	}
	
	public static String getProperty(String key){
		return getProperties().getProperty(key);
	}
	
	public static Properties loadAllProperties(String resourceName) throws IOException {
		Properties properties = new Properties();
		Enumeration<URL> urls = PropertiesProvider.class.getClassLoader().getResources(resourceName);
		while(urls.hasMoreElements()){
			URL url = urls.nextElement();
			InputStream in = null;
			try {
				in = url.openStream();
				properties.load(in);
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				if(null!=in){
					in.close();
				}
			}
		}
		return properties;
		
	}
	
}
