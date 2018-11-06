package me.tatarka.shard.nav;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * int[] wrapper that grows when items are appended.
 */
final class IntArrayStack implements Parcelable {

    private static final int[] ZERO_ARRAY = new int[0];

    private int[] elements;
    private int size;

    public IntArrayStack() {
        elements = ZERO_ARRAY;
    }

    IntArrayStack(Parcel in) {
        elements = in.createIntArray();
        size = elements.length;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(toArray());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private int[] toArray() {
        if (elements.length == 0) {
            return ZERO_ARRAY;
        }
        return Arrays.copyOfRange(elements, 0, size);
    }

    public void push(int value) {
        if (elements.length == 0) {
            elements = new int[8];
        }
        elements[size++] = value;
        if (size >= elements.length) {
            doubleCapacity();
        }
    }

    public int pop() {
        if (size == 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return elements[--size];
    }

    public int size() {
        return size;
    }

    private void doubleCapacity() {
        int n = elements.length;
        int newCapacity = n << 1;
        if (newCapacity < 0) {
            throw new RuntimeException("Max array capacity exceeded");
        }
        int[] a = new int[newCapacity];
        System.arraycopy(elements, 0, a, 0, size);
        elements = a;
    }

    public static final Creator<IntArrayStack> CREATOR = new Creator<IntArrayStack>() {
        @Override
        public IntArrayStack createFromParcel(Parcel in) {
            return new IntArrayStack(in);
        }

        @Override
        public IntArrayStack[] newArray(int size) {
            return new IntArrayStack[size];
        }
    };
}
