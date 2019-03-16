package me.tatarka.shard.nav;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;

@Navigator.Name("test")
public class TestOptimizingNavigator extends OptimizingNavigator<TestOptimizingNavigator.TestDestination, TestOptimizingNavigator.TestPage, TestOptimizingNavigator.TestState> implements NavController.OnDestinationChangedListener {

    public final List<Transaction> transactions = new ArrayList<>();
    public final List<Integer> destinations = new ArrayList<>();
    public final List<TestPage> savedPages = new ArrayList<>();
    public final List<TestState> restoredStates = new ArrayList<>();

    public TestOptimizingNavigator() {
    }

    @NonNull
    @Override
    protected TestPage createPage(TestDestination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Extras extras) {
        return new TestPage(destination.name);
    }

    @Override
    protected void replace(@Nullable TestPage oldPage, @Nullable TestPage newPage, int backStackEffect) {
        transactions.add(new Transaction(oldPage, newPage, backStackEffect));
    }

    @NonNull
    @Override
    protected TestState savePageState(TestPage testPage) {
        savedPages.add(testPage);
        return new TestState(testPage.name);
    }

    @NonNull
    @Override
    protected TestPage restorePageState(TestState testState) {
        restoredStates.add(testState);
        return new TestPage(testState.name);
    }

    @NonNull
    @Override
    public TestDestination createDestination() {
        return new TestDestination(this);
    }

    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
        destinations.add(destination.getId());
    }

    public static class Transaction {
        @Nullable
        public final TestPage oldPage;
        @Nullable
        public final TestPage newPage;
        @Direction
        public final int direction;

        public Transaction(@Nullable TestPage oldPage, @Nullable TestPage newPage, @Direction int direction) {
            this.oldPage = oldPage;
            this.newPage = newPage;
            this.direction = direction;
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

        @Override
        public String toString() {
            return "TestPage{" +
                    "name='" + name + '\'' +
                    '}';
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
