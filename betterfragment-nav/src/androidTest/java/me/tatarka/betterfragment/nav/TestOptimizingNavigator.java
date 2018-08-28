package me.tatarka.betterfragment.nav;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;

@Navigator.Name("test")
public class TestOptimizingNavigator extends OptimizingNavigator<TestOptimizingNavigator.TestDestination, TestOptimizingNavigator.TestPage, TestOptimizingNavigator.TestState> {

    public final List<Transaction> transactions = new ArrayList<>();

    @NonNull
    @Override
    protected TestPage createPage(TestDestination destination, @Nullable Bundle args, @Nullable NavOptions navOptions) {
        return new TestPage(destination.name);
    }

    @Override
    protected void replace(@Nullable TestPage oldPage, @Nullable TestPage newPage, int backStackEffect) {
        transactions.add(new Transaction(oldPage, newPage, backStackEffect));
    }

    @NonNull
    @Override
    protected TestState savePageState(TestPage testPage) {
        return new TestState(testPage.name);
    }

    @NonNull
    @Override
    protected TestPage restorePageState(TestState testState) {
        return new TestPage(testState.name);
    }

    @NonNull
    @Override
    public TestDestination createDestination() {
        return new TestDestination(this);
    }

    public static class Transaction {
        @Nullable
        public final TestPage oldPage;
        @Nullable
        public final TestPage newPage;
        public final int backStackEffect;

        public Transaction(@Nullable TestPage oldPage, @Nullable TestPage newPage, int backStackEffect) {
            this.oldPage = oldPage;
            this.newPage = newPage;
            this.backStackEffect = backStackEffect;
        }
    }

    public static class TestDestination extends NavDestination {
        private String name;

        public TestDestination(@NonNull Navigator<? extends NavDestination> navigator) {
            super(navigator);
        }

        public TestDestination id(int id) {
            setId(id);
            return this;
        }

        public TestDestination name(String name) {
            this.name = name;
            return this;
        }
    }

    public static class TestPage {
        public final String name;

        public TestPage(String name) {
            this.name = name;
        }
    }

    public static class TestState implements Parcelable {
        public final String name;

        public TestState(String name) {
            this.name = name;
        }

        TestState(Parcel in) {
            name = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<TestState> CREATOR = new Creator<TestState>() {
            @Override
            public TestState createFromParcel(Parcel in) {
                return new TestState(in);
            }

            @Override
            public TestState[] newArray(int size) {
                return new TestState[size];
            }
        };
    }
}
