package com.ecloudiot.framework.widget.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.GsonUtil;
import com.google.gson.JsonObject;

public class ListViewCellTwoLineText extends ListViewCellBase {

    public ListViewCellTwoLineText(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
        super(cellType, context, listViewBaseAdapter);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent, JsonObject jObject) {
        ViewHolder holder = null;
        Model model = GsonUtil.fromJson(jObject, Model.class);
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_twoline_text, null);
            holder = new ViewHolder();
            holder.headTitle = (TextView) convertView.findViewById(R.id.widget_listview_item_twoline_title);
            holder.headTime = (TextView) convertView.findViewById(R.id.widget_listview_item_twoline_time);
            holder.subTitle = (TextView) convertView.findViewById(R.id.widget_listview_item_twoline_subtile);
            holder.expandTitle = (TextView) convertView.findViewById(R.id.widget_listview_item_twoline_expandtext);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.headTitle.setText(model.getHeadTitle());
        holder.headTime.setText(model.getHeadTime());
        holder.subTitle.setText(model.getSubTitle());

        if (model.expandTitle.isEmpty()) {
            holder.expandTitle.setVisibility(View.GONE);
        } else {
            holder.expandTitle.setText(model.getExpandTitle());
            holder.expandTitle.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    class ViewHolder {
        private TextView headTitle;
        private TextView headTime;
        private TextView subTitle;
        private TextView expandTitle;

        public TextView getHeadTitle() {
            return headTitle;
        }

        public void setHeadTitle(TextView headTitle) {
            this.headTitle = headTitle;
        }

        public TextView getHeadTime() {
            return headTime;
        }

        public void setHeadTime(TextView headTime) {
            this.headTime = headTime;
        }


        public TextView getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(TextView subTitle) {
            this.subTitle = subTitle;
        }

        public TextView getExpandTitle() {
            return expandTitle;
        }

        public void setExpandTitle(TextView expandTitle) {
            this.expandTitle = expandTitle;
        }
    }

    class Model {
        private String headTitle;
        private String headTime;
        private String subTitle;
        private String expandTitle;

        public String getHeadTitle() {
            return headTitle;
        }

        public void setHeadTitle(String headTitle) {
            this.headTitle = headTitle;
        }

        public String getHeadTime() {
            return headTime;
        }

        public void setHeadTime(String headTime) {
            this.headTime = headTime;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public String getExpandTitle() {
            return expandTitle;
        }

        public void setExpandTitle(String expandTitle) {
            this.expandTitle = expandTitle;
        }
    }
}
