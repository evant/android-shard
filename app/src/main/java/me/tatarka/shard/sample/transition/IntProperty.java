/*
 * Copyright (C) 2016 Andrey Kulikov (andkulikov@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.tatarka.shard.sample.transition;

import android.annotation.SuppressLint;
import android.util.Property;

import androidx.annotation.NonNull;

/**
 * Helper class for properties with int values.
 * Unfortunately we can't just extend android.util.IntProperty here,
 * because of a bug that was fixed only in 4.2.2:
 * https://android.googlesource.com/platform/frameworks/base/+/c5d43f76fd7c3ccb91f1b75618a9c9e8f202505b
 * <p/>
 * To apply internal optimizations to avoid autoboxing use object that will be
 * returned by method {@link #optimize()}
 * <p/>
 * Created by Andrey Kulikov on 17/04/16.
 */
public abstract class IntProperty<T> extends Property<T, Integer> {

    public IntProperty() {
        super(Integer.class, null);
    }

    public abstract void setValue(@NonNull T object, int value);

    @Override
    final public void set(@NonNull T object, @NonNull Integer value) {
        setValue(object, value);
    }

    /**
     * Just default realisation. Some of properties can have no getter. Override for real getter
     */
    @Override
    @NonNull
    public Integer get(T object) {
        return 0;
    }

    @SuppressLint("NewApi")
    @NonNull
    public Property<T, Integer> optimize() {
        return new android.util.IntProperty<T>(null) {
            @Override
            public void setValue(@NonNull T object, int value) {
                IntProperty.this.setValue(object, value);
            }

            @Override
            @NonNull
            public Integer get(@NonNull T object) {
                return IntProperty.this.get(object);
            }
        };
    }
}
