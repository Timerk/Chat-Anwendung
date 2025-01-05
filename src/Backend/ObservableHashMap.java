package Backend;

import java.util.HashMap;

//HashMap um übwachen zu können wann ein Element hinzugefügt wird 
public class ObservableHashMap<K, V> extends HashMap<K, V> {
    private MapSizeChangeListener<K> listener;

    public interface MapSizeChangeListener<K> {
        void onSizeChanged(K Key);
    }

    public void setMapSizeChangeListener(MapSizeChangeListener<K> listener) {
        this.listener = listener;
    }

    @Override
    public V put(K key, V value) {
        V result = super.put(key, value);
        if (listener != null) {
            listener.onSizeChanged(key);      //Implementation erfolgt Kostruktor der Klasse SeverGui
        }
        return result;
    }

}
