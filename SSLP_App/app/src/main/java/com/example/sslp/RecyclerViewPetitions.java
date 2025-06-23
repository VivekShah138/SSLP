package com.example.sslp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class RecyclerViewPetitions extends RecyclerView.Adapter<RecyclerViewPetitions.ViewHolder> {

    List<Petitions> data;
    final private Context context;
    public RecyclerViewPetitions(Context context,List<Petitions> data){
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewPetitions.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_row_petition_display,parent,false);
        return new RecyclerViewPetitions.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewPetitions.ViewHolder holder, int position) {
        holder.textViewPetitionTitle.setText(data.get(position).getTitle_());
        holder.textViewSignatures.setText(String.format("%d",data.get(position).getSignatureSize_()) + " Signatures");
        holder.textViewPetitionStatus.setText(data.get(position).getStatus_());
        if(!data.get(position).getIsClosed()){

            holder.textViewPetitionStatus.setTextColor(ContextCompat.getColor(context, R.color.GreenA700));
        }
        else{
            holder.textViewPetitionStatus.setTextColor(ContextCompat.getColor(context, R.color.RedA700));
        }


        holder.relativeLayoutSingleRowPetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDetails(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textViewPetitionTitle,textViewSignatures, textViewPetitionStatus;
        RelativeLayout relativeLayoutSingleRowPetition;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewSignatures = itemView.findViewById(R.id.textViewSingleRowPetitionSignatures);
            textViewPetitionTitle = itemView.findViewById(R.id.textViewSingleRowPetitionTitle);
            textViewPetitionStatus = itemView.findViewById(R.id.textViewSingleRowPetitionStatus);
            relativeLayoutSingleRowPetition = itemView.findViewById(R.id.relativeLayoutSingleRowPetitions);


        }
    }

    private void openDetails(int position) {

        Intent intent = new Intent(context,SinglePetitionDetails.class);
        intent.putExtra("Petition", data.get(position));
        context.startActivity(intent);


    }
}
