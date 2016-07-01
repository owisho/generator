package per.owisho.learn.generator.model;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import per.owisho.learn.generator.DbModelProvider;
import per.owisho.learn.generator.util.StringHelper;

public class Table {

	String sqlName;

	String className;

	private String ownerSynonymName = null;

	@SuppressWarnings("rawtypes")
	List columns = new ArrayList();

	@SuppressWarnings("rawtypes")
	List primaryKeyColumns = new ArrayList();

	public String getClassName() {
		return className == null ? StringHelper.makeAllWordFirstLetterUpperCase(sqlName) : className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@SuppressWarnings("rawtypes")
	public List getColumns(){
		return columns;
	}
	
	public void setColumns(@SuppressWarnings("rawtypes") List columns){
		this.columns = columns;
	}
	
	public String getOwenerSynonymName(){
		return ownerSynonymName;
	}
	
	public void setOwenerSynonymName(String ownerSynonymName){
		this.ownerSynonymName = ownerSynonymName;
	}
	
	@SuppressWarnings("rawtypes")
	public List getPrimaryKeyColumns(){
		return primaryKeyColumns;
	}
	
	public void setPrimaryKeyColumns(@SuppressWarnings("rawtypes") List primaryKeyColumns){
		this.primaryKeyColumns = primaryKeyColumns;
	}
	
	public String getSqlName(){
		return sqlName;
	}
	
	public void setSqlName(String sqlName){
		this.sqlName = sqlName;
	}
	
	@SuppressWarnings("unchecked")
	public void addColumn(Column cloumn){
		columns.add(cloumn);
	}
	
	public boolean isSingleId(){
		int pkCount = 0;
		for(int i=0;i<columns.size();i++){
			Column c = (Column)columns.get(i);
			if(c.isPk()){
				pkCount++;
			}
		}
		return pkCount>1?false:true;
	}
	
	public boolean isCompositeId(){
		return !isSingleId();
	}
	
	public List getCompositeIdColumns(){
		List results = new ArrayList();
		List columns = getColumns();
		for(int i=0;i<columns.size();i++){
			Column c = (Column)columns.get(i);
			if(c.isPk())
				results.add(c);
		}
		return results;
	}
	
	public Column getIdColumn(){
		List columns = getColumns();
		for(int i=0;i<columns.size();i++){
			Column c = (Column)columns.get(i);
			if(c.isPk())
				return c;
		}
		return null;
	}
	
	public void initImportedKeys(DatabaseMetaData dbmd) throws java.sql.SQLException{
		ResultSet fkeys = dbmd.getImportedKeys(catalog,schema,this.sqlName);
		while(fkeys.next()){
			String pktable = fkeys.getString(PKTABLE_NAME);
			String pkcol = fkeys.getString(PKCOLUMN_NAME);
			String fktable = fkeys.getString(FKTABLE_NAME);
			String fkcol = fkeys.getString(FKCOLUMN_NAME);
			String seq = fkeys.getString(KEY_SEQ);
			Integer iseq = new Integer(seq);
			getImportedKeys().addForeignKey(pktable,pkcol,fkcol,iseq);
		}
		fkeys.close();
	}
	
	public void initExportedKeys(DatabaseMetaData dbmd) throws java.sql.SQLException{
		ResultSet fkeys = dbmd.getExportedKeys(catalog, schema, this.sqlName);
		while(fkeys.next()){
			String pktable = fkeys.getString(PKTABLE_NAME);
			String pkcol = fkeys.getString(PKCOLUMN_NAME);
			String fktable = fkeys.getString(FKTABLE_NAME);
			String fkcol = fkeys.getString(FKCOLUMN_NAME);
			String seq = fkeys.getString(KEY_SEQ);
			Integer iseq = new Integer(seq);
			getExportedKeys().addForeignKey(fktable, fkcol, pkcol, iseq);
		}
		fkeys.close();
	}
	
	public ForeignKeys getExportedKeys(){
		if(exportedKeys == null){
			exportedKeys = new ForeignKeys(this);
		}
		return exportedKeys;
	}
	
	public ForeignKeys getImportedKeys(){
		if(importedKeys==null){
			importedKeys = new ForeignKeys(this);
		}
		return importedKeys;
	}
	
	String catalog = DbModelProvider.getInstance().catalog;
	String schema = DbModelProvider.getInstance().schema;
	
	private ForeignKeys exportedKeys;
	private ForeignKeys importedKeys;
	
	public static final String PKTABLE_NAME = "PKTABLE_NAME";
	public static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";
	public static final String FKTABLE_NAME = "FKTABLE_NAME";
	public static final String FKCOLUMN_NAME = "FKCOLUMN_NAME";
	public static final String KEY_SEQ = "KEY_SEQ";
}
