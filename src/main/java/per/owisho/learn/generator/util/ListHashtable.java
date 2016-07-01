package per.owisho.learn.generator.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@SuppressWarnings({ "rawtypes", "serial" })
public class ListHashtable extends Hashtable {

	protected List orderedKeys = new ArrayList();

	public synchronized void clear() {
		super.clear();
		orderedKeys = new ArrayList();
	}

	@SuppressWarnings("unchecked")
	public synchronized Object put(Object aKey, Object aValue) {
		if (aKey instanceof Integer) {
			Integer key = (Integer) aKey;
			int pos = getFirstKeyGreater(key.intValue());
			if (pos > 0) {
				orderedKeys.add(pos, aKey);
			} else {
				orderedKeys.add(aKey);
			}
		} else {
			orderedKeys.add(aKey);
		}
		return super.put(aKey, aValue);
	}

	private int getFirstKeyGreater(int aKey) {
		int pos = 0;
		int numKeys = getOrderedKeys().size();
		for (int i = 0; i < numKeys; i++) {
			Integer key = (Integer) getOrderKey(i);
			int keyval = key.intValue();
			if (keyval < aKey) {
				++pos;
			} else {
				break;
			}
		}
		if (pos >= numKeys) {
			pos = -1;
		}
		return pos;
	}

	public synchronized Object remove(Object aKey) {
		if (orderedKeys.contains(aKey)) {
			int pos = orderedKeys.indexOf(aKey);
			orderedKeys.remove(pos);
		}
		return super.remove(aKey);
	}

	@SuppressWarnings("unchecked")
	public void reorderIntegerKeys() {
		List keys = getOrderedKeys();
		int numKeys = keys.size();
		if (numKeys <= 0)
			return;
		if (!(getOrderKey(0) instanceof Integer))
			return;
		List newKeys = new ArrayList();
		List newValues = new ArrayList();
		for (int i = 0; i < numKeys; i++) {
			Integer key = (Integer) getOrderKey(i);
			Object val = getOrderedValue(i);
			int numNew = newKeys.size();
			int pos = 0;
			for (int j = 0; j < numNew; j++) {
				Integer newKey = (Integer) newKeys.get(j);
				if (key.intValue() > newKey.intValue()) {
					++pos;
				} else {
					break;
				}
			}
			if (pos >= numKeys) {
				newKeys.add(key);
				newValues.add(val);
			} else {
				newKeys.add(pos, key);
				newValues.add(pos, val);
			}
		}
		this.clear();
		for (int l = 0; l < numKeys; l++) {
			put(newKeys.get(l), newValues.get(l));
		}
	}

	public String toString() {
		StringBuffer x = new StringBuffer();
		x.append("Ordered Keys: ");
		int numKeys = orderedKeys.size();
		x.append("[");
		for (int i = 0; i < numKeys; i++) {
			x.append(orderedKeys.get(i) + " ");
		}
		x.append("]\n");

		x.append("Ordered Values: ");
		x.append("[");

		for (int j = 0; j < numKeys; j++) {
			x.append(getOrderedValue(j) + " ");
		}
		x.append("]\n");
		return x.toString();
	}
	
	public void merge(ListHashtable newTable){
		int num = newTable.size();
		for(int i=0;i<num;i++){
			Object aKey = newTable.getOrderKey(i);
			Object aValue = newTable.getOrderedValue(i);
			this.put(aKey, aValue);
		}
	}

	public Object getOrderedValue(int i) {
		return super.get(getOrderKey(i));
	}

	public List getOrderedKeys() {
		return orderedKeys;
	}

	private Object getOrderKey(int i) {
		return getOrderedKeys().get(i);
	}
	
	public Object getKeyForValue(Object aValue){
		int num = getOrderedValues().size();
		for(int i=0;i<num;i++){
			Object tmpVal = getOrderedValue(i);
			if(tmpVal.equals(aValue)){
				return getOrderKey(i);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List getOrderedValues(){
		List values = new ArrayList();
		int numKeys = orderedKeys.size();
		for(int i=0;i<numKeys;i++){
			values.add(getOrderedValue(i));
		}
		return values;
	}

}
