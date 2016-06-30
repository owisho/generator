package per.owisho.learn.generator.util;

import java.sql.Types;
import java.util.HashMap;

public class DatabaseDataTypesUtils {

	private final static IntStringMap _preferredJavaTypeForSqlType = new IntStringMap();
	
	/**
	 * 判断数据库数据类型是否为浮点型
	 * 如果数据库数据类型为Float或者Double或者BigDecimal类型，则返回true
	 * 否则返回false
	 * @param sqlType
	 * @param size
	 * @param decimalDigits
	 * @return
	 */
	public static boolean isFloatNumber(int sqlType,int size,int decimalDigits){
		String javaType = getPreferredJavaType(sqlType,size,decimalDigits);
		if(javaType.endsWith("Float")||javaType.endsWith("Double")||javaType.endsWith("BigDecimal")){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断数据库数据类型是否为整形
	 * 如果数据库数据类型为Integer或者Long或者Short类型，则返回true
	 * 否则返回false
	 * @param sqlType
	 * @param size
	 * @param decimalDigits
	 * @return
	 */
	public static boolean isIntegerNumber(int sqlType,int size,int decimalDigits){
		String javaType = getPreferredJavaType(sqlType, size, decimalDigits);
		if(javaType.endsWith("Long")||javaType.endsWith("Integer")||javaType.endsWith("Short")){
			return true;
		}
		return false;
			
	}
	
	/**
	 * 判断数据库数据类型是否为日期类型
	 * 如果数据库数据类型为Date或者TimeStamp类型，则返回true
	 * 否则返回false
	 * @param sqlType
	 * @param size
	 * @param decimalDigits
	 * @return
	 */
	public static boolean isDate(int sqlType,int size,int decimalDigits){
		String javaType = getPreferredJavaType(sqlType, size, decimalDigits);
		if(javaType.endsWith("Date")||javaType.endsWith("TimeStamp")){
			return true;
		}
		return false;
	}
	
	public static String getPreferredJavaType(int sqlType,int size,int decimalDigits){
		if((sqlType==Types.DECIMAL||sqlType==Types.NUMERIC)&&decimalDigits==0){
			if(size==1){
				return "java.lang.Boolean";
			}else if(size<3){
				return "java.lang.Byte";
			}else if(size<5){
				return "java.lang.Short";
			}else if(size<10){
				return "java.lang.Integer";
			}else if(size<19){
				return "java.lang.Long";
			}else{
				return "java.math.BigDecimal";
			}
		}
		String result = _preferredJavaTypeForSqlType.getString(sqlType);
		if(result==null){
			result = "java.lang.Object";
		}
		return result;
	}
	
	static{
		_preferredJavaTypeForSqlType.put(Types.TINYINT, "java.lang.Byte");
		_preferredJavaTypeForSqlType.put(Types.SMALLINT, "java.lang.Short");
		_preferredJavaTypeForSqlType.put(Types.INTEGER, "java.lang.Integer");
		_preferredJavaTypeForSqlType.put(Types.BIGINT, "java.lang.Long");
		_preferredJavaTypeForSqlType.put(Types.REAL, "java.lang.Float");
		_preferredJavaTypeForSqlType.put(Types.FLOAT, "java.lang.Double");
		_preferredJavaTypeForSqlType.put(Types.DOUBLE, "java.lang.Double");
		_preferredJavaTypeForSqlType.put(Types.DECIMAL, "java.math.BigDecimal");
		_preferredJavaTypeForSqlType.put(Types.NUMERIC, "java.math.BigDecimal");
		_preferredJavaTypeForSqlType.put(Types.BIT, "java.lang.Boolean");
		_preferredJavaTypeForSqlType.put(Types.CHAR, "java.lang.String");
		_preferredJavaTypeForSqlType.put(Types.VARCHAR, "java.lang.String");
		// according to resultset.gif, we should use java.io.Reader, but String is more convenient for EJB
		_preferredJavaTypeForSqlType.put(Types.LONGVARCHAR, "java.lang.String");
		_preferredJavaTypeForSqlType.put(Types.BINARY, "byte[]");
		_preferredJavaTypeForSqlType.put(Types.VARBINARY, "byte[]");
		_preferredJavaTypeForSqlType.put(Types.LONGVARBINARY, "java.io.InputStream");
		_preferredJavaTypeForSqlType.put(Types.DATE, "java.sql.Date");
		_preferredJavaTypeForSqlType.put(Types.TIME, "java.sql.Time");
		_preferredJavaTypeForSqlType.put(Types.TIMESTAMP, "java.sql.Timestamp");
		_preferredJavaTypeForSqlType.put(Types.CLOB, "java.sql.Clob");
		_preferredJavaTypeForSqlType.put(Types.BLOB, "java.sql.Blob");
		_preferredJavaTypeForSqlType.put(Types.ARRAY, "java.sql.Array");
		_preferredJavaTypeForSqlType.put(Types.REF, "java.sql.Ref");
		_preferredJavaTypeForSqlType.put(Types.STRUCT, "java.lang.Object");
		_preferredJavaTypeForSqlType.put(Types.JAVA_OBJECT, "java.lang.Object");
	}
	
	@SuppressWarnings({ "serial", "rawtypes" })
	private static class IntStringMap extends HashMap{
		
		public String getString(int i){
			return (String)get(i);
		} 
		
		@SuppressWarnings("unused")
		public String[] getStrings(int i){
			return (String[])get(i);
		}
		
		public void put(int i,String s){
			put(i,s);
		}
		
		@SuppressWarnings("unused")
		public void put(int i,String[] sa){
			put(i,sa);
		}
	}
	
	
}
