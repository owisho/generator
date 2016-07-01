package per.owisho.learn.generator.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import per.owisho.learn.generator.util.DatabaseDataTypesUtils;
import per.owisho.learn.generator.util.StringHelper;

public class Column {
	
	private Table _table;
	
	private int _sqlType;
	
	private String _sqlTypeName;
	
	private String _sqlName;
	
	private boolean _isPk;
	
	private boolean _isFk;
	
	private int _size;
	
	private int _decimalDigits;
	
	private boolean _isNullable;
	
	private boolean _isIndexed;
	
	private boolean _isUnique;
	
	private String _defaultValue;
	
	private static Log _log = LogFactory.getLog(Column.class);
	
	/*public Column(Table table,int sqlType,String sqlTypeName,
			String sqlName,int size,int decimalDigits,boolean isPk,
			boolean isNullable,boolean isIndexed,boolean isUnique,
			String defaultValue){
		
		_table = table;
		_sqlType = sqlType;
		_sqlName = sqlName;
		_sqlTypeName = sqlTypeName;
		_size = size;
		_decimalDigits = decimalDigits;
		_isPk = isPk;
		_isNullable = isNullable;
		_isIndexed = isIndexed;
		_isUnique = isUnique;
		_defaultValue = defaultValue;
		
		_log.debug(sqlName+"isPk->"+_isPk);
		
	}*/
	
	private Column(Builder builder){
		this._table = builder._table;
		this._sqlType = builder._sqlType;
		this._sqlTypeName = builder._sqlTypeName;
		this._sqlName = builder._sqlName;
		this._isPk = builder._isPk;
		this._isFk = builder._isFk;
		this._size = builder._size;
		this._decimalDigits = builder._decimalDigits;
		this._isNullable = builder._isNullable;
		this._isIndexed = builder._isIndexed;
		this._isUnique = builder._isUnique;
		this._defaultValue = builder._defaultValue;
	}
	
	public static class Builder{
		
		private Table _table;
		
		private int _sqlType;
		
		private String _sqlTypeName;
		
		private String _sqlName;
		
		private boolean _isPk;
		
		private boolean _isFk;
		
		private int _size;
		
		private int _decimalDigits;
		
		private boolean _isNullable;
		
		private boolean _isIndexed;
		
		private boolean _isUnique;
		
		private String _defaultValue;
		
		public Builder _table(Table _table){
			this._table = _table;
			return this;
		}
		
		public Builder _sqlType(int _sqlType){
			this._sqlType = _sqlType;
			return this;
		}
		
		public Builder _sqlTypeName(String _sqlTypeName){
			this._sqlTypeName = _sqlTypeName;
			return this;
		}
		
		public Builder _sqlName(String _sqlName){
			this._sqlName = _sqlName;
			return this;
		}
		
		public Builder _isPk(boolean _isPk){
			this._isPk = _isPk;
			return this;
		}
		
		public Builder _isFk(boolean _isFk){
			this._isFk = _isFk;
			return this;
		}
		
		public Builder _size(int _size){
			this._size = _size;
			return this;
		}
		
		public Builder _decimalDigits(int _decimalDigits){
			this._decimalDigits = _decimalDigits;
			return this;
		}
		
		public Builder _isNullable(boolean _isNullable){
			this._isNullable = _isNullable;
			return this;
		}
		
		public Builder _isIndexed(boolean _isIndexed){
			this._isIndexed = _isIndexed;
			return this;
		}
		
		public Builder _isUnique(boolean _isUnique){
			this._isUnique = _isUnique;
			return this;
		}
		
		public Builder _defaultValue(String _defaultValue){
			this._defaultValue = _defaultValue;
			return this;
		}
		
		public Column build(){
			return new Column(this);
		}
	}
	
	public int getSqlType(){
		return _sqlType;
	}
	
	public Table getTable(){
		return _table;
	}
	
	public int getSize(){
		return _size;
	}
	
	public int getDecimalDigits(){
		return _decimalDigits;
	}
	
	public String getSqlTypeName(){
		return _sqlTypeName;
	}
	
	public String getSqlName(){
		return _sqlName;
	}
	
	public String getSetMethod(){
		return "set"+StringHelper.capitalize(getSqlName());
	}
	
	public String getGetMethod(){
		return "get"+StringHelper.capitalize(getSqlName());
	}
	
	public boolean isPk(){
		return _isPk;
	}
	
	public boolean isFk(){
		return _isFk;
	}
	
	public final boolean isNullable(){
		return _isNullable;
	}
	
	public final boolean isIndexed(){
		return _isIndexed;
	}
	
	public boolean isUnique(){
		return _isUnique;
	}
	
	public final String getDefaultValue(){
		return _defaultValue;
	}
	
	public int hashCode(){
		return (getTable().getSqlName()+"#"+getSqlName()).hashCode();
	}
	
	public boolean equals(Object o){
		return this == o;
	}
	
	public String toString(){
		return getSqlName();
	}
	
	protected final String prefsPrefix(){
		return "tables/"+getTable().getSqlName()+"/columns/"+getSqlName();
	}
	
	public String getColumnName(){
		return StringHelper.makeAllWordFirstLetterUpperCase(getSqlName());
	}
	
	public String getColumnNameLower(){
		return StringHelper.uncapitalize(getColumnName());
	}
	
	public String getSqlNameLower(){
		return StringHelper.uncapitalize(getSqlName());
	}
	
	public boolean getIsNotIdOrVersionField() {
		return !isPk();
	}
	
	public String getValidateString() {
		String result = getNoRequiredValidateString();
		if(!isNullable()) {
			result = "required " + result;
		}
		return result;
	}
	
	public String getNoRequiredValidateString() {
		String result = "";
		if(getSqlName().indexOf("mail") >= 0) {
			result += "validate-email ";
		}
		if(DatabaseDataTypesUtils.isFloatNumber(getSqlType(), getSize(), getDecimalDigits())) {
			result += "validate-number ";
		}
		if(DatabaseDataTypesUtils.isIntegerNumber(getSqlType(), getSize(), getDecimalDigits())) {
			result += "validate-integer ";
		}
		return result;
	}
	
	public boolean getIsDateTimeColumn() {
		return DatabaseDataTypesUtils.isDate(getSqlType(), getSize(), getDecimalDigits());
	}
	
	public boolean isHtmlHidden() {
		return isPk() && _table.isSingleId();
	}
	
	public String getJavaType() {
		return DatabaseDataTypesUtils.getPreferredJavaType(getSqlType(), getSize(), getDecimalDigits());
	}
}
