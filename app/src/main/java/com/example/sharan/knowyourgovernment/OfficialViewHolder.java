package com.example.sharan.knowyourgovernment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class OfficialViewHolder extends RecyclerView.ViewHolder {

    public TextView nameAndParty;
    public TextView office;

    public OfficialViewHolder(@NonNull View itemView) {
        super(itemView);
        nameAndParty = itemView.findViewById(R.id.politicanAndPartyName);
        office = itemView.findViewById(R.id.officeName);
    }
}
