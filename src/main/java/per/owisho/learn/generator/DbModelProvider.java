package per.owisho.learn.generator;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import per.owisho.learn.generator.model.Column;
import per.owisho.learn.generator.model.Table;

public class DbModelProvider {

	private static final Log _log = LogFactory.getLog(DbModelProvider.class);

	public String catalog;

	public String schema;

	private Connection connection;

	private static DbModelProvider instance = new DbModelProvider();

	private DbModelProvider() {
		init();
	}

	private void init() {
		this.schema = PropertiesProvider.getProperty("jdbc.schema", "");
		if ("".equals(schema.trim())) {
			this.schema = null;
		}
		this.catalog = PropertiesProvider.getProperty("jdbc.catalog", "");
		if ("".equals(catalog.trim())) {
			this.catalog = null;
		}

		System.out.println("jdbc.schema=" + this.schema + "jdbc.catalog=" + this.catalog);
		try {
			Class.forName(PropertiesProvider.getProperty("jdbc.driver"));
		} catch (Exception e) {
		}
	}

	public static DbModelProvider getInstance() {
		return instance;
	}

	private Connection getConnection() throws SQLException {
		if (connection == null || connection.isClosed()) {
			connection = DriverManager.getConnection(PropertiesProvider.getProperty("jdbc.url"),
					PropertiesProvider.getProperty("jdbc.username"), PropertiesProvider.getProperty("jdbc.password"));
		}
		return connection;
	}

	@SuppressWarnings("rawtypes")
	public List getAllTables() throws Exception{
		Connection conn = getConnection();
		return getAllTables(conn);
	}
	
	public Table getTable(String sqlTableName) throws Exception{
		Connection conn = getConnection();
		DatabaseMetaData dbMetaData = conn.getMetaData();
		ResultSet rs = dbMetaData.getTables(catalog, schema, sqlTableName, null);
		while(rs.next()){
			Table table = createTable(conn, rs);
			return table;
		}
		throw new RuntimeException("not found table with give name:"+sqlTableName);
	}
	
