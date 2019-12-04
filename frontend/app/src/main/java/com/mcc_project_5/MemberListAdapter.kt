package com.mcc_project_5

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.mcc_project_5.DataModels.ProjectMember
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.list_of_projects_members_list_layout.view.*

class MemberListAdapter(val items: ArrayList<ProjectMember>): RecyclerView.Adapter<MemberListAdapter.MemberViewHolder>() {
    private val picasso = Picasso.get()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_of_projects_members_list_layout, parent, false) as LinearLayout
        return MemberViewHolder(linearLayout)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.linearLayout.x -= 15 * position
        picasso.load(items[position].imageUrl).transform(CropCircleTransformation()).into(holder.linearLayout.memberImageView)
    }

    class MemberViewHolder(val linearLayout: LinearLayout): RecyclerView.ViewHolder(linearLayout)

}
