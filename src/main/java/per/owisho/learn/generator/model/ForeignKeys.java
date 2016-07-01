package per.owisho.learn.generator.model;

import per.owisho.learn.generator.util.ListHashtable;

public class ForeignKeys {

	protected Table parentTable;
	protected ListHashtable associatedTables;
	
	public ForeignKeys(Table aTable){
		parentTable = aTable;
		associatedTables = new ListHashtable();
	}
	
	public void addForeignKey(String tableName,String columnName,String parentColumn,Integer seq){
		ForeignKey tbl = null;
		if(associatedTables.containsKey(tableName)){
			tbl = (ForeignKey)associatedTables.get(tableName);
		}
		else{
			tbl = new ForeignKey(parentTable,tableName);
			associatedTables.put(tableName, tbl);
		}
	}
	
	public ListHashtable getAssociatedTables(){
		return associatedTables;
	}
	
	public int getSize(){
		return getAssociatedTables().size();
	}
	
	public boolean getHasImportedKeyColumn(String aColumn){
		boolean isFound = false;
		int numKeys = getSize();
		for(int i=0;i<numKeys;i++){
			ForeignKey aKey = (ForeignKey)getAssociatedTables().getOrderedValue(i);
			if(aKey.getHasImportedKeyColumn(aColumn)){
				isFound = true;
				break;
			}
		}
		return isFound;
	}
	
	public ForeignKey getAssociatedTable(String name){
		Object fkey = getAssociatedTables().get(name);
		if(fkey!=null){
			return (ForeignKey) fkey;
		}
		else return null;
	}
	
	public Table getParentTable(){
		return parentTable;
	}
	
	public boolean getHasImportedKeyParentColumn(String aColumn) {
		boolean isFound = false;
		int numKeys = getSize();
		for(int i=0;i<numKeys;i++){
			ForeignKey aKey = (ForeignKey) getAssociatedTables().getOrderedValue(i);
			if(aKey.getHasImportedKeyParentColumn(aColumn)){
				isFound = true;
				break;
			}
		}
		return isFound;
	}
	
	public ForeignKey getImportedKeyParentCoumn(String aColumn){
		ForeignKey aKey = null;
		int numKeys = getSize();
		for(int i=0;i<numKeys;i++){
			aKey = (ForeignKey)getAssociatedTables().getOrderedValue(i);
			if(aKey.getHasImportedKeyParentColumn(aColumn)){
				break;
			}
		}
		return aKey;
	}
}
