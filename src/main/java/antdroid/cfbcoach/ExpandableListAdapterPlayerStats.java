package antdroid.cfbcoach;

/*
  Created by Achi Jones on 2/21/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Class used for the recruiting expandable list view
 */
class ExpandableListAdapterPlayerStats extends BaseExpandableListAdapter {

    private final Activity context;
    private final Map<String, List<String>> playersInfo;
    private final List<String> players;
    private final MainActivity mainAct;

    public ExpandableListAdapterPlayerStats(Activity context, MainActivity mainAct, List<String> players, Map<String, List<String>> playersInfo) {
        this.context = context;
        this.players = players;
        this.playersInfo = playersInfo;
        this.mainAct = mainAct;
    }

    public String getChild(int groupPosition, int childPosition) {
        return playersInfo.get(players.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String playerDetail = getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        convertView = inflater.inflate(R.layout.child_player_stats, null);

        // Set up Text for player details
        TextView itemL = convertView.findViewById(R.id.textPlayerStatsLeftChild);
        TextView itemR = convertView.findViewById(R.id.textPlayerStatsRightChild);
        TextView itemC = convertView.findViewById(R.id.textPlayerStatsCenter);
        itemC.setText("");
        itemL.setText("");
        itemR.setText("");

        String[] detailSplit = playerDetail.split(">");
        if (detailSplit.length == 2) {
            if (playerDetail.substring(0, 3).equals("[I]")) {
                itemL.setText(detailSplit[0].substring(3));
                itemR.setText(detailSplit[1]);
                itemL.setTextColor(Color.YELLOW);
                itemR.setTextColor(Color.YELLOW);
            } else if (playerDetail.substring(0, 3).equals("[T]")) {
                itemL.setText(detailSplit[0].substring(3));
                itemR.setText(detailSplit[1]);
                itemL.setTextColor(Color.RED);
                itemR.setTextColor(Color.RED);
            } else if (playerDetail.substring(0, 3).equals("[S]")) {
                itemL.setText(detailSplit[0].substring(3));
                itemR.setText(detailSplit[1]);
                itemL.setTextColor(Color.MAGENTA);
                itemR.setTextColor(Color.MAGENTA);
            } else if (playerDetail.contains("[RS]")) {
                itemL.setText(detailSplit[0]);
                itemR.setText(detailSplit[1]);
                itemL.setTextColor(Color.RED);
                itemR.setTextColor(Color.RED);
            } else {
                itemL.setText(detailSplit[0]);
                itemR.setText(detailSplit[1]);
                colorizeRatings(itemL, detailSplit[0]);
                colorizeRatings(itemR, detailSplit[1]);
            }
        } else {
            // center
            itemC.setText(playerDetail);
            itemL.setVisibility(View.GONE);
            itemR.setVisibility(View.GONE);
        }

        Button buttonViewStats = convertView.findViewById(R.id.buttonPlayerStatsViewAll);
        buttonViewStats.setText("View Career Stats");
        buttonViewStats.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mainAct.examinePlayer(players.get(groupPosition));
            }
        });

        if (players.get(groupPosition).equals("BENCH > BENCH")) {
            /* Last group, meaning its the bench
            itemL.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    mainAct.examinePlayer(playerDetail);
                }
            });*/
            buttonViewStats.setVisibility(View.GONE);
        } else {
            if (!isLastChild) buttonViewStats.setVisibility(View.GONE);
            else buttonViewStats.setVisibility(View.VISIBLE);
            itemL.setOnClickListener(null);
        }

        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return playersInfo.get(players.get(groupPosition)).size();
    }

    public String getGroup(int groupPosition) {
        return players.get(groupPosition);
    }

    public int getGroupCount() {
        return players.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.group_player_stats, null);

        String[] detailSplit = getGroup(groupPosition).split(">");
        TextView itemL = convertView.findViewById(R.id.textPlayerStatsLeft);
        itemL.setText(detailSplit[0]);
        itemL.setTypeface(null, Typeface.BOLD);

        TextView itemR = convertView.findViewById(R.id.textPlayerStatsRight);
        itemR.setText(detailSplit[1]);
        itemR.setTypeface(null, Typeface.BOLD);

        // Highlight POTYs, All Americans, and All Conf players
        int playerAwards = 0;
        if (getGroup(groupPosition).equals("BENCH > BENCH")) playerAwards = 0;
        else playerAwards = mainAct.checkAwardPlayer(getGroup(groupPosition));

        if (playerAwards == 3) {
            // POTY
            itemL.setTextColor(Color.parseColor("#FF9933"));
            itemR.setTextColor(Color.parseColor("#FF9933"));
        } else if (playerAwards == 2) {
            // All American
            itemL.setTextColor(Color.parseColor("#1A75FF"));
            itemR.setTextColor(Color.parseColor("#1A75FF"));
        } else if (playerAwards == 1) {
            // All Conf
            itemL.setTextColor(Color.parseColor("#00B300"));
            itemR.setTextColor(Color.parseColor("#00B300"));
        }

        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void colorizeRatings(TextView textV, String rating) {
        String[] ratSplit = rating.split(" ");
        // The last index is always the rating: A+, C, etc
        if (ratSplit.length > 0 && rating.split(",").length == 1) {
            String letter = ratSplit[ratSplit.length - 1];
            if (letter.contains("A")) {
                textV.setTextColor(Color.parseColor("#5994de"));
            } else if (letter.contains("B")) {
                textV.setTextColor(Color.parseColor("#00b300"));
            } else if (letter.contains("C")) {
                textV.setTextColor(Color.YELLOW);
            } else if (letter.contains("D")) {
                textV.setTextColor(Color.parseColor("#e68a00"));
            } else if (letter.contains("F")) {
                textV.setTextColor(Color.RED);
            }
        }
    }

} //end class
