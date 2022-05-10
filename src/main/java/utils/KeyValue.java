package utils;

public class KeyValue<K,V> {
	private K key;
	private V value;
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof KeyValue<?,?>) {
			KeyValue<?,?> o = (KeyValue<?,?>)obj;
			if(o.getKey() == null && o.getValue() != null) {
				if(this.getValue().equals(o.getValue()))
					return true;
				return false;
			}else if(o.getKey() == null && o.getValue() == null) {
				
			}
		}
		return false;
	}
	
	public K getKey() {
		return key;
	}
	public void setKey(K key) {
		this.key = key;
	}
	public V getValue() {
		return value;
	}
	public void setValue(V value) {
		this.value = value;
	}	
	
	
	public static <K,V> KeyValue<K,V> fromKey(K key){
		KeyValue<K,V> kv = new KeyValue<K,V>();
		kv.setKey(key);
		return kv;
	}
	public static <K,V> KeyValue<K,V> fromValue(V value){
		KeyValue<K,V> kv = new KeyValue<K,V>();
		kv.setValue(value);
		return kv;
	}
}
