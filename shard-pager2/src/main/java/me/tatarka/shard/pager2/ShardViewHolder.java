package me.tatarka.shard.pager2;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public final class ShardViewHolder extends RecyclerView.ViewHolder {
    ShardAdapter.Page page;

    ShardViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    static ShardViewHolder create(ViewGroup parent) {
        FrameLayout container = new FrameLayout(parent.getContext());
        container.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));
        return new ShardViewHolder(container);
    }

    ViewGroup getContainer() {
        return (ViewGroup) itemView;
    }
}
