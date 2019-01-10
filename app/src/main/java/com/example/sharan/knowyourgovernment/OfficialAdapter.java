package com.example.sharan.knowyourgovernment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder> {

    private  MainActivity ma;
    private List<Official> officialList;

    public OfficialAdapter(MainActivity ma, List<Official> officialList) {
        this.ma = ma;
        this.officialList = officialList;
    }

    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.official, viewGroup, false);

        itemView.setOnClickListener(ma);
        itemView.setOnLongClickListener(ma);

        return new OfficialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficialViewHolder officialViewHolder, int i) {
        Official newOfficial = officialList.get(i);

        String party = newOfficial.getParty()!= null ? newOfficial.getParty() : "Unknown";

        officialViewHolder.nameAndParty.setText(newOfficial.getName() + " (" + party + ")");
        officialViewHolder.office.setText(newOfficial.getOffice());
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