	private Table createTable(Connection conn, ResultSet rs) throws SQLException {
//		ResultSetMetaData rsMetaData = rs.getMetaData();
//		String schemaName = rs.getString("TABLE_SCHEM") == null ? "" : rs.getString("TABLE_SCHEM");
		String realTableName = rs.getString("TABLE_NAME");
		String tableType = rs.getString("TABLE_TYPE");

		Table table = new Table();
		table.setSqlName(realTableName);
		if ("SYNONYM".equals(tableType) && isOracleDataBase()) {
			table.setOwenerSynonymName(getSynonymOwner(realTableName));
		}
		retriveTableColumns(table);
		table.initExportedKeys(conn.getMetaData());
		table.initImportedKeys(conn.getMetaData());
		return table;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getAllTables(Connection conn) throws SQLException{
		DatabaseMetaData dbMetaData = conn.getMetaData();
		ResultSet rs = dbMetaData.getTables(catalog, schema, null, null);
		List tables = new ArrayList();
		while(rs.next()){
			Table table = createTable(conn, rs);
			tables.add(table);
		}
		return tables;
	}

	private boolean isOracleDataBase() {
		boolean ret = false;
		try {
			ret = (getMetaData().getDatabaseProductName().toLowerCase().indexOf("oracle") != -1);
		} catch (Exception e) {
		}
		return ret;
	}

	private String getSynonymOwner(String synonyName) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String ret = null;
		try {
			ps = getConnection()
					.prepareStatement("select table_owner from sys.all_synonyms where tablename = ? and owner = ?");
			ps.setString(1, synonyName);
			ps.setString(2, schema);
			rs = ps.executeQuery();
			if (rs.next()) {
				ret = rs.getString(1);
			} else {
				String databaseStructure = getDatabaseStructureInfo();
				throw new RuntimeException(
						"Wow! Synonym" + synonyName + " not found how can it happen? " + databaseStructure);
			}
		} catch (Exception e) {
			String databaseStructure = getDatabaseStructureInfo();
			_log.error(e.getMessage(), e);
			throw new RuntimeException("Exception in getting synonym owner" + databaseStructure);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (Exception e2) {
				}
			}
		}
		return ret;
	}

	private String getDatabaseStructureInfo() {

		ResultSet schemaRs = null;
		ResultSet catalogRs = null;
		String nl = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer(nl);
		sb.append("Configured schema:").append(schema).append(nl);
		sb.append("Configured catalog:").append(catalog).append(nl);
		try {
			schemaRs = getMetaData().getSchemas();
			sb.append("Available schema:").append(nl);
			while (schemaRs.next()) {
				sb.append("   ").append(schemaRs.getString("TABLE_SCHEM")).append(nl);
			}
		} catch (Exception e) {
			_log.warn("Couldn't get schemas", e);
			sb.append("  ?? Couldn't get schemas ??").append(nl);
		} finally {
			if (null != schemaRs) {
				try {
					schemaRs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			catalogRs = getMetaData().getCatalogs();
			sb.append("Available catalog:").append(nl);
			while (catalogRs.next()) {
				sb.append("   ").append(catalogRs.getString("TABLE_CAT")).append(nl);
			}
		} catch (Exception e) {
			_log.warn("Couldn't get catalogs", e);
			sb.append("  ?? Couldn't get catalogs ??").append(nl);
		} finally {
			if (null != catalogRs) {
				try {
					catalogRs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	private DatabaseMetaData getMetaData() throws SQLException {
		return getConnection().getMetaData();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void retriveTableColumns(Table table) throws SQLException {
		_log.debug("-------setColumns(" + table.getSqlName() + ")");
		List primaryKeys = getTablePrimaryKeys(table);
		table.setPrimaryKeyColumns(primaryKeys);
		List indices = new LinkedList();
		Map uniqueIndices = new HashMap();
		Map uniqueColumns = new HashMap();
		ResultSet indexRs = null;
		try {
			if(table.getOwenerSynonymName()!=null){
				indexRs = getMetaData().getIndexInfo(catalog, table.getOwenerSynonymName(), table.getSqlName(), false, true);
			}else{
				indexRs = getMetaData().getIndexInfo(catalog, schema, table.getSqlName(), false, true);
			}
			while(indexRs.next()){
				String columnName = indexRs.getString("COLUMN_NAME");
				if(columnName!=null){
					_log.debug("debug:"+columnName);
					indices.add(indexRs);
				}
				String indexName = indexRs.getString("INDEX_NAME");
				boolean nonUnique = indexRs.getBoolean("NON_UNIQUE");
				if(!nonUnique&&columnName!=null&&indexName!=null){
					List l = (List)uniqueColumns.get(indexName);
					if(l==null){
						l = new ArrayList();
						uniqueColumns.put(indexName, l);
					}
					l.add(columnName);
					uniqueIndices.put(columnName, indexName);
					_log.debug("unique:"+columnName+"("+indexName+")");
				}
			}
		} catch (Exception e) {
		} finally{
			if(null!=indexRs){
				indexRs.close();
			}
		}
		List columns = getTableColumns(table, primaryKeys, indices, uniqueIndices, uniqueColumns);
		for(Iterator i = columns.iterator();i.hasNext();){
			Column column = (Column)i.next();
			table.addColumn(column);
		}
		if(primaryKeys.size()==0){
			_log.warn("WARNING: The JDBC driver didn't report any primary key columns in " + table.getSqlName());
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getTableColumns(Table table,List primaryKeys,List indices,Map uniqueIndices,Map uniqueColumns) throws SQLException{
		
		List columns = new LinkedList();
		ResultSet columnRs = getColumnsResultSet(table);
		while(columnRs.next()){
			int sqlType = columnRs.getInt("DATA_TYPE");
			String sqlTypeName = columnRs.getString("TYPE_NAME");
			String columnName = columnRs.getString("COLUMN_NAME");
			String columnDefaultValue = columnRs.getString("COLUMN_DEF");
			boolean isNullable = (DatabaseMetaData.columnNullable==columnRs.getInt("NULLABLE"));
			int size = columnRs.getInt("COLUMN_SIZE");
			int decimalDigits = columnRs.getInt("COLUMN_DIGITS");
			
			boolean isPk = primaryKeys.contains(columnName);
			boolean isIndexed = indices.contains(columnName);
			
			String uniqueIndex = (String)uniqueIndices.get(columnName);
			List columnsInUniqueIndex = null;
			if(uniqueIndex!=null){
				columnsInUniqueIndex = (List)uniqueColumns.get(uniqueIndex);
			}
			boolean isUnique = columnsInUniqueIndex!=null&&columnsInUniqueIndex.size()==1;
			if(isUnique){
				_log.debug("unique column:"+columnName);
			}
			Column column = new Column.Builder()._table(table)._sqlType(sqlType)._sqlTypeName(sqlTypeName)
					._sqlName(columnName)._size(size)._decimalDigits(decimalDigits)._isPk(isPk)._isNullable(isNullable)
					._isIndexed(isIndexed)._isUnique(isUnique)._defaultValue(columnDefaultValue).build();
			columns.add(column);
		}
		columnRs.close();
		return columns;
	}
	
	private ResultSet getColumnsResultSet(Table table) throws SQLException{
		ResultSet columnRs = null;
		if(table.getOwenerSynonymName()!=null){
			columnRs = getMetaData().getColumns(catalog, table.getOwenerSynonymName(), table.getSqlName(), null);
		}else{
			columnRs = getMetaData().getColumns(catalog, schema, table.getSqlName(), null);
		}
		return columnRs;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getTablePrimaryKeys(Table table) throws SQLException{
		List primaryKeys = new LinkedList();
		ResultSet primaryKeyRs = null;
		if(table.getOwenerSynonymName()!=null){
			primaryKeyRs = getMetaData().getPrimaryKeys(catalog, table.getOwenerSynonymName(), table.getSqlName());
		}
		else{
			primaryKeyRs = getMetaData().getPrimaryKeys(catalog, schema, table.getSqlName());
		}
		while(primaryKeyRs.next()){
			String columnName = primaryKeyRs.getString("COLUMN_NAME");
			_log.debug("primary key:"+columnName);
			primaryKeys.add(columnName);
		}
		primaryKeyRs.close();
		return primaryKeys;
	}
}
