package com.alibaba.jvm.sandbox.core.api;


import com.alibaba.jvm.sandbox.core.api.event.Event;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件监听器
 *
 **/
public interface EventListener {

    void onEvent(Event event) throws Throwable;

    String getId();

    String getClassPattern();

    String getMethodPattern();

    int getParaCnt();

    /**
     * Event types support
     * @return
     */
    Event.Type[] support();

    public class Factory {

        private static Map<String, EventListener> listenerMap = new ConcurrentHashMap<>();

        public static void register(EventListener listener) {
            listenerMap.put(listener.getId(), listener);
        }

        public static void unregister(String id) {
            listenerMap.remove(id);
        }

        public static EventListener getEventListener(String id) {
            return listenerMap.get(id);
        }

        public static Set<String> getAdviceIds() {
            return listenerMap.keySet();
        }

        public static void destroy() {
            listenerMap.clear();
        }
    }
}
