package com.sprytar.android.util;

import com.sprytar.android.R;

public class BadgeUtils {

    private static final String PARK_VISITED_1 = "Visited one park";
    private static final String PARK_VISITED_5 = "Visited five parks";
    private static final String PERCENT_GAME_100 = "100% correct game";
    private static final String ADVENTURER = "Adventurer";
    private static final String BEGINNER_GAMER = "Beginner gamer";
    private static final String DISCOVERY = "Discovery";
    private static final String EXPLORER = "Explorer";
    private static final String GUIDE = "Guide";
    private static final String MASTER_GAMER = "Master gamer";
    private static final String MUSEUMS = "Museums";
    private static final String PRO_GAMER = "Pro gamer";
    private static final String ROOKIE_GAMER = "Rookie gamer";
    private static final String VENUE_CLEARED = "Venue cleared";

    public static int getBadgeResource(String badgeName){
        switch (badgeName){
            case PARK_VISITED_1:
                return R.drawable.one_park_visited;
            case PARK_VISITED_5:
                return R.drawable.five_park_visited;
            case PERCENT_GAME_100:
                return R.drawable.hundred_percent_game;
            case ADVENTURER:
                return R.drawable.adventurer;
            case BEGINNER_GAMER:
                return R.drawable.beginner_gamer;
            case DISCOVERY:
                return R.drawable.discovery;
            case EXPLORER:
                return R.drawable.explorer;
            case GUIDE:
                return R.drawable.guide;
            case MASTER_GAMER:
                return R.drawable.master_gamer;
            case MUSEUMS:
                return R.drawable.museums;
            case PRO_GAMER:
                return R.drawable.pro_gamer;
            case ROOKIE_GAMER:
                return R.drawable.rookie_gamer;
            case VENUE_CLEARED:
                return R.drawable.venue_cleared;
        }

        return R.drawable.one_park_visited;
    }
}
