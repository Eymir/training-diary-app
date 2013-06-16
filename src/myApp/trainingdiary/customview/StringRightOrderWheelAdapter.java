/*
 * 
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package myApp.trainingdiary.customview;

import android.content.Context;

import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

/**
 * Numeric Wheel adapter.
 */
public class StringRightOrderWheelAdapter<T> extends ArrayWheelAdapter<T> {

    private final T[] items;

    /**
     * Constructor
     *
     * @param context the current context
     */
    public StringRightOrderWheelAdapter(Context context, T[] strings) {
        super(context, strings);

        this.items = strings;
    }

    @Override
    public CharSequence getItemText(int index) {
        if (index >= 0 && index < items.length) {
            T item = items[items.length - index - 1];
            if (item instanceof CharSequence) {
                return (CharSequence) item;
            }
            return item.toString();
        }
        return null;
    }

    public int getIndexByValue(String value) {
        for (int i = 0; i < getItemsCount(); i++) {
            if (getItemText(i).equals(value)) {
                return i;
            }
        }
        return 0;
    }

}
