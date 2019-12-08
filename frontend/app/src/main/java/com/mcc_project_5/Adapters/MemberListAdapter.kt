package com.mcc_project_5.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.mcc_project_5.DataModels.ProjectMember
import com.mcc_project_5.R
import com.mcc_project_5.Tools.ImageStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_of_projects_members_list_layout.view.*
import kotlin.collections.ArrayList

class MemberListAdapter(context: Context, val items: ArrayList<ProjectMember>): RecyclerView.Adapter<MemberListAdapter.MemberViewHolder>() {
    private var context = context
    private val picasso = Picasso.get()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_of_projects_members_list_layout, parent, false) as LinearLayout
        return MemberViewHolder(linearLayout)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.linearLayout.x += 15 * position
        if (items[position].imageUrl != "") {
            ImageStorage(context).loadToImageView(items[position].imageUrl, holder.linearLayout.memberImageView)
            //picasso.load(items[position].imageUrl).transform(CropCircleTransformation()).fit().into(holder.linearLayout.memberImageView)
        } else {
            holder.linearLayout.memberImageView.setImageResource(R.drawable.ic_account_circle_black_24dp)
        }

    }

    class MemberViewHolder(val linearLayout: LinearLayout): RecyclerView.ViewHolder(linearLayout)

}
