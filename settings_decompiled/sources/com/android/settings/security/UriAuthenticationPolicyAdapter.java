package com.android.settings.security;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import java.util.List;
/* loaded from: classes.dex */
public class UriAuthenticationPolicyAdapter extends RecyclerView.Adapter<UriViewHolder> {
    private final List<Uri> mUris;

    /* loaded from: classes.dex */
    public class UriViewHolder extends RecyclerView.ViewHolder {
        TextView mUriNameView;

        public UriViewHolder(View view) {
            super(view);
            this.mUriNameView = (TextView) this.itemView.findViewById(R$id.uri_name);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UriAuthenticationPolicyAdapter(List<Uri> list) {
        this.mUris = list;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public UriViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new UriViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R$layout.app_authentication_uri_item, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(UriViewHolder uriViewHolder, int i) {
        uriViewHolder.mUriNameView.setText(Uri.decode(this.mUris.get(i).toString()));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mUris.size();
    }
}
